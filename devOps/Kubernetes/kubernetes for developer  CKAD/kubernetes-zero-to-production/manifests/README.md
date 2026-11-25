# Manifests

Ready-to-use Kubernetes YAML manifests referenced by the lessons and labs.

## Purpose

Keep every manifest that is explained in a lesson here so it can be reused, referenced, and verified. Manifests in this directory are the source of truth for the documentation.

## Conventions

- File naming: `kebab-case-name.yaml`
- Organize by module and lesson: `NN-lesson-slug/`
- Every manifest is annotated with the lesson and module it belongs to
- Manifests are validated against the Kubernetes version used in the lessons
- Secrets and credentials are never committed

## Layout

```text
manifests/
  README.md
  01-fundamentals/      pod.yaml, multi-container-pod.yaml, namespace-with-quota.yaml
  02-architecture/      affinity.yaml, priority-class.yaml, tolerations.yaml
  03-workloads/         deployment.yaml, replicaset.yaml, statefulset.yaml, daemonset.yaml, job.yaml, cronjob.yaml
  04-networking/        service.yaml, service-with-deployment.yaml, nodeport-service.yaml, ingress.yaml, networkpolicy.yaml, network-policy-ingress.yaml
  05-storage/           pvc.yaml, pvc-deployment.yaml, statefulset-headless-service.yaml, storageclass.yaml, storage-class-retain.yaml
  06-configuration/     configmap.yaml, secret.yaml, limit-range.yaml, resource-quota.yaml
  07-security/          service-account.yaml, rbac.yaml, security-context.yaml
  08-observability/     probes.yaml, probes-all.yaml, servicemonitor.yaml
  09-packaging/         Chart.yaml, values.yaml
  10-gitops/            application.yaml, sealed-secret.yaml
  11-operators/         crontab-crd.yaml, crontab-custom-resource.yaml
  12-production/        hpa.yaml, argo-rollout.yaml, velero-schedule.yaml
  13-troubleshooting/   debug-pod.yaml, stress-pod.yaml, event-exporter-config.yaml
  14-certifications/   etcd-snapshot-backup.yaml, kubeconfig-reference.yaml
```

Manifests are validated against a current Kubernetes release (kind/minikube) and annotated with the module and lesson they belong to. No secrets or credentials are committed.

## Validating

```bash
kubectl apply --dry-run=client -f manifests/03-workloads/deployment.yaml
kubectl apply -f manifests/03-workloads/deployment.yaml
```

[Back to Repository Home](../README.md)
