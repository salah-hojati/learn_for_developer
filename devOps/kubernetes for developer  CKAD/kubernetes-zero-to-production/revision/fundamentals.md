---
title: Revision - Fundamentals
module: 01 Fundamentals
status: Complete
tags: [revision, fundamentals, containers, namespaces, cgroups, kubectl]
---

# Revision - Fundamentals

## 1. The Mental Model

Kubernetes is a platform for running and orchestrating containers at scale. A container is not a virtual machine: it is a standard Linux process whose visibility is constrained by **namespaces** (what it can *see*) and whose resource usage is constrained by **cgroups** (what it can *use*). You describe the cluster's **desired state** in declarative YAML, and Kubernetes **controllers** continuously reconcile reality toward that state. `kubectl` is only a client that talks to the API Server, and **kubeconfig** tells kubectl which cluster, user, and namespace to contact. Everything in this module hangs together: containers are the workload, Pods are their smallest packaging unit, Kubernetes Namespaces organize them, cgroups protect the host, and kubectl plus kubeconfig are your interface to all of it.

## 2. Core Concepts

### Containers and Images

An **image** is a read-only template that contains your application code plus every dependency it needs: libraries, runtimes, and configuration. Images are built in **layers**; every instruction in the Dockerfile adds a layer, and layers are cached and shared across images, which is why pulling a new image is fast when you already have most of its layers. Images are stored in a **registry** (Docker Hub, quay.io, GHCR, ECR) and pulled by the runtime. A **container** is a running instance of an image: the runtime extracts the image filesystem and starts the process inside fresh namespaces and cgroups.

Kubernetes does not create containers itself. The **kubelet** on each worker node calls a container runtime through the **CRI** (Container Runtime Interface), a standard API that any runtime (containerd, CRI-O) can implement. This abstraction is why you can swap runtimes without rewriting any manifest.

### Containers vs Virtual Machines

| Dimension | Virtual Machine | Container |
|-----------|-----------------|-----------|
| OS kernel | Own full guest kernel | Shares the host kernel |
| Isolation | Hardware-level (hypervisor) | OS-level (namespaces and cgroups) |
| Size | Gigabytes (GB) | Megabytes (MB) |
| Boot time | Minutes | Seconds |
| Resource overhead | High (runs a full OS) | Low (shares the kernel) |
| Security boundary | Stronger (hardware isolation) | Weaker (kernel is shared) |

The shared kernel is why containers are small and fast, and also why a host kernel panic kills every container on the host while a VM survives.

### Container Lifecycle

A container is not always running; it moves through a defined set of states:

| State | Description |
|-------|-------------|
| Created | The runtime has registered the container, but the application process has not started yet. |
| Running | The application process is executing inside the container. |
| Paused | The process is frozen by the runtime; all threads are suspended. |
| Stopped | The process has exited or been killed, deliberately or by the OOM killer. |
| Deleted | The container filesystem and metadata are removed; all traces are gone. |

A container stuck in `Created` is a debug signal: the runtime registered the container but could not start the process, so check the runtime and node-level logs instead of the app. A container that keeps cycling between `Running` and `Stopped` with high exit codes usually points to a bug or a missing dependency.

### Linux Namespaces (isolation: the blindfold)

| Namespace | Isolates |
|-----------|----------|
| PID | Process IDs: container sees only its own process tree, with itself as PID 1 | A process inside a container believes it is alone on the machine.

| Namespace | Isolates |
|-----------|----------|
| PID | Process IDs: container sees only its own process tree, with itself as PID 1 |
| NET | Network stack: its own IP address, interfaces, and routing table |
| MNT | Mount points: its own filesystem view |
| UTS | Hostname and domain: it can set its own |
| IPC | Inter-process communication: own message queues and shared memory |
| USER | User and group IDs: own UID mapping |

Why this matters: without namespaces, two apps on one host would collide on ports, PIDs, and hostnames. With them, each app runs as if it owned the machine.

### cgroups (limitation: the leash)

**cgroups** (Control Groups) account for, limit, and isolate how much CPU, memory, and I/O a collection of processes can consume. In cgroup **v1** each resource has its own hierarchy (for example `/sys/fs/cgroup/memory/memory.limit_in_bytes`); in **v2** there is a single unified hierarchy under `/sys/fs/cgroup` with files such as `cpu.max` and `memory.max`. CPU limits are enforced by the **CFS** (Completely Fair Scheduler) through throttling. Memory limits are enforced by the memory cgroup: if a process exceeds its limit, the kernel's **OOM Killer** terminates it and the container exits with code 137 (`OOMKilled`). Why this matters: cgroups are the "noisy neighbor" defense that stops one leaked app from crashing the whole node, and the accounting they provide is what lets Kubernetes schedule workloads by **requests**.

You can see the same mechanism in plain Docker: the runtime flags `--memory="512m"` and `--cpus="1"` each create a cgroup behind the scenes (a memory cgroup limiting RAM to 512 MB, and a CPU cgroup capping the process to one core), so `kubectl`'s `resources` block is just the Kubernetes-native way of expressing those same cgroup limits.

### Container Hardening

A container is **not** a hard security boundary. It is only OS-level isolation: namespaces plus cgroups. A **breakout** escapes that isolation and usually results in host access, so treat containers as internally isolated but externally hostile by hardening them with defense in depth:

- **Run as a dedicated non-root user.** Base images often default to root; running root means an escape is root on the host. Define a non-root user in the Dockerfile and enforce it in Pod specs with `securityContext.runAsNonRoot` and `runAsUser`.
- **Use a read-only root filesystem** where the app writes nothing: `securityContext.readOnlyRootFilesystem` blocks writes to the rootfs and reduces the attack surface.
- **Drop/minimize Linux capabilities.** A container process starts with a broad set of Linux capabilities. Drop `ALL` and re-add only what the app actually needs.
- **Never bake secrets into images.** Image layers are immutable and can be inspected; inject credentials at runtime via Kubernetes **Secrets** (Module 06).
- **Prefer minimal base images and multi-stage builds** to shrink the image surface (fewer packages to exploit).
- **Use `hostPID: true` deliberately.** Sharing the host PID namespace lets a container see host processes; it is useful for debug or node agents but weakens isolation and should not be default.

### Pods

The **Pod** is the smallest deployable unit in Kubernetes. A Pod wraps one or more containers. Containers inside the same Pod share the same network namespace (one IP address), can talk to each other on `localhost`, and share volumes. The Pod, not the container, is the scheduling unit: the kubelet is told to run a Pod, and the Pod is what the scheduler places on a node. Pods are ephemeral by design; anything that must survive a restart belongs in a volume.

### Declarative vs Imperative and Controllers

The **imperative** model says "do this now" (`kubectl run nginx --image=nginx`). The **declarative** model says "this is the state I want" (`kubectl apply -f pod.yaml`). Kubernetes is built around the declarative model: the **desired state** is stored in etcd, and **controllers** (in the controller manager) run reconciliation loops that watch the API Server, compare actual state to desired state, and take actions to close the gap. A Deployment controller sees 2 replicas desired but 1 running and creates another ReplicaSet-owned Pod. You declare, Kubernetes converges.

### kubectl and kubeconfig

`kubectl` is the command-line client. Every kubectl command is an HTTP request (REST, JSON) sent to the **API Server**, which authenticates, authorizes, validates (including admission controllers), and stores state in etcd. **kubeconfig** is the file that tells kubectl where and as whom to talk. By default it lives at `~/.kube/config`. It contains:

- **clusters**: name, API Server URL, and the CA certificate to trust.
- **users**: name and credentials (client certificate, token, or username/password).
- **contexts**: a named tuple of `(cluster, user, namespace)`.
- **current-context**: which context is used when you do not specify one.

kubectl selects config by precedence: `--kubeconfig` flag, then the `KUBECONFIG` environment variable (which can name several files that get merged), then `~/.kube/config`. Switching namespaces is as simple as `kubectl config set-context --current --namespace=<ns>`.

### Kubernetes Namespaces (logical grouping)

Do not confuse kernel namespaces with Kubernetes **Namespaces**. A Kubernetes Namespace is a logical partition of the cluster: a scope for names (the same Pod name can exist in `dev` and `prod`) and a boundary for RBAC and quotas. Namespaced resources: Pods, Services, Deployments. **Cluster-scoped** resources (not namespaced): Nodes, PersistentVolumes, StorageClasses. Four namespaces always exist: `default`, `kube-system`, `kube-public`, `kube-node-lease`. Namespaces do **not** provide network isolation (use NetworkPolicy) and deleting a Namespace deletes everything inside it.

#### ResourceQuota and LimitRange

A **ResourceQuota** caps the *total* CPU/memory, requests, and limits in a namespace, enforced synchronously by the API Server admission phase (exceeding it returns 403 Forbidden before the object is stored). Its `spec.hard` keys constrain namespace-wide sums: `requests.cpu`, `requests.memory`, `limits.cpu`, and `limits.memory`. Two facts are commonly confused:

- A quota caps namespace totals, not individual usage. A single Pod's ceiling is governed by its own `resources.limits`, so a Pod can use more than the namespace's total requests as long as the sum of all Pods stays within the quota.
- Lowering a quota below current usage does **not** kill existing Pods; it only rejects **new** objects until usage drops back under the limit.

The full admission flow: the ResourceQuota admission controller rejects the object, so it is **never saved to etcd**, and neither the Scheduler nor the kubelet ever see it. That is why a rejected Pod fails instantly with `exceeded quota` instead of staying `Pending`.

A **LimitRange** sets per-Pod constraints. Its `default` and `defaultRequest` fields automatically inject default requests/limits into any Pod created without `resources`, which is important because a quota-active namespace requires every Pod to declare requests.

#### Namespace as the RBAC boundary

Namespaces are the primary boundary for RBAC role binding: a User/ServiceAccount is granted a Role (scoped to one namespace) via a RoleBinding, so a developer holding the `dev` Role can only create or read resources inside `dev`. A ClusterRoleBinding is what you need for cluster-scoped access. This is also why you should protect Namespaces from deletion via RBAC: never grant `delete` on the Namespace resource to teams that only need their own workspace.

All of these compose into the standard multi-tenancy pattern: **one large cluster, namespace-per-customer**, each backed by a ResourceQuota (budget) and strict RBAC (boundary), which is far cheaper than one cluster per customer. Two scaling caveats: Namespaces are just string prefixes in etcd, so 5,000+ of them can slow down API Server list operations; and if a customer requires strict physical isolation, use a separate cluster.

## 3. Key Commands

```bash
# Cluster and API
kubectl cluster-info
kubectl get nodes
kubectl api-resources                 # list resource types
kubectl explain pod.spec              # built-in docs

# Pods
kubectl get pods                      # in current namespace
kubectl get pods -A                   # all namespaces
kubectl get pods -o wide              # extra columns: IP, node
kubectl describe pod <name>           # details + events
kubectl logs <pod>                    # logs
kubectl logs <pod> --previous         # logs of previous crashed instance
kubectl exec -it <pod> -- /bin/sh     # shell into a pod
kubectl run nginx --image=nginx --dry-run=client -o yaml   # generate YAML
kubectl delete pod <name> --grace-period=0 --force

# Declarative CRUD
kubectl apply -f file.yaml
kubectl delete -f file.yaml
kubectl apply -f file.yaml --dry-run=client
kubectl diff -f file.yaml

# Kubernetes Namespaces
kubectl get ns
kubectl create namespace <name>
kubectl delete namespace <name>       # deletes everything inside
kubectl get all -n <ns>
kubectl describe quota -n <ns>        # usage vs hard limits

# kubeconfig, contexts, and current namespace
kubectl config view
kubectl config get-contexts
kubectl config current-context
kubectl config use-context <name>
kubectl config set-context --current --namespace=<ns>
kubectl config set-context <name> --namespace=<ns> --cluster=<c> --user=<u>

# Linux-level inspection (containers)
cat /proc/self/cgroup
cat /sys/fs/cgroup/memory/memory.limit_in_bytes 2>/dev/null   # cgroup v1
docker run -it --memory="512m" --cpus="1" ubuntu:22.04 /bin/bash    # each flag creates its own cgroup
ps aux                                   # inside the container: app is PID 1
ip link                                  # inspect the container's network namespace
exit
docker ps -a
docker inspect <id> | grep -i oom

# Container runtime state (diagnoses OOMKilled, crash loop, stuck in Created)
kubectl get pod <name> -o jsonpath='{.status.containerStatuses[0].state}'
```

Setup commands (Docker and kind):

```bash
# Ubuntu/Debian
sudo apt-get update && sudo apt-get install -y docker.io
sudo usermod -aG docker $USER            # log out and back in for group change
docker --version

# kind (Kubernetes IN Docker) for local cluster
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-amd64
chmod +x ./kind && sudo mv ./kind /usr/local/bin/kind
kind create cluster --name learning
kubectl cluster-info --context kind-learning
```

## 4. YAML Patterns

A minimal Pod:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: resource-demo
  namespace: default
spec:
  containers:
    - name: app
      image: nginx:1.25.2-alpine
      resources:
        requests:
          cpu: "0.5"
          memory: "256Mi"
        limits:
          cpu: "1"
          memory: "512Mi"
```

Field meanings: `apiVersion: v1` selects the core API group for Pods and Namespaces; `kind: Pod` tells the API Server what object this is; `metadata.name` is the unique name within the namespace; `metadata.namespace` selects which Kubernetes Namespace the object lands in (when omitted it uses the current context's namespace). `spec.containers` is a list, because a Pod may hold several containers that share a network namespace and IP. `image` is the exact registry image reference; pin the tag, never `latest`. The `resources` block is where namespaces and cgroups meet Kubernetes: `requests` map to cgroup shares (`cpu.shares`) and are a scheduling *guarantee* (the scheduler only places the Pod where the sum of requests fits), while `limits` are hard ceilings enforced by CFS for CPU (throttling) and by the memory cgroup for memory (breach triggers the OOM Killer and exit code 137). Rule of thumb: requests protect you from noisy neighbors; limits protect your neighbors from you.

A Namespace:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: team-alpha
```

Field meanings: `kind: Namespace` is cluster-scoped, so there is no namespace field. `metadata.name` is the Namespace's name, which becomes the `metadata.namespace` value that every namespaced object inside it uses. Create it with `kubectl create namespace team-alpha` or `kubectl apply -f namespace.yaml`. Pair it with a ResourceQuota and, when helpful, a LimitRange to control what can be created inside it.

A ResourceQuota:

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: compute-quota
  namespace: team-alpha
spec:
  hard:
    requests.cpu: "1"
    requests.memory: 1Gi
    limits.cpu: "2"
    limits.memory: 2Gi
```

Field meanings: `spec.hard` is the total namespace budget. `requests.cpu` caps the sum of all Pod CPU requests in the namespace at 1 core; `requests.memory`, `limits.cpu`, and `limits.memory` do the same for memory requests, CPU limits, and memory limits respectively. `metadata.namespace` scopes the quota to `team-alpha`. When a Pod would push the namespace over these sums, the API Server's admission phase rejects it with 403 Forbidden. Diagnose exhaustion with `kubectl describe quota -n team-alpha` and compare the `Used` and `Hard` columns; a Pod stuck `Pending` with no node resources is a different problem.

A LimitRange:

```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: defaults
  namespace: team-alpha
spec:
  limits:
    - type: Container
      default:
        cpu: "500m"
        memory: 512Mi
      defaultRequest:
        cpu: "100m"
        memory: 128Mi
```

Field meanings: `default` is auto-injected as the `limits` and `defaultRequest` as the `requests` into any Pod/Container created without `resources`. This matters because a quota-active namespace requires every Pod to declare requests: the LimitRange supplies them automatically so developers do not get `exceeded quota` errors for forgetting the block.

## 5. How It Fits Together

From Docker image to running Pod:

1. You build an image with a Dockerfile (layers of code plus dependencies) and push it to a registry.
2. You write a Pod manifest describing the desired image, resources, and namespace, and run `kubectl apply -f pod.yaml`.
3. The API Server validates the manifest, writes it to etcd, and (if a quota applies) checks admission, rejecting with 403 if the Namespace's ResourceQuota would be exceeded.
4. The Scheduler sees an unscheduled Pod and assigns it to a worker node with enough capacity for its `requests`.
5. The kubelet on that node receives the Pod assignment and calls the runtime via the CRI.
6. The runtime pulls the image and makes kernel calls (`clone()`/`unshare()`) that create the container's namespaces and cgroups.
7. The process starts inside its isolated, limited bubble; the kubelet reports status back to the API Server.

How kubectl talks to the cluster:

1. kubectl loads kubeconfig, picks the `current-context`, and resolves its `(cluster, user, namespace)` tuple.
2. kubectl sends an HTTP request to the cluster's API Server URL, authenticated as the context's user.
3. The API Server authenticates and authorizes (RBAC), runs admission controllers (validation, quotas), and stores the object in etcd.
4. Controllers in the controller manager watch etcd for changes and reconcile actual state toward the desired state.
5. When the desired state changes (a Pod is deleted), a controller creates a replacement; when it is stable, nothing happens. The loop is continuous.

## 6. Common Mistakes and Gotchas

| Mistake | Why It Happens | How to Avoid It |
|---------|----------------|-----------------|
| Treating containers as VMs | Confusing isolation models | Remember containers share the host kernel; a kernel panic kills every container on the host. |
| No memory limit | Oversight or fear of throttling | Always set `resources.limits.memory`; an unlimited leak can OOM the whole node. |
| Using `latest` tags | Speed of iteration | Pin tags such as `nginx:1.25.2-alpine`, promote by immutable digest in production. |
| Storing state in the container | Not understanding ephemerality | Use volumes or PersistentVolumes. |
| Expecting Namespaces to isolate network | Assuming "isolate" means everything | Namespaces are logical grouping; add a NetworkPolicy for a network boundary. |
| Running destructive commands in the wrong context | Forgetting which context/namespace is active | Verify `kubectl config current-context` before `delete namespace`. |
| Missing requests when a quota exists | Not reading the admission error | If a ResourceQuota is active, every Pod must specify `resources.requests`. |
| Confusing `requests` with `limits` | Similar field names | requests = scheduling guarantee; limits = hard ceiling. |
| Setting huge CPU limits "just in case" | Trying to avoid throttling | Measure real usage; aggressive CPU limits cause CFS throttling and latency spikes. |

## 7. Quick Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| Pod stuck in `CrashLoopBackOff` | Process exits immediately (missing config, bad start) | `kubectl logs <pod> --previous`; check image and command. |
| Container `OOMKilled` (exit 137) | Exceeded the memory cgroup limit; OOM Killer fired | Raise `resources.limits.memory` or fix the memory leak; `kubectl describe pod` or `kubectl get pod <name> -o jsonpath='{.status.containerStatuses[0].state}'`. |
| Container stuck in `Created` | Runtime registered the container but could not start the process | Inspect the container/runtime state and node events; the process never started. |
| `Permission denied` on startup | Container runs as root or lacks a required capability | Run as a non-root user; re-add only the needed Linux capabilities. |
| Cannot see host processes | Inside a PID namespace by design | Use `hostPID: true` in the Pod spec if host visibility is intentional. |
| `Error from server (Forbidden): exceeded quota` | Pod requests exceed the Namespace ResourceQuota | `kubectl describe quota -n <ns>`; compare `Used` vs `Hard`; raise the quota or lower requests. |
| Pod stuck in `Pending` | Not schedulable (node resources) or, if rejected, quota | `kubectl describe quota -n <ns>` and compare `Used` vs `Hard`; a quota rejection is a 403 at admission, while a `Pending` Pod usually means no node fits its `requests`. |
| Pod not shown by `kubectl get pods` | Wrong context or namespace | `kubectl config current-context`; `kubectl get pods -n <ns>`; switch context. |
| `kubectl` talks to the wrong cluster | kubeconfig merge or current-context is wrong | `kubectl config view`; `kubectl config use-context <name>`. |
| Namespace deletion hangs | Resources still exist inside it | `kubectl get all -n <ns>`; delete resources first, then the Namespace. |
| High latency on a busy pod | CFS throttling from a strict CPU limit | Relax the CPU limit or increase requests. |
| Host is down but a pod seems fine | One app starved the node of memory/CPU | Check limits; pods without requests can be evicted under pressure. |

## 8. 30-Second Recap

- Container = Linux process + namespaces (isolation) + cgroups (limits). Not a VM: it shares the host kernel.
- Namespace types: PID, NET, MNT, UTS, IPC, USER. Blindfold = isolation.
- cgroups bound CPU, memory, I/O. CPU limits use CFS (throttling); memory limits use the OOM Killer (exit 137, `OOMKilled`). Leash = limitation.
- An image is a read-only, layered template in a registry; a container is a running instance of it.
- Kubernetes runs containers via kubelet -> CRI -> runtime (containerd, CRI-O) -> kernel.
- Pod = smallest deployable unit; one or more containers sharing a network namespace, IP, and volumes. Ephemeral.
- Declarative model: write desired state, apply it, and controllers reconcile reality toward it.
- kubectl -> API Server -> etcd -> controllers -> scheduler -> kubelet. It is just an HTTP client.
- kubeconfig holds clusters, users, contexts (cluster + user + namespace), and current-context; `KUBECONFIG` env var overrides the default file.
- Container lifecycle: Created -> Running -> Paused -> Stopped -> Deleted. Stuck in `Created` = runtime could not start the process; stuck in `OOMKilled` = cgroup memory limit breached.
- Containers are NOT a hard security boundary: namespaces + cgroups isolate visibility and usage only, and a breakout grants host access. Harden with non-root users, read-only root filesystems, dropped capabilities, and minimal multi-stage images; inject secrets at runtime, never bake them in.
- Kubernetes Namespaces are logical partitions (name scoping, RBAC, quotas). Nodes, PVs, StorageClasses are cluster-scoped.
- ResourceQuota = namespace budget for sums of `requests.*`/`limits.*`, enforced at admission (403, object never hits etcd). Quota caps namespace totals, not one Pod's ceiling; lowering a quota never evicts existing Pods, it only blocks new ones. LimitRange injects `default`/`defaultRequest` automatically.
- Namespaces are the primary RBAC boundary (Role + RoleBinding per namespace); protect Namespaces from deletion via RBAC; namespace-per-customer + quota + RBAC is the multi-tenancy pattern on one big cluster.
- requests protect you; limits protect your neighbors. Set both, pin image tags, and verify your context before destructive commands.

## Related Lessons

- [Lesson 1 - The Anatomy of a Container](../docs/01-fundamentals/lesson-01-anatomy-of-a-container.md)
- [Lesson 2 - Namespaces and Contexts](../docs/01-fundamentals/lesson-02-namespaces-and-contexts.md)

## Related Material

- [kubectl Cheat Sheet](../cheatsheets/kubectl-cheatsheet.md)
- [Interview - Fundamentals](../interview/fundamentals.md)

[Back to Revision Index](README.md)
