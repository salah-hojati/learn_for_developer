---
title: Interview - Architecture
module: 02 Architecture
status: Complete
tags: [interview, architecture, control-plane, nodes, kubelet, scheduler]
---

# Interview - Architecture

## Beginner

**Q: What does the API Server do?**

A: It is the front door to the cluster. It authenticates and authorizes requests, runs admission control, and persists objects to etcd. It is the only component that talks directly to etcd.

**Q: What does the scheduler do?**

A: It watches for Pods with no node assigned and binds them to nodes by filtering (capacity, taints, affinity) then scoring candidates.

**Q: What is etcd, and what does it store?**

A: etcd is the cluster's backing key-value store. It stores every API object (Pods, Services, ConfigMaps, Secrets, CRDs) plus lease/heartbeat data. The API server is the only client that talks to it, and it is typically deployed as a 3- or 5-member cluster using Raft for consistency.

**Q: What is the kubelet responsible for?**

A: It is the node agent. It registers and watches its node, receives Pod specs (via the API server or as static manifests), creates Pods through the CRI, runs probes, mounts volumes, reports node status and resources, and enforces the node's resource allocation.

## Intermediate

**Q: What happens when you type `kubectl run nginx --image=nginx`?**

A: kubectl POSTs a Pod manifest to the API Server; the API Server authenticates and authorizes (RBAC), runs admission controllers, and stores the Pod in etcd. The scheduler sees the Pending Pod, picks a node, and binds it. The kubelet on that node sees the bound Pod, asks the CRI (containerd) to pull the image and start the container via runc, then reports status back.

**Q: A node goes offline. What happens to its Pods?**

A: The node controller marks the node NotReady after the grace period (default 40s for `node-monitor-grace-period`). Pods on it are evicted after `pod-eviction-timeout` (default 5m). The ReplicaSet controller schedules replacement Pods on healthy nodes, and the EndpointsController removes the dead Pod IPs from Services.

**Q: What is the role of kube-proxy?**

A: It maintains network rules (iptables/ipvs) that implement Services, translating ClusterIP:port into backing Pod IPs (DNAT).

## Advanced

**Q: How does etcd elect a leader and what happens when the leader fails?**

A: etcd uses the Raft consensus protocol. Members vote, and a candidate wins when it collects a majority. All writes go through the current leader, which replicates them to a quorum of members before acknowledging. If the leader fails or a member loses contact, a new election starts and the cluster must again make a quorum (majority is enough; a non-voting minority is fine as long as more than half remain reachable). A lost leader does not stop reads for the surviving majority, but anything requiring a write (e.g., a new Pod) blocks until quorum is reestablished.

**Q: What is the quorum math for a 3-member and a 5-member etcd?**

A: Quorum = floor(N/2) + 1. For 3 members, quorum is 2, so you can lose 1 member only. For 5 members, quorum is 3 and you can lose 2. That is why an odd number matters: with 2 members you can lose 0 and a split-brain becomes two minorities; with 4 members you still do not gain in failure tolerance (you lose one) while the latency cost goes up. In practice run 3 for most clusters, 5 for large control planes.

**Q: What is the difference between a `list` and a `watch`, and what does `resourceVersion` mean?**

A: `list` fetches a full snapshot of current objects with a `resourceVersion`. A `watch` reuses that `resourceVersion` to stream subsequent change events (Added/Modified/Deleted). Since a watch can drop events (restarts, errors), clients use a versioned watch and the API server returns a `410 Gone` if a resourceVersion is too old, telling the client to re-list and start a fresh watch. Controllers and informers do exactly this.

**Q: What is a static Pod and a mirror Pod?**

A: A static Pod is defined in a directory (default `/etc/kubernetes/manifests`) and read by the kubelet directly, without going through the API server. The kubelet creates a reflected, read-only copy of it in the API, called a mirror Pod (annotated `kubernetes.io/config.mirror`). You cannot `kubectl delete` a static Pod through the normal path; you remove the manifest file and the kubelet removes both. Static pods have no owner references, so they are never evicted by the kubelet's node lifecycle.

**Q: In which order do admission controllers run?**

A: A request is authenticated and authorized first, then runs the admission chain, and within it all mutating webhooks run in the configured `admissionregistration.k8s.io` order, then the object is validated (schema + ValidatingAdmissionWebhooks) in order too. Mutating webhooks may run sequentially, and the API server re-runs defaulting after mutation. Since a webhook can change the object others see, each admission controller defines a deterministic order; that order is surfaced in the API server's dynamic admission DAG (the `order` field on `ValidatingWebhookConfiguration`/`MutatingWebhookConfiguration`). Mutating is *always* completed before validating.

**Q: What are the two phases of the scheduler, and what is an extender?**

A: In the Filter phase, the scheduler removes nodes that cannot host the Pod (unsatisfied `nodeSelector`, taints/tolerations, affinity, `unsatisfied affinity`, or resource insufficiency — this is the Feasibility/Predicates step). In the Score phase it ranks the remaining nodes (balanced resource allocation, image locality, rack/zone balance). You can attach a scheduler extender (webhook) either after Filter to prune more or after Score to bias the ranking; note the scheduler's HTTP API lets an extender filter and prioritize but the actual binding is still done by the scheduler core.

**Q: What is the difference between `controller-manager` and `kube-scheduler`?**

A: Both run as controllers on the control plane. The kube-scheduler only assigns Pods to nodes (it produces a new `NodeName` and posts a Binding). Everything else — observing the desired vs current state and acting — is done by the kube-controller-manager: it runs many controllers behind it (ReplicaSetController, DeploymentController, NodeController / lifecycle and eviction, EndpointsController, ServiceAccount/Token controllers, etc.), each watching its own resources. Most also use a leader-election lease so that replicated instances pick a single active leader.

**Q: What happens on a graceful node shutdown and what are `PreStop` / `terminationGracePeriodSeconds`?**

A: When a node shuts down, the kubelet (with the `GracefulNodeShutdown` feature) identifies terminal pods and marks them with `shutdown-grace-period` priority, and for each, runs `preStop` hooks and waits for `terminationGracePeriodSeconds` (default 30s) before the container runtime sends SIGKILL; the kubelet also drains workloads that can be stopped. `preStop` gives the process a chance to unregister, flush, and clean up before SIGTERM crosses. The timer applies per-container and is not global; a long-running graceful drain therefore needs the pod's `terminationGracePeriodSeconds` raised.

**Q: What are the PodSecurityContext fields and what could go wrong with them?**

A: The `securityContext` (or `PodSecurityContext`) on a Pod covers `runAsUser`, `runAsGroup`, `fsGroup`, `runAsNonRoot`, `seccompProfile`, `allowPrivilegeEscalation`, and `readOnlyRootFilesystem`. A classic trap is setting `runAsNonRoot` to true on a Pod whose image runs as root anyway — the runtime lists it as not non-root and fails to start. `fsGroup` creates a group change on the volume and is irrelevant to processes; people confuse it for an actual user. `readOnlyRootFilesystem` alone does nothing to stop writes to the /dev tmpfs or an overlay.

**Q: What happens when a webhook refuses a request during admission?**

A: The request is rejected atomically: the API server does not create or modify the object, the error is returned to the caller, and any mutation the webhook already applied is discarded alongside the failed attempt. That is why webhooks must be idempotent and deterministic, and why a Pod stuck in Pending with a failed create is a diagnostic clue — check `kubectl get events` for the `FailedCreate` reason and fix or remove the webhook, then retry.

**Q: What is API Priority and Fairness (APF)?**

A: APF replaced the coarse `--max-requests-in-flight` / `--max-mutating-requests-in-flight` flags with per-flow schemas and queues. Requests are classified into priority levels (e.g., `system`, `leader-election`, `workload-high`, `catch-all`) and served from queues with a bounded number of "seats"; a single loud client cannot starve others. `kubectl get --raw /debug/api_priority_and_fairness/dump_priority_levels` shows the current queue state while debugging throttling.

**Q: What are the leader-elected singletons on the control plane?**

A: kube-controller-manager, kube-scheduler, and cloud-controller-manager each run leader-elected: one holds a Lease (`coordination.k8s.io`) and works, the others wait and take over on failure. They give failure tolerance, not throughput. The only component you horizontally scale for request handling is the stateless API server.

**Q: "The scheduler only needs CPU and memory to place a Pod." True or False?**

A: False. It also checks taints/tolerations, node and pod affinity, `nodeSelector`, topology spread constraints, inter-pod affinity, and the kubelet's actual allocatable vs capacity. Even a seemingly tiny pod is filtered out if the kubelet's reserved system resources (`--system-reserved` and `--kube-reserved`) shrink the allocatable below the request.

**Q: "A taint with `NoExecute` evicts a Pod immediately."**

A: No. `NoExecute` tells the scheduler not to place new Pods and starts the event clock on existing ones, but those Pods are only evicted after `tolerationSeconds` expires (checked every cycle). For `node.kubernetes.io/not-ready:NoExecute` the default is 300 seconds. So a node that is NotReady does not immediately kill its Pods; the kubelet-node cadence plus `pod-eviction-timeout` (5m) for the node controller drives the actual removal.

**Q: "When the API server dies, the scheduler cannot run."**

A: False. The scheduler is stateless: it keeps no local state of its own, and on reconnection it re-lists from the current `resourceVersion` and continues. The trap is thinking the scheduler stores the "best node" and that the API death loses work; what actually happens is the control-plane binaries block on their watch. Restart the API server and the watch re-establishes.

**Q: "Every node has a `kubelet`; a single kubelet can be pointed at two control planes."**

A: Trap. A kubelet registers exactly one Node object on one API server at a time, using its `--kubeconfig`. Pointing it at a second API server is not a supported trick; a multi-cluster setup needs separate agent processes. This trap shows up when people try to sync storage or launch static manifests on a "shared" node.

**Q: "A `PreStop` hook always runs before `SIGTERM`."**

A: Yes, but only when Kubernetes can do it: the hook group (hook + terminationGracePeriodSeconds) runs before the container receives SIGTERM. The trap: PreStop does not guarantee *completion* before SIGTERM — it competes with the grace period and SIGKILL can end it mid-flight. So plan for the process to be able to handle being killed even after a short hook.

**Q: "A `PodDisruptionBudget` protects a Deployment from losing replicas."**

A: Half true. A PDB does not protect against node loss (`node.kubernetes.io/not-ready` taint eviction is *not* a PDB event), against the ReplicaSet deleting a bad pod, or against a forced eviction. It only constrains voluntary disruptions like `drain` and `scale-down`. During a crash or an OOM the PDB is invisible.

**Q: "A scheduler with one replica and a controller-manager with three replicas is fine."**

A: Yes — this is intended. Only one scheduler runs (`--leader-elect`), and the controllers-manager elects one leader; extra replicas are idle hot standby. The trap: the scheduler and controller are both `leader-elected`, so scaling a control-plane to many replicas does not parallelize scheduling the way horizontal scaling of the API server does.

## Scenario

**Q: The control plane is down and etcd lost quorum. What do you do?**

A: Restore etcd from the latest backup to one member, let it form quorum again, then restart the remaining control plane components. Keep etcd backups current and test restores regularly.

**Q: A Node reports `NodeNotReady` but the kubelet process is running. What do you check first?**

A: For a node, look at `kubectl describe node` conditions first, then on the node check `journalctl -u kubelet`, the container runtime socket (`/var/run/containerd/containerd.sock`) health, and the CNI/network plugin. A hanging overlay or disk can keep kubelet running while Pods cannot start, so also check `/var/lib/kubelet` disk space and mount health.

**Q: You `kubectl cordon` a node, but the ReplicaSet still matches. Why do the Pods stay Running?**

A: `cordon` only marks the node unschedulable; it does not evict existing Pods. Eviction requires the `node.kubernetes.io/not-ready` taint's `tolerationSeconds` to expire (default 300s) or a `kubectl drain --ignore-daemonsets`, and a `PodDisruptionBudget` (`minAvailable`/`maxUnavailable`) can still block eviction. Check `kubectl get pdb --all-namespaces` and the node's taints; if a PDB blocks you, approve or temporarily disable it, then drain.

**Q: You restart a Controller (informer) and it "missed" an event. Should you worry?**

A: No, that is the design. Informers `list` and then `watch` from a `resourceVersion`; if a watch is broken the API server returns `410 Gone` and the informer re-lists from the new `resourceVersion`. What you lose is only the affected period, whose state is re-read by the next list. A deeper problem is if you write code that relies on the initial list only and does not keep watching.

## Commands and Quick Reference

| Command | Why you run it |
| --- | --- |
| `kubectl get --raw /readyz?verbose` | Confirm the API server's own health checks (etcd connectivity, authorizer, controller); `?verbose` lists each check individually. |
| `kubectl get events --field-selector reason=FailedScheduling` | See exactly why a Pod was not placed on a candidate node. |
| `kubectl describe node NODE` | Get capacity, allocatable, taints, and the pod list on a single node. |
| `kubectl taint nodes node key=value:NoSchedule` | Temporarily make a node unschedulable for maintenance without deleting it. |
| `kubectl drain NODE --ignore-daemonsets` | Safely evict pods before maintenance; add `--delete-emptydir-data` when emptied volumes are acceptable. |
| `kubectl get nodes -o jsonpath='{.items[*].status.conditions[?(@.type=="Ready")].status}'` | Extract the Ready condition of every node in one expression for a prompt. |
| `kubectl debug node/NODE --image=busybox` | Run a privileged pod on a node to inspect its host network, filesystem, or cgroup state. |
| `kubectl get --raw /metrics` and `kubectl top nodes` | Inspect kubelet and cAdvisor metrics when a node is slow or spiking. |
| `kubectl get pods --all-namespaces --field-selector spec.nodeName=NODE` | See everything a given node is currently running. |
| `crictl ps -a` / `crictl logs` | Inspect containers from the CRI side, below the kubelet's abstraction. |
| `etcdctl endpoint health` / `etcdctl member list` | Verify etcd quorum and member health before any etcd surgery. |
| `kubectl get pdb --all-namespaces` | Find the PDB that is silently blocking a drain/eviction. |

## Related

- [Revision - Architecture](../revision/architecture.md)
- [Lesson 45 - etcd Backup and Restore](../docs/14-certifications/lesson-45-etcd-backup-and-restore.md)
- [Lesson 03 - Worker Node Architecture](../docs/02-architecture/lesson-03-worker-node-architecture.md)
- [Lesson 04 - Pod Priority and Preemption](../docs/02-architecture/lesson-04-pod-priority-and-preemption.md)
- [Lesson 05 - Node Affinity and Anti-Affinity](../docs/02-architecture/lesson-05-node-affinity-and-anti-affinity.md)

[Back to Interview Index](README.md)