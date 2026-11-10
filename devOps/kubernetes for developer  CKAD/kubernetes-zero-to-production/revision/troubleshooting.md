---
title: Revision - Troubleshooting
module: 13 Troubleshooting
status: Complete
tags: [revision, troubleshooting, debugging, logs, eviction, diagnosis, node-pressure, crashloopbackoff, imagepullbackoff]
---

# Revision - Troubleshooting

This note is a self-contained refresher for Module 13. Read it top to bottom to re-learn how to debug workloads, nodes, and networking without skimming. Everything a supervisor or interviewer cares about is here.

## 1. The Mental Model

Kubernetes is a distributed system. You cannot SSH into one machine and "see" the problem, so Kubernetes is deliberately transparent: every controller action is recorded as an **Event**, every container's output is captured in **logs**, and every resource exposes a **status**. Debugging is the skill of letting the system tell you what broke instead of guessing.

Central concept: the **diagnostic funnel**. Start as wide as possible (the whole cluster) and narrow down one layer at a time. Never jump straight into a pod's YAML.

```text
Cluster  : kubectl get nodes, get events          (control plane health)
Namespace: kubectl get pods -A, get svc           (what exists)
Pod      : kubectl describe pod                   (events / root cause)
Container: kubectl logs --previous, exec          (application behavior)
Code     : read the stack trace, fix the app      (the actual bug)
```

Work **outside-in**: Service -> Endpoints -> Pod -> Container -> code. A Service with no endpoints is often the real problem even when the pod is Running.

### The SRE 5-Step Methodology

Treat this as mandatory order, not a menu:

1. **What happened / Assess state:** `kubectl get pods`. Read the STATUS column.
2. **Reproduce:** confirm you can trigger it again (is it transient or constant?).
3. **Inspect state:** `kubectl describe pod` -> read the Events and Conditions at the bottom.
4. **Inspect runtime logs:** `kubectl logs` (and `--previous` for crashed containers).
5. **Lateral thinking:** check dependencies (Services, Endpoints, ConfigMaps) and permissions before blaming the app. (+ `kubectl auth can-i` for RBAC.)

The canonical funnel names the same order: **Get -> Describe -> Logs -> Dependencies -> RBAC**. Later stages almost never explain earlier failures, so exhaust each stage before jumping to RBAC or the app code.

When an incident is active the order collapses to: **Detection -> Investigation -> Mitigation (stop the bleeding) -> Root Cause Analysis -> Post-Mortem**. Mitigation first, RCA later.

## 2. Core Concepts

### Pod Lifecycle Phases

A pod moves through discrete phases. Knowing which phase you are in tells you which component is at fault: the Scheduler (can't place), the Kubelet (can't prepare/start), the Container Runtime (can't pull), or the application (can't run).

| Phase | Meaning |
|-------|---------|
| Pending | Accepted by the API server but not scheduled (Scheduler problem). |
| ContainerCreating | Assigned to a node; Kubelet is pulling the image and mounting volumes. |
| Running | At least one container is running. |
| CrashLoopBackOff | A container started, crashed, and is being restarted with backoff. |
| Error | Container exited with a non-zero code. |
| OOMKilled | The Linux kernel killed the container for exceeding its cgroup memory limit. |
| Evicted | The Kubelet terminated the pod to relieve node pressure. |
| ImagePullBackOff | The container runtime could not fetch the image. |

### Interpreting `kubectl describe` and Events

`kubectl describe pod` aggregates the spec, status, conditions, and recent events into one read. The **Events section at the bottom is the security camera footage**: always read it before guessing.

| Event / Terminal state | What it means |
|------------------------|---------------|
| `FailedScheduling` | Scheduler cannot place the pod (capacity, taints, affinity). Remedy Scheduling problem. |
| `Failed to pull image ... NotFound` | Bad image name/tag -> ImagePullBackOff. |
| `CreateContainerConfigError` | Pod references a ConfigMap/Secret key that does not exist. |
| `MountVolume.SetUp failed` | Volume mount failed or CSI driver stuck. |
| `Liveness probe failed` / exit 1 | App started but crashed at runtime -> CrashLoopBackOff. |
| `OOMKilled` (exit 137) | Container exceeded its memory limit -> kernel kill. |
| `Evicted` (Reason) | Kubelet killed the pod to save the node. |

**Exit codes:** 137 = SIGKILL (usually OOM), 1 = generic app crash, 143 = SIGTERM (graceful shutdown).

### Events are time-limited

Events are stored in etcd for about 1 hour by default. If you wait too long the evidence is gone, so capture events early.

### Node Pressure and Evictions (vs OOMKilled)

When a **node** runs out of a resource, the Kubelet sets a **Node Condition** and may **evict** pods.

| Condition | Resource |
|-----------|----------|
| MemoryPressure | Node available memory is below threshold. |
| DiskPressure | Node disk space is low (often logging/emptyDir). |
| PIDPressure | Node is low on process IDs (PID cgroup). |

### DiskPressure: the logging agent and emptyDir trap

- **Logging agents** such as Fluent Bit (Deployment/DaemonSet) write logs to the node disk. If the agent has no rotation or retention, chatty apps (and the agent itself) fill `/var/log` until `nodefs.available` crosses the eviction threshold.
- **emptyDir** shares the node's ephemeral storage. A container writing large temporary data into an `emptyDir` can fill the node disk no matter how small its memory limit.

Prevention and monitoring:

- Set `requests` and `limits` on the container's `ephemeral-storage` to cap transient writes.
- Give the logging agent a rotation/retention policy and its own storage limit.
- Alert on node disk free/usage so you act before `DiskPressure=True`, and locate the top emptyDir writers (`du` from a debug container) to resize or fix them.

When a hard eviction threshold is crossed (default memory: `memory.available < 100Mi`), the Kubelet ranks every pod and kills the lowest ones until resources recover:

1. BestEffort (no request/limit) dies first.
2. Burstable, killed in order.
3. Guaranteed (request == limit) only as a last resort.

The ranking is decided by three criteria applied in order:

1. **How far a pod exceeds its requests** -- the biggest over-consumer is ranked first.
2. **QoS class** -- BestEffort above Burstable above Guaranteed.
3. **PriorityClass** -- the lower the priority value, the earlier the pod is evicted. This is why a Guaranteed pod with a low PriorityClass can still lose before a Burstable pod with a high one: protect critical work with Guaranteed QoS AND high priority.

The eviction signal flow is graceful by default: the Kubelet sends **SIGTERM** so containers shut down and drain, then **SIGKILL** if the process does not exit. In a critical state the Kubelet may skip SIGTERM and go straight to SIGKILL. Exit codes line up: 143 = graceful SIGTERM, 137 = SIGKILL (also the OOM killer).

Distinguish the two death causes:

- **OOMKilled** = the Linux kernel killed ONE container for exceeding ITS OWN limit. The node is healthy.
- **Evicted** = the Kubelet killed the WHOLE pod to save the NODE from pressure.

### Soft vs Hard Eviction

- **Hard eviction threshold:** the critical line. Kubelet evicts immediately when crossed.
- **Soft eviction threshold:** a warning line set earlier that provides a grace period (`eviction-soft-grace-period`) before the Kubelet violates it, giving pods time to shed work.

While `MemoryPressure=True`, the scheduler stops placing new pods on that node, and the ReplicaSet controller recreates evicted replicas on healthier nodes.

### NodeAllocatable: the capacity that is actually schedulable

A node's `capacity` is not all schedulable. The Kubelet first reserves `system-reserved` (host OS, SSH, system daemons) and `kube-reserved` (kubelet, container runtime, CNI, kube-proxy), plus the eviction thresholds. Only the remainder, **NodeAllocatable**, is offered to pods:

```text
NodeAllocatable = Capacity - system-reserved - kube-reserved - eviction threshold
```

`kubectl describe node <node>` prints this in the `Allocatable:` section, and it is the only number the scheduler can fill. Reserve too little and the node's own daemons starve during pressure; reserve too much and schedulable capacity is wasted. Treat the reservation itself as a resource: a leak in the node-level reservation drains memory just like a pod.

### Tuning eviction thresholds (`--eviction-hard`)

The `100Mi` default is a kubelet flag, not a universal constant. Tune it per node profile:

```text
--eviction-hard=memory.available<100Mi,nodefs.available<10%,imagefs.available<15%
```

Set `--eviction-soft` earlier and `--eviction-soft-grace-period` for a warning window that gives batch workloads time to finish. Match thresholds to node size and burst profile: a huge memory node with a tiny threshold evicts too late.

### Eviction thrashing (evicted -> recreated -> evicted)

Eviction is not self-healing. The ReplicaSet controller recreates every evicted replica; if the node is still stressed or the pressure was never fixed, the replacement pod is evicted again. This **evicted -> recreated -> evicted** loop burns CPU, network, and disk cluster-wide. Break it by fixing the leaking workload, giving it proper limits (or Guaranteed QoS), scaling the node, or draining and temporarily scaling the ReplicaSet down.

### PodDisruptionBudget: voluntary only

A **PodDisruptionBudget (PDB)** keeps a minimum number of replicas available across **voluntary disruptions**: cordon, drain, cluster upgrades. Node-pressure evictions are **involuntary** and ignore the PDB entirely. To keep a critical app alive during MemoryPressure, use Guaranteed QoS + limits + a high PriorityClass — not a PDB.

### ResourceQuota and LimitRange: preventing evictions before they start

Eviction is the safety net; the first line of defense is admission-time limits per namespace:

- `ResourceQuota` caps the total CPU/memory/ephemeral-storage that a namespace can claim.
- `LimitRange` caps per-pod/container values and applies default requests/limits so a developer cannot create a BestEffort pod by omission.

Together they are the DoS protection in shared or multi-tenant clusters: without them, one leaking team's workload can starve a node and evict every other tenant.

### Networking: Endpoints and DNS

A Service routes to pods through **Endpoints**. If Endpoints are empty, the Service is broken even if the pods are Running.

```bash
kubectl get endpoints <svc>          # are there backing IPs?
kubectl get svc -o wide              # ClusterIP and selector
kubectl get pods -l <selector>       # do any pods carry the Service's selector labels?
```

Pod-to-pod communication uses the in-cluster DNS name `<service>.<namespace>.svc.cluster.local`. If the app cannot resolve a peer, test DNS and connectivity from inside the cluster.

## 3. Key Commands

```bash
# Always start here (order matters)
kubectl get nodes
kubectl get pods -A
kubectl get events -A --sort-by='.lastTimestamp'

# Drill into a pod
kubectl describe pod <name>            # Events + Conditions (root cause)
kubectl describe pod <name> | grep -A 5 Events   # skip straight to the Events block
kubectl logs <name>                     # current container logs
kubectl logs <name> --previous           # logs of the crashed container
kubectl logs <name> -c <container>       # specific container (multi-container)
kubectl exec -it <name> -- /bin/sh      # shell into a running container
kubectl top nodes / top pods            # live usage

# Networking
kubectl get endpoints <svc>             # backing pod IPs
kubectl get svc                         # ClusterIP / selector
kubectl get pods -o wide                # pod IPs
kubectl port-forward svc/<svc> 8080:80   # mirror a local port to a cluster service/pod

# Node / resource
kubectl describe node <node>            # Conditions: Memory/Disk/PIDPressure
kubectl get events -A | grep -i evicted # find evictions
kubectl get pod <name> -o jsonpath='{.status.qosClass}'   # QoS class
kubectl auth can-i <verb> <resource>    # RBAC check
```

- **`kubectl exec`** = get a shell inside a running container to inspect processes, files, or run tools. Use it when the container IS running but behaving strangely.
- **`kubectl port-forward`** = map a local port to a pod/service so you can test connectivity from your machine to a service that has no external exposure. Use it to verify the app responds on the right port during diagnostics. It is a debugging aid only, not a production access mechanism.

### Centralized logging (Loki / ELK) at scale

`kubectl logs` reads one pod at a time; on 1000+ pods it is far too slow to search. Forward logs to a centralized store — Loki (Grafana) or ELK (Elasticsearch + Logstash + Kibana) — and query by namespace/pod over a time window in one pass. Central logs also survive pod restarts and node moves, so the evidence is never lost with the container.

## 4. A Structured Debugging Flow

Use this exact numbered sequence when any pod or service misbehaves:

1. **Assess at the widest scope.** Run `kubectl get nodes`, `kubectl get pods -A`, `kubectl get events -A`. Note every non-Running status and repeated error.
2. **Pick the most fundamental dependency first** (e.g., a database the frontend depends on). Do not fix a leaf before its root.
3. **Drill in with `kubectl describe pod <name>`.** Read the Events and State. This points at Scheduling, Pull, Mount, or Runtime.
4. **Follow each event to its fix.**
   - `FailedScheduling` -> inspect taints, tolerations, affinity, and requests/limits; uncordon or request more capacity.
   - `Failed to pull image` -> fix the tag, imagePullSecrets, or target an available registry.
   - `CreateContainerConfigError` -> the ConfigMap/Secret key does not exist. Verify with `kubectl get configmap/secret -o yaml`.
   - `MountVolume.SetUp failed` -> check the PVC/StorageClass/CSI volume.
   - `Liveness/Readiness probe failed` -> the app endpoint is wrong or the app is not yet listening.
5. **If the container is running but crashing**, read the logs. `--previous` is mandatory: a just-restarted container generally has empty logs, so read the crashed instance.
6. **Check dependencies and network.** Verify the Service selector matches pod labels and Endpoints are populated. Confirm DNS resolution from inside the cluster.
7. **Check RBAC** only after the above. `kubectl auth can-i` — legitimate if the pod cannot read a ConfigMap/Secret it needs.
8. **Change one variable at a time.** Apply a fix, verify recovery, then move to the next issue. Do not guess. Delete nothing until the evidence is captured.

Order recap: **get pods -> describe pod (events) -> logs (previous) -> dependencies/endpoints -> permissions.**

### RBAC for application team self-debugging

Developers must be able to self-diagnose; otherwise every event query becomes an SRE ticket. Grant them a read-only Role in their namespace so they can `kubectl describe` and `kubectl logs` their own pods:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: pod-debugger
rules:
- apiGroups: [""]
  resources: ["pods", "events"]
  verbs: ["get", "list", "watch"]
- apiGroups: [""]
  resources: ["pods/log", "pods/status"]
  verbs: ["get"]
```

With this in place developers run the debug funnel themselves and only escalate with evidence. Read-only is enough; do not grant delete or exec() unless there is an explicit need.

## 5. Pod Status Cheat

| Status | Cause | Fix |
|--------|-------|-----|
| Pending | FailedScheduling: no capacity, taints, affinity | Examine events; adjust resources, tolerations, or free capacity |
| ContainerCreating (stuck) | Volume mount fail, missing Secret/ConfigMap, container runtime issue | `kubectl describe pod` -> read events; fix mount or config |
| ImagePullBackOff | Bad image name/tag or missing imagePullSecrets | Verify image exists, credentials, registry reachability |
| CrashLoopBackOff | App crashes (exit 1), liveness killing, OOM | `kubectl logs --previous`; fix code/config or raise limit |
| OOMKilled | Container exceeded its own memory limit | Raise the memory limit or fix the leak (node likely healthy) |
| Evicted | Node pressure (Memory/Disk/PID) | Fix the pressure source; make app Guaranteed QoS |
| Error / NotReady | Container exit non-zero or probe failing | Check exit code, logs, probes |
| Terminating (stuck) | Finalizers, PDB, node gone | Check finalizers/PDB; fix the blocking resource |
| Unknown | Node unreachable | Check kubelet, network, node isalive status |

## 6. Common Mistakes and Gotchas

- **Deleting the evidence:** Do not `kubectl delete` a crashed pod before reading its events and logs; you destroy the forensic record.
- **Ignoring Events:** staring at `kubectl get pods` while the fix sits in the Events section.
- **Not using `--previous`:** `kubectl logs <pod>` is empty right after a restart, hiding the crash; always try `--previous`.
- **Fixing multiple variables at once:** you cannot tell which change fixed it (or introduced the next bug).
- **Confusing OOMKilled with Evicted:** OOMKilled is container-local (kernel), Evicted is node-level (Kubelet). They are fixed differently.
- **Assuming a node pressure is only your pod:** one leaking container evicts healthy neighbors; check node conditions.
- **Port-forward as a production slip:** port-forward is a debugging aid, not production access.
- **Forgetting empty Endpoints:** a Service with no Endpoints is broken even when the pods are Running but not Ready (selector mismatch / readiness).
- **Failure to capture events quickly:** events expire after ~1 hour by default.
- **Treating eviction as self-healing (thrashing):** an evicted pod is recreated immediately and can be evicted again, looping `evicted -> recreated -> evicted` until the pressure source is fixed. Fix the workload or the node; do not wait it out.
- **Relying on a PDB against node pressure:** PDBs constrain voluntary disruptions (drain, cordon, upgrade); node-pressure evictions ignore them. Protect critical workloads with Guaranteed QoS, limits, and a high PriorityClass.
- **Tailing logs per pod at fleet scale:** on a 1000+ pod cluster, query a centralized detail engine (Loki/ELK) instead of looping `kubectl logs`; central logs also survive restarts.
- **No `ResourceQuota`/`LimitRange` in shared clusters:** one team's leaked workload can pressure a node and evict its neighbors; admission-time limits stop this at creation.

## 7. Quick Troubleshooting Scenarios (Workers)

- **Pods stuck Running but users get 502.** Pods may be NotReady or selectors mismatched. Check `kubectl get endpoints <svc>`; if empty, the Service selector does not match pod labels or the app fails readiness.
- **Deployment stuck in ImagePullBackOff though the image exists.** Likely a typo, a private registry without `imagePullSecrets`, or the node cannot reach the registry.
- **Frontend cannot connect to its DB but the DB pod is Running.** Check the DB Service's Endpoints, the Service DNS name, and the port. Verify in-cluster DNS and that the backend listens on the configured port.
- **Pods vanish and reappear on other nodes.** Inspect `kubectl describe node` Conditions; `MemoryPressure=True` / `DiskPressure=True` means evictions. `kubectl get events -A | grep -i evicted` confirms. Find the leaking/bursty workload.
- **Container OOMKilled with a healthy node.** Its own memory limit too low. Raise limits or fix the leak.
- **Node reports NotReady.** Check kubelet (`journalctl -u kubelet`), runtime, CNI, and disk pressure; then drain/uncordon only after explicitly diagnosed.

## 8. 30-Second Recap

- Mental model: start wide (cluster) and funnel to the narrowest part (code). Outside-in: Service -> Endpoints -> Pod -> Container -> code.
- 5-step funnel: Get pods -> Describe -> Logs -> Dependencies -> RBAC (use `--previous` for crashed containers).
- Pending = Scheduler; ImagePullBackOff = Runtime; ContainerCreating = Kubelet (mounts/secret/ConfigMap); CrashLoopBackOff = App code.
- OOMKilled = kernel kills a container for its own limit; Evicted = Kubelet saves the node from pressure.
- Eviction ranking: exceed requests (1), QoS class (2), PriorityClass (3, lowest first).
- Eviction signal: SIGTERM first, then SIGKILL if needed; exit codes 143 / 137.
- Node conditions: `MemoryPressure` / `DiskPressure` / `PIDPressure`. Default hard threshold: `memory.available < 100`.
- `NodeAllocatable = Capacity - system-reserved - kube-reserved - eviction threshold`; that is the only capacity the scheduler can fill.
- Evictions are involuntary and ignore PDBs (which only bound voluntary drains); prevent DoS with `ResourceQuota` + `LimitRange`.
- Eviction thrashing is `evicted -> recreated -> eviction `; fix the pressure source or the workload, do not wait it out.
- While under pressure the scheduler skips that node; ReplicaSet re-creates evicted pods on healthy nodes.
- `kubectl exec` inspects a running container; `kubectl port-forward` tests network reachability.
- Change one variable at a time; never delete evidence; always read events before guessing.

## Related Lessons

- [Lesson 41 - The SRE Troubleshooting Masterclass](../docs/13-troubleshooting/lesson-41-sre-troubleshooting-masterclass.md)
- [Lesson 42 - Node Pressure and Evictions (Saving the Ship)](../docs/13-troubleshooting/lesson-42-node-pressure-and-evictions.md)

## Related Material

- [Troubleshooting Cheat Sheet](../cheatsheets/troubleshooting-cheatsheet.md)
- [Interview - Troubleshooting](../interview/troubleshooting.md)

[Back to Revision Index](README.md)