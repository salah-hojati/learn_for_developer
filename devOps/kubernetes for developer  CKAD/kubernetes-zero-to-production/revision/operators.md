---
title: Revision - Operators
module: 11 Operators
status: Complete
tags: [revision, operators, crd, custom-resource, operator-pattern, controller, reconciliation]
---

# Revision 11 - Operators

## 1. The Mental Model

Kubernetes ships knowing native kinds: Pod, Service, Deployment, StatefulSet. For anything beyond those, two questions arise:

1. **Can the API Server hold my object?** That is a CRD (CustomResourceDefinition). It teaches the API Server a brand new kind and a schema to validate it against.
2. **Who takes action when that object changes?** That is a controller, and when that controller encodes deep application-specific knowledge it is called an Operator.

Three nested ideas to remember as a chain:

- **CRD** = the dictionary definition. It introduces a new noun the API knows, such as `CronTab`.
- **CR** = a sentence that uses the word; one instance such as "my CronTab runs at 5 AM", stored in etcd.
- **Operator** = the worker who reads every new sentence and builds the Pods, PVCs, and Services that make it true.

So an Operator is really `CRD + controller + domain knowledge`. The controller part is not a brand-new mechanism: it is the same informer/workqueue/reconcile loop the built-in Deployment controller uses, driven by the `client-go` library that watches the API Server. The Operator is simply a running Pod that watches a custom kind and reacts.

That is why Kubernetes is often described as a platform for platforms. Rather than special-casing every database or message queue inside the core, Kubernetes exposes one generic extension mechanism (CRDs + controllers) and lets each application plug in its own domain knowledge on top, using the same admission, validation, storage, and reconciliation lifecycle as built-in objects. The core stays small and stable while the ecosystem grows without bound.

## 2. Core Concepts

### CRD vs CR

| Term | Meaning | Who creates it |
|------|---------|----------------|
| CRD | Schema or blueprint. Tells the API Server what fields a kind may have. Registered under `apiextensions.k8s.io/v1`. | Admin, one per kind, cluster-scoped |
| CR | An instance conforming to the schema, stored in etcd like built-in objects. | Developer, one per thing to run |

Create a CR before its CRD and the API Server rejects it: `error: unable to recognize ... no matches for kind "CronTab"`. The API Server is strictly typed and validates every object against its registered schema.

### CRD structure: group / version / kind

The API identity is the triple `(group, version, kind)`, carried in each object's `apiVersion`:

- **group** - the collection namespace for your types, for example `stable.example.com`. It must be a DNS subdomain you control.
- **version** - maturity of the API: `v1alpha1`, `v1beta1`, `v1`. A CRD can serve several versions (multiple `served: true`) but only **one** is `storage: true`.
- **kind** - the uppercase type name, for example `CronTab`. The plural (lowercase, used in `kubectl get url`) is `crontabs`.
- **shortNames** - quick aliases, for example `ct`.
- **metadata.name** MUST equal `<plural>.<group>`, for example `crontabs.stable.example.com`.
- **scope** - `Namespaced` or `Cluster`.
- **schema.openAPIV3Schema** - OpenAPI v3 schema validating every CR; the API Server rejects CRs that do not conform.

### The controller / reconcile loop

A controller is a loop: **watch -> diff -> act**. It uses a SharedInformer (a watch) to notice changes, enqueues the affected key, and a reconcile function compares the Desired State (the CR spec) against the Live State (actual Deployments, StatefulSets, PVCs) and issues create/update/delete calls to close the gap. If a step fails, the key is requeued and retried with backoff. The loop is idempotent: re-running it after a partial action is safe because it always recomputes the diff from scratch.

### The Operator pattern

Operator = a CRD that models the application (for example `Redis` with `spec.redis.replicas`) plus a controller that encodes the application's day-2 operational knowledge (bootstrap, failover, backup, upgrade). The Operator owns the CR, while the standard controllers (Deployment or StatefulSet controllers) own the Pods the Operator creates. The Operator knows the domain; the core controllers know the mechanics.

### Status subresource

`spec` is the desired state written by the user. `status` is the observed state written by the controller. Declaring the `status` subresource on the CRD splits the two: only the controller may update `status`, which prevents user/controller write conflicts and gives the user a read-only window into progress and errors via `kubectl describe`.

### Finalizers

Finalizers are an object references names in `metadata.finalizers`. They delay deletion: while a finalizer exists, a deleted CR flips to `Terminating` but is not removed from etcd. The controller then performs cleanup (cloud DNS records, S3 buckets, PVCs) before removing the finalizer, which releases the object. A finalizer makes CR deletion a controlled, ordered process instead of instant erasure.

### Watching vs reacting

Watching means the informer receives events. Reacting means the controller does something about them. The reconcile loop is event-driven: an informer event triggers a reconcile, but the controller also re-runs reconciliation itself (or when it owns objects that change), which decouples "I was told" from "I am caught up". A healthy Operator therefore detects state drift even without a direct event, and the `status` field reports progress and errors back to the user.

### Operator SDK / Helm-based operators

The Operator SDK scaffolds a controller in Go, Ansible, or Helm:

- **Go** - full control using `controller-runtime`; preferred for complex stateful logic.
- **Ansible / Helm** - translate a CR into roles/templates; you write YAML instead of Go, with simpler day-2 operations.

All three run the same informer/reconcile loop; the difference is language and effort, not the pattern.

### etcd-operator patterns

Stateful Operators (etcd, Redis, PostgreSQL) follow a standard shape. Where a Deployment only scaled Pods, an Operator adds domain operations:

- bootstrap and replication topology,
- failover (promote a replica when a node dies),
- rolling upgrades and schema migrations,
- scheduled backups.

Day-1 provisioning (create the StatefulSets and PVCs) is the easy part; Day-2 operations (keep healthy, upgrade, recover) are where the operator earns its keep.

### Performance: CRs live in etcd

CRs are stored in etcd exactly like built-in objects. Heavy or high-cardinality CRs (thousands of instances, huge specs, aggressive status updates) bloat etcd and slow the API Server, because every watch and reconciliation traverses the stored objects. Keep CR specifications small, throttle how often the controller updates `status`, and never treat a CR as a general-purpose datastore for large payloads.

### Real-world operators

Do not write an operator from scratch for a well-trodden workload; reuse battle-tested projects:

| Operator | Manages | Example CR |
|----------|---------|------------|
| CrunchyData PGO | PostgreSQL | `PostgresCluster` |
| Strimzi | Apache Kafka | `Kafka`, `KafkaTopic` |
| Prometheus Operator | Prometheus / Alertmanager | `Prometheus`, `ServiceMonitor` |
| cert-manager | TLS certificates | `Certificate`, `Issuer` |

cert-manager is the cleanest live example of the whole pattern in action: install it and you can `kubectl get crd | grep cert-manager`, then create a `Certificate` CR that references an `Issuer`. The cert-manager controller watches the `Certificate`, issues the certificate, writes the TLS Secret, and renews it automatically before expiry. One small CR replaces a manual `openssl` and cron renewal strategy.

### Control plane vs data plane

For a stateful application, the Operator acts as the application's control plane and the StatefulSets and Pods it creates are the application's data plane. The Operator decides topology, failover, and upgrades (control plane); the actual workload serves traffic (data plane). This is why deleting or damaging the Operator does not immediately kill the app, but it does freeze lifecycle operations.

## 3. Key Commands

```bash
kubectl get crd                                  # list all CRDs
kubectl get crontabs                             # list CR instances (plural name)
kubectl get ct                                   # same, using shortName 'ct'
kubectl describe crd crontabs.stable.example.com # schema, versions, served/storage
kubectl api-resources | grep stable              # verify served group/version
kubectl apply -f crd.yaml                        # register the CRD
kubectl apply -f my-cron.yaml                    # create a CR
kubectl get crontab my-cron -yaml                # view spec + stored metadata
kubectl describe crontab my-cron                 # read controller-reported status
kubectl delete crontab my-cron                   # delete (honored by finalizers)
kubectl logs -n <ns> <operator-pod>               # diagnose an operator failure
# Redis Operator install (OT Container Kit)
helm repo add ot-helm https://ot-container-kit.github.io/helm-charts/
helm repo update
helm install redis-operator ot-helm/redis-operator --namespace redis-operator-system --create-namespace
# Manage the managed workload through its CR, not raw resources
kubectl get redis my-redis -o yaml
kubectl patch redis my-redis --type='json' -p='[{"op": "replace", "path": "/spec/redis/replicas", "value": 1}]'
kubectl delete redis my-redis                     # tells the operator to tear everything down
```

## 4. YAML Patterns (a CRD and a sample CR) with explanation

### The CRD (the schema)

```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: crontabs.stable.example.com   # must be <plural>.<group>
spec:
  group: stable.example.com
  scope: Namespaced
  names:
    plural: crontabs
    singular: crontab
    kind: CronTab
    shortNames: [ct]
  versions:
  - name: v1
    served: true
    storage: true
    subresources:
      status: {}                     # splits spec (user) from status (controller)
    schema:
      openAPIV3Schema:
        type: object
        properties:
          spec:
            type: object
            properties:
              cronSpec: { type: string }
```

- `metadata.name` equals `plural.group`; this identity is mandatory.
- `scope: Namespaced` means instances live in a namespace; `Cluster` means one object per cluster.
- `served` vs `storage`: you serve many versions but store exactly one; later breaking versions require a conversion webhook.
- `subresources.status: {}` gives the controller its own write path for `status`.
- `schema.openAPIV3Schema` validates every CR; in production add `additionalProperties: false` so spec typos are rejected.

### The Custom Resource (the instance)

```yaml
apiVersion: stable.example.com/v1   # must exactly match the CRD group + served version
kind: CronTab
metadata:
  name: my-cron
spec:
  cronSpec: "* * * * */5"
```

`apiVersion` and `kind` bind the CR to the CRD, and `spec` is validated against `openAPIV3Schema`. In a real system the Operator watches this object and then builds the Pods, Services, and PVCs it needs.

A production operator CR looks the same: the CR is the application intent, and the operator maps it onto real resources. The Redis Operator (OT Container Kit, installed via the `ot-helm` repo) is a complete example:

```yaml
apiVersion: redis.opstreelabs.in/v1beta2
kind: Redis
metadata:
  name: my-redis
spec:
  redis:
    replicas: 3
    image: redis:7.0-alpine
    resources:
      requests:
        cpu: 100m
        memory: 128Mi
      limits:
        cpu: 200m
        memory: 256Mi
  kubernetesConfig:
    imagePullPolicy: IfNotPresent
    updateStrategy:
      type: RollingUpdate
```

- `apiVersion: redis.opstreelabs.in/v1beta2` is the group/version the operator registered in its CRD; this ties the CR to that Redis operator and no other.
- `spec.redis` is the application block: `replicas` (how many Redis nodes), `image` (which Redis version), and `resources` (CPU/memory requests and limits applied to the resulting Pods).
- `kubernetesConfig` is the operator-side policy: `imagePullPolicy` (for example `IfNotPresent`) and `updateStrategy` (for example rolling updates) control how the operator manages the workload.
- To scale, edit the CR (for example `kubectl patch redis my-redis` changing `spec.redis.replicas`); the operator reconciles the StatefulSet. If the operator Pod is down while you patch, the API accepts the change but nothing moves until the operator returns.

## 5. How It Fits Together (CR created -> API -> controller reconcile -> status)

```text
[ Admin ]  applies a CRD
        -> apiextensions-apiserver validates -> API Server registers
            REST endpoint /apis/stable.example.com/v1/crontabs
[ Dev ]    applies a CR (kind: CronTab, spec.replicas: 3)
        -> validated vs openAPIV3Schema -> stored in etcd (Desired State)
                                                            |
   Operator Pod (SharedInformer watch)  <----------- sees new CR
        v
   Reconcile loop: compare actual (0 StatefulSets) vs desired (3 replicas)
        -> POST StatefulSet, Service, PVC (constructs real resources)
        -> the standard StatefulSet/Deployment controllers create the Pods
        -> Operator writes progress/errors to status subresource
        v
[ user ]  kubectl describe crontab my-cron <---- reads observed status
```

Flow summary: the CR is the wish (Desired State), the Operator is the worker (Reconcile Loop) that turns the wish into real StatefulSets and PVCs, and the `status` subresource reports the result. Deleting the CR triggers the Operator, respecting any finalizer, to clean up everything it owns.

## 6. Common Mistakes and Gotchas

| Mistake | Gotcha | Fix |
|---------|--------|-----|
| Apply a CR before the CRD | `no matches for kind` - the API has no schema | Install the CRD, wait for registration, then create the CR |
| Spec typos silently ignored | OpenAPI only validates declared properties | Set `additionalProperties: false` |
| No `status` subresource | Any client can write `status` and fight the controller | Declare `subresources.status` |
| Missing finalizers | Deleting a CR hard-erases resources; cloud resources leak | Register finalizers; exercise deletion before real data |
| Treating a CR as cosmetic | Deleting the CR tells the operator to destroy backing resources and PVCs | Document deletion semantics; add finalizers and backups |
| Operator for a stateless web app | One Deployment + Helm chart | Reserve operators for day-2 stateful workloads |
| Operator RBAC too broad | A compromised operator is a sensitive new class ClusterRole | Least-privilege namespaced Role and dedicated ServiceAccount |
| Operator Pod crashes unnoticed | Workload keeps running but scaling/backup/failover freeze | Monitor operator Pods and CR status as first-class |
| Operator Pod down while you patch the CR | API accepts the change but the workload stays frozen until the operator returns | Restore/scale the operator first, then verify the reconcile |
| Operator never requeue | Event fires but nothing converges; status is stale | Reconcile should return requeue; also watch the resources it generates |

## 7. Quick Troubleshooting

**Is the app down because the operator Pod is down?**

No. The operator is the control plane for the app; the workload is the data plane. When the operator Pod dies the workload keeps serving, but lifecycle operations (scaling, backup, failover, upgrades) freeze because no one reconciles the CR anymore. Scale the operator back up; the next reconcile picks up any pending CR changes.

**Symptom: `no matches for kind ...` when applying a CR.**

The CRD is missing, or the CR's `apiVersion` mismatched group/version.

```bash
kubectl get crd | grep -i cront
kubectl api-resources | grep <group>
```

**Symptom: a CR hangs in `Terminating`.**

A finalizer is blocking deletion because the Operator is down. Restore the Operator (scale it up) so it can run cleanup, then the finalizer will be removed. Do not force-remove the finalizer while external resources still exist.

**Symptom: the Operator logs `Reconciler error` and no resources are created.**

The Operator Pod lacks RBAC to create StatefulSets/PVCs, or it is crashing.

```bash
kubectl get pods -n <ns>
kubectl logs -n <ns> <operator-pod>    # permission denied / reconcile error
kubectl describe <crontab> <name>       # controller-reported status and errors
```

**Symptom: the CR `status` never updates.**

The `status` subresource is not declared on the CRD, or the Operator never writes it. Inspect `kubectl get crd -o yaml` for `subresources.status`.

## 8. 30-Second Recap

- CRD = dictionary definition (schema + scope) teaching the API Server a new kind; CR = an instance stored in etcd.
- Operator = `CRD + controller + knowledge`; it watches a kind and runs the same informer/workqueue/reconcile loop used by built-in controllers.
- The reconcile loop compares Desired (CR spec) with actual (Pods/Services) and always converges, retrying with backoff.
- Kubernetes is a platform for platforms; real-world operators: CrunchyData PGO, Strimzi, Prometheus Operator, cert-manager.
- CRs live in etcd; heavy or high-cardinality CRs bloat etcd and slow the API Server.
- The Operator is the app's control plane; the workload it creates is the data plane. If the operator dies, the app keeps serving but lifecycle work freezes.
- `spec` is the user's wish; `status` is observed reality, split by the `status` subresource.
- With a Redis-type operator you operate the CR (`kubectl patch redis`) and the operator reconciles the underlying StatefulSet; a dead operator ignores CR changes.
- Finalizers make CR deletion controlled: an object stays `Terminating` until cleanup finishes.
- Operator is used because Helm renders once; an Operator continuously drives day-2 operations: scaling, failover, backup, upgrade.

## Related Lessons

- [Lesson 33 - Extending Kubernetes (CRDs and Operators)](../docs/11-operators/lesson-33-extending-kubernetes-crds-and-operators.md) - CRDs, CRs, and the Operator pattern, including creating a CR before the CRD.
- [Lesson 34 - Operators in Practice (Managing Stateful Apps)](../docs/11-operators/lesson-34-operators-in-practice.md) - the reconciliation loop in action, the Redis Operator, and failure recovery.

## Related Material

- [Interview - Operators](../interview/gitops.md)

[Back to Revision Index](README.md)