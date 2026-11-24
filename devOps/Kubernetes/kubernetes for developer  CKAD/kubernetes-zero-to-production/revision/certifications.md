---
title: Revision - Certifications and Capstone
module: 14 Certifications
status: Complete
tags: [revision, certifications, cka, ckad, cks, kubeconfig, etcd, capstone, interview, career]
---

# Revision - Certifications and Capstone

## 1. The Mental Model

Certifications exist to prove you can operate a cluster under pressure, not to test recall. Treat every exam task and every interview question as a **reconciliation loop**: compare Desired State against Actual State, act to close the gap, and verify.

The three-stage ladder:

- **CKA** (Certified Kubernetes Administrator): run the platform. Cluster administration, control plane, troubleshooting, etcd backup/restore, node lifecycle.
- **CKAD** (App Developer): write to the platform. Workloads, config, services; no cluster admin, no etcd.
- **CKS** (Security Specialist): harden the platform. RBAC, admission controllers, image security, Pod Security. Requires a current CKA.

**CKA exam format and rules**

- Performance-based: roughly 15-17 tasks run on real remote clusters in about 2 hours. Passing score is 66% with no per-section cutoff, so collect every easy point first.
- During the exam, only the official Kubernetes documentation, the Kubernetes GitHub repositories, and the Kubernetes blog are allowed.
- Forbidden: generic search engines, StackOverflow, AI tools, and any third-party CLI such as k9s. The exam toolset is kubectl, etcdctl, and the standard shell.

For the career path, everything in the capstone is a business lever. Argo Rollouts reduces rollback time, the HPA lowers cost, Network Policies shrink compliance scope, GitOps makes every deployment a reversible commit. Hiring managers fund outcomes, not YAML.

**The roles on the platform team you interview for**

| Role | Focus | Key Tools |
|------|-------|-----------|
| DevOps Engineer | CI/CD pipelines, automation, cloud delivery | Jenkins, GitHub Actions, Terraform |
| SRE | Reliability, metrics, incident response | Prometheus, Grafana, Argo Rollouts |
| Platform Engineer | Internal developer platform (IDP) | Kubernetes, ArgoCD, Helm, Crossplane |

Career closing loop: build the public portfolio repository (Helm chart + GitHub Actions workflow + ArgoCD configs), add the link to your LinkedIn and resume, update your skills, schedule the CKA, then apply to Platform and SRE roles.

## 2. Core Concepts

**Certification landscape**

| Exam | Focus | Requires |
|------|-------|----------|
| CKA | Cluster administration, control plane, etcd, troubleshooting, upgrades | None |
| CKAD | Application design, build, config, expose | None |
| CKS | Security hardening, RBAC, admission, image security | CKA |

Passing score is 66% with no per-section cutoff, so collect every easy point first.

**The reconciliation world-view** - every controller is the same pattern (watch, diff, act, repeat): ReplicaSet reconciles Pod count, HPA reconciles replicas to metrics, ArgoCD reconciles the cluster to Git, Argo Rollouts reconciles traffic weight to error rate.

**kubeconfig** - the stateless VIP pass at `~/.kube/config` that tells kubectl who you are (user), where to go (cluster), and which namespace to default to (context). Three sections:

- `clusters`: API server URL + CA.
- `users`: client cert/key, token, or exec plugin.
- `contexts`: binding of a user to a cluster, optional namespace.
- `current-context`: the single pointer read on every command.

**etcd** - the consistent, highly-available key-value store that holds ALL cluster metadata (not application data). Runs as a Static Pod whose manifest is `/etc/kubernetes/manifests/etcd.yaml`. Client traffic on `2379`, peer traffic on `2380`, strict mTLS.

**Cluster components and who manages them**

| Component | Role | Managed By |
|-----------|------|------------|
| kubeadm | Bootstraps the cluster: generates TLS certs, writes static Pod manifests, sets up etcd, writes `admin.conf` | Admin (manually, one-time) |
| kubelet | Node agent; drives containers via the CRI; NOT managed by Kubernetes | systemd (OS level) |
| containerd (CRI) | Container runtime; the third node-side binary behind the kubelet | systemd |
| API Server | Entry point of all API calls; itself runs as a static Pod | kubelet (static Pod) |
| kubectl | CLI that reads the kubeconfig | User (local machine) |

- `kubeadm init` step sequence: generate TLS certs, write static Pod manifests, start kubelet as a systemd service, kubelet boots the control-plane components (api-server, controller-manager, scheduler, etcd), then write `/etc/kubernetes/admin.conf`.
- Workers join later with `kubeadm join <token> <apiserver>`.

## 3. Speed Tips and Must-Memorize Commands

Configure both files at the very start of the CKA exam, before reading questions.

```bash
# .bashrc additions
alias k='kubectl'
alias kgp='kubectl get pods'
alias kgs='kubectl get svc'
alias kgd='kubectl get deploy'
alias kgn='kubectl get nodes'
alias kgns='kubectl get ns'
alias kd='kubectl describe'
alias kdp='kubectl describe pod'
alias krm='kubectl delete'
alias kdr='kubectl drain --ignore-daemonsets --delete-emptydir-data'
alias kunc='kubectl uncordon'
export do='--dry-run=client -o yaml'
export now='--force --grace-period 0'
source ~/.bashrc
```

```bash
# .vimrc additions (YAML hates tabs; use spaces/2)
set expandtab
set tabstop=2
set shiftwidth=2
set autoindent
set smartindent
set nu
set ic
```

**Imperative generators (never hand-type YAML):**

```bash
k create ns NAME
k run POD --image=nginx
k create deploy web --image=nginx --replicas=3 $do > deploy.yaml
k expose deploy web --port=80 --target-port=8080 --type=NodePort $do > svc.yaml
k create cm my-cm --from-literal=DB=postgres $do > cm.yaml
k create secret generic my-secret --from-literal=password=pass $do > sec.yaml
k create sa my-sa
k create role read-pods --verb=get,list,watch --resource=pods
k create rolebinding my-rb --role=read-pods --serviceaccount=default:my-sa
```

**Context switching (never skip):**

```bash
kubectl config get-contexts
kubectl config use-context cluster2
kubectl config current-context
kubectl config set-context --current --namespace=apps   # pin namespace for the current context
kubectl config delete-context <name>                     # drop a context you never need again
kubectl config view                                      # inspect the merged kubeconfig
```

- `kubectl explain <resource>.<field>` renders the official API reference for the field; check it during the exam when unsure of a field's exact meaning.
- Generators fall back to `--dry-run=client` (local, offline) or `--dry-run=server` (the API validates and stores nothing). Prefer `client` for generated scaffolds; use `server` when you want real admission validation before writing.
- Add `--ignore-not-found` to delete-and-recreate patterns: the delete never errors when the object is already gone, so re-run is idempotent.
- Vim reflexes: `:wq` to save-and-quit and `:q!` to abandon a changed file are as important as `:retab`.

**etcd backup and verify:**

```bash
ETCDCTL_API=3 etcdctl snapshot save snapshot.db \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key
ETCDCTL_API=3 etcdctl snapshot status snapshot.db -w table
```

**Must-memorize facts:** etcd client port 2379; API server 6443; `node-monitor-grace-period` 40s; `pod-eviction-timeout` 5m; QoS Guaranteed = requests == limits on all containers; Cilium = eBPF, no iptables; `helm history <name>` shows release history.

## 4. Key YAML/Config patterns (kubeconfig YAML, etcd command) with explanation

**Kubeconfig YAML**

```yaml
apiVersion: v1
kind: Config
clusters:
- name: prod-cluster
  cluster:
    certificate-authority: /path/to/ca.crt   # verifies the API server identity
    server: https://10.0.0.1:6443
users:
- name: admin
  user:
    client-certificate: /path/to/cert.crt     # proves who you are to the server
    client-key: /path/to/cert.key
contexts:
- name: prod-admin
  context:
    cluster: prod-cluster
    user: admin
    namespace: default
current-context: prod-admin
```

- `cluster.server` is the address; point it wrong and you get a connection refused/timeout. A mismatched CA produces `certificate signed by unknown authority` (x509) - fix the `certificate-authority` path, not the server URL.
- `context.namespace` is the default namespace for that binding (overrides the `-n` requirement).
- `current-context` is the single pointer read on every command - the reason "wrong file/context" means working on the wrong cluster.
- Merge files with `KUBECONFIG=file1:file2 kubectl config view --raw > merged`; the `--kubeconfig` flag or `KUBECONFIG` env overrides the default.
- `users` can be an exec plugin (e.g. `aws-iam-authenticator`, `gke-gcloud-auth-plugin`) that shells out to mint credentials on demand; this is how managed clusters refresh short-lived tokens without storing keys.
- Never commit the kubeconfig to Git and rotate the credentials on exposure, not just `chmod 600` the file.
- On local clusters, `kind get kubeconfig --name <cluster> > ~/.kube/config` rebuilds the kubeconfig after a kind cluster is recreated.

**etcd restore to a new data directory**

```bash
# 1. stop the control plane (kubelet sees no static manifests, stops the pods)
mv /etc/kubernetes/manifests/*.yaml /tmp/
# 2. restore snapshot into a fresh directory, never the live one
ETCDCTL_API=3 etcdctl snapshot restore snapshot.db --data-dir=/var/lib/etcd-restored
# 3. edit /etc/kubernetes/manifests/etcd.yaml so the data volume hostPath points to /var/lib/etcd-restored
# 4. move the manifests back to start the control plane again
mv /tmp/*.yaml /etc/kubernetes/manifests/
# 5. kubelet starts etcd on the restored dir and the cluster recovers
```

The magic trick is the `hostPath` in `etcd.yaml`: `volumes[].etcd-data.hostPath.path` must point at the restored directory, or etcd boots against the old corrupted data and the restore appears to do nothing. Also requires the mandatory `ETCDCTL_API=3`, otherwise etcdctl defaults to the v2 API without snapshot support. Cert paths come from `/etc/kubernetes/pki/etcd/` (client + CA, not the api-server certs). `--cacert` verifies the etcd server, while `--cert`/`--key` present the client identity; all three are mandatory for a secure endpoint.

**etcd health and write path:**

- `etcdctl endpoint health --cluster` checks every member (the natural first step of any "etcd is down" diagnosis).
- `--endpoints` defaults to `127.0.0.1:2379`; on a control plane it always works because etcd runs as a static Pod on the node.
- Write path: API server sends the request to the etcd leader, etcd replicates to a quorum of members, and only after quorum acknowledgement does the write succeed and the API return success.
- `context deadline exceeded` = etcd unreachable (no reply in time): check `endpoint health`, then certs, then port 2379 reachability/firewall.
- "apply request took too long" means the cluster lost quorum or is oversubscribed; verify member health and disk before blaming the app.
- The snapshot binary and the running etcd must match the **same minor version** - restore a v3.4 snapshot with a v3.5 etcdctl and the restore fails.
- Encrypt the backups at rest and protect Secrets with encryption-at-rest (KMS) on the API server; retention via S3 Object Lock or WORM. Automate snapshots with a CronJob or a systemd timer, and restore-test on a schedule.

## 5. How It Fits Together (exam workflow; etcd restore flow)

**Exam workflow**

```text
Switch context -> generate base YAML with $do -> edit the delta only in vim
   -> kubectl apply -f -> verify with kubectl get / describe -> next question
```

1. Read the question; note the cluster context.
2. `kubectl config use-context <context>`; verify with `current-context`.
3. Generate nearly every resource with `kubectl create ... $do > file.yaml`.
4. Open the file in vim and edit exactly what the question needs (resources, probe, label).
5. `kubectl apply -f file.yaml`, then `kubectl get` it to confirm it is Running/Succeeded.
6. Collect easy points first; skip the hard ones and return if time remains (2 min/question pace).

**etcd restore flow**

```
stop control plane (move manifests out) -> snapshot restore to NEW --data-dir
   -> edit etcd.yaml hostPath -> move manifests back (restart)
   -> kubelet starts etcd -> cluster recovers
```

Backups are offsite, encrypted, automated, and tested (a backup you never restored from is worthless). Pair an etcd snapshot with Velero for PV/app data; etcd holds cluster metadata only.

## 6. Common Mistakes and Gotchas

| Mistake | Why It Happens | How to Avoid It |
|---------|----------------|-----------------|
| Writing YAML from scratch | Believing it is more reliable | Always generate with `kubectl create ... $do`. |
| Working on the wrong cluster | Not checking the context | `use-context` first, verify with `current-context`. |
| Tabs in pasted YAML | Editors default to tabs; YAML forbids them | `expandtab` in `.vimrc`; fix a broken file with `:retab`. |
| Missing `ETCDCTL_API=3` | etcdctl defaults to v2 | Prefix every etcdctl snapshot/restore call. |
| Restoring into a running etcd | Treating restore like apply | Stop control plane, restore to new dir, update manifest, restart. |
| Wrong cert paths | Using api-server, not etcd certs | Use `/etc/kubernetes/pki/etcd/` client pair. |
| Leaving hostPath misdirected | Forgot to edit `etcd.yaml` | Confirm the data volume hostPath points to the restored dir. |
| `kubectl logs` shows nothing | Restart cleared the log | Use `kubectl logs <pod> --previous` for the crashed instance. |

**Gotchas:** `The connection to the server localhost:8080 was refused` means kubectl can find no kubeconfig (it defaults to unsecured localhost:8080). A Pending pod is usually an unschedulable node or missing PVC, not a YAML bug. Protect the kubeconfig with `chmod 600`, and never `kubectl apply` straight into production - use Git via ArgoCD.

## 7. Quick self-check (mini Q/mini table)

| Question | Answer |
|----------|--------|
| What port does etcd use for client traffic? | 2379 (peer 2380) |
| What env var must precede `etcdctl snapshot`? | `ETCDCTL_API=3` |
| Where do the etcd TLS certs live in kubeadm? | `/etc/kubernetes/pki/etcd/` |
| Passing score for CKA? | 66% |
| Three kubeconfig sections? | clusters, users, contexts |
| Which flag generates YAML without contacting the API server? | `--dry-run=client -o yaml` |
| Default node monitor / pod eviction timers? | 40s / 5m |
| What does `kubectl explain` give you? | The field-level API reference for a resource |
| How to check every etcd member is healthy? | `etcdctl endpoint health --cluster` |
| What error means etcd answered too slowly? | `context deadline exceeded` |

**The full `kubectl run` path** (an end-to-end worldview): POST -> authentication -> authorization -> admission (mutating then validating) -> stored in etcd -> scheduler binds to a node -> kubelet calls the CRI -> containerd invokes runc -> kernel starts the container. It is also the story of the capstone's reconciliation loop, and the answer to "what is the difference between a Pod and a container" (a Pod is the scheduling unit and shares namespaces; containers inside it are the running processes).

**The three interview gates** every loop through the capstone story: Core Knowledge (can you build it?), Production Troubleshooting (can you fix it under pressure?), and Behavioral (can you work with people, STAR format). Prepare at least one story per gate.

**Taints and tolerations** quiz fact: taints repel Pods (effect NoSchedule/NoExecute); tolerations let specific Pods schedule onto tainted nodes; control-plane nodes carry `node-role.kubernetes.io/control-plane:NoSchedule`.

**The end-to-end data path** (the capstone's network answer): Cloud LoadBalancer -> NGINX Ingress (L7, terminates TLS) -> Service ClusterIP (L4) -> kube-proxy iptables DNAT -> Pod IP -> application, with an optional Istio sidecar adding mTLS between Pods. When the client sees 502/503/504, walk the path hop by hop: Ingress -> Service Endpoints -> targetPort -> Pod.

**Node failure mechanics** quiz fact: the Node Controller marks a dead node `Unknown` after ~40s (`node-monitor-grace-period`), the EndpointsController drops its Pod IPs from Endpoints after ~5m (`pod-eviction-timeout`), and a ReplicaSet rebuilds the lost replicas on surviving nodes.

**Progressive delivery quiz fact:** Argo Rollouts canary = 10% weight + an AnalysisTemplate that queries Prometheus and auto-aborts on 5xx beyond a threshold; the stable ReplicaSet gets the remaining traffic and a manual `kubectl rollout undo` reverts everything.

**HPA metrics source quiz fact:** the kubelet and its embedded cAdvisor expose per-container metrics via the kubelet Summary API; Metrics Server aggregates them over the `metrics.k8s.io` API (in-memory only, never etcd), and the HPA reads that API.

Answer the capstone five correctly and you are interview-ready:
1. Why `kubectl logs --previous`? Current logs are the fresh restart; `--previous` shows the crashed instance (the real stack trace or OOMKilled).
2. HPA shows `<unknown>/50%`? Metrics Server absent, or the Deployment has no `resources.requests.cpu` baseline.
3. PVC stuck `Terminating`? A `kubernetes.io/pvc-protection` finalizer waits on a using Pod, or a `Retain` PV. Remove with `kubectl patch pvc <name> -p '{"metadata":{"finalizers":null}}'`.
4. ArgoCD detects drift? Polls Git vs live; `selfHealing` re-applies Git state to overwrite manual edits.
5. Headless Service (`clusterIP: None`) for StatefulSets? No load balancing; per-Pod DNS A-records let `db-0` reach `db-1` directly.

## 8. 30-Second Recap

Aliases (`k`, `do`) set at exam start; `dry-run=client -o yaml` generates base YAML, edit only the delta. Switch context before each task. `ETCDCTL_API=3` snapshot to offsite storage; restore = stop control plane, `snapshot restore --data-dir` to a new dir, retarget the etcd hostPath, restart. Kubeconfig three parts - clusters, users, contexts - plus `current-context`. Everything in /ops is a control loop (ReplicaSet, HPA, ArgoCD, Rollouts). Declarative over imperative, verify every apply, bind every action to a business outcome, and answer interview questions with the symptoms-data-hypothesis-action-verify funnel and the STAR method for behavioral stories.

## Related Lessons

- [Lesson 43 - CKA Exam Masterclass (Speed and Shortcut Techniques)](../docs/14-certifications/lesson-43-cka-exam-masterclass.md)
- [Lesson 44 - Cluster Architecture and the Kubeconfig File](../docs/14-certifications/lesson-44-cluster-architecture-and-the-kubeconfig-file.md)
- [Lesson 45 - etcd Backup and Restore](../docs/14-certifications/lesson-45-etcd-backup-and-restore.md)
- [Lesson 46 - The Capstone (Architecture, Career and Interview Mastery)](../docs/14-certifications/lesson-46-the-capstone-architecture-career-and-interview-mastery.md)

## Related Material

- [Interview - Capstone](../interview/capstone.md)
- [All Cheat Sheets](../cheatsheets/README.md)

[Back to Revision Index](README.md)