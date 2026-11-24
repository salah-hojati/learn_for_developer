---
title: Interview - Security
module: 07 Security
status: Complete
tags: [interview, security, rbac, service-account, pod-security, admission]
---

# Interview - Security

## Beginner

**Q: What is RBAC?**

A: Role-based access control. Roles define permissions; bindings attach them to subjects (ServiceAccounts, users, groups). Namespaced (Role/RoleBinding) or cluster-wide (ClusterRole/ClusterRoleBinding).

**Q: What is a ServiceAccount?**

A: An identity for Pods to authenticate with the API Server. Its token is mounted at `/var/run/secrets/kubernetes.io/serviceaccount`; Pods can use it for API calls.

## Intermediate

**Q: How do you verify if a user can create deployments?**

A: `kubectl auth can-i create deployments --as=<user-or-sa>`. For an SA: `--as=system:serviceaccount:<ns>:<sa>`.

**Q: What are Pod Security Standards?**

A: Three tiers - privileged, baseline, restricted - enforced by Pod Security Admission via namespace labels (`pod-security.kubernetes.io/enforce=restricted`). Restricted requires non-root, dropping capabilities, and seccomp.

## Advanced

Q: How do you harden a container image at the Pod level?

A: Set `runAsNonRoot: true`, `runAsUser`, drop all capabilities with `capabilities.drop: [ALL]`, `allowPrivilegeEscalation: false`, `readOnlyRootFilesystem: true`, and a RuntimeDefault seccomp profile. Enforce these centrally with Kyverno policies.

**Q: Why can a user run `kubectl get pods` but fail with `kubectl logs pod/x`?**

A: Logs are a subresource. `get` on `pods` does not imply `get` on `pods/log` or `create` on `pods/exec`. API verbs (get, list, watch, create, update, patch, delete) are granted independently per resource. Add separate rules for `pods/log` and `pods/exec` or use the wildcard `pods/*`.

**Q: How do `apiGroups`, `resources`, `verbs`, and `resourceNames` compose in a rule?**

A: A rule grants `verbs` on `resources` in `apiGroups`. The core group is `""` (pods, services, configmaps, secrets); Deployments and ReplicaSets live under `apps`; legacy APIs under `extensions`. `resourceNames` limits the rule to named objects only (for example one specific Secret). `resourceNames` applies to `get`, `update`, `delete`, and `patch`; the API server ignores it for `list` and `watch`.

**Q: Can a RoleBinding bind a ClusterRole? Can a ClusterRoleBinding bind a Role?**

A: Yes to the first, no to the second. A RoleBinding may reference a ClusterRole; the permissions are then scoped to that binding's namespace. A ClusterRoleBinding cannot reference a Role because a Role is namespaced and cannot be elevated cluster-wide.

**Q: What is a ClusterRole aggregationRule?**

A: A ClusterRole may declare `aggregationRule` with label selectors. Every ClusterRole whose labels match the selector contributes its rules. This is how Kubernetes composes the built-in `view`, `edit` and `admin` ClusterRoles from smaller pieces; adding a labeled ClusterRole recomputes the aggregated result.

**Q: How do Pods get a ServiceAccount token?**

A: A Pod uses `serviceAccountName` to choose the SA; otherwise the namespace default is used. The kubelet mounts the token at `/var/run/secrets/kubernetes.io/serviceaccount` unless `automountServiceAccountToken: false` disables it. Modern projected tokens carry `audience` and `expirationSeconds`; the legacy long-lived `kubernetes.io/service-account-token` Secret is deprecated. Prefer a dedicated ServiceAccount per workload.

**Q: How do you deploy a Pod without the default token?**

A: Set `automountServiceAccountToken: false` in the Pod spec, and pick the SA with `serviceAccountName`. The projected token uses a bounded lifetime (defaults to hours) and is rotated by the kubelet before expiry.

**Q: What is the difference between a PodSecurityContext and a container securityContext?**

A: The Pod-level securityContext sets defaults that all containers share (user, group, fsGroup, seccomp, SELinux options). The container securityContext overrides per-container settings: `runAsUser`, `runAsGroup`, `runAsNonRoot`, `allowPrivilegeEscalation`, `capabilities`, `readOnlyRootFilesystem`. `runAsNonRoot: true` rejects images running as uid 0; `runAsUser` sets the uid explicitly. `allowPrivilegeEscalation: false` is critical whenever an image contains setuid binaries.

**Q: Why is allowPrivilegeEscalation critical for setuid binaries?**

A: A setuid binary can raise its effective uid regardless of `runAsNonRoot`. `allowPrivilegeEscalation: false` tells the container runtime that a process cannot gain more privileges than its parent, which blocks the setuid escalation path. Combine it with a non-zero `runAsUser`.

**Q: How do capabilities work and what is NET_BIND_SERVICE vs SYS_ADMIN?**

A: Root in a container still lacks privileged capabilities unless granted. Start from `drop: [ALL]` and `add` only what is needed: `NET_BIND_SERVICE` to bind low ports, `SYS_ADMIN` for privileged mount behavior (close to a container escape; avoid unless truly required). Use drop-all then a minimal addlist for least privilege.

**Q: Seccomp: what is the difference between RuntimeDefault and Localhost?**

A: `RuntimeDefault` applies the container runtime's own default seccomp profile, which blocks a curated set of risky syscalls. `Localhost` points to a profile file on the node (`localhostProfile`), giving full control but requiring the profile to be present on every worker. Pod Security restricted requires a `RuntimeDefault` seccomp profile or the Pod is rejected.

**Q: What is AppArmor, and is SELinux native to Kubernetes?**

A: AppArmor protects via an annotation (`container.apparmor.security.beta.kubernetes.io/<name>`) because the profile type is not an API field. SELinux is a node-level facility configured by the distribution and set through `seLinuxOptions` in the securityContext, not something Kubernetes itself manages via Pod spec.**

**Q: In what order does Kubernetes run admission webhooks?**

A: Mutating admission controllers (webhooks) run first, then validating admission controllers (webhooks and built-in validating admission such as Pod Security based on the namespace label). Mutating webhooks see and can patch the object before validation, which is why the effective `securityContext` is whatever the mutator produced.

**Q: What does the restricted Pod Security level require?**

A: Set `pod-security.kubernetes.io/enforce=restricted` on the namespace. A restricted Pod may not: run as root, add capabilities (must drop ALL), set `privileged: true`, or use `hostNetwork/hostPID/hostIPC`. Fields that must be set: `runAsNonRoot: true` (or a non-zero user in `runAsUser`), `capabilities: [drop: ["ALL"]]`, `allowPrivilegeEscalation: false`, `seccomp: RuntimeDefault`, `readOnlyRootFilesystem: true`. Baseline adds the middle tier: no privileged or host pods. The namespace label enforces the tier:

```yaml
pod-security.kubernetes.io/enforce: restricted
pod-security.kubernetes.io/warn: restricted
```

**Q: Secret types, and is base64 encryption?**

A: Types: `Opaque`, `kubernetes.io/tls`, `kubernetes.io/service-account-token` (legacy SA), `kubernetes.io/dockerconfigjson`. `.data` is base64 encoded, `.stringData` is plain text input. Base64 is not encryption: someone with `get` on the Secret sees the decoded value. For at-rest protection, configure `EncryptionConfiguration` with a `kms` provider (prefer KMS v2) or `aescbc`.

**Q: When to use `.stringData`?**

A: `stringData` accepts a plain-text value and the API server base64-encodes it into `.data` on write. It is useful for creating Secrets from readable config before the server persists them as base64.

**Q: How does image pull from a private registry work?**

A: The Pod specifies `imagePullSecrets` so the kubelet can authenticate to the registry. The Secret has type `kubernetes.io/dockerconfigjson` (produced by `kubectl create secret docker-registry`). For supply-chain trust, sign images (cosign/sigstore) and validate signatures at admission time; a private registry can also require node credentials via the kubelet (`--image-credential-provider-bin-dir`).

## Scenario

Q: A Pod is rejected by admission. Where do you look?

A: The event on the Pod and the webhook/admission controller (Kyverno policy, Pod Security label, or `LimitRange`). Check `kubectl describe pod`, then the relevant policy and namespace labels.

**Q: A CI ServiceAccount can list pods but `kubectl exec` returns forbidden. What is wrong?**

A: Missing the `pods/exec` subresource rule (or `pods/attach`) for that SA. `get pods` does not include `create pods/exec`. Add the subresource to the Role or RoleBinding that binds this SA:

```yaml
rules:
- apiGroups: [""]
  resources: ["pods", "pods/exec"]
  verbs: ["get", "list", "create"]
```

**Q: An image pull fails with `ImagePullBackOff` while the registry credentials are correct. Why?**

A: Check that `imagePullSecrets` is present on the Pod *and* in the right namespace; the Secret must match type `kubernetes.io/dockerconfigjson` and the `imagePullSecrets.registry` for the image. Different reasons: the Secret is in another namespace, the registry hostname is mistyped, or the SA policy does not allow the kubelet to fetch it.

**Q: Pods fail to start in a namespace labeled `restricted` while they ran fine in `default`. Why?**

A: The namespace label enforces the tier, so Pod spec must satisfy the `restricted` policy. Usually root user, missing `seccompProfile`, `privileged`, host paths, or missing `readOnlyRootFilesystem`. Start by asking for `pod-security.kubernetes.io/audit=restricted` to see the violations, then fix the Pod spec.

## True/False

- A ClusterRole can be bound within a namespace using a RoleBinding. (True)
- Secret values are encrypted at rest in etcd by default. (False - they are base64; encryption must be enabled.)
- `resourceNames` works with list/watch verbs. (False - it is only honored for get/update/delete/patch and API server ignores it for list and watch.)
- `allowPrivilegeEscalation: false` is optional for setuid-free images. (True - it is about escalation via setuid; harmless to keep as False/True.) **(Keep True)**
- A ClusterRoleBinding cannot bind a Role. (True)

## Related

- [Revision - Security](../revision/security.md)
- [Lesson 22 - RBAC and Service Accounts](../docs/07-security/lesson-22-rbac-and-service-accounts.md)
- [Lesson 23 - Locking Down the Container (Security Contexts)](../docs/07-security/lesson-23-locking-down-the-container-security-contexts.md)

[Back to Interview Index](README.md)