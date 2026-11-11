---
title: Interview - Storage
module: 05 Storage
status: Complete
tags: [interview, storage, pv, pvc, storageclass, csi]
---

# Interview - Storage

## Beginner

**Q: What is a PersistentVolume?**

A: A cluster-scoped storage object representing a piece of storage (disk, NFS share, cloud volume) that outlives Pods.

**Q: What is a PersistentVolumeClaim?**

A: A request for storage, like a Pod requests CPU/memory. The PVC binds to a PV that satisfies its capacity and access mode, or to a dynamically provisioned PV from a StorageClass.

**Q: What is the difference between a Pod volume and a PersistentVolume?**

A: A Pod volume is created and destroyed with the Pod and is namespace-scoped. A PersistentVolume is a cluster-level resource with its own lifecycle that is independent of any single Pod, which is what lets it survive restarts and be shared or reused.

**Q: What is a StorageClass?**

A: A template that describes how storage should be provisioned dynamically: the provisioner (CSI driver), parameters such as type or tier, reclaim policy, volume binding mode, and whether volumes can be expanded. A PVC that names a StorageClass triggers dynamic provisioning.

## Intermediate

**Q: What are the three access modes?**

A: ReadWriteOnce (RWO, single node read-write), ReadOnlyMany (ROX, many nodes read-only), and ReadWriteMany (RWX, many nodes read-write, e.g. NFS).

**Q: How does dynamic provisioning work?**

A: A PVC with `storageClassName` triggers the CSI provisioner defined in that class to create the volume and a matching PV automatically.

**Q: Why does a PVC stay Pending?**

A: No matching PV exists (capacity/access mode), a StorageClass is missing or can not provision, or `volumeBindingMode: WaitForFirstConsumer` is waiting for a Pod. Check `kubectl describe pvc` and storage events.

**Q: What are the reclaim policies and what does each do?**

A: `Retain` keeps the volume (and its cloud disk) after the PVC is deleted - data survives but must be cleaned manually. `Recycle` runs a scrub job and makes the volume available again (deprecated). `Delete` deletes the underlying storage object when the PVC is deleted, which is the default for most dynamic provisioners.

**Q: What does `volumeBindingMode: WaitForFirstConsumer` change?**

A: Binding is delayed until a Pod that uses the PVC is scheduled. `Immediate` binds at PVC creation even before any Pod exists. `WaitForFirstConsumer` lets the scheduler pick a node first, which is required for local volumes and often best for topology-aware cloud disks so the volume is created in the same zone as the Pod.

**Q: What is a default StorageClass and why does it matter?**

A: Each cluster can mark one StorageClass as the default with the annotation `storageclass.kubernetes.io/is-default-class: "true"`. A PVC that omits `storageClassName` then binds against that class, so users do not need to name one. If two classes are marked default the admission controller rejects PVCs without an explicit class to avoid ambiguity.

## Advanced

**Q: What are the PV lifecycle phases and how does a PV move between them?**

A: `Available` (bound to no PVC), `Bound` (claimed by a PVC), `Released` (the PVC was deleted and the reclaim policy left the data, e.g. `Retain`), and `Failed` (an automatic reclaim, such as `Recycle` or `Delete`, could not complete). A Released PV is not reusable until the data is cleaned manually and the object is recreated.

**Q: Walk through the Retain-then-delete flow. What happens to an orphaned volume?**

A: Deleting a PVC with a `Retain` policy does not delete the backing disk: the PV moves to `Released` and keeps a `claimRef` pointing at the deleted claim. To release or reuse the storage you must clean up the data on the disk, then delete the PV and finally release the cloud disk. Because the claim is gone but the PV never becomes `Available` again, it is "orphaned" - usable neither to bind new claims nor to rebind on its own. For an orphaned cloud disk you should verify the root cause: intentional retention from `Retain`, or a leaked provisioned volume left over after a `Delete` operation failed.

```bash
kubectl get pv -o wide
kubectl patch pv <name> -p '{"metadata":{"finalizers":null}}'
```

**Q: What are the valid volume modes, and where do they differ?**

A: `Filesystem` mode mounts a filesystem device (the only mode before Block volumes) and presents a directory path to the Pod. `Block` mode presents the raw block device (a /dev node) for applications that manage their own filesystem, such as databases or specialized storage engines. Not all CSI drivers support Block; volume mode is selected in the PVC/mount.

**Q: How is `volumeAttributesClassName` different from StorageClass parameters?**

A: StorageClass `parameters` are provisioning-time settings that are baked into the volume when `CreateVolume` runs. `volumeAttributesClassName` on a PVC instead references a separate `VolumeAttributesClass` object carrying a `driverName` and `attributes` block that the CSI driver can consume to set or adjust attributes on the volume, including while the volume is in use. In practice StorageClass parameters cover most setups; VolumeAttributesClass is the escape hatch for driver attributes that should not force a re-create or provision a different class.

**Q: How does the StorageClass drive provisioning with CSI?**

A: The PVC carries the StorageClass name. The storage controller reads the provisioner and parameters from the class and asks the CSI driver to create the volume (`CreateVolume`). The driver requests the volume from the backend (EBS, NFS, Ceph, etc.) and returns a handle; Kubernetes builds the PV, binds the claim, and the node mounts it once a Pod is scheduled.

**Q: How do local PersistentVolumes work and what constraints do they impose?**

A: A local PV attaches to a dedicated directory that physically exists on a single node, so it needs `nodeAffinity` in the PV spec and typically `volumeBindingMode: WaitForFirstConsumer` on the class so scheduling is decided before binding. They are fast and cheap, give single-node access only, and the data disappears if the node is lost, so most deployments pair them with durability controls elsewhere in the plan.

```bash
kubectl get pvc,pv -o wide
kubectl describe node <node>
```

**Q: What protects a PVC from being deleted while a Pod is still using it?**

A: The `kubernetes.io/pvc-protection` finalizer. The PVC cannot be removed while a Pod that references it is still running; the controller must wait for the Pod to be gone or its owner removed. A PVC still in use stays in `Terminating` (see the Advanced question on stuck Terminating PVCs above).

**Q: Walk through the end-to-end mount order when a Pod starts.**

A: Kubelet resolves all volumes under `spec.volumes` first, then for each one it ensures the storage is available: for PVCs backing cloud disks the attach operation runs, then the CSI driver stages and publishes the volume on the node, and only then is the mount placed at the container `mountPath`. This all happens before the container entrypoint runs. `subPath` mounts a single file or directory from a volume instead of the whole volume, and `subPathExpr` resolves the subpath from environment variables. A mount marked `readOnly: true` on the `volumeMount` is exposed read-only even if the underlying volume and PV support writes; ConfigMap and Secret mounts are always read-only by default.

**Q: How is storage capacity managed and how can you expand a volume?**

A: Capacity is declared as the PVC request size under `resources.requests.storage`. To expand, set `allowVolumeExpansion: true` on the StorageClass, grow the PVC request, and the controller expands the volume through the CSI driver (`ControllerExpandVolume` then `NodeExpandVolume`). Not every driver supports resize, and a Filesystem-mode volume usually needs the Pod restarted so the kubelet can remount and grow the filesystem.

**Q: What is the CSI schema that maps a driver to nodes and capabilities?**

A: `CSIDriver` and `CSINode` objects hold the driver name, attach/expand and mountable capabilities, and the topology keys the scheduler must honor when a claim uses `WaitForFirstConsumer`. The scheduler reads those topology constraints to place the Pod on a node that can reach the volume.

**Q: What role does a VolumeAttachment play, and does every volume use one?**

A: A `VolumeAttachment` records that a specific CSI driver has attached a specific volume to a specific node; its status carries the device path. It is created by the attach controller and consumed by kubelet during mount. Not every volume needs one: `hostPath`, `local`, and `emptyDir` volumes have no attach step, while cloud block volumes are attached before they are staged and mounted.

## Gotchas

**Q: True or False: Deleting the PVC always frees the underlying storage.**

A: False. That is only true with a `Delete` reclaim policy. `Retain` leaves both the PV and the cloud volume in place (orphaned) until an admin cleans up and deletes them manually. Confirm the reclaim policy before tearing down workloads.

**Q: True or False: Any PV whose access modes include the claim's mode can bind to it.**

A: False. The binder requires the claim's access modes to be a subset of the PV's modes, and also matches capacity, volume mode, and node affinity. Multiple PVs can satisfy a claim; the binder picks one deterministically but not predictably, so do not write code that relies on a specific PV being chosen.

**Q: True or False: "RWX" works on any volume type.**

A: False. The claim is only honored if the underlying backend (CSI driver or in-tree plugin) actually supports multi-node access. Cloud block storage (EBS, Azure Disk) is effectively RWO-only even if it accepts the mode. NFS, CephFS and other shared filesystems provide real RWX. Declaring a mode on a disk does not make it capable.

**Q: True or False: A reclaim policy of `Recycle` safely wipes and returns volumes to the pool.**

A: False. Recycle is deprecated and its "scrub" is a simple script, not a guarantee to securely purge data; it also only works with a limited set of basic volume plugins. Treat it as a legacy leftover and use `Delete` or `Retain` instead.

**Q: True or False: A PV in `Released` state can be re-bound by a new claim automatically.**

A: False. A `Retain` PV moves to `Released` and never returns to `Available` on its own; new claims only bind to `Available` PVs. You must clean the data, delete the old PV, and recreate it before it can be used again.

## Commands and Quick Reference

| Task | Command |
|------|---------|
| List PVs and PVCs | `kubectl get pv,pvc` |
| Inspect PVC details | `kubectl describe pvc <name>` |
| PV details | `kubectl describe pv <name>` |
| List StorageClasses | `kubectl get storageclass` |
| Show a StorageClass YAML | `kubectl get storageclass <name> -o yaml` |
| Delete a PVC (PV stays if `Retain`) | `kubectl delete pvc <name>` |
| Remove a stuck PVC finalizer (last resort) | `kubectl patch pvc <name> -p '{"metadata":{"finalizers":null}}'` |
| List CSI nodes and capabilities | `kubectl get csinodes` |
| List active attachments | `kubectl get volumeattachment` |

## Scenario

**Q: A StatefulSet database restarts and loses its data. What went wrong?**

A: Likely no `volumeClaimTemplates` (stateless PVC), or the PVC was deleted with the wrong reclaim policy. Each replica should mount its own persistent PVC; use `Retain` or backups for critical data.

**Q: After a node is deleted, a service relies on a local PV and its Pod can't be scheduled.**

A: The PV is bound to that node's `nodeAffinity`, so the scheduler cannot place the Pod anywhere else. The storage (disk) is gone with the node. If data was persisted on the machine before, restore from backup or external replication. If it was a scratch local disk, request a network-backed StorageClass instead, or plan for the recovery time objective of `WaitForFirstConsumer` local volumes.

**Q: A PVC is expanded from 100Gi to 200Gi but the Pod still sees the old size.**

A: Check that `allowVolumeExpansion: true` exists on the StorageClass and the CSI driver supports expansion. After the PV/PVC report the larger size, the filesystem must be resized, which usually requires the Pod to be restarted (or the volume remounted) so the kubelet re-runs the expand and the container sees the new capacity.

**Q: A 3-replica StatefulSet writes to one shared volume but the app shows corrupt data.**

A: The volume is likely RWO bound to one node, so the other replicas cannot really mount it concurrently - "shared" is an illusion. For multi-writer workloads use an RWX-capable class (NFS, CephFS) or give each replica its own volumeClaimTemplate. Confirm the backend actually supports multiple nodes before changing the access mode.

**Q: A ProvisioningFailed event appears with an "invalid template" error for a newly created claim.**

A: The StorageClass parameters or CSI driver version moved to an incompatible combination. Verify `kubectl get storageclass <name> -o yaml` against the CSI version, check the provisioner sidecar logs, and re-apply the class if the driver name or parameters changed. The claim stays Pending until the class can provision, so inspect the StorageClass and provisioner events to find the exact reject reason.

## Related

- [Revision - Storage](../revision/storage.md)
- [Lesson 19 - Persistent Storage (PVs, PVCs, and StorageClasses)](../docs/05-storage/lesson-19-persistent-storage-pv-pvc-sc.md)
- [Module 05 - Storage index](../docs/05-storage/README.md)

[Back to Interview Index](README.md)