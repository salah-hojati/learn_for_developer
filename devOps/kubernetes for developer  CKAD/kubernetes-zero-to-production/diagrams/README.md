# Diagrams

Architecture and concept diagrams for the lessons.

## Purpose

Visual explanations of Kubernetes components, flows, and topologies. Diagrams live as ASCII art inside lessons and as standalone files here for larger or reusable visuals.

## Conventions

- File naming: `topic-name.txt` for ASCII diagrams, `topic-name.svg` for image diagrams
- Organize by module
- ASCII diagrams use monospace-safe box drawing characters that render on GitHub
- Every diagram explains the module and lesson it belongs to

## Diagrams

Standalone ASCII diagrams for larger or reusable visuals. Organized by module; each is also embedded (or linked) from the relevant lesson.

| File | Covers |
|------|--------|
| [control-plane-architecture.txt](control-plane-architecture.txt) | Control plane and worker node components |
| [request-flow.txt](request-flow.txt) | Data path from kubectl to a Pod, and external traffic flow |
| [deployment-rollout.txt](deployment-rollout.txt) | Rolling update, rollback, pause, history |
| [service-and-ingress.txt](service-and-ingress.txt) | Service selectors, Endpoints, Ingress routing |
| [storage-provisioning.txt](storage-provisioning.txt) | PV/PVC/StorageClass dynamic provisioning lifecycle |
| [gitops-argocd.txt](gitops-argocd.txt) | ArgoCD GitOps pull model and drift reconciliation |
| [01-fundamentals-container-anatomy.txt](01-fundamentals-container-anatomy.txt) | Container anatomy: namespaces, cgroups, image layers, containerd/CRI |
| [02-architecture-control-plane-worker.txt](02-architecture-control-plane-worker.txt) | Control plane plus worker node internals, request flow |
| [02-scheduling-and-affinity.txt](02-scheduling-and-affinity.txt) | Scheduler filtering/scoring, node affinity, taints/tolerations, priority/preemption, topology spread |
| [03-workloads-pod-lifecycle.txt](03-workloads-pod-lifecycle.txt) | Pod lifecycle phases, Deployment rollout, StatefulSet ordinals, DaemonSet, Jobs |
| [04-networking-cni-service-ingress.txt](04-networking-cni-service-ingress.txt) | CNI, Pod IPs, kube-proxy (iptables/IPVS), Service, Ingress controller traffic flow |
| [04-network-policy.txt](04-network-policy.txt) | NetworkPolicy default-deny, ingress/egress rules, DNS egress exception |
| [05-storage-pvc-csi-pv.txt](05-storage-pvc-csi-pv.txt) | PV/PVC lifecycle, CSI driver, StorageClass, reclaim policies |
| [06-configuration-configmap-secret-qos.txt](06-configuration-configmap-secret-qos.txt) | ConfigMaps, Secrets, resource requests/limits, QoS classes |
| [06-resource-qos.txt](06-resource-qos.txt) | QoS eviction order, LimitRange, ResourceQuota, node pressure |
| [07-security-rbac-sa-podsecurity.txt](07-security-rbac-sa-podsecurity.txt) | RBAC flow, ServiceAccount tokens, Pod Security Standards |
| [07-rbac-flow.txt](07-rbac-flow.txt) | User/SA to Role/ClusterRole/RoleBinding, admission mutate/validate order |
| [08-observability-prometheus-loki-probes.txt](08-observability-prometheus-loki-probes.txt) | Prometheus stack, Loki logging, probes and health checks |
| [09-helm-release.txt](09-helm-release.txt) | Helm chart structure, release lifecycle, upgrade/rollback |
| [11-operator-reconciliation.txt](11-operator-reconciliation.txt) | CRD registration, informer/reconcile loop, owned resources, finalizers |
| [12-hpa-velero-dr.txt](12-hpa-velero-dr.txt) | HPA/PDB scaling, VPA/Cluster-autoscaler, Velero backup/restore, canary delivery |
| [13-debug-funnel.txt](13-debug-funnel.txt) | Top-down debugging funnel, ephemeral containers, node pressure |
| [14-cka-workflow.txt](14-cka-workflow.txt) | CKA/CKAD/CKS exam workflow, debugging toolkit, time-boxing & STAR |

## ASCII Example

```text
+---------------------+
|   Control Plane     |
+---------------------+
        |
        v
+---------------------+
|   Worker Node       |
+---------------------+
```

[Back to Repository Home](../README.md)
