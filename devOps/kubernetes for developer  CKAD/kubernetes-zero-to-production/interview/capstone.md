---
title: Interview - Capstone and Certifications
module: 14 Certifications
status: Complete
tags: [interview, capstone, cka, career, STAR]
---

# Interview - Capstone and Certifications

## The Three Interview Gates

1. **Core knowledge** - rapid-fire conceptual questions (e.g., `kubectl run` flow).
2. **Production troubleshooting** - scenario debugging (e.g., 502s).
3. **Behavioral / incident** - "Tell me about a time you handled production." Use the STAR method.

## STAR Method

**Situation, Task, Action, Result.** Structure incidents that way: set the context, state your task, explain the specific action, and quantify the result (e.g., "reduced time to detect by 80%").

## Certification Comparison

| Attribute | CKA | CKAD | CKS |
|-----------|-----|------|-----|
| Focus | Cluster administration and operations | Application design, build, and deployment | Security of clusters and containerized workloads |
| Core role | Cluster admin: etcd backup/restore, kubeadm, node management, RBAC, upgrades | App developer: Pods, Deployments, Services, ConfigMaps, storage | Security specialist: hardening, admission, network security |
| Number of questions | 15-20 performance-based tasks | 15-20 performance-based tasks | 15-20 performance-based tasks |
| Duration | 2 hours | 2 hours | 2 hours |
| Passing score | 66% | 66% | 67% |
| Exam environment | Remote-proctored, browser terminal with real clusters, kubectl only | Same environment, per-task clusters | Same environment; requires a valid CKA first |
| Allowed resources | kubectl + official Kubernetes docs only (kubernetes.io/docs, kubernetes.io/blog and subdomains, helm.sh/docs, Gateway API docs); no arbitrary internet access | Same doc set as CKA | Same doc set plus security-focused docs |
| Distinct skill | Cluster admin: scheduling, taints, upgrades, TLS, troubleshooting | App dev: rollout patterns, probes, metrics, configurability | Security: hardening, audit logging, runtime security, network policies |
| Recertification | Certificate valid for 2 years (3 years if earned before 1 Apr 2024); renew by passing again | Certificate valid for 2 years (3 years if earned before 1 Apr 2024) | Certificate valid for 2 years; renew by passing |

## Exam-Taking Strategy

### Time Apportioning

- Skim every question first; solve the easy ones early so the time budget adds up.
- Budget roughly 13-18 minutes per high-value scenario and never spend more than 30 minutes on a single task. Flag long ones and return if time allows.
- Revisit flagged questions only after every easy task is fully answered.

### Know the Exam Environment

- The exam gives multiple clusters; each question targets a different one. Start every question with `kubectl config current-context`, then `kubectl config use-context <name>`.
- Generate templates with `kubectl run|create --dry-run=client -o yaml > manifest.yaml`, then apply.
- Small changes: `kubectl patch` or `kubectl edit`. Use `kubectl apply --force` only when a conflicting resource must be recreated.

### Reusable Snippets / Cheatsheet

- Set aliases and functions in the exam bashrc at the start; they save 10-15 minutes across 15+ tasks.

```bash
alias k='kubectl'
alias kg='kubectl get'
alias kd='kubectl describe'
alias kn='kubectl config current-context'
alias krf='kubectl apply --force -f'
```

The shell persists between questions, so the aliases defined once save minutes on every task. Keep each question's generated YAML in the home directory so you can patch and re-apply fast.

### Do NOT Recreate the Cluster

- Rebuilds cost minutes; never tear down the whole cluster for one broken object.
- Recover instead: `kubectl delete <object> --force --grace-period=0` then apply a fixed manifest, or `kubectl delete pod --force` to let Deployments reconcile.

### Verify With kubectl

- `kubectl get all -n <ns>`, `kubectl describe <res>`, and `kubectl explain <res>` are your primary diagnostics inside the exam.

## Interview Strategy / Do and Don't

**Do:**

- Admit when you don't know, then reason out loud.
- Show a debugging approach; walk through logical step-by-step isolation (events, describe, logs, taints, CNI).
- Ask clarifying questions before jumping to a fix.
- Mention tradeoffs explicitly (e.g., "that trades latency for resilience").
- Tie every technical answer to a production consequence ("this change would restart the Pod and drop in-flight requests").
- Use the STAR method for behavioral answers.

**Don't:**

- Answer with memorized jargon without explaining it.
- Dismiss tool versions (state which version you last used).
- Ignore business impact (RTO/RPO, cost, user disruption).
- Downplay security ("it's a test cluster, no one will attack it").

## Recap Checklist / 24-Hour Final Review

The night before the interview or exam, run this loop rapidly:

- **Rapid-fire recall:** ports (etcd 2379, API server 6443, kubelet 10250), NodePort range 30000-32767, probe types, Service types, QoS classes, taint/toleration syntax, PV/PVC flow, RBAC verb set.
- **One scenario demo:** re-run a single debugging drill end to end - "Pod CrashLoopBackOff" -> `kubectl describe pod` -> `kubectl logs --previous` -> fix the root cause -> verify with `kubectl get all` and `curl`.
- **STAR story ready:** rehearse one real incident out loud with a measured outcome (time to detect, requests recovered).
- **Review the environment:** if it is CKA/CKAD day, walk through the bashrc snippet above once more so the setup is instant.

## Quick Facts to Recall

| Fact | Value |
|------|-------|
| etcd port | 2379 |
| API Server | 6443 |
| Kubelet port | 10250 |
| NodePort range | 30000-32767 |
| node-monitor-grace-period | 40s (NotReady) |
| pod-eviction-timeout | 5m |
| QoS class | Guaranteed = requests == limits |
| CNI with eBPF | Cilium |
| Helm history command | `helm history <name>` |

## Portfolio Blueprint

- Public repo: your Helm charts, GitHub Actions pipeline, ArgoCD/GitOps config.
- A README explaining the architecture diagram (CI -> Git -> ArgoCD -> K8s -> Ingress -> Pod).
- Sanitized examples: no proprietary manifests or secrets.
- Link it on LinkedIn and your resume. Target Platform Engineering and SRE roles.

## Common myths

- You must know Go? No: controllers are Go, but most Kubernetes engineering is YAML, Helm, and infrastructure.
- You need a CS degree? No. Systems and self-taught DevOps backgrounds are common.

## Related

- [Revision - Certifications](../revision/certifications.md)
- [Module 14 - Certifications Index](../docs/14-certifications/README.md)
- [Lesson 43 - CKA Exam Masterclass](../docs/14-certifications/lesson-43-cka-exam-masterclass.md)
- [Lesson 44 - Cluster Architecture and the Kubeconfig File](../docs/14-certifications/lesson-44-cluster-architecture-and-the-kubeconfig-file.md)
- [Lesson 45 - etcd Backup and Restore](../docs/14-certifications/lesson-45-etcd-backup-and-restore.md)
- [Lesson 46 - The Capstone (Architecture, Career and Interview Mastery)](../docs/14-certifications/lesson-46-the-capstone-architecture-career-and-interview-mastery.md)

[Back to Interview Index](README.md)