---
title: Interview - Operators
module: 11
status: Complete
tags: [kubernetes, interview, crd, operator, controllers]
---

# Interview - Operators

## Beginner

**Q: What is a Kubernetes Operator?**

A: A custom controller that extends Kubernetes to manage a specific application, usually a stateful one. It registers a new resource type with a CRD and runs a reconciliation loop that continuously matches the actual cluster state to the desired state declared in a Custom Resource, automating backups, scaling, upgrades, and failover.

**Q: What is the difference between a CRD and a CR?**

A: A CRD (Custom Resource Definition) is the schema or blueprint that teaches the API Server a new object type. A CR (Custom Resource) is an instance of that type, declared in YAML and stored in etcd.

**Q: What happens if you apply a Custom Resource before its CRD is installed?**

A: The API Server rejects it with an error such as `no matches for kind "CronTab" in version "stable.example.com/v1"`, because the schema for that kind does not exist yet.

**Q: Where are CRDs and CRs stored?**

A: Both are stored in etcd, just like built-in resources. The CRD is the definition; each CR is a stored object against that definition.

**Q: What is group/version/kind in Kubernetes?**

A: The grouping system for API resources. The group is the logical scope (for example, `stable.example.com` for a custom API), the version is the API maturity level (`v1alpha1`, `v1beta1`, `v1`), and the kind is the object type (for example, `CronTab`). They combine to form the `apiVersion` used in manifests.

**Q: True or False: Operators replace Deployments.**

A: False. Operators typically create and manage Deployments, StatefulSets, Services, and other standard resources rather than replacing them.

## Intermediate

**Q: How does an Operator know when a Custom Resource is created?**

A: It runs a SharedInformer that watches the API Server for changes to its kind. A new, updated, or deleted CR triggers the reconciliation loop.

**Q: What is the reconciliation loop?**

A: The continuous process of comparing the Desired State (what the Custom Resource spec declares) with the Live State (the actual Deployments, Pods, PVCs in the cluster) and taking action to close the gap. For example, if the CR says `replicas: 3` but zero StatefulSets exist, the Operator creates one.

**Q: Why use an Operator instead of a Helm chart?**

A: Helm renders YAML and applies it once; it has no Day 2 operations. An Operator runs continuously and manages the application lifecycle - scaling, backups, failover, and upgrades - which a one-shot chart install cannot do.

**Q: What is the `status` subresource?**

A: A separate subresource on the CR that the Operator writes to report the live state and progress or errors, for example `status.conditions`. Writing to `status` does not trigger a `spec` reconcile and is excluded from normal resource quotas and validation so controllers cannot mistake status for user intent.

**Q: What are finalizers and why do Operators need them?**

A: A finalizer is a metadata marker that blocks deletion of a CR until the Operator removes the finalizer. It ensures external resources (cloud DNS records, S3 buckets, load balancers) are cleaned up before the CR disappears from etcd. Without it, deleting a CR leaks external resources or deletes data prematurely.

**Q: What RBAC does an Operator need?**

A: Operators typically need broad CRUD permissions to create Deployments, StatefulSets, Services, PVCs, and Secrets, plus read permissions on the CRs. Because a compromised Operator Pod can destroy many resources, scope the ServiceAccount to a namespace-scoped Role with least privilege rather than a broad ClusterRole.

**Q: What is `apiextensions.k8s.io/v1`?**

A: The API group and version that the `CustomResourceDefinition` object itself uses (`apiVersion: apiextensions.k8s.io/v1`). Its `spec` holds the group, scope, plural/singular/kind/short names, served and storage versions, and the `openAPIV3Schema` used to validate Custom Resource instances.

**Q: What are `shortNames` on a CRD?**

A: Abbreviated names `kubectl` resolves for the resource, defined in `spec.names.shortNames`, for example `ct` for `crontabs`, so `kubectl get ct` works like `kubectl get crontabs`.

**Q: How do Operators validate CRs?**

A: The CRD defines an `openAPIV3Schema` that the API Server enforces at admission time. Using `additionalProperties: false` rejects unknown fields, so typos fail before the Operator ever sees them.

**Q: How are multiple CRD versions handled?**

A: A CRD can serve several versions with `served: true`, but only one is `storage: true`. Breaking schema changes across versions require a conversion webhook; otherwise keep changes non-breaking and structural. `apiVersion` in a CR manifest must match the CRD group, version, and kind exactly.

**Q: True or False: An operator is just a Helm chart with a longer name.**

A: False. Helm installs once; an Operator is a continuously running controller that watches and reconciles its Custom Resources forever.

## Advanced

**Q: How do client-go and controller-runtime build an Operator?**

A: An Operator typically uses client-go (direct API client, informers, workqueue) or higher-level controller-runtime (Manager, reconcile function, builder). The SharedInformer lists and watches resources, converts events into the workqueue, and the reconcile function "desired vs live" compares state and creates, updates, or deletes resources until the cluster matches the declaration.

**Q: What does the Operator pattern mean by "Kubernetes is a platform for platforms"?**

A: Kubernetes' core API is generic and extensible: it can store, validate, and reconcile any resource with the same machinery. Domain knowledge (how to run a database, a message queue, or TLS issuer) is encoded not in the core but in operators, letting each team build a platform tailored to its workload while the core stays stable.

**Q: How do ownerReferences enable garbage collection?**

A: A CR typically creates owned resources with `ownerReferences` that point back to the CR, so deleting the parent garbage-collects the children. This is how built-in controllers clean up and how a well-written operator avoids leaked resources. Finalizers extend this for external (non-Kubernetes) resources.

**Q: How do you implement field immutability on a CR?**

A: OpenAPI schema alone cannot express cross-field or cross-resource rules, so enforce immutability with a validating admission webhook (a `ValidatingWebhookConfiguration`) that compares the new CR to the stored version and rejects forbidden changes, or set `x-kubernetes-validations` CEL rules (from Kubernetes 1.25) where the check is expressible.

**Q: When should you build an Operator vs not?**

A: Build one when the app has complex lifecycle needs: stateful workloads with backups, failover, upgrades, or cluster-wide orchestration, and no existing community operator satisfies the need. Do not build one for a stateless app a Deployment plus a Helm chart can run. Prefer battle-tested operators from OperatorHub and the CNCF ecosystem (for example, cert-manager, the Prometheus Operator, Strimzi, crunchy PGO) over an in-house one.

## Scenario

**Q: You need a PostgreSQL cluster with automated backups and failover. How do you implement it?**

A: Install a PostgreSQL Operator (for example, CrunchyData PGO), which registers a `PostgresCluster` CRD. Create a `PostgresCluster` CR declaring replicas, storage size, and backup schedule. The Operator watches the CR and provisions the StatefulSets, PVCs, and CronJobs needed to run the cluster, perform backups, and automatically promote a replica to master if the current master fails.

**Q: You created a CR but no Pods appear. The Operator is not acting. Where do you troubleshoot?**

A: Check the Operator Pod is running (`kubectl get pods -n <operator-namespace>`), tail its logs (`kubectl logs`), and read the CR's `status`/`describe` output. The most common root cause is missing RBAC to create the backing resources - verify the Operator's Roles instead of a ClusterRole and fix the binding.

**Q: A CR hangs in `Terminating` after the Operator was scaled to zero. What is happening?**

A: A finalizer blocks deletion until the Operator performs external cleanup. Because the Operator Pod is down it cannot finalize, so restart the Operator (and restore it before deleting the CR) and let it remove the finalizer and backing resources.

**Q: Your team wants an in-house "order-processing" CRD strictly for stateless Pods. Do you build it?**

A: Probably not. A Deployment plus Helm covers stateless workloads; writing a CRD and controller (an Operator) would add maintenance and RBAC burden without Day-2 automation benefits. Reserve an Operator for stateful lifecycle work, and prefer established operators such as cert-manager or the Prometheus Operator to prove the pattern first.

**Q: A `Certificate` CR created with cert-manager never produces a TLS Secret.**

A: Verify the Certificate CRD is installed (`kubectl get crd | grep cert-manager`), the `Certificate` CR matches the CRD's group/version/kind, and the cert-manager controller Pod is Running. Then check its status conditions (`kubectl describe certificate`) to see DNS validation issues or an incorrect Issuer reference.

**Q: Your Operator has two replicas for high availability, but the same CR's `status` flaps, events flood, and you see `Operation cannot be fulfilled on ...: the object has been modified` conflicts. What is the cause?**

A: The controller was not started with leader election, so both replicas act as leaders and race to update the same CR. Two replicas updating the same object produce conflicting writes, and each successful reconcile triggers another watch event, causing the event storm. Fix it by enabling leader election, for example `--leader-elect` in Kubebuilder/controller-runtime controllers, so exactly one replica reconciles. Confirm the holder with `kubectl get lease -n <namespace>` and check the followers' logs for a message such as `not holding the leader lease yet` or `attempting to acquire leader lease`.

**Q: You changed the CR `spec`, the Operator worked, but `status.observedGeneration` still shows the previous value and the dashboard says it is not yet applied. Why?**

A: Kubernetes bumps `metadata.generation` on every `spec` change, and well-written controllers mirror that value in `status.observedGeneration` so that clients and dashboards can tell the status refers to the latest desired spec. If it is stuck, the controller finished reconciling but failed to persist the status (for example, it returned before writing status, or a later watch read stale data from the informer cache). The fix is to write the status subresource after the reconcile succeeds and stamp the current generation there:

```yaml
status:
  observedGeneration: 3
  observedVersion: "13.1"
  conditions:
  - type: Ready
    status: "True"
```

Consumers treat `status` as current only when `observedGeneration == metadata.generation`, so keeping the two in sync is a key correctness contract for any CRD design.

## Hot Topics / Listening for Senior roles

**Q: How do client-go and the controller-runtime `Manager` differ, and can you build an Operator without Kubebuilder or the Operator SDK?**

A: client-go gives the raw building blocks: the typed and REST clients, cache-backed informers, and the workqueue. controller-runtime wraps this in a `Manager` type that composes the cached client, watch registration, the reconcile loop (a `Reconcile(ctx, Request)` function), owner reference garbage collection, leader election, health and ready endpoints, metrics, and webhook server registration. Kubebuilder and the Operator SDK are scaffolding generators, not dependencies: you can hand-write the same code, building the Manager, registering the reconciler, and writing your own RBAC manifests. Doing so is a good way to prove you understand the machinery instead of relying on generated code.

**Q: How does controller leader election use Leases to run an Operator in high availability?**

A: Each leader-candidate replica races to acquire a `Lease` object (`coordination.k8s.io/v1`) in the operator namespace. The winner stores its identity in `holderIdentity` and renews the lease before `leaseDurationSeconds` expires; losing replicas stay idle in the cache and only reconcile after the lease expires (for example, the leader crashes) and one of them acquires it. This is the same mechanism kube-controller-manager uses with `--leader-elect`. Verify the current holder with `kubectl get lease -n <namespace>`; followers log messages such as `not holding the leader lease` or `attempting to acquire leader lease`. HA operators must have exactly one active controller, so without leader election the replicas fight over the same CRs and generate conflict errors.

**Q: Why does a controller need rate-limited workqueues and informer indexers?**

A: The workqueue decouples watching from reconciling and adds a delay between retries so a broken resource cannot hot-loop the API Server. The default `DefaultControllerRateLimiter` combines an exponential failure limiter (base delay that grows to a cap, for example 5 ms to 1000 s) with a token bucket that limits overall to a given rate per second. Indexers build secondary lookups over the informer cache inside the Manager, so the reconciler can look up objects by namespace, label, or owner reference instead of relisting and filtering everything on every loop. Both are what keep a single faulty CR from saturating the API Server, the informer cache, and resource counters.

**Q: How do `x-kubernetes-validations` CEL expressions compare with validating webhooks, and when do you need a conversion webhook?**

A: CEL expressions live in the CRD OpenAPI schema and are evaluated by the API Server at admission with no extra request - they were introduced in Kubernetes 1.23 and reached GA in 1.25, and they are the recommended way to express self-contained rules such as `self.minReplicas <= self.maxReplicas`. They cannot read other objects or external state, so cross-resource logic needs a validating webhook and a `ValidatingWebhookConfiguration`. A conversion webhook is a different concern: it is required when you serve multiple CRD versions and the storage version is not the one clients read and write, using a hub-and-spoke model (for example, `v1` as hub, `v1beta1` as spoke). Validation and conversion are separate admission steps, and interview answers often conflate them.

**Q: How do you design mutating versus validating admission webhooks, and what is the CSR pattern?**

A: A `MutatingWebhookConfiguration` runs first and changes the object being requested (defaults, labels, sidecar injection), while a `ValidatingWebhookConfiguration` runs afterward and rejects invalid objects; both receive an `AdmissionReview` with the new and old object. Mutating webhooks must be idempotent (re-invoking them on `dry-run` requests or on the same object must not change its output again). The CertificateSigningRequest (CSR) pattern is the standard way to bootstrap and rotate certificates in the cluster: an agent generates a private key, submits the public key as a CSR, an approver approves it, and the signing controller puts the signed `PEM` into `status.certificate`; vendors such as cert-manager use this for webhook serving certificates.

The maturity model helps assess whether an operator design is senior-grade: OperatorHub's model ranks from Basic Install up to Autopilot. Use reference operators to benchmark your design - cert-manager for webhook and CSR hygiene, Strimzi for multi-version CRD design and leader election at scale, and Crunchy PGO for stateful status conditions and finalizer discipline. A candidate operator that does not handle seamless upgrades and full lifecycle is not production-ready.

## Commands and Quick Reference

| Command | What it does |
|---------|--------------|
| `kubectl get crd` | List installed CRDs with their group, scope, and kind |
| `kubectl describe crd <name>` | Show the CRD group, scope, versions, short names, and OpenAPI schema |
| `kubectl get <plural-resource>` | List Custom Resource instances, for example `kubectl get crontabs` |
| `kubectl get apiservices` | Verify the aggregated API service behind a CRD is in `Available` state |
| `kubectl get events --field-selector involvedObject.kind=<Kind>` | Filter events by the involved object's kind to trace CR activity |
| `kubectl proxy -v=9` | Run a local API proxy with verbose logging to trace the admission and webhook traffic |
| `kubectl apply --server-side` | Server-side apply for Custom Resources, records field ownership and reduces conflicts |
| `kubectl explain <resource>` | Print the field-by-field OpenAPI documentation for a CRD kind |

## Related

- [Revision - Operators](../revision/operators.md)
- [Lesson 33 - Extending Kubernetes (CRDs and Operators)](../docs/11-operators/lesson-33-extending-kubernetes-crds-and-operators.md)
- [Lesson 34 - Operators in Practice](../docs/11-operators/lesson-34-operators-in-practice.md)
- [Interview - GitOps and Packaging](gitops.md)

[Back to Interview Index](README.md)