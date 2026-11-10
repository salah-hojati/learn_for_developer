---
title: Lab 33 - Extending Kubernetes (CRDs and Operators)
lesson: 33
module: 11 Operators
tags: [kubernetes, crd, operator, custom-resources, controller]
---

# Lab 33 - Extending Kubernetes (CRDs and Operators)

## Objective

In this lab you will extend a running kind cluster by teaching the API Server a new object type through a Custom Resource Definition (CRD). You will install a `CronTab` CRD, verify the API Server registers the new endpoint, create a Custom Resource (CR) instance of it, intentionally try to create a CR before the CRD exists to observe the exact API rejection, trigger a schema validation failure with a bad field, and then explore how a real-world operator such as Cert-Manager registers and manages its own CRDs.

## Prerequisites

- A running kind cluster
- kubectl installed and configured
- Completion of Lessons 1 through 32

## Pre-Lab Checklist

- [ ] kind cluster running
- [ ] `kubectl get nodes` shows Ready status
- [ ] kubectl can reach the default namespace
- [ ] Internet access for the optional Cert-Manager step
- [ ] `curl` available for the optional Cert-Manager install

---

## Step 1: Define the CronTab CRD

Create a file named `crontab-crd.yaml`:

```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: crontabs.stable.example.com
spec:
  group: stable.example.com
  scope: Namespaced
  names:
    plural: crontabs
    singular: crontab
    kind: CronTab
    shortNames:
    - ct
  versions:
  - name: v1
    served: true
    storage: true
    schema:
      openAPIV3Schema:
        type: object
        properties:
          spec:
            type: object
            properties:
              cronSpec:
                type: string
              image:
                type: string
```

Key facts about this manifest:

- `metadata.name` must be `<plural>.<group>`, in this case `crontabs.stable.example.com`.
- `spec.scope: Namespaced` means every instance lives inside a namespace.
- `spec.names.shortNames` registers `ct` as a fast alias for `kubectl`.
- `spec.versions[].schema.openAPIV3Schema` is the OpenAPI v3 schema the API Server uses to validate every `CronTab` CR. It declares `spec.cronSpec` and `spec.image` as strings.

## Step 2: Apply the CRD and Verify the API Server Learned It

Apply the CRD:

```bash
kubectl apply -f crontab-crd.yaml
```

Expected output:

```text
customresourcedefinition.apiextensions.k8s.io/crontabs.stable.example.com created
```

Verify the CRD is registered:

```bash
kubectl get crd
```

Expected output:

```text
NAME                        CREATED AT
crontabs.stable.example.com  ...
```

Wait a few seconds for the API Server to register the new endpoint, then confirm the new API group is discoverable:

```bash
kubectl api-resources | grep crontabs
```

Expected output:

```text
crontabs                                   crontab.stable.example.com/v1        false        CronTab
```

The third column (`false`) means the resource is namespaced. The API Server now accepts `CronTab` objects under the group `stable.example.com/v1`.

## Step 3: Create a CronTab Custom Resource

Create a file named `my-cron.yaml`:

```yaml
apiVersion: stable.example.com/v1
kind: CronTab
metadata:
  name: my-cron
spec:
  cronSpec: "* * * * */5"
  image: my-cron-image:v1
```

Apply it:

```bash
kubectl apply -f my-cron.yaml
```

Expected output:

```text
crontab.stable.example.com/my-cron created
```

List instances using the plural name:

```bash
kubectl get crontabs
```

Expected output:

```text
NAME      AGE
my-cron   10s
```

List instances using the `ct` short name:

```bash
kubectl get ct
```

Expected output:

```text
NAME      AGE
my-cron   10s
```

The CR is now stored in etcd just like any built-in resource.

## Step 4: Create a CR Before the CRD Exists (Expected Rejection)

Clean up first so the CRD is gone, then try to create a CR. Delete the CRD only (keep `my-cron.yaml` on disk):

```bash
kubectl delete -f crontab-crd.yaml
```

Delete the CR instance if it still exists:

```bash
kubectl delete crontab my-cron
```

Now try to apply the CR without the CRD:

```bash
kubectl apply -f my-cron.yaml
```

Expected output and error:

```text
error: unable to recognize "my-cron.yaml": no matches for kind "CronTab" in version "stable.example.com/v1"
```

This is the exact error the API Server returns. The server is strictly typed: it searches its internal registry for a schema matching kind `CronTab` in group `stable.example.com/v1`, finds nothing, and rejects the request. No CR is created because the CRD that defines the schema is missing.

Restore the CRD for the remaining steps:

```bash
kubectl apply -f crontab-crd.yaml
```

## Step 5: Trigger a Schema Validation Failure

Re-apply the valid CR first:

```bash
kubectl apply -f my-cron.yaml
```

Now create a file named `bad-crontab.yaml` with an unknown field under `spec`. The CRD schema does not declare `spec.parallelism`, so the API Server must reject it:

```yaml
apiVersion: stable.example.com/v1
kind: CronTab
metadata:
  name: bad-cron
spec:
  cronSpec: "* * * * *"
  image: my-cron-image:v1
  parallelism: 4
```

Apply it:

```bash
kubectl apply -f bad-crontab.yaml
```

Expected output and error:

```text
The CronTab "bad-cron" is invalid: spec.parallelism: Forbidden: unknown field 'parallelism' in io.k8s.apiextensions-apiserver.pkg.apis.apiextensions.v1.CronTab.spec
```

The OpenAPI v3 schema is enforced. Because the CRD does not declare a `parallelism` property, the API Server rejects the CR and does not store it. This is a validation failure, completely separate from the `no matches for kind` rejection in Step 4.

## Step 6: Inspect the CRD and CR with explain and describe

`kubectl explain` reads the CRD schema and prints the fields kubectl knows about:

```bash
kubectl explain crontab.spec
```

Expected output:

```text
KIND:     CronTab
VERSION:  stable.example.com/v1

RESOURCE: spec <object>

DESCRIPTION:
     <empty>apis
```

To see the full schema, served versions, and scope:

```bash
kubectl describe crd crontabs.stable.example.com
```

Expected output (abbreviated):

```text
Name:         crontabs.stable.example.com
Scope:        Namespaced
Kind:         CronTab
Metadata:
  Creation Timestamp:  ...
Spec:
  Group:  stable.example.com
  Names:
    Kind:       CronTab
    Plural:     crontabs
    Short Names:
      ct
    Singular:   crontab
  Scope:        Namespaced
  Versions:
    Name:  v1
    Served:  true
    Storage:  true
```

## Step 7: Observe That No Controller Exists Yet

The CRD enabled the API Server to store `my-cron`, but nothing reconciles it. List the Pods to confirm the cluster did not create anything from the CR:

```bash
kubectl get pods
```

Expected output:

```text
No resources found in default namespace.
```

A CRD alone is a passive schema. A `CronTab` CR is just data in etcd. The Operator pattern is what adds the reconciliation loop that watches the CR and provisions real Deployments, Services, or other resources based on it. Without an Operator watching `stable.example.com/v1`, nothing acts on the CR.

### Optional: Cert-Manager as a Real Operator Example

Install Cert-Manager, a popular operator, and inspect the CRDs it registers with the API Server:

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.3/cert-manager.yaml
```

Verify the operator registered its own CRDs:

```bash
kubectl get crd | grep cert-manager
```

Expected output (abbreviated):

```text
certificaterequests.cert-manager.io     2026-...
certificates.cert-manager.io           2026-...
issuers.cert-manager.io                2026-...
orders.acme.cert-manager.io            2026-...
```

Create an issuer-style kind like `Certificate` or `Issuer`, and let the Cert-Manager pods watch it and write the TLS Secret for you. Unlike your `CronTab`, a `Certificate` CR is reconciled: the Cert-Manager operator sees it, obtains the certificate, and creates the Secret.

```bash
kubectl get pods -n cert-manager
```

Expected output (abbreviated):

```text
NAME                                       READY   STATUS    RESTARTS   AGE
cert-manager-xxxx-xxxxx                    1/1     Running   0          25s
cert-manager-cainjector-xxxx-xxxxx         1/1     Running   0          25s
cert-manager-webhook-xxxx-xxxxx            1/1     Running   0          25s
```

Optional cleanup of the Cert-Manager installation:

```bash
kubectl delete -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.3/cert-manager.yaml
```

## Step 8: Cleanup

Delete the custom resources and the CRD:

```bash
kubectl delete -f my-cron.yaml
kubectl delete -f crontab-crd.yaml
```

Verify the cluster returns to a clean state and the API group registry deregisters the kind:

```bash
kubectl get ct
```

Expected output:

```text
error: the server doesn't have a resource type "ct"
```

Also visible through the full CRD list:

```bash
kubectl get crd | grep crontabs
```

The `crontabs.stable.example.com` row disappears. Deleting the CRD deletes all of its stored Custom Resources cluster-wide.

---

## Lab Questions

1. What exactly happens at the API Server when you `kubectl apply` a Custom Resource (CR) and the corresponding CRD does not exist yet?
2. What is the difference between a CRD and a CR? Use `CronTab` and `my-cron` as examples.
3. `kubectl get ct` works but `kubectl get crontabs` works too. What feature of the CRD makes both work?
4. The CRD schema in this lab does not define `spec.parallelism`. What happens if you apply a CR containing that field?
5. After you create a `CronTab` CR, no Pods appear. Why? What pattern would make them appear?
6. How does Cert-Manager prove the Operator Pattern? Name the CRDs it registers and what a `Certificate` or `Issuer` CR causes the operator to do.

---

## Expected Results

After completing this lab:

- You know how to define a CRD with an OpenAPI v3 schema and a custom API group
- You saw the API register the new kind, including the discoverable `crontabs` plural and `ct` short name
- You reproduced the exact `no matches for kind` error and the exact schema validation rejection
- You confirmed that a CR without a controller is just stored state in etcd
- You installed a real operator (Cert-Manager) and saw it register and reconcile its own CRDs

---

## Key Commands Reference

| Command | Purpose |
|---------|---------|
| `kubectl apply -f crontab-crd.yaml` | Register a new CRD with the API Server |
| `kubectl get crd` | List all Custom Resource Definitions |
| `kubectl api-resources \| grep crontabs` | Confirm the new API group and scope |
| `kubectl apply -f my-cron.yaml` | Create a Custom Resource instance |
| `kubectl get crontabs` / `kubectl get ct` | List CRs using the plural or short name |
| `kubectl explain crontab.spec` | Show the CRD's fields from the schema |
| `kubectl describe crd crontabs.stable.example.com` | Show the CRD's versions, scope, and schema |
| `kubectl get crd \| grep cert-manager` | List the CRDs registered by Cert-Manager |

---

## Next

- Return to the [Lesson 33 file](../docs/11-operators/lesson-33-extending-kubernetes-crds-and-operators.md) to review the concepts
- Apply `kubectl explain crontab` before and after the schema, and add `spec.schedule` with a `required` list to see validation in action
- Try the Mini Project from the lesson: install Cert-Manager, create a `Certificate` CR, and watch the operator create the TLS Secret
- Proceed to Lesson 34 to build an operator of your own with the Operator SDK