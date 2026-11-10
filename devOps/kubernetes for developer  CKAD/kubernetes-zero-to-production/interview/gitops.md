---
title: Interview - GitOps and Packaging
module: Modules 09-11
status: Complete
tags: [interview, gitops, argocd, helm, kustomize, operators]
---

# Interview - GitOps and Packaging

## Beginner

**Q: What is GitOps?**

A: An operating model where Git is the single source of truth for desired state. A controller (ArgoCD) continuously reconciles the cluster to what is in Git, making deployments declarative and reversible. The two core principles are a declaratively described desired state in Git and automated continuous reconciliation toward that state.

**Q: What is a Helm chart?**

A: A packaged set of Kubernetes manifests, using Go templates and values, so one chart can deploy an app with different values per environment.

## Intermediate

**Q: How does ArgoCD detect drift?**

A: It continuously polls Git and compares it to live cluster state. A manual `kubectl edit` makes the app "Out of Sync"; with `selfHeal`, ArgoCD patches it back to the Git state. `--auto-prune` removes resources deleted from Git.

**Q: What is the difference between Helm and Kustomize?**

A: Helm is templating with values and releases (install/upgrade/rollback, third-party charts). Kustomize is a patches/overlay tool with no templating, built into kubectl, adding/merging labels, names, and images over a base.

**Q: What is a CRD?**

A: A CustomResourceDefinition extends the Kubernetes API with a new resource type. The Operator pattern pairs it with a custom controller that reconciles the resource toward desired state (e.g., Prometheus Operator, Argo Rollouts).

## Advanced

**Q: What changed in Helm 3 versus Helm 2?**

A: Helm 3 removed Tiller, the server-side cluster agent. In Helm 2, Tiller held a privileged cluster role and its own release store, a well-known privilege-escalation vector. Helm 3 is client-only: charts are rendered locally and release state is stored as Kubernetes Secrets in the release namespace. There is no cluster-side component, so `helm install`, `upgrade`, and `rollback` need no elevated in-cluster permissions, and the upgrade uses a three-way strategic merge instead of Helm 2's two-way patch.

**Q: What is a Helm release and how does `helm ls` work?**

A: A release is a deployed instance of a chart — a name, namespace, version, and revision pointer to the rendered manifests. `helm ls -n <ns>` lists every release with its status and last deployed revision. `helm status <release>` shows details, and `helm history <release>` gives the revision timeline that `helm rollback` uses to restore an earlier state.

**Q: What is a three-way merge in Helm?**

A: An upgrade compares three states: the live objects, the previous revision's manifests (stored in the release Secret), and the new manifests from the chart. Helm computes a strategic merge patch across all three. The key win over Helm 2: values or keys removed from templates are actually deleted from the live resources instead of silently lingering.

**Q: What are Helm chart hooks and why would you use them?**

A: Hooks are Jobs, Pods, or other resources flagged with annotations, executed at a lifecycle point (`pre-install`, `post-install`, `pre-upgrade`, `post-upgrade`, `pre-rollback`, `post-rollback`, `pre-delete`, `post-delete`, `test`). They run before a release is marked deployed, so you can sequence one-time tasks such as a database migration:

```yaml
annotations:
  "helm.sh/hook": pre-install
  "helm.sh/hook-weight": "5"
  "helm.sh/hook-delete-policy": before-hook-creation,hook-succeeded,hook-failed
```

- `hook-weight`: execution order within a phase, lowest first, default 0.
- `hook-delete-policy`: `before-hook-creation` (default), `hook-succeeded`, or `hook-failed` — controls when the transient resource is cleaned up.
- If a hook fails, the install/upgrade is aborted and the release is marked failed; use `before-hook-creation` so a retry starts from a clean resource.

**Q: How do you work with chart dependencies?**

A: Declare `dependencies` in `Chart.yaml` (`name`, `version`, `repository`), then `helm dependency update` downloads them as `.tgz` files into `charts/`. The parent overrides a subchart's values in its own `values.yaml`:

```yaml
postgresql:
  persistence:
    enabled: false
```

You can also alias a dependency (`alias: postgres-primary`) so the same chart is used twice in one release.

**Q: What is the Helm values precedence order?**

A: For a given chart, lowest to highest: the chart's built-in `values.yaml` defaults, then user values files passed with `-f/--values`, then `--set`/`--set-string`/`--set-file` overrides. Subchart values are scoped under the parent key and are merged down into the child chart, but a subchart never overrides a parent value. So: defaults < override files < CLI `--set`.

**Q: What does the Helm template engine do and what helpers exist?**

A: Charts render with Go `text/template`, plus the sprig function library for helpers such as `index`, `lower`, `trimPrefix`, `printf`, and `coalesce`. The injected objects are `.Values`, `.Release`, `.Chart`, `.Capabilities`, and `.Files`. `include` reuses named templates, and `toYaml` serializes map values:

```yaml
name: {{ include "chart.fullname" . }}
labels: {{- toYaml .Values.labels | nindent 4 }}
```

Because the engine emits raw text into YAML, indentation (`nindent`), quoting (`| quote`), and missing-value defaults are the most common sources of template bugs.

**Q: What is a Kustomize overlay and how do bases work?**

A: A `base` is a plain set of YAML describing the common resources. An `overlay` references the base and applies patches plus transformations, giving environment-specific variants (dev, staging, prod) without duplicating files. In current kustomize the `resources` field lists bases, `patches` apply targeted changes, and `components` share reusable overlays. `kustomize build` renders the final manifest set.

**Q: Strategic merge vs JSON patch — what is the difference in Kustomize?**

A: Strategic merge patches (the default `patch: strategicMerge`) merge maps and merge lists keyed with `patchMergeKey`, so you can modify one field inside a container without rewriting the whole resource. JSON patches (`patches: json`) are ordered RFC 6902 operations (`add`, `remove`, `replace`) against exact paths, and fail if a path does not exist. Use strategic merge for field updates and JSON patch when you must delete a list element by index or explicitly remove a key.

**Q: How do `commonLabels`, `namePrefix`, and `images` transformations work?**

A: Transformations rewrite every matched resource uniformly:

```yaml
resources:
  - ../../base
namePrefix: staging-
commonLabels:
  env: staging
images:
  - name: nginx
    newTag: 1.25
```

- `commonLabels` adds labels to every resource and its selectors — so changing them breaks selector match unless you also change the matching labels.
- `namePrefix` and `namespace` scope resources per environment.
- `images` rewrites image fields by name, ideal for promoting a digest or tag through environments without editing YAML.

**Q: How does ArgoCD synchronize an application?**

A: An ArgoCD `Application` points at a source (Git repo `path` + `targetRevision`, or a Helm chart) and a destination cluster/namespace. Sync is ordered by sync waves: resources annotated `argocd.argoproj.io/sync-wave: "5"` are applied after wave 0, and within a wave resources are ordered by kind priority (Namespace, CRD, then Deployment, then Service). Sync behavior is configured in `syncPolicy`:

```yaml
spec:
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - Validate=false
      - CreateNamespace=true
      - PrunePropagationPolicy=foreground
```

- `automated` syncs continuously; `prune` deletes resources removed from Git; `selfHeal` reverts out-of-band live changes.
- `syncOptions` tune how sync applies (`Validate=false`, `CreateNamespace=true`, `PrunePropagationPolicy`, `Replace=true`, `ServerSideApply=true`).

**Q: What does `PrunePropagationPolicy` do?**

A: It controls deletion propagation when pruning resources that were removed from Git. `foreground` blocks until dependent objects are deleted, `background` (default) lets the Kubernetes garbage collector proceed, and `orphan` leaves descendants behind. It matters when an app shares subresources with other apps or runs finalizers.

**Q: What are ArgoCD Application, AppProject, and the App of Apps pattern?**

A: `Application` is one source-to-cluster sync unit. `AppProject` is a scoping boundary that restricts destination clusters/namespaces, allowed source repos, and cluster-scoped resources, and defines roles and RBAC. The "App of Apps" pattern is a bootstrapping approach: a single root Application renders manifests whose `kind: Application` describe the rest of the fleet, so one Git path declaratively manages a full portfolio of applications.

**Q: Sync status vs Health status in ArgoCD?**

A: Sync status is about Git vs live state: `Synced` or `OutOfSync`. Health is an independent diagnostic of the live workload: `Healthy`, `Progressing`, `Degraded`, `Missing`, `Suspended`. A Deployment can be Synced (matches Git) yet Degraded (its rollout is failing). Argo Rollouts extends this with health checks and AnalysisTemplates that gate a rollout's health.

**Q: How do you keep Git as the single source of truth when drift appears?**

A: Drift is any live change that does not match Git. With `selfHeal` plus `prune` enabled, ArgoCD continuously reverts manual edits and deletes removed resources, making the cluster effectively immutable from an operator's hands. When a legitimate change is needed, it must go through Git (a PR), which keeps the change reviewable, auditable, and revertible. Otherwise the cluster becomes an undocumented source of truth.

**Q: How does Kustomize handle secrets and external state?**

A: Kustomize has `secretGenerator` and `configMapGenerator`, which create hashed-named resources and trigger pod restarts on change, but the source data still lives in Git. For real secrets the ecosystem offers:

- **SealedSecrets** — a controller decrypts an encrypted `SealedSecret` into a plaintext Secret cluster-side, so ciphertext is safe in Git.
- **External Secrets Operator (ESO)** — synchronizes Secrets from external stores (AWS Secrets Manager, GCP, Vault) into cluster Secrets.
- **SOPS + age** — encrypts values in Git and decrypts them during CI/CD.

**Q: Why use GitOps for everything: apps, config, and infra?**

A: ConfigMaps, Secrets (encrypted), CRDs, operators, and even the platform itself should be versioned so the whole system shares one reviewable, auditable, revertible pipeline. ArgoCD syncs Helm charts, Kustomize overlays, or plain YAML from the same repos, so the same PR/approval/rollback flow governs infra, and `AppProject` scopes who can deploy where.

**Q: How does progressive delivery work with Argo Rollouts?**

A: Argo Rollouts replaces the Deployment with a `Rollout` resource supporting canary steps, blue-green, and weighted traffic splits. An `AnalysisTemplate` queries metrics (Prometheus, Datadog, etc.) during the rollout and can auto-abort and roll back if thresholds fail. Canary steps gradually shift `stableWeight` from 0 to 100 with optional pauses for manual approval.

**Q: How does ArgoCD compare to Flux?**

A: Both are CNCF GitOps controllers. ArgoCD: its own `Application` CRD, a built-in UI, SSO/RBAC, project scoping, sync waves, and App of Apps; it polls Git by default with webhooks for speed. Flux (GitOps Toolkit): a set of purpose-built controllers (Source, Kustomize, Helm, Notification, Image), native Helm support via `HelmRelease`, image automation for drift-free tag promotion, and works directly with `kustomize build`. Choose ArgoCD for a UI-driven platform with RBAC; choose Flux when you want modular controllers, image automation, and deeper Helm/Kustomize-native workflows.

## Gotchas

- `kubectl apply`-ed resources that Helm also manages are overwritten on the next `helm upgrade`; do not hand-edit chart-managed objects.
- Helm does not lint your rendered YAML for you on install. Always inspect `helm template <release> <chart> --debug` output before automating a release.
- Kustomize `commonLabels` also injects into `selector` and `template.metadata.labels`; changing it breaks selector matching unless labels are updated consistently.
- Strategic merge list patches without a `patchMergeKey` append instead of replacing; use a JSON patch when you need to delete an element by index.
- A failing Helm hook aborts the whole install/upgrade and marks the release failed; give hooks a `hook-delete-policy` and `--debug` before retrying.
- ArgoCD `selfHeal` without `prune` leaves deleted-from-Git resources running; the two should be enabled together with an `AppProject` allowlist.
- `PrunePropagationPolicy=orphan` on shared resources can strand dependent objects; test prune behavior on a canary cluster before production.
- Pinning charts to mutable tags or deleted `targetRevision`s produces "not found" drift; pin `targetRevision` to a branch/tag that will not be removed.
- Sync waves do not add a wait-for-ready guarantee by default; resources in the same wave apply in kind-priority order, so a Service can be created before its backend Deployment is healthy.

## Commands and Quick Reference

```bash
# Helm
helm ls -A                                   # list all releases
helm install myapp ./chart -n myns --create-namespace
helm upgrade myapp ./chart --values prod.yaml --set image.tag=v2
helm template myapp ./chart > /tmp/render.yaml
helm history myapp
helm rollback myapp 2
helm package ./chart --version 1.2.3
helm dependency update ./chart

# Kustomize
kustomize build ./overlays/prod
kubectl kustomize ./overlays/prod
kubectl apply -k ./overlays/prod

# ArgoCD
argocd app list
argocd app diff <app>                      # what changed vs Git
argocd app sync <app> --force
argocd app set <app> --sync-policy automated --auto-prune
kubectl get applications -A -o wide
argocd proj create myproj
argocd proj allow-cluster myproj <cluster-url> <namespace>
```

## Scenario

**Q: Helm upgrade broke your app. How to recover quickly?**

A: Roll back with `helm rollback <release> <revision>`, then reproduce the issue from `helm diff`/values before re-releasing. In a GitOps setup, `git revert` the change and let ArgoCD reconcile.

**Scenario: A Helm upgrade fails because a `post-install` validation hook exits non-zero, and every retry leaves the release in a failed state.**

A: Inspect `helm history <release>` to confirm the failed revision and `kubectl get jobs -n <ns>` to find the hook Job. Read its logs (`kubectl logs job/<name> -n <ns>`) for the exit reason. Add `"helm.sh/hook-delete-policy": before-hook-creation` so a retry gets a fresh Job instead of a completed one, fix the hook logic, then `helm upgrade --force` or delete and reinstall. Use `helm template --debug` to verify the rendered hook before re-running.

**Scenario: A monorepo of 12 Deployments is migrated to ArgoCD. A ConfigMap referenced by all of them is deleted from Git. What happens and how do you protect against it?**

A: With automated `prune`, ArgoCD deletes the ConfigMap and every app that mounts it is affected, potentially marking them Degraded. Protection: scope apps with an `AppProject` that restricts namespaces, generate the ConfigMap with a Kustomize `configMapGenerator` so its name carries a content hash (reverting the commit restores it), review auto-sync changes via a webhook/notification gate, and test prune behavior in a staging cluster before production.

## Related

- [Revision - Packaging and GitOps](../revision/packaging-gitops.md)
- [Revision - Operators](../revision/operators.md)
- [Lesson 29 - Helm](../docs/09-packaging/lesson-29-helm.md)
- [Lesson 30 - Helm Deep Dive Writing Production Charts](../docs/09-packaging/lesson-30-helm-deep-dive-writing-production-charts.md)
- [Lesson 31 - GitOps Principles and Practices](../docs/10-gitops/lesson-31-gitops-principles-and-practices.md)
- [Lesson 32 - CI/CD Pipelines, GitHub Actions and ArgoCD](../docs/10-gitops/lesson-32-cicd-pipelines-github-actions-and-argocd.md)
- [Lesson 33 - Extending Kubernetes CRDs and Operators](../docs/11-operators/lesson-33-extending-kubernetes-crds-and-operators.md)
- [Lesson 34 - Operators in Practice](../docs/11-operators/lesson-34-operators-in-practice.md)

[Back to Interview Index](README.md)