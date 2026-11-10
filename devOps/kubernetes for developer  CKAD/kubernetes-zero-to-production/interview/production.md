---
title: Interview - Production
module: 12 Production
status: Complete
tags: [interview, production, hpa, node-maintenance, velero, multi-cluster]
---

# Interview - Production

## Beginner

**Q: What does a HorizontalPodAutoscaler do?**

A: It scales the number of replicas of a Deployment/ReplicaSet based on metrics (typically CPU) so the app handles load without manual scaling.

**Q: What is a PodDisruptionBudget?**

A: A policy that limits how many Pods of an app can be voluntarily disrupted at once (via node drains, upgrades, or VPA), protecting availability.

## Intermediate

**Q: How do you safely upgrade a node?**

A: Cordon it so no new Pods schedule, drain it (`kubectl drain <node> --ignore-daemonsets --delete-emptydir-data`) so Pods move elsewhere, upgrade/reboot, then uncordon. PDBs protect critical apps during the drain.

**Q: What does Velero back up?**

A: Kubernetes objects plus one or more volumes (PVC/PV), stored in an object store, and can restore them to a cluster or migrate them.

**Q: When would you need an HPA?**

A: When load varies: you want to scale replicas out automatically for bursts and back down for quiet periods. The HPA uses the Pod's `resources.requests` baseline to compute a utilization percentage, so an accurate baseline is required.

## Advanced

**Q: How does the HPA compute desired replicas?**

A: The controller computes per-metric ratios: `desiredReplicas = ceil(currentReplicas x (currentMetricValue / desiredMetricValue))`, then takes the maximum ratio across all configured metrics and clamps it to `minReplicas`/`maxReplicas`. For CPU/memory the utilization is measured against each Pod's `resources.requests`, which is why an accurate request baseline matters. If the metric is unavailable (no ready Pods), autoscaling does not guess; it keeps the current count.

**Q: What are HPA cooldowns and stabilization windows?**

A: Modern HPA replaces raw cooldowns with per-policy behavior in `spec.behavior`:

```yaml
behavior:
  scaleDown:
    stabilizationWindowSeconds: 300
    selectPolicy: Max
    policies:
      - type: Percent
        value: 100
        periodSeconds: 60
  scaleUp:
    stabilizationWindowSeconds: 0
    policies:
      - type: Percent
        value: 100
        periodSeconds: 15
```

- `scaleDown` defaults to a 300-second stabilization window, so you avoid flapping when load dips briefly.
- `scaleUp` has no stabilization by default and grows fast; `selectPolicy: Max/Min` chooses which policy wins, and `Disabled` freezes a direction.
- Both `Percent` and absolute `Pods` policies are honored.

**Q: What metric types can an HPA consume?**

A: Five API surfaces:

- `Resource` (`metrics.k8s.io` / metrics-server) — CPU, memory against `requests`.
- `ContainerResource` — per-container CPU/memory (v1.27+).
- `Pods` (`custom.metrics.k8s.io`) — a per-pod custom metric like requests/second.
- `Object` (`custom.metrics.k8s.io`) — a cluster object metric such as an Ingress request rate.
- `External` (`external.metrics.k8s.io`) — cluster-external sources like an SQS queue depth.

The HPA controller then aggregates and normalizes each into a utilization ratio.

**Q: How do VPA and HPA coexist?**

A: They scale different dimensions: HPA changes replica count, VPA recommends CPU/memory `requests`. Running both on CPU for the same workload fights (VPA grows requests to reduce utilization, HPA scales out, thrash). The standard pattern: HPA scales replicas using a custom metric, while VPA only tunes resource requests, or VPA runs on a single-pod workload. VPA is not GA; test it before production.

**Q: What is the difference between Cluster Autoscaler and HPA?**

A: HPA horizontally scales Pods inside a cluster; Cluster Autoscaler adds/removes nodes so unschedulable Pods get capacity. They compose: the HPA requests more replicas, they stay Pending, the Cluster Autoscaler provisions a node, then the Pods start. CA needs configurable node groups and cloud provider API access, and it cannot help when Pods are stuck for non-capacity reasons (affinity, taints, quotas).

**Q: PDB `minAvailable` vs `maxUnavailable` math?**

A: With N replicas: `minAvailable: 4` allows up to N-4 voluntary disruptions at once; `maxUnavailable: 1` allows exactly one (equivalent to `minAvailable: N-1`). They cannot both be set. Percent values are converted to absolute numbers with rounding, and on low replica counts the rounding direction matters (e.g., 50% of 5 is 2 or 3 depending on rounding) — verify the effective budget with `kubectl get pdb`. The eviction API refuses an eviction that would violate the budget; during a drain the Pod stays on the node until budget allows.

**Q: What happens during a `kubectl drain` with PDBs?**

A: Drain first cordons the node, then evicts pods one by one through the eviction API. If evicting a pod would drop availability below the PDB, eviction is refused (HTTP 429) and drain blocks until another pod comes back or the budget is restored. DaemonSet pods are only deleted with `--ignore-daemonsets`; pods with `emptyDir` need `--delete-emptydir-data`; unmanaged pods need `--force`. `--disable-eviction` deletes pods directly, bypassing PDB checks — use with care.

**Q: How does Velero schedule and store backups?**

A: `velero schedule` creates periodic backups with a TTL; after TTL the backup and its data are garbage-collected from the object store. `BackupStorageLocation` points at the object store (S3/GCS/Azure), while `VolumeSnapshotLocation` points at the provider for CSI/cloud snapshots or the node-agent (file-level) backups. Remember: Velero restores to the same or a new cluster; it does not back up the ETCD cluster state or cluster-wide RBAC unless you include it.

**Q: What is the difference between volume snapshots and file-level backups?**

A: Cloud/CSI snapshots (snapshot location) replicate the volume block-level, require the provider plugin, and restore whole PVCs. File-level backups use a node-agent per node (restic originally, kopia as the newer default, e.g., Velero 1.15+) and can back up `emptyDir`/hostPath PVCs cheaply but run within the cluster and need namespace permissions. Choose snapshots for block storage DR, file-level for cheap selective file backup.

**Q: What is RTO/RPO and how does backup design affect them?**

A: RPO is the maximum acceptable data loss measured in time (e.g., 1 hour = you can lose at most the last hour). RTO is the time to restore service after a declared disaster. Velero's schedule frequency drives RPO: a daily schedule gives up to ~24h of possible loss, more frequent schedules (and shorter TTLs for pruning) tighten it. RTO is reduced by practicing restores, warm standby (images and manifests pushed ahead of time), and pre-provisioned infrastructure so the restore does not fight for capacity.

**Q: How do you make workloads HA across availability zones?**

A: Deploy multiple replicas with:

```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          topologyKey: topology.kubernetes.io/zone
```

`topologySpreadConstraints` (`topologyKey: topology.kubernetes.io/zone` with `maxSkew: 1`) distributes replicas evenly. Both protect against a whole zone failure. For stateful applications you also need the data plane across zones: use regionally replicated disks (or multi-AZ replication) and ensure the node pool spans zones so a zone loss does not strand the Pods.

**Q: How does multi-cluster failover and DR work?**

A: Two axes: active/active (both clusters serve traffic, load balanced across) and active/passive (warm standby with readiness gates and DNS or geographic failover). GitOps is the source of truth for the manifest: on failover you resync the same App-of-Apps into the standby cluster. Databases are the hard part — use provider-managed replication (RDS, Aurora, Cloud SQL, Spanner) or intercluster backups, and point the standby app at the replicated data. Test RTO/RPO with regular failover drills, then do the blue-green switch of the cluster itself.

**Q: How do you upgrade a cluster one node at a time?**

A: Pick one node: cordon, drain with `--ignore-daemonsets`, watch the new Pods land elsewhere, upgrade the node, uncordon, and move to the next. Drains only complete if PDBs allow the evictions — a PDB that demands availability the cluster cannot satisfy (e.g., `minAvailable: 3` on 3 replicas where one is down) blocks the drain forever, so fix or temporarily lower the budget first. Sequence upgrades across nodes one at a time (maxSurge-style, one node is downscaled before the next) and keep a rollback path for the kubelet/containerd version.

**Q: What is Ingress-canary / blue-green vs canary?**

A: Canary: new version receives a small, gradually increasing percentage of traffic. Blue-green: two full environments, switch all traffic via a Service/Ingress once the new one is healthy — a fast rollback. The takeaway: canary = traffic split, blue-green = environment swap; pick canary when you need metrics-steered release and blue-green when switching whole envs fast is simpler.

## Gotchas

- Set `spec.behavior.scaleDown.stabilizationWindowSeconds` if you see replica churn when load spikes briefly; the default 300s window prevents flapping.
- With multiple metrics, the HPA scales on the metric that produces the MAX ratio; a CPU-only HPA stays quiet during a custom-metric spike.
- Missing CPU metrics (`Unknown`) stall scaling; keep `resources.requests` above observed CPU so utilization is always measurable.
- `maxUnavailable` percentages are rounded; inspect the effective budget with `kubectl get pdb -A` before a maintenance window.
- VPA is not GA: `UpdateMode: Auto` restarts pods to apply recommendations; combining it with an HPA on the same resource metric causes oscillation.
- Node drains can block indefinitely on a PDB that cannot be satisfied; `--force` deletes pods directly and bypasses the eviction API — use it only after lifting the budget.
- Velero does not back up etcd or the control plane by default; cluster-scoped objects and RBAC must be explicitly included.
- Multi-cluster DR requires testing the failover path, not just the backup: verify storage classes and CRDs already exist in the DR cluster before a restore.
- HPA does not react instantly: default sync ~15s, plus metric scrape and stabilization windows; plan SLOs accordingly.

## Commands and Quick Reference

```bash
# HPA
kubectl get hpa -A
kubectl describe hpa myapp
kubectl autoscale deployment myapp --min=2 --max=10 --cpu-percent=70
kubectl edit hpa myapp

# Node maintenance
kubectl cordon <node>
kubectl uncordon <node>
kubectl drain <node> --ignore-daemonsets --delete-emptydir-data
kubectl drain <node> --ignore-daemonsets --delete-emptydir-data --force
kubectl get pdb -A
kubectl describe pdb <name>

# Velero
velero install --provider aws --bucket mybucket ...
velero backup create <name> --include-namespaces <ns>
velero schedule create daily --schedule "@daily" --ttl 720h
velero backup describe <name>
velero restore create <name> --from-backup <backup>
velero get backup-locations
kubectl get backups.velero.io -A

# Scaling and capacity
kubectl top nodes
kubectl top pods -n <ns>
kubectl scale deployment myapp --replicas=5
kubectl get nodes --show-labels | grep topology
```

## Scenario

**Scenario: A node reports DiskPressure and Pods are being evicted. What do you do?**

A: Identify what fills the disk (container logs, images, emptyDir) with `kubectl describe node` and node events, delete unneeded images, cap log storage, shrink volumes, and increase disk capacity. Cordon & evict cleanly before taking the node offline if needed.

**Scenario: You need to replace a node in a 3-node cluster, but a Deployment has `minAvailable: 2` on 3 replicas and one replica is already down.**

A: With only 2 healthy replicas left, an eviction would drop availability below the budget, so the eviction API returns 429 and the drain blocks. Resolution: temporarily widen the budget to `minAvailable: 1` for the maintenance window, or scale the Deployment up to 4 replicas so evicting one leaves 3 running. Rotate the Pod off the node, upgrade/uncordon it, verify, then restore the original PDB and replica count.

**Scenario: Your boss wants a multi-cluster DR for a stateful app. What three non-negotiable parts do you test?**

A: 1) Object restore — Velero restores the app manifests, and the storage classes, CRDs, and namespaces they reference must already exist in the DR cluster or the restore fails. 2) Data — the PVCs must be reachable from the DR cluster through either cloud snapshots or node-agent (restic/kopia) repos in the DR's storage location. 3) Cutover — DNS/Ingress must be redirected only after the DR app passes readiness, and the old cluster must be quiesced to avoid split-brain. Run a full-failover drill at least quarterly and measure the result against the agreed RTO.

## Related

- [Revision - Production](../revision/production.md)
- [Lesson 35 - Building a 3-Tier Web Application](../docs/12-production/lesson-35-building-a-3-tier-web-application.md)
- [Lesson 36 - Horizontal Pod Autoscaler](../docs/12-production/lesson-36-horizontal-pod-autoscaler.md)
- [Lesson 37 - Cluster Upgrades and Maintenance](../docs/12-production/lesson-37-cluster-upgrades-and-maintenance.md)
- [Lesson 38 - Backups and Disaster Recovery with Velero](../docs/12-production/lesson-38-backups-and-disaster-recovery-with-velero.md)
- [Lesson 39 - Multi-Cluster Kubernetes](../docs/12-production/lesson-39-multi-cluster-kubernetes.md)
- [Lesson 40 - Progressive Delivery with Argo Rollouts](../docs/12-production/lesson-40-progressive-delivery-argo-rollouts.md)

[Back to Interview Index](README.md)