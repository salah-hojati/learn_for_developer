---
title: Revision - Workloads
module: 03 Workloads
status: Complete
tags: [revision, workloads, pod, deployment, statefulset, daemonset, job]
---

# Revision - Workloads

## 1. The Mental Model

Kubernetes is **declarative**: you state the result you want (for example, "run 3 copies of this image"), and a controller reconciles reality to match it. Workloads are a nesting doll of controllers that each close the gap between desired and actual state:

```text
Deployment  ->  ReplicaSet  ->  Pod  ->  Container
(the recipe)   (the counter)   (the cup)  (the drink)
```

- **Pod** is the smallest schedulable unit, the "cup" that groups containers so they share one IP, one port space, and shared volumes.
- **ReplicaSet** is the "tray": it counts Pods by label and instantly replaces any that die.
- **Deployment** is the "recipe book": it manages ReplicaSets for update and rollback.
- **StatefulSet, DaemonSet, Job, CronJob** are specialized controllers for cases where "identical replaceable Pods" is the wrong model: databases need stable identity and disk, every node needs an agent, and batch scripts run to completion and stop.

## 2. Core Concepts

### Pods and multi-container patterns

A Pod groups one or more containers that share the network namespace (one IP and port space, so they reach each other via `localhost`), the IPC namespace, and any attached volumes. They are **tightly coupled**: scheduled together on one node and rescheduled together. The classic helper is a **sidecar** -- a second container that lives as long as the main app to do logging, metrics, or mTLS. Do not put a frontend and backend in one Pod; those belong in separate Deployments.

### initContainers

Init containers run to completion (exit code 0) **before** the app containers, in strict order. If one fails, the Pod restarts and retries it, and the main container is never created. Use them to wait for a database or download configs. They share Pod volumes, so the standard pattern is an `emptyDir`: the init container writes to it and the app reads from it. Read their logs with `kubectl logs <pod> -c <init-container-name>` (the `-c` is mandatory).

The kubelet gates the app through the `Initialized` Pod condition: it starts as `False`, flips to `True` only after every init container exits 0, and only then is the main container started. Because init containers run strictly in order, each one adds boot time (five init containers can make startup roughly 5x slower); combine setup into fewer containers when possible. Keep tooling images small (Alpine-based). Give each init container its own `securityContext` when it needs different permissions than the main app, and build in timeouts and error handling so a "wait for the database" loop does not spin forever against a service that never comes up.

### restartPolicy

| `restartPolicy` | Behavior | Allowed in |
|------------------|----------|------------|
| `Always` | Restart no matter how it exited | Pods, Deployments |
| `OnFailure` | Restart only on a non-zero exit | Jobs |
| `Never` | Never restart; the Job controller makes a fresh Pod | Jobs |

Deployments require `Always`; Jobs use `Never` or `OnFailure`, never `Always`. For Jobs, `OnFailure` restarts the same container in place (saves Pod-creation overhead), while `Never` makes the Job controller build a brand-new Pod each retry and leaves the failed Pod in `Error` state so you can inspect its logs.

### Resource requests and limits

Set `resources.requests` and `resources.limits` on **every container**. Requests reserve capacity for the scheduler; limits cap how much a Pod may consume, so a leaky container cannot starve the node. For Pods of any kind, a missing limit lets memory growth OOM the node and evict neighbors. DaemonSet Pods especially need limits because a runaway agent multiplies across every node.

### Pod Disruption Budgets

A **Pod Disruption Budget (PDB)** protects a guaranteed minimum number of available Pods during **voluntary** disruptions (node drains, cluster upgrades). Use `minAvailable: 2` for a Deployment or StatefulSet; a drain will not evict below that floor. It does not cover involuntary events (node crash); the ReplicaSet handles those.

### Labels and selectors

Use a **structured label convention** -- for example `app: billing`, `tier: backend`, `env: prod` -- so selectors, dashboards, and `kubectl` queries stay organized and consistent. Selectors pin a controller to exactly these labels and are immutable after creation.

### Deployment security

Give Pods a dedicated **ServiceAccount with strict RBAC** (never the default account with broad permissions) unless the app truly must talk to the API server, run as a **non-root user** (`securityContext.runAsNonRoot`), and mount **read-only root filesystems** where feasible. This limits blast radius if a workload is compromised.

### Readiness, liveness, startup probes

| Probe | Failure means | Effect |
|-------|---------------|--------|
| `startupProbe` | Still booting | Grace window before liveness starts (for slow apps) |
| `livenessProbe` | Dead or hung | Container killed and recreated |
| `readinessProbe` | Not ready to serve | Removed from Service Endpoints (no restart) |

Handlers are `httpGet`, `tcpSocket`, or `exec`. A readiness probe is the linchpin of zero-downtime rolling updates.

### ReplicaSet

A ReplicaSet keeps a desired number of identical Pods running. It uses a label selector (`spec.selector.matchLabels`) to find its Pods, compares the count to `spec.replicas`, and creates or deletes to match. The selector must match `spec.template.metadata.labels` and is immutable after creation. You almost never make a ReplicaSet directly; a Deployment wraps one. The API server rejects a selector/template mismatch at creation time because otherwise the ReplicaSet would create Pods that do not match its own selector, see "0 managed", and create more forever -- an infinite create loop.

### Deployment

A Deployment layers above ReplicaSets to give **rolling updates, rollbacks, pausing, and scaling**. Change the Pod template (for example the image) and the controller creates a new ReplicaSet, scales it up while scaling the old one down, then keeps the old ReplicaSet scaled to 0 (not deleted) so rollback is instant. A rolling update never modifies Pods in place; the new ReplicaSet creates all-new Pods and the old ones are deleted.

RollingUpdate parameters (both default 25%):

- `maxSurge`: extra Pods allowed above desired.
- `maxUnavailable`: Pods allowed below desired.

For strict zero downtime use `maxUnavailable: 0` with `maxSurge: 1`: the new Pod is ready before any old one is touched. Concrete surge/down alternation with `replicas: 4`, `maxSurge: 1`, `maxUnavailable: 1`: scale v2 up by 1 (total 5) -> scale v1 down by 1 (total 4) -> scale v2 up by 1 (total 5) -> scale v1 down by 1 (total 4) ... repeats until v1 has 0 and v2 has 4. Raising `maxSurge` makes large rollouts faster, but only if the cluster has spare capacity. **Recreate** kills all old Pods before starting new ones, causing downtime; use it only when two versions cannot run at once.

Kubernetes does **not** auto-rollback on failure; it pauses the rollout (old Pods keep serving) and waits for manual `kubectl rollout undo`. When an app requires a database schema migration, run the migration before the rolling update and keep the schema backward-compatible, because old and new Pods run side by side. Other fields: `revisionHistoryLimit` (old ReplicaSets kept, default 10), `minReadySeconds` (time a Pod must be ready before it counts available), and `progressDeadlineSeconds` (how long a rollout may take, default 600s). Because the old ReplicaSet is kept, `rollout undo` is fast: it just scales the previous ReplicaSet back up. Test rollbacks in staging before relying on them in production.

### StatefulSet

Deployments fail for databases because their Pods are interchangeable and lack stable storage. A **StatefulSet** gives **stable identity and stable storage**: Pods are `<app>-0`, `<app>-1`, `<app>-2`; if `db-1` dies, its replacement is also `db-1` and reattaches the _same_ PVC, so data survives. It needs a **headless Service** (`clusterIP: None`) so CoreDNS returns per-Pod records like `db-0.db.svc.cluster.local` instead of one virtual IP.

- `serviceName`: links the StatefulSet to its headless Service.
- `volumeClaimTemplates`: creates one PVC per replica (`data-db-0`, `data-db-1`, ...).
- `podManagementPolicy`: `OrderedReady` (default) creates Pods sequentially, so a failing `db-0` freezes the rollout; `Parallel` boots all at once. Scaling down and updates happen in reverse order. Deleting a StatefulSet removes Pods but **not** PVCs. In production use a Helm chart (Bitnami) or an Operator (CloudNativePG, Strimzi) instead of hand-writing the YAML.

The controller wires identity itself: it sets `hostname` and `subdomain` on each Pod and attaches an `ownerReference` so the Pods are garbage collected when the StatefulSet is deleted. Use **Pod anti-affinity** so `db-0`, `db-1`, `db-2` land on different nodes; if they share one node and it dies you lose the whole cluster. StatefulSets often run privileged databases, so put them in a dedicated namespace with strict RBAC, and back up their PVCs with Velero as part of disaster recovery.

### DaemonSet

A DaemonSet runs **exactly one Pod on every matching node** and auto-places one on any node that joins. There is no `replicas` field; replicas equal the node count. Use it for node-level infrastructure: log shippers (Fluent Bit, Promtail), monitoring agents (Datadog, Node Exporter), and CNI/network plugins. It **bypasses the kube-scheduler** by setting `spec.nodeName` directly, which guarantees placement even on a "full" node. Tainted nodes (like the control plane) are skipped unless you add a matching `tolerations`.

DaemonSet Pods need the same guardrails as any Pod: set CPU/memory limits and monitor their resource use, since a buggy agent runs once per node and waste multiplies. Use `priorityClassName: system-node-critical` for essential agents (CNI, kube-proxy-like shippers). Configure `updateStrategy: type: RollingUpdate` and roll back with `kubectl rollout undo daemonset/<name>`. hostPath mounts are a privileged operation; only run trusted images and lock down the ServiceAccount so a compromised agent cannot read host files.

### Job and CronJob

A Job runs a task to completion and stops. Exit code 0 marks it complete; a non-zero exit is a failure that is retried. Key fields: `completions` (total successful Pod exits required), `parallelism` (Pods run concurrently), `backoffLimit` (max retries before Failed, using exponential backoff), `activeDeadlineSeconds` (hard wall-clock timeout), `restartPolicy: Never` or `OnFailure` (never `Always`), and `ttlSecondsAfterFinished` (auto-deletes finished Pods).

The `JobController` runs inside `kube-controller-manager`. It creates each Pod with an `ownerReference` pointing back to the Job, tracks the Pod's `Failed`/`Succeeded` phase, and increments success and failure counters. On a failed Pod it waits an exponential backoff cadence of **10s, 20s, 40s**, and so on, before creating the next Pod so the retry storm does not hammer the API server.

A **CronJob** runs a Job on a cron schedule, always in **UTC**. Set `concurrencyPolicy: Forbid` to stop overlapping runs, and history limits so old Jobs do not pile up in etcd. If a CronJob seems to not run, verify the schedule math for UTC and check that `concurrencyPolicy: Forbid` is not being blocked by a still-running previous Job. Use Jobs for backups, migrations, or finite batch work; never for a long-running API.

### Storage for stateful workloads

Temporary scratch uses `emptyDir` (wiped when the Pod leaves). Persistent per-Pod disks use `volumeClaimTemplates` in a StatefulSet, which request a PVC backed by a StorageClass/PV, usually `ReadWriteOnce` (one node mounts it). Because the StatefulSet reattaches the same PVC after a crash, database data survives restarts. A `Retain` reclaim policy protects disks, but it is not a backup -- use Velero or database-native dumps.

For databases, use **high-IOPS disks** (AWS `io2`/`gp3`) and avoid NFS for the primary database node because of latency. Enforce **one master per node** with Pod anti-affinity and dedicated node pools. Encrypt Secrets at rest, and use the External Secrets Operator instead of hardcoded passwords. Save passwords to a vault and never hardcode them (a common pitfall is setting `global.postgresql.auth` inline and losing access when a random password is generated).

**RabbitMQ mini-project:** install with `helm install rabbitmq bitnami/rabbitmq`, then `kubectl port-forward svc/rabbitmq 15672:15672` and log into the management UI at `http://localhost:15672` (user `user`, password from the `rabbitmq` Secret). Send a test message through the UI, delete the RabbitMQ Pod (`kubectl delete pod rabbitmq-0`), and confirm the queue persists after the StatefulSet reattaches its PVC.

## 3. Key Commands

Local cluster setup with kind or minikube:

```bash
# kind (Kubernetes IN Docker)
curl -LO https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-amd64
chmod +x ./kind && sudo mv ./kind /usr/local/bin/kind
kind create cluster --name learning          # create
kubectl cluster-info --context kind-learning  # verify
kind delete cluster --name learning          # delete

# minikube
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube /usr/local/bin/minikube
minikube start
minikube stop
minikube delete
```

Workload commands:

```bash
kubectl get pods -l app=web -w
kubectl describe pod <name>
kubectl logs <pod> -c <init-container-name>   # -c required for init/sidecar

kubectl get deploy,rs,pods
kubectl set image deployment/web web=nginx:1.26
kubectl rollout status deployment/web
kubectl rollout undo deployment/web            # rollback
kubectl rollout undo deployment/web --to-revision=2
kubectl rollout history deployment/web
kubectl rollout restart deployment/web         # new Pods, same image
kubectl rollout pause/resume deployment/web
kubectl scale deployment/web --replicas=5
kubectl create poddisruptionbudget web-pdb --selector app=web --min-available=2

kubectl get statefulset,svc,pvc
kubectl scale statefulset db --replicas=5
kubectl run dns-test --rm -it --image=busybox -- nslookup db-0.db
kubectl delete statefulset db                  # PVCs persist!

kubectl get daemonset
kubectl get pods -o wide -l app=<label>
kubectl rollout restart daemonset/<name>
kubectl rollout undo daemonset/<name>

kubectl get jobs,cronjobs
kubectl logs -l job-name=<name> --previous
kubectl create job manual --from=cronjob/backup
kubectl wait --for=condition=complete job/pi
kubectl delete job <name>
```

## 4. YAML Patterns

### Deployment (rolling strategy)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: web
  labels:
    app: web
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  minReadySeconds: 10
  revisionHistoryLimit: 10
  selector:
    matchLabels:
      app: web
  template:
    metadata:
      labels:
        app: web
    spec:
      containers:
      - name: web
        image: nginx:1.25-alpine
        ports:
        - containerPort: 80
        resources:
          requests: {cpu: 100m, memory: 128Mi}
          limits: {cpu: 500m, memory: 256Mi}
        readinessProbe:
          httpGet: {path: /, port: 80}
          initialDelaySeconds: 2
          periodSeconds: 3
```

Field-by-field: `spec.replicas` declares the desired Pod count. `spec.strategy` sets update behavior: `maxUnavailable: 0` never lets the live count drop below desired while `maxSurge: 1` allows one extra during the change -- zero-downtime. `minReadySeconds` forces a Pod to stay healthy before it counts as available. `revisionHistoryLimit` keeps old ReplicaSets for rollback. `spec.selector.matchLabels` must equal `spec.template.metadata.labels` -- that is how the ReplicaSet finds and names them, and it cannot change after creation. The `readinessProbe` makes the rollout wait until a new Pod can actually serve before touching an old one. `resources` (requests + limits) is required on every container in production; a container without it can consume the whole node.

### StatefulSet

```yaml
apiVersion: v1
kind: Service
metadata:
  name: db
spec:
  clusterIP: None
  selector: {app: db}
  ports:
  - port: 5432
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: db
spec:
  serviceName: db
  replicas: 3
  selector:
    matchLabels: {app: db}
  template:
    metadata:
      labels: {app: db}
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchLabels: {app: db}
            topologyKey: kubernetes.io/hostname
      containers:
      - name: db
        image: postgres:16
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: [ReadWriteOnce]
      resources:
        requests:
          storage: 10Gi
```

Field-by-field: `clusterIP: None` makes the Service **headless**, so DNS resolves per-Pod (`db-0.db`, `db-1.db`) instead of load-balancing a virtual IP. `serviceName: db` bonds the StatefulSet to that headless Service. `podAntiAffinity` with `topologyKey: kubernetes.io/hostname` forces `db-0`, `db-1`, and `db-2` onto different nodes, so one node failure cannot take the whole cluster down. `volumeClaimTemplates` mints a separate `data-db-0`, `data-db-1`, `data-db-2` PVC per replica so each Pod owns a disk that survives replacement. `accessModes: ReadWriteOnce` allows only one node to mount the disk, as a database requires. With default `OrderedReady`, `replicas: 3` means `db-0` must be Running and Ready before `db-1` is created, and so on.

## 5. How It Fits Together (lifecycle)

1. **Create.** `kubectl apply` sends the Deployment to the API server / etcd; the `kube-controller-manager` runs the controller loops -- notably the `DeploymentController` and the `ReplicaSetController` -- which make a ReplicaSet, make Pods, the scheduler places them, and kubelets start the containers.
2. **Scale.** Change `spec.replicas` (or `kubectl scale`). The ReplicaSet adds or removes Pods instantly; scaling does not trigger a rolling update. Avoid jumping 1 -> 1000 replicas at once: it can overwhelm the API server, scheduler, and network. For massive one-shot fan-out use a Job instead.
3. **Update.** Change `spec.template` (new image). A brand new ReplicaSet is created and converges, scaling it up and the old one down within `maxSurge` / `maxUnavailable`.
4. **Rollback.** If the new version fails readiness (or is bad in production), `kubectl rollout undo` scales the previous ReplicaSet back up -- the reason the Deployment kept it.
5. **Operate.** `kubectl rollout restart` rotates Pods on the same image to pick up new secrets/configmaps; pause/resume gives manual control.
6. **Cleanup.** `kubectl delete deployment` cascades Deployment -> ReplicaSet -> Pods. StatefulSets differ: `kubectl delete statefulset` leaves the PVCs, so delete those explicitly if you want the data gone.

## 6. Common Mistakes and Gotchas

| Mistake | Why it happens | How to avoid it |
|---------|---------------|-----------------|
| Selector/label mismatch | `matchLabels` differs from template labels | The API rejects it; keep them identical and immutable |
| Rapid 1 -> 1000 scaling | API server / scheduler / network overwhelmed | scale in steps, or use a Job for one-shot fan-out |
| Not undoing a bad rollback | rolling forward again while still broken | fix the code before rolling forward; test rollbacks in staging |
| Using `latest` tags | the tag never changes, no new revision | Pin tags or Git SHAs |
| Scaling to 0 to "pause" | thinking it stores state | Kills all Pods; use `rollout pause` |
| No readiness probe | traffic routed to a booting Pod (502s) | Always set `readinessProbe` |
| Init logs returned empty | forgot `-c` | `kubectl logs <pod> -c <init>` |
| Init container runs a long process | main container never starts | only short-lived setup; use native sidecars for daemons |
| Init wait loop with no timeout | waiting for a service that never comes up | add a timeout + error handling inside the init script |
| Deleting a StatefulSet leaves PVCs | old data can crash a new version | delete PVCs deliberately |
| Deployment for a database | no `volumeClaimTemplates`, replicas race one disk | use a StatefulSet / Helm / Operator |
| DaemonSet missing nodes | taints or `nodeSelector` mismatch | add tolerations or relabel nodes |
| Jobs using `restartPolicy: Always` | copied from a Deployment | Jobs need `Never` or `OnFailure` |
| CronJobs in local timezone | Kubernetes is UTC | compute the UTC offset |
| The frozen rollout | `OrderedReady` blocks on a failing `db-0` | fix `db-0` first; everything waits on it |
| No Pod anti-affinity on a DB | all replicas land on one node | add `podAntiAffinity` with `topologyKey: kubernetes.io/hostname` |

## 7. Quick Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| `CrashLoopBackOff` | app crashes; container restarts | `kubectl logs <pod> --previous`; `describe pod`; fix the app |
| `Init:CrashLoopBackOff` | an init container keeps failing; main never starts | `kubectl logs <pod> -c <init>`; fix the setup step |
| `Init:Error` | an init container exited non-zero (not yet looping) | `kubectl logs <pod> -c <init>`; fix the init command or image |
| `ImagePullBackOff` | tag doesn't exist / can't pull | check the tag; `rollout undo` if it was the deploy |
| `Pending` | no capacity / claim unbound | `describe pod | grep Events`; check storageclass and PVCs |
| Rollout hangs forever | new Pods never become ready | `rollout status`; check the probe; relax `maxUnavailable` |
| Only `db-0` exists | `OrderedReady` blocks on it | fix `db-0`; the rest boot afterward |
| StatefulSet DNS resolution fails | headless Service missing or not linked | add `clusterIP: None` + `serviceName`; `kubectl run busybox -- nslookup db-0.db` |
| StatefulSet created no PVCs | `volumeClaimTemplates` missing from the spec | add `volumeClaimTemplates`; `kubectl describe statefulset <name>` |
| DaemonSet less than nodes | taint/toleration or selector | `kubectl get nodes --show-labels`; add tolerations |
| DaemonSet Pod gone after a drain | `node.kubernetes.io/unschedulable` taint persists | `kubectl taint nodes <node> node.kubernetes.io/unschedulable-` |
| CronJob never fires | UTC schedule wrong, or `concurrencyPolicy: Forbid` blocks on a still-running Job | check schedule math in UTC; `kubectl get jobs` for a running predecessor |
| Job `backoff limit exceeded` | script exits non-zero | `kubectl logs -l job-name=... --previous`; fix and rerun |
| Rollback not working | no old revision kept | check `revisionHistoryLimit` |

## 8. 30-Second Recap

- Run **Pods**, not containers; a Pod groups tightly-coupled containers over one IP and shared volumes.
- **Deployment -> ReplicaSet -> Pod** manages rolling updates, scale, and rollback for stateless apps.
- **RollingUpdate** swaps within `maxSurge` / `maxUnavailable`; **Recreate** kills all first.
- **StatefulSet** names Pods `db-0, db-1, ...` and gives each a stable PVC via `volumeClaimTemplates` behind a headless Service -- for databases and queues.
- **DaemonSet** runs one Pod per node and bypasses the scheduler. **Job** runs to completion; **CronJob** schedules Jobs in UTC.
- **Init containers** gate the app, sharing `emptyDir` data. **Probes** gate starts (startup), restarts (liveness), and traffic (readiness).
- Set **requests/limits**, a **PDB**, and a scoped **ServiceAccount**; `kube-controller-manager` runs the Deployment/ReplicaSet/Job controllers.

## Related Lessons

- [Lesson 6 - Pods, ReplicaSets, and Deployments](../docs/03-workloads/lesson-06-pods-replicasets-and-deployments.md)
- [Lesson 7 - Deployments and Rollout Strategies](../docs/03-workloads/lesson-07-deployments-and-rollout-strategies.md)
- [Lesson 8 - StatefulSets](../docs/03-workloads/lesson-08-statefulsets.md)
- [Lesson 9 - DaemonSets](../docs/03-workloads/lesson-09-daemonsets.md)
- [Lesson 10 - Jobs and CronJobs](../docs/03-workloads/lesson-10-jobs-and-cronjobs.md)
- [Lesson 11 - Init Containers](../docs/03-workloads/lesson-11-init-containers.md)
- [Lesson 12 - Advanced Stateful Workloads (Databases and Message Queues)](../docs/03-workloads/lesson-12-advanced-stateful-workloads.md)

## Related Material

- [Workload Cheat Sheet](../cheatsheets/workload-cheatsheet.md)
- [Interview - Workloads](../interview/workloads.md)

[Back to Revision Index](README.md)