---
title: Interview - Workloads
module: 03 Workloads
status: Complete
tags: [interview, workloads, pod, deployment, statefulset, daemonset, job]
---

# Interview - Workloads

## Beginner

**Q: What is the difference between a Pod and a container?**

A: A container is a Linux process isolated by namespaces and cgroups. A Pod is a Kubernetes abstraction that wraps one or more containers sharing a network IP and volumes.

**Q: Do I need a Deployment for a simple one-off Pod?**

A: Not usually. Use a Pod for a quick test, or a Job for a run-to-completion task. Deployments are for apps that should stay running and be scaled.

**Q: What is an init container?**

A: A container that runs to completion before the main container starts, often for setup (wait for dependencies, populate config).

## Intermediate

**Q: How does a rolling update differ from Recreate?**

A: RollingUpdate scales up the new ReplicaSet while scaling down the old one, respecting `maxUnavailable` and `maxSurge`, so there is minimal downtime. Recreate kills all old Pods first, causing downtime but guaranteeing only one version exists.

**Q: When would you choose a StatefulSet over a Deployment?**

A: When the workload needs stable network identity, stable storage per replica (databases, caches, queues), or ordered deployment/scaling. It relies on a headless Service and `volumeClaimTemplates`.

**Q: How does readiness differ from liveness?**

A: A failing readiness probe removes the Pod from Service endpoints (no restart). A failing liveness probe restarts the container.

## Advanced

**Q: How do `maxSurge` and `maxUnavailable` work mathematically during a rolling update?**

A: Both accept an absolute replica count or a percentage of `spec.replicas`. Percentages round up for `maxSurge` and round down for `maxUnavailable`. For a Deployment with `replicas: 10` and both set to 25%, the rollout wave looks like this:

| Phase | Old ReplicaSet | New ReplicaSet | Total Pods |
|-------|----------------|----------------|------------|
| 0     | 10             | 0              | 10         |
| 1     | 8              | 3              | 11         |
| 2     | 5              | 5              | 10         |
| 3     | 2              | 8              | 10         |
| 4     | 0              | 10             | 10         |

Two invariants hold at every step: the total Pod count never exceeds `replicas + maxSurge` (13), and the number of Available Pods never drops below `replicas - maxUnavailable` (8). The controller grows the new ReplicaSet up to the surge ceiling and drains the old one only as new Pods become Ready. Both fields default to 25% when unset. Setting `maxUnavailable: 0` with `maxSurge: 0` is invalid and is rejected by validation.

**Q: Why is the Deployment selector immutable, and how can a selector mistake orphan ReplicaSets?**

A: `.spec.selector` is immutable: any update that changes it is rejected by the API server, so you are forced to delete and recreate the Deployment. A recreate with a different selector leaves the old ReplicaSet with its own selector, and because the new Deployment no longer matches it, that ReplicaSet is orphaned: nothing scales it, nothing deletes it, and its Pods keep running outside the release process forever. Treat labels and selectors as a committed contract decided once at application birth, and migrate workloads by keying a new Deployment to a new label set rather than by mutating an existing selector.

**Q: What makes a Deployment create a new ReplicaSet versus just scaling the existing one?**

A: Only changes to `.spec.template` start a new rollout. The Deployment controller computes a hash of the pod template and stores it in the `pod-template-hash` label; when the hash changes, it creates a new ReplicaSet at the new revision. Changes to `replicas`, `strategy`, or `revisionHistoryLimit` mutate the existing ReplicaSet in place and never roll Pods. That is why `kubectl rollout restart` works: it injects a `kubectl.kubernetes.io/restartedAt` annotation into the template, which changes the template hash:

```
$ kubectl get pods --show-labels -l app=web
NAME                   READY  STATUS   LABELS
web-64f55d9b7d-7f2c1   1/1    Running  app=web,pod-template-hash=64f55d9b7d
```

**Q: How do StatefulSet ordinals, `podManagementPolicy`, and the headless Service create stable identity?**

A: Pods are named `<name>-0` through `<name>-N-1`. With the default `OrderedReady` policy, Pods are created strictly in index order and each must become Ready before the next is created; scaling down deletes in reverse order (N-1 down to 0). `podManagementPolicy: Parallel` creates and destroys all Pods at once, which suits replicated workers that do not need ordered readiness. Identity comes from pairing the StatefulSet with a headless Service (`clusterIP: None`), which gives each Pod a fixed DNS name (`name-0.svc.namespace.svc.cluster.local`) that survives rescheduling. Combined with `volumeClaimTemplates`, both the ordinal name and the PVC are sticky, so the database master does not float between replicas.

**Q: How does a DaemonSet target nodes, and how does its rollout differ from a Deployment?**

A: A DaemonSet schedules one Pod per node matching the node selection: `nodeSelector`, Node `affinity`, and `tolerations` (a toleration is required to land on tainted nodes such as a control-plane `NoSchedule` taint). Since Kubernetes 1.12, DaemonSet Pods go through the real scheduler, so resource requests and PriorityClass apply like any other Pod. Updates use `updateStrategy: RollingUpdate` with `maxUnavailable` (default 1), so at most one node is without its daemon during a rollout. For Guaranteed QoS Pods (`requests == limits`), the kubelet CPU manager can pin DaemonSet containers to dedicated CPU cores; a metrics or logging DaemonSet can therefore compete with application Pods for exclusive cores, so size requests carefully.

**Q: What do `backoffLimit`, `activeDeadlineSeconds`, `parallelism`, `completions`, and `ttlSecondsAfterFinished` control on a Job?**

A: `backoffLimit` (default 6) is the total number of retries for failed Pods before the Job is Failed; the kubelet backs off exponentially (10s, 20s, 40s...). `activeDeadlineSeconds` is a hard wall-clock deadline for the whole Job, after which it is Failed and remaining Pods are terminated regardless of backoff. `parallelism` is the maximum number of concurrently running Pods, and `completions` is the number of successes needed to mark the Job Completed. With `completionMode: NonIndexed` any Pod count matters; `Indexed` (v1.22+) names Pods `job-0`..`job-N-1` and injects a `JOB_COMPLETION_INDEX` variable, which is what parallel work libraries (e.g. MPI launch, JDBC partitioned loads) rely on. `ttlSecondsAfterFinished` lets the TTL controller garbage-collect Finished Jobs so completed workloads do not pile up.

**Q: What is PodFailurePolicy, and when would you use it?**

A: Before v1.28 the Job controller retried every failed Pod until `backoffLimit`, which is wasteful for failures a retry can never fix (bad parameters, unsupported config). PodFailurePolicy (graduated to stable in 1.28) lets you define rules matching container exit codes and container states and trigger one of two actions: `FailJob` to fail the whole Job immediately (for permanent errors) or `Ignore` (for transient errors that should not consume backoff). It mitigates the cost of mini-waves of retries and shortens time-to-detection.

**Q: How do init containers order, and what changed with sidecar init containers?**

A: Init containers run strictly in list order and each must exit 0 before the next starts, and none of the main containers start until all init containers succeed. If an init container fails, the whole Pod is restarted (subject to the `restartPolicy`) until it exits cleanly. Since 1.28, an init container with `restartPolicy: Always` is promoted to a sidecar: it starts before the main container (in order) but does not block it, and it stays alive alongside the app, which is the pattern for log-forwarders and proxies. A common footgun is an init container staging data into a volume the main container also mounts: the init writes are NOT guaranteed to be visible instantly (volume attach races), so the app should tolerate an empty initial read or the setup should not depend on cross-container ordering.

**Q: When would you use a startup probe in addition to liveness and readiness on the same port?**

A: Liveness probes begin firing as soon as the container starts. An app that takes 40 seconds to open its listener while liveness has a short timeout gets restarted in a loop (CrashLoopBackOff) even though it only needed time, all on the same port. The startup probe fixes this: while it is failing, liveness and readiness are not executed, and once it succeeds it is permanently disabled. The pattern is a relaxed startup probe that covers the boot window (failureThreshold x periodSeconds) plus stricter liveness and readiness that take over only after start is complete. Keep health endpoints distinct from business endpoints where possible so probes do not measure request load.

**Q: How does `terminationGracePeriodSeconds` relate to SIGTERM and SIGKILL, and what do preStop and postStart hooks do?**

A: On deletion, the kubelet runs the `preStop` hook if present (a script or HTTP GET), then sends SIGTERM to pid 1, waits up to `terminationGracePeriodSeconds` (default 30), then sends SIGKILL if the process is still alive. Exit code 143 (128 + SIGTERM) means the app caught the signal and shut down gracefully; exit code 137 (128 + SIGKILL) means it was force killed. `preStop` is the right place to drain connections, deregister from discovery, and flush state, and `terminationGracePeriodSeconds` should exceed the app's worst-case drain time. `postStart` runs when the container is created but has no ordering guarantee versus the entrypoint, so it can race with the main process and is best kept idempotent.

**Q: What does an exit code of 137 tell you, and how do OOMKilled and CrashLoopBackOff relate?**

A: Exit code 137 = 128 + 9 (SIGKILL). The most common cause is the kernel OOM killer, which the kubelet reports as `OOMKilled` when the container exceeds `resources.limits.memory`. Exit 143 is a graceful SIGTERM; exit 1 is an ordinary application error. `restartPolicy: Always` (required by Deployments) restarts the container, not the Pod. When a container keeps restarting, the kubelet applies exponential backoff and the status shows `CrashLoopBackOff`. The two are related but distinct: OOMKilled is a resource cause (fix limits/compute the leak), CrashLoopBackOff is the observed symptom of repeated failures of any kind, OOM included.

## Gotchas and Traps

**Q: Common trap: I deleted the Pod so the Deployment recreates it, but the crash persists.**

A: Deleting a Pod only recreates the same Pod from the same template; it does not force a new image pull unless the tag changed or `imagePullPolicy: Always`. If the same image/tag is still broken, the replacement crashes identically. To actually change behavior, edit the Deployment (image, args, env) and let a rollout happen, or pin a fresh digest.

**Q: Common trap: a rolling update hangs and both the old and new ReplicaSet stay active.**

A: `kubectl rollout status` hangs when new Pods never become Ready or old Pods refuse to terminate. Typical causes: a failing liveness/readiness probe in the new template, a `PodDisruptionBudget` with `maxUnavailable: 0` that blocks eviction of the old Pods, or a preStop hook longer than the grace period. Diagnose the new Pod's events and logs, check `kubectl get pdb`, then either fix the template or `kubectl rollout undo`.

**Q: Common trap: changing `resources.limits` made the cluster shed traffic or pods get OOMKilled.**

A: The scheduler places Pods on `requests`, not `limits`. If you raise limits but leave requests tiny, Pods can be packed tightly and then constantly pushed into OOM when they run at the limit. Raise requests together with limits so scheduling reflects intended capacity; a Guaranteed QoS Pod gets limits == requests and is placed only on nodes that can honor the full request.

**Q: Common trap: "readiness always restarts my Pod when it is slow."**

A: No. Readiness never restarts; it removes the Pod from Service endpoints. Only a liveness failure restarts the container. A flapping readiness probe gracefully sheds traffic, which is often desirable; a liveness probe with an aggressive timeout will crash-loop you. Make sure each probe checks what it means to: liveness for stuck-dead processes, readiness for endpoints.

**Q: Common trap: I deleted a StatefulSet PVC to reset the data store, and the Pod came back degraded.**

A: The PVC is the sticky, named identity of the StatefulSet ordinal. Deleting it does not delete the data (dynamic PVs keep the data) but it disconnects a bind in a way that can block rescheduling or reattach to a different node. To wipe data intentionally, scale down and delete both the PVC and the PV (or use a StorageClass `reclaimPolicy: Delete`), never delete just one side of the pair.

**Q: Common trap: "maxSurge and maxUnavailable are percentages, so they always fraction."**

A: Percentages are rounded: `maxSurge` rounds up and `maxUnavailable` rounds down. On a 1-replica Deployment, `maxUnavailable: 25%` becomes 0 and `maxSurge: 25%` becomes 1, so you still get a zero-downtime rollout. On a 2-replica Deployment the same percentages produce `maxUnavailable: 0` and `maxSurge: 1`, so at most two Pods run during the update. The rounding rule means small replicas counts behave differently from what one might intuit, so reason in terms of absolute counts for small workloads.

**Q: Common trap: a Job fails permanently, and retries keep burning compute.**

A: Without PodFailurePolicy, every failed Pod is retried until `backoffLimit`, which wastes engine time on deterministic failures (missing env, wrong args). Use PodFailurePolicy `FailJob` for permanent failure codes, or set a low `backoffLimit` plus `activeDeadlineSeconds` so the whole Job has a wall-clock ceiling. Also set `ttlSecondsAfterFinished` so finished Jobs do not accumulate in `kubectl get jobs`.

## Scenario

**Q: A Deployment shows all Pods `CrashLoopBackOff`. How do you debug?**

A: Read the crash log with `kubectl logs <pod> --previous`, describe the Pod for exit codes/OOM, exec into it if runnable, and check the image, liveness probe, and resource limits.

**Q: New Pods stay `Pending`, nodes are healthy, but the Deployment is Unschedulable.**

A: Run `kubectl describe pod` and read the Events, which name the precise reason: Insufficient `cpu` or `memory` (requests exceed node capacity), nodeSelector/affinity mismatch, or a `NoSchedule` taint with no matching toleration. Check requests with `kubectl describe deployment` and node labels/taints with `kubectl describe node`. The fix is one of: add a toleration, loosen affinity, or reduce requests so the scheduler has somewhere to place the Pod.

**Q: A Deployment is stuck in a rollout, both old and new ReplicaSets keep Pods Running.**

A: Confirm the state first: `kubectl rollout status`, `kubectl rollout history`. If the new RS Pods never turn Ready, read their probes and logs (`describe pod`, `logs --previous`). If they are Ready but old Pods stay `Terminating`, look for a PDB that blocks eviction or long `terminationGracePeriodSeconds`. Unblock according to cause, or return to the last good revision with `kubectl rollout undo deployment/<name>`.

**Q: An image pull is failing with `ImagePullBackOff` and `imagePullPolicy: Always` is set.**

A: `Always` queries the registry on every Pod creation, so a deleted tag or purged registry repo fails immediately while "latest" sits stale. Distinguish failures from `kubectl get events`: `manifest unknown`/`404` is a missing tag (pin a `sha256` digest or a real tag), while `401/403` is credentials (verify `imagePullSecrets`). Verify resolve locally with `docker pull` or `crane` before re-applying.

## Commands and Quick Reference

| Command | What it does |
|---------|--------------|
| `kubectl rollout status deployment/nginx` | Wait for a rollout to finish, failing if it times out. |
| `kubectl rollout status deployment/nginx --timeout=90s` | Wait with a timeout to fail fast in CI. |
| `kubectl rollout undo deployment/nginx --to-revision=3` | Return to a previous revision of the pod template. |
| `kubectl rollout restart deployment/nginx` | Start a new rollout by changing the pod template hash. |
| `kubectl rollout history deployment/nginx` | List revisions with their change-cause. |
| `kubectl get pod <pod> -o yaml` | Inspect the full spec: labels, ownerRef, probes, limits, status. |
| `kubectl describe pod <pod>` / `kubectl describe deployment <name>` | Get the event stream and container statuses for the Pod. |
| `kubectl get events --sort-by=.metadata.creationTimestamp` | Cluster-wide events (scheduling, pulls, health) in time order. |
| `kubectl top pod/<pod>` | Pull current CPU/memory vs requested limits. |
| `kubectl logs <pod> --previous` | Read the crash log of the previous container instance. |
| `kubectl get endpoints <service>` | See which Pod IPs are in the Service endpoints. |
| `kubectl get ds --all-namespaces` | List DaemonSets and their per-node Desired/Ready counts. |
| `kubectl get jobs` | List Job states and Completions/Active versus `spec.completions`. |

## True/False

- Deployment selector is immutable after creation. (True)
- A failing readiness probe restarts the container. (False - it removes the Pod from endpoints.)
- Exit code 137 always means the container was OOM killed. (False - 137 is SIGKILL; OOM is one common cause.)
- A startup probe, once it succeeds, keeps running forever. (False)
- An init container failure prevents all main containers from starting. (True)
- Deleting a Pod does not make the Deployment pull a new image unless the tag or imagePullPolicy changed. (True)

## Related

- [Revision - Workloads](../revision/workloads.md)
- [Lesson 8 - StatefulSets](../docs/03-workloads/lesson-08-statefulsets.md)
- [Lesson 6 - Pods, ReplicaSets, and Deployments](../docs/03-workloads/lesson-06-pods-replicasets-and-deployments.md)
- [Lesson 7 - Deployments and Rollout Strategies](../docs/03-workloads/lesson-07-deployments-and-rollout-strategies.md)
- [Lesson 9 - DaemonSets](../docs/03-workloads/lesson-09-daemonsets.md)
- [Lesson 10 - Jobs and CronJobs](../docs/03-workloads/lesson-10-jobs-and-cronjobs.md)
- [Lesson 11 - Init Containers](../docs/03-workloads/lesson-11-init-containers.md)
- [Lesson 12 - Advanced Stateful Workloads](../docs/03-workloads/lesson-12-advanced-stateful-workloads.md)

[Back to Interview Index](README.md)