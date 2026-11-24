---
title: Revision - Packaging and GitOps
module: Modules 09-10
status: Complete
tags: [revision, helm, kustomize, gitops, argocd, packaging]
---

# Revision - Packaging and GitOps

## 1. The Mental Model

Think of Helm as a **factory**, ArgoCD as a **strict building inspector**, and Git as the **single source of truth**.

- The repository has thousands of YAML files that drift apart when you copy and paste them across environments. Helm stops this with **templates** (the blueprint) and **values** (the crayons): one blueprint, colored differently for Dev, Staging, and Prod.
- GitOps inverts the deployment model: instead of a CI tool pushing YAML into the cluster, a controller that **runs inside** the cluster pulls Git state down and reconciles it. Git is the brain; ArgoCD is the hands that reshape the cluster to match the brain.
- Everything hangs on one loop, *desired state vs live state*. Git holds desired state, ArgoCD reads it, diffs against the live cluster, and reconciles any mismatch.

If you remember only three sentences: Helm packages and versions configs, Kustomize patches them without logic, and ArgoCD continuously makes the cluster match Git.

## 2. Core Concepts

### Helm

| Term | Meaning |
|------|---------|
| Chart | A packaged blueprints of Kubernetes objects (Chart.yaml, values.yaml, templates/) |
| Values | Variables injected into templates; override per environment |
| Charts/ | Vendored dependency charts |
| Release | A deployed instance of a Chart, tracked across revisions |
| template engine | Go templates (actions) that turn values + templates into final YAML |
| built-in objects | `.Values`, `.Release.Name`, `.Release.Namespace`, `.Chart`, `.Capabilities` |

Helm 3 is **client-side only**; there is no Tiller server in the cluster. State is stored as a Kubernetes Secret `sh.helm.release.v1.<release>.<revision>` in the release namespace, which is exactly what makes rollbacks work (Helm re-applies a previous revision).

### The full install pipeline

```text
helm install
  1. validate Chart.yaml and resolve dependencies
  2. merge values (chart defaults, then -f files, then --set)
  3. render Go templates into final YAML
  4. validate the rendered YAML against the Kubernetes OpenAPI schema
  5. send the manifest set to the API Server (like `kubectl apply`)
  6. save a copy of the rendered YAML + values as the release Secret
```

`helm upgrade` runs the same pipeline before step 5, then fetches the stored Secret state, computes the diff between the old and new releases, sends only the needed PATCH requests, and saves an amended Secret for the new revision.

Kubernetes is tool-agnostic: the API server has no idea Helm exists and accepts any valid YAML, whether it came from Helm, `kubectl apply`, or a human editor. This is why Helm, Kustomize, and raw manifests can coexist on one cluster without changing the platform.

Helm's release state reflects **API acceptance, not app health**. `helm upgrade` (and `helm list`) reports `deployed` as soon as the API Server accepts the YAML, even if the new Pods immediately sit in `ImagePullBackOff` or `CrashLoopBackOff`. Check `kubectl get pods` for real health; the failed revision is a rollback away.

Performance: Helm renders templates entirely on your workstation, so a deploy imposes **zero load on the cluster control plane**. Security trade-off: the same render step can read local files (e.g. a template calling `.Files.Get`), so inspect the `templates/` directory of any public chart and never run untrusted charts blindly.

Key render directives: `{{- if ... }}` includes or omits a whole resource; `{{- range $k, $v := .Values.env }}` emits repeated blocks from a list/map; `| quote` wraps a value in quotes; `| toYaml .Values.resources | nindent 10` injects multi-line YAML at the correct indentation; `{{-` and `-}}` trim surrounding whitespace.

Named sub-templates: `{{- define "web.labels" -}}...{{- end }}` lives in `templates/_helpers.tpl` and is called anywhere with `{{ include "web.labels" . | nindent 4 }}`. This acts like a function call, so the same labels, selector, and resource block exist once instead of being copy-pasted across the Deployment, Service, and Ingress.

The `tpl` function re-renders a string coming out of `values.yaml`: `tpl .Values.nginxConfig .` inside a ConfigMap turns a stored string into a rendered template, e.g. an Nginx config segment that references `{{ .Release.Name }}` at render time.

`{{ else }}` branch: `{{- if .Values.ingress.enabled }}...{{- else }}...{{- end }}` renders the first block when true and the fallback block when false, keeping both variants in one guard.

Remaining `.Release` built-ins: `.Release.Service` (always the string `Helm`), `.Release.Revision` (current revision number), `.Release.IsUpgrade` (true when running `helm upgrade`), `.Release.IsInstall` (true when running `helm install`).

`--reuse-values` in an upgrade keeps the previous revision's values and merges only the explicitly given `-f`/`--set`; `--reset-values` starts over from the chart defaults. Both matter for repeatable, non-destructive pipeline re-runs.

OCI registry hosting treats the packaged chart as an immutable artifact: `helm push ./mychart-1.0.0.tgz oci://registry.example.com/charts`, then pull by exact tag or digest. In production the chart itself is a reviewed artifact, not something every developer can push.

Multi-namespace releases are just `-n`: `helm install -n prod web ./mychart -f prod.yaml`. Each namespace holds its own release Secrets, so one chart can run Dev, Staging, and Prod side by side as independent releases.

Chart defaults should carry precise `requests`/`limits` that fit inside a target namespaced `ResourceQuota`. If the defaults over-request, every install fails with `Forbidden: exceeded quota`; right-sizing the chart defaults removes that class of failure.

### Kustomize

Kustomize does **no templating**. It builds an overlay by patching a `base` with plain YAML. Built into kubectl.

Core elements: `resources`, `patches`, `commonLabels`, `namePrefix`/`nameSuffix`, `images` (for tag replacement), `configMapGenerator`.

### GitOps

| Term | Meaning |
|------|---------|
| GitOps | Git is the single source of truth for cluster state |
| Desired state | The manifests in Git |
| Live state | What is actually running in the cluster |
| Reconciliation | The controller repeatedly brings live state toward desired state |
| Drift | Live state differs from Git (e.g. manual `kubectl edit`) |
| Sync | ArgoCD applying the Git state to the cluster |
| push vs pull | Push (Jenkins) needs kubeconfig to push out; pull (ArgoCD) runs in-cluster |

GitOps principles: declarative desired state in Git, pull-based sync, everything auditable/reversible, drift is a bug. Rollback is `git revert`; ArgoCD reconciles it.

ArgoCD is itself a Kubernetes controller using a **CRD** called `Application` (so `kubectl get applications` works). It natively renders Helm and Kustomize. `syncPolicy.automated.prune: true` deletes resources removed from Git; `selfHeal: true` reverts any manual change back to Git.

Installation: create the `argocd` namespace and apply the single manifest. The initial admin password is base64-decoded from the `argocd-initial-admin-secret`.

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
```

Application status is a state machine:

| State | Meaning |
|-------|---------|
| Synced | Live cluster state matches Git |
| OutOfSync | Git or the cluster changed; a sync (auto or manual) is required |
| Degraded | Deployed but failing (e.g. CrashLoopBackOff); resources exist but are unhealthy |
| Deletion cascade | Deleting the Application (optionally with auto-prune) deletes the resources it managed |

Because `Application` CRDs are ordinary objects stored in etcd, you can point ArgoCD at the ArgoCD install manifests: GitOps can manage ArgoCD itself, and `git revert` rolls back the whole instance.

ArgoCD only ever reaches out over outbound polling; GitHub webhooks are optional, so the GitOps pipeline needs no inbound firewall rule for GitHub to reach ArgoCD.

Private repositories require credentials: register the repo in ArgoCD with an SSH key (or a token), so the repo server can clone without leaking secrets into the manifests.

ArgoCD has its own RBAC and project model: AppProject-scoped roles let you restrict which teams can sync which Applications and which destinations/clusters they may target.

### CI vs CD

CI (GitHub Actions) builds and tests code, builds a Docker image, pushes it to a registry, **tags with `${{ github.sha }}`**, then commits the new image tag back into the config repo. CD (ArgoCD) watches that config repo and applies the change. CI never needs kubectl; it only writes to Git.

Environment promotion is **by SHA, not by rebuild**: the exact image built and tested in staging is promoted to prod by bumping `image.tag` to the same `github.sha`, never by building a new image. Dev, Staging, and Prod each point at the same artifact and differ only in where the tag is written.

The workflow that pushes the manifest bump back to Git must run with GitHub Actions **"Read and write permissions"** (Repo Settings -> Actions -> General -> Workflow permissions); a read-only token fails the `git push` step, so the image is built but the manifest never lands in Git.

Docker layer caching: GitHub Actions runners are ephemeral, so without cache every build starts cold. Wire up layer caching on the build action (e.g. `cache-from`/`cache-to` with a registry or the built-in action cache) so unchanged layers are reused and builds finish in seconds instead of minutes.

## 3. Key Commands

### Helm

```bash
curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
chmod 700 get_helm.sh && ./get_helm.sh    # install the CLI
helm version                              # verify the CLI
helm create mychart                        # scaffold a chart
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo list
helm search repo bitnami
helm install web ./mychart                # create release (revision 1)
helm install web ./mychart -f prod.yaml --set image.tag=v2
helm install -n prod web ./mychart -f prod.yaml   # multi-namespace release
helm upgrade web ./mychart --set image.tag=v3   # new revision; add --reuse-values to keep prior values
helm upgrade --install web ./mychart -f prod.yaml   # create-or-update (CI-friendly)
helm history web                          # all revisions
helm rollback web <revision>              # revert everything at once
helm list -A                              # all releases
helm uninstall web
helm template my-app .                    # render to stdout, no cluster
helm lint .                               # validate chart syntax + values.schema.json
helm dependency update ./mychart          # pull Chart.yaml dependencies
helm get values web                       # show merged/effective values
helm push ./mychart-1.0.0.tgz oci://registry.example.com/charts   # publish to an OCI registry
```

### Kustomize

```bash
kubectl kustomize ./overlays/prod        # render to stdout
kubectl apply -k ./overlays/prod         # build + apply
kubectl diff -k ./overlays/prod          # what would change, no apply
```

### ArgoCD

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
argocd app create web --repo https://github.com/myorg/config.git \
  --path apps/web --dest-server https://kubernetes.default.svc --dest-namespace web
kubectl get applications -n argocd
kubectl port-forward svc/argocd-server -n argocd 8080:443
argocd app sync web --prune
argocd app diff web
argocd app rollback web <revision>
argocd app list
argocd app delete web
```

### CI/CD pipeline

```bash
yq -i '.image.tag = "<sha>"' helm/values.yaml    # edit YAML in place
git add helm/values.yaml
git commit -m "Update image tag to <sha>"
git push
```

## 4. YAML Patterns

### Chart.yaml

```yaml
apiVersion: v2
name: web
description: A web service
type: application
version: 0.1.0          # bump when the chart changes
appVersion: "1.0"      # bump when the app changes
dependencies:
- name: redis
  version: "18.x.x"
  repository: https://charts.bitnami.com/bitnami
```

`version` and `appVersion` are distinct: `version` tracks template logic, `appVersion` tracks the packaged app. `dependencies` lets you pull in sub-charts like redis; run `helm dependency update`.

### values.yaml

```yaml
replicaCount: 2
image:
  repository: nginx
  tag: "1.25"
  pullPolicy: IfNotPresent
ingress:
  enabled: true
resources:
  requests:
    cpu: 100m
    memory: 128Mi
```

Values precedence (highest to lowest): `--set` > `-f/--values` (multiple files, later wins) > chart `values.yaml`.

### Chart layout and guards

```text
my-app/
  Chart.yaml           # metadata (name, version, appVersion, dependencies)
  values.yaml          # default configuration
  values.schema.json   # optional JSON Schema; helm lint validates values against it
  templates/
    _helpers.tpl       # named sub-templates (define + include)
    deployment.yaml
    service.yaml
    ingress.yaml
  charts/              # vendored dependencies
  .helmignore          # files excluded from the packaged chart (like .gitignore)
```

`values.schema.json` catches typos and wrong value types before anything reaches the cluster: `helm lint .` fails fast on a schema violation. `_helpers.tpl` holds the `{{- define ... -}}` blocks that `{{ include ... }}` calls reuse across templates. `.helmignore` keeps local dev files and Secrets out of the packaged `.tgz`.

### A template snippet

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Release.Name }}-web
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      app: {{ .Release.Name }}
  template:
    metadata:
      labels:
        app: {{ .Release.Name }}
    spec:
      containers:
      - name: web
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        resources:
          {{- toYaml .Values.resources | nindent 10 }}
        env:
        {{- range $k, $v := .Values.env }}
        - name: {{ $k }}
          value: {{ $v | quote }}
        {{- end }}
```

The whole template is the blueprint. `{{ .Release.Name }}` guarantees unique names per release so several releases can coexist. `toYaml ... | nindent 10` injects the resources block at the right indentation. The `range` loop emits one env var per key.

A conditional whole-file guard (e.g. an Ingress that only exists when enabled):

```yaml
{{- if .Values.ingress.enabled }}
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: {{ .Release.Name }}-ingress
spec:
  ingressClassName: nginx
{{- end }}
```

This creates the resource only when `ingress.enabled` is true.

### ArgoCD Application

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: web
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/myorg/kubernetes-manifests
    targetRevision: main
    path: apps/web          # folder with YAML or Helm chart/Kustomize dir
  destination:
    server: https://kubernetes.default.svc
    namespace: web
  syncPolicy:
    automated:
      prune: true           # delete resources removed from Git
      selfHeal: true        # revert manual kubectl drift back to Git
    syncOptions:
    - CreateNamespace=true  # create destination namespace if missing
```

- `source` describes `what` to deploy from Git (repo, branch, path).
- `destination` says where in the cluster (server and namespace).
- `syncPolicy.automated` is what makes the whole thing GitOps. `prune` + `selfHeal` together mean the cluster will always end up exactly like Git.

### GitHub Actions workflow

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main]

concurrency:
  group: ci-main
  cancel-in-progress: true

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      - name: Login to registry
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      - name: Build and push image
        uses: docker/build-push-action@v6
        with:
          push: true
          tags: myrepo/myapp:${{ github.sha }}
      - name: Bump and commit manifest
        run: |
          yq -i '.image.tag = "${{ github.sha }}"' helm/values.yaml
          git config user.name "GitHub Actions"
          git config user.email "actions@github.com"
          git add helm/values.yaml
          git commit -m "Update image tag to ${{ github.sha }}"
          git push
```

- `on.push` triggers the run when code is pushed (to `main` here).
- `uses:` calls a reusable action; pin it to a full commit SHA (not a mutable tag) for supply-chain safety, or at least an exact `@vN` tag.
- `${{ secrets.* }}` injects encrypted credentials at runtime; never hardcode them.
- A `concurrency` group cancels a superseded run when the developer pushes another change while the first is running.
- `run:` executes shell commands; the `yq -i` edit plus `git push` is the manifest bump ArgoCD waits for.

## 5. How It Fits

### GitHub Actions (CI) to ArgoCD (CD)

```text
[ Dev ] --git push--> [ GitHub Repo ]
                          |
                          v
              [ GitHub Actions - CI ]
               1. build Docker image
               2. push to registry (DOCKER_USERNAME/PASSWORD from Secrets)
               3. tag = ${{ github.sha }}
               4. yq update helm/values.yaml image.tag -> git push
                          |
                          v
              [ ArgoCD polls config repo (~3 min) / webhook ]
                          |
                          v
              [ Kubernetes API Server: renders chart, rollout ]
```

- The security split, by design: CI only writes Git and never holds kubeconfig; ArgoCD only reads Git and writes to the cluster. A compromised CI runner can push a bad image but cannot reach the cluster directly.

Promote by SHA, never rebuild: one artifact flows through every environment by editing only the image tag.

```text
image myrepo/myapp:abc123 built once by CI
   |
   +--> Dev     ArgoCD Application sets image.tag=abc123
   +--> Staging ArgoCD Application sets image.tag=abc123
   +--> Prod    ArgoCD Application sets image.tag=abc123 (after review)
```

The exact bytes tested in staging are what ships to production; environments differ only in the values file, never in the artifact.

### Helm lifecycle

| Stage | Command | Helm behavior |
|-------|---------|---------------|
| Install | `helm install` | Validates, renders, schema-validates, applies, saves Secret v1 |
| Upgrade | `helm upgrade` | Fetches current state from the Secret, computes the diff, sends PATCHes, saves amended Secret v2 |
| Rollback | `helm rollback` | Re-applies a past revision's saved YAML |
| Uninstall | `helm uninstall` | Deletes resources and Helm Secrets |

`deployed` in `helm list`/`helm history` only means the API Server accepted the release, not that the app is healthy; pair Helm status with `kubectl get pods`.

Helm and GitOps naturally combine: the chart and `values.yaml` live in the config repo; ArgoCD renders and deploys them, `git revert` becomes the rollback, and Helm's release Secret gives Deployment-level-safe rollbacks when working directly.

## 6. Common Mistakes and Gotchas

| Mistake | Why it happens | How to avoid |
|---------|----------------|--------------|
| Hardcoding values in templates | Forgetting to use `{{ .Values.xxx }}` | Parameterize every runtime value in `values.yaml` |
| Overusing `--set` | quick overrides grow into long CLI calls | use per-env values files stored in Git |
| Blank-line / indentation errors | Missing the `-` in `{{- weekend -}}` | run `helm template` and inspect the rendered output |
| Deleting the Helm release Secret | cleaning up `sh.helm.release.v1.*` | never hand-delete these; rollback depends on them |
| Using `latest` for an image tag | doesn't change in Git | tag with `${{ github.sha }}` |
| Mixing `kubectl apply` with Helm-owned resources | two owners conflict (`managedFields`) | use `helm upgrade --install` for the whole workload |
| Manual `kubectl edit`/`scale` in prod | creates drift | committed to Git or selfHeal reverts it |
| Giving CI kubectl access | easier to reason about | keep CI on Git only; let ArgoCD deploy |
| Auto-prune without PR reviews | deleting YAML in Git deletes from cluster | branch protection + PR review |
| Env-by-hand deployments | `argocd app rollback` misused | Sync is the go-to; rollback is the exception |
| Trusting `helm list` status for health | release state reflects API acceptance only | confirm with `kubectl get pods`; roll back the failing revision |
| `helm template` errors "function not defined" | chart uses functions newer than the installed CLI | `helm version`; upgrade the Helm CLI |
| Installing untrusted public charts | templates can read local files | inspect `templates/` before installing |
| Chart defaults overshoot a ResourceQuota | requests/limits sum exceeds the quota | keep precise, modest defaults in the chart |
| Pinning Actions to mutable tags | a moving action tag is a supply-chain risk | pin actions to full commit SHAs |
| Racing pushes both bump `values.yaml` | the last writer silently wins | add a `concurrency` group to cancel superseded runs |

## 7. Quick Troubleshooting

- **`helm template` shows wrong indentation / blank lines**: missing trims. Put `{{-` on loop/if openers and `-}}` on closers; use `| nindent`.
- **`helm template` fails "function not defined"**: the chart uses template functions your Helm version lacks. `helm version`; upgrade Helm (or run the version the chart targets).
- **Pods `ImagePullBackOff` after `helm upgrade`**: bad image tag. `helm history <release>` then `helm rollback <release> <rev>`.
- **`helm upgrade` says `deployed` but Pods crash**: `deployed` is API acceptance, not health. `kubectl get pods`, then roll back the failed revision.
- **`helm upgrade` fails "release not found"**: the release Secret was deleted. `helm list -A` to confirm, then reinstall.
- **Install fails with "exceeded quota"**: chart defaults overshoot the namespace `ResourceQuota`; right-size the chart resources.
- **Ingress never appears**: the template conditional is `{{/*- if .Values.ingress.enabled }}`; confirm the effective value with `helm get values <release>`.
- **ArgoCD shows OutOfSync but never syncs**: `syncPolicy.automated` is missing; enable it.
- **ArgoCD cannot read a private repo**: the repository needs a registered SSH key or token credential; the clone fails silently without it.
- **ArgoCD shows "Degraded"**: app is running but failing (CrashLoopBackOff); `kubectl describe pod`.
- **ArgoCD never detects a commit**: polling default is ~3 minutes; configure a GitHub webhook for instant sync (or verify targetRevision).
- **CI built the image but cluster is stale**: the values bump was never pushed, the workflow lacked "Read and write" permissions, or the tag was `latest`. Confirm the Git commit and the ArgoCD target branch.

## 8. 30-Second Recap

- **Helm** = Chart (blueprint) + Values (crayons) = Release. Helm 3 is client-side; state lives in Secrets; `helm template` to dry-run, `helm rollback` to undo.
- **Pipeline** = validate manifest / resolve values -> render templates -> validate against the Kubernetes OpenAPI schema -> apply -> save the release Secret. Upgrade fetches stored state, diffs, PATCHes, saves an amended Secret.
- **Chart** layout: `Chart.yaml` (metadata/appVersion/version/dependencies), `values.yaml`, `values.schema.json` (checked by `helm lint`), `templates/` (with `_helpers.tpl` named templates, `if`/`else`/`range`, `nindent`, `tpl`) and `charts/` plus `.helmignore`. OCI registries host charts via `helm push`.
- **Kustomize** = overlays patch a base; `kubectl apply -k`; no templating logic.
- **GitOps** = Git is the single source of truth; pull-based; Desired state in Git vs live state (drift) -> reconcile.
- **ArgoCD** uses `Application` CRD (repo URL/path/destination) and `syncPolicy.automated` + `selfHeal` to make the cluster match Git. States: Synced, OutOfSync, Degraded, Deletion cascade. Install is one manifest; admin password base64-decoded; own RBAC; private repos need SSH; GitOps can manage ArgoCD itself.
- **Kubernetes is tool-agnostic**: the API server accepts any valid YAML, so Helm and Kustomize coexist. Helm's `deployed` reflects API acceptance, not app health; check `kubectl get pods`.
- **CI/CD flow**: GitHub Actions builds and pushes an image tagged `${{ github.sha }}`, `yq` bumps and commits `values.yaml` ("Read and write" permissions required), and ArgoCD deploys. Promote environments by the same SHA without rebuilding, pin actions to commit SHAs, cancel superseded runs with `concurrency`; rollback is `git revert`.

Memory trick: GitHub Actions is the **factory robot** that builds parts and updates the manual; the Docker registry is the **warehouse**; ArgoCD is the **floor manager** that reads the manual and installs the part; the git SHA is the **serial number**.

## Related Lessons

- [Lesson 29 - Helm](../docs/09-packaging/lesson-29-helm.md)
- [Lesson 30 - Helm Deep Dive (Writing Production Charts)](../docs/09-packaging/lesson-30-helm-deep-dive-writing-production-charts.md)
- [Lesson 31 - GitOps Principles and Practices](../docs/10-gitops/lesson-31-gitops-principles-and-practices.md)
- [Lesson 32 - CI/CD Pipelines (GitHub Actions and ArgoCD)](../docs/10-gitops/lesson-32-cicd-pipelines-github-actions-and-argocd.md)

## Related Material

- [Packaging Guide Cheat Sheet](../cheatsheets/packaging-cheatsheet.md)
- [GitOps Cheat Sheet](../cheatsheets/gitops-cheatsheet.md)
- [Interview - GitOps](../interview/gitops.md)

[Back to Revision Index](README.md)