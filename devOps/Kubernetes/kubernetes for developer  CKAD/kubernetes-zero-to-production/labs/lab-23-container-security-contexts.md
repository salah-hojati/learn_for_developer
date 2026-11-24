---
title: Lab 23 - Container Security Contexts
lesson: 23
module: 07 Security
tags: [kubernetes, security, security-context, rbac, non-root, capabilities]
---

# Lab 23 - Container Security Contexts

## Objective

In this lab you will lock down containers using Security Contexts. You will deliberately deploy a Pod that the kubelet refuses to start because the image defaults to root under `runAsNonRoot: true`, then run a hardened non-root Pod with a read-only filesystem and restricted capabilities, drop all capabilities and re-add only `NET_BIND_SERVICE` so a non-root app can bind to port 80, and finally enforce the same restrictions at the namespace level with Pod Security Admission (PSA). The lab teaches you how Security Contexts are enforced, why they fail fast, and how to build images and manifests that honor them.

## Prerequisites

- A running kind cluster
- kubectl installed and configured
- Completion of Lessons 1 through 22, especially Lesson 22 (RBAC and Service Accounts)
- A registry puller: `docker` or `crictl` for inspecting image metadata

## Pre-Lab Checklist

- [ ] kind cluster running
- [ ] `kubectl get nodes` shows Ready status
- [ ] Cluster is new enough for PSA (kind ships an admission controller that supports it)
- [ ] The default namespace is free of other workloads
- [ ] `docker` or `nerdctl` available to inspect image defaults

---

## Step 1: Create a Pod That Fails With a Root Image Under runAsNonRoot

Create `broken-secure.yaml`:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: broken-secure
spec:
  securityContext:
    runAsNonRoot: true
  containers:
  - name: app
    image: redis:alpine
```

Note that the `redis:alpine` image does not declare a `USER` in its Dockerfile, so it defaults to root (UID 0). The `runAsNonRoot: true` check is a hard validation done by the kubelet before the container starts. Apply it:

```bash
kubectl apply -f broken-secure.yaml
```

## Step 2: Observe the CreateContainerConfigError

Wait a few seconds and inspect the Pod state:

```bash
kubectl get pod broken-secure
```

Expected output:

```text
NAME            READY   STATUS                  RESTARTS   AGE
broken-secure   0/1     CreateContainerError    0          15s
```

List all events for the exact message:

```bash
kubectl describe pod broken-secure
```

Expected Events output:

```text
Events:
  Type     Reason       Age   From     Message
  ----     ------       ----  ----     -------
  Normal   Scheduled    15s   default  Successfully assigned default/broken-secure to kind-worker
  Warning  FailedCreate 10s   kubelet  Error: container has runAsNonRoot and image will run as root
```

Why this happens: the kubelet acts as a bouncer. Before calling the container runtime it reads the image metadata for the default user. When the image defaults to UID 0 and the Pod demands `runAsNonRoot: true`, the kubelet refuses to start the container and reports `CreateContainerError`. The container never reaches the OCI runtime. Deleting and re-deploying with an explicit user or a fixed image is the fix:

```bash
kubectl delete pod broken-secure
```

## Step 3: Create a Hardened NonRoot Pod With Read-Only Filesystem

Create `secure-pod.yaml`:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: secure-app
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 1000
  containers:
  - name: app
    image: busybox:latest
    command: ["sleep", "3600"]
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      capabilities:
        drop:
        - ALL
```

The Pod-level `runAsUser: 1000` overrides the image's default root user, which satisfies the non-root check and lets the container start as a normal user. Apply and wait:

```bash
kubectl apply -f secure-pod.yaml
kubectl get pod secure-app -w
```

Expected output:

```text
NAME         READY   STATUS    RESTARTS   AGE
secure-app   1/1     Running   0          8s
```

## Step 4: Prove the Restrictions With kubectl exec

Enter the container:

```bash
kubectl exec -it secure-app -- sh
```

Verify the user identity:

```sh
id
```

Expected output:

```text
uid=1000 gid=1000 groups=1000
```

Try to write to the root filesystem:

```sh
touch /hacked.txt
```

Expected output (the read-only rootfs rejects the write):

```text
touch: /hacked.txt: Read-only file system
```

Try to bind a privileged port. Port 80 is guarded by the kernel capability `CAP_NET_BIND_SERVICE`, which was dropped along with everything else:

```sh
nc -l -p 80
```

Expected output:

```text
nc: bind: Permission denied
```

Exit the container:

```sh
exit
```

The read-only root filesystem is enforced at the OCI mount level, so every process in the container cannot create files outside mounted volumes. Any app that needs to write temporary data must be given an `emptyDir` (or other writable) volume mounted at the write location, otherwise it fails with `Permission denied` or signals at startup.

## Step 5: Create a Writable tmp Directory

Deploy a minimal app that needs to write temporary files under a read-only rootfs. Create `tmp-writer.yaml`:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: temp-writer
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 1000
  containers:
  - name: app
    image: busybox:latest
    command: ["sh", "-c", "sleep 3600"]
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      capabilities:
        drop:
        - ALL
    volumeMounts:
    - name: scratch
      mountPath: /tmp
  volumes:
  - name: scratch
    emptyDir: {}
```

Apply and confirm the temporary directory is writable:

```bash
kubectl apply -f tmp-writer.yaml
kubectl exec temp-writer -- touch /tmp/hello.txt
```

Expected output: no error, and the root filesystem stays read-only. The `emptyDir` volume provides the only writable location in a Pod that otherwise has its root filesystem mounted read-only.

## Step 6: Drop ALL Capabilities and Add NET_BIND_SERVICE for Port 80

Non-root processes cannot bind to privileged ports below 1024 because the kernel requires `CAP_NET_BIND_SERVICE`, which is normally only granted to root. There are two production options: run the app on 8080 behind an Ingress that translates 80 to 8080, or restore the single capability under a non-root user.

Deploy an Nginx that runs as UID 101 (the nginx user) on port 80 with only `NET_BIND_SERVICE` added back. Create `nginx-nonroot.yaml`:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-nonroot
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 101
  containers:
  - name: nginx
    image: nginx:1.25-alpine
    ports:
    - containerPort: 80
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      capabilities:
        drop:
        - ALL
        add:
        - NET_BIND_SERVICE
    volumeMounts:
    - name: nginx-tmp
      mountPath: /tmp
  volumes:
  - name: nginx-tmp
    emptyDir: {}
```

Apply and confirm the Pod starts:

```bash
kubectl apply -f nginx-nonroot.yaml
kubectl get pod nginx-nonroot
```

Expected output:

```text
NAME             READY   STATUS    RESTARTS   AGE
nginx-nonroot    1/1     Running   0          10s
```

Prove the capability was granted by asking the kernel directly:

```bash
kubectl exec nginx-nonroot -- sh -c 'grep CapEff /proc/self/status'
```

Expected output (0x400 = bit 10 = `CAP_NET_BIND_SERVICE`):

```text
CapEff: 0000000000000400
```

Without the explicit add, the same Nginx would fail to bind to port 80 and crash. The read-only rootfs also requires the `/tmp` emptyDir because Nginx writes its worker pid and temp files under runtime directories.

## Step 7: Enforce With Pod Security Admission

Pod Security Admission (PSA) is built into the kube-apiserver admission controller on a current kind cluster. Create a dedicated namespace and apply the `baseline` level first, then escalate to `restricted` to see a workload rejected:

```bash
kubectl create namespace psa-live
kubectl label namespace psa-live pod-security.kubernetes.io/enforce=baseline
kubectl label namespace psa-live pod-security.kubernetes.io/audit=restricted
kubectl label namespace psa-live pod-security.kubernetes.io/warn=restricted
```

A normal privileged-looking Deployment that does not set `runAsNonRoot` should still pass baseline evaluation. Now harden the enforcement level to `restricted`:

```bash
kubectl label --overwrite namespace psa-live pod-security.kubernetes.io/enforce=restricted
kubectl run pry -n psa-live --image=busybox --restart=Never --command -- sleep 3600
```

Expected: the namespace's admission controller rejects the Pod because it runs as root and lacks the required capabilities, with a warning such as:

```text
Error from server (Forbidden): pods "pry" is forbidden: violates PodSecurity "restricted:latest": running as root is not allowed (no container is set to runAsNonRoot)
```

The `secure-pod.yaml` manifest from Step 3 already satisfies the restricted requirements: `runAsNonRoot: true`, `runAsUser: 1000`, `allowPrivilegeEscalation: false`, and dropped capabilities. Deploy it into the `psa-live` namespace:

```bash
kubectl -n psa-live apply -f secure-pod.yaml
```

Expected output (the Pod passes the restricted admission check and is created):

```text
pod/secure-app created
```

The two layers complement each other. PSA evaluates the Pod at admission time in the API server and rejects anything violating the namespace level before it is stored. The Security Context is applied later, at runtime, by the container runtime via the CRI. Together they make it impossible for developers to opt out of the cluster baseline.

## Cleanup

Remove the Pods created in this lab and the PSA test namespace:

```bash
kubectl delete pod broken-secure secure-app tmp-writer nginx-nonroot --ignore-not-found
kubectl delete ns psa-live
kubectl get pods
```

If you also applied the reference Deployment from `manifests/07-security/security-context.yaml` (which uses `runAsUser: 10001`, `fsGroup: 2000`, seccomp RuntimeDefault, a read-only rootfs, and dropped capabilities with `NET_BIND_SERVICE` added back), delete it as well:

```bash
kubectl delete deploy hardened --ignore-not-found
```

---

## Lab Questions

1. A Pod has `runAsNonRoot: true` without a `runAsUser`, and the image does not declare a user. What Pod status and error message do you expect, and what two ways can you fix it?
2. What does `readOnlyRootFilesystem: true` actually mount as read-only, and what error do apps that write temp files hit? What volume solves it?
3. Why can a non-root process not bind to port 80, and what capability makes it possible?
4. What is the difference between the Pod-level `securityContext` and the container-level `securityContext` in a multi-container Pod?
5. How does a namespace-level PSA label (`pod-security.kubernetes.io/enforce=restricted`) relate to the per-Pod `runAsNonRoot` check, and why should you use both layers?

---

## Expected Results

After completing this lab:

- You can deploy a Pod that uses a root image under `runAsNonRoot: true` and read the `CreateContainerConfigError` from the describe events
- You know why the kubelet refuses to start the container and what `runAsUser` does to bypass it
- You run a non-root, read-only, capability-stripped container and prove it with `kubectl exec` and `id`, plus failed file and port writes
- You can grant exactly one capability (`NET_BIND_SERVICE`) so a non-root app binds to port 80
- You can enforce the same policy at the namespace level with PSA and observe a Pod rejected for running as root

---

## Key Commands Reference

| Command | Purpose |
|---------|---------|
| `kubectl get pod <name>` | Check Pod status (look for `CreateContainerError`) |
| `kubectl describe pod <name>` | Read the kubelet Events message `container has runAsNonRoot and image will run as root` |
| `kubectl exec -it <pod> -- sh` | Enter the container and test writes and binds |
| `kubectl exec <pod> -- id` | Confirm the effective user inside the container |
| `kubectl exec <pod> -- grep CapEff /proc/self/status` | Decode the effective capability set |
| `kubectl label ns <name> pod-security.kubernetes.io/enforce=restricted` | Enforce PSA restricted on a namespace |

---

## Next

- Review the corresponding [Lesson 23 file](../docs/07-security/lesson-23-locking-down-the-container-security-contexts.md) for the field-by-field YAML explanation
- Read the [hardened reference manifest](../manifests/07-security/security-context.yaml) and adapt it for your own images
- Continue to the next lesson on image security, software supply chain, and PSA/Kyverno enforcement