---
title: Interview - Configuration
module: 06 Configuration
status: Complete
tags: [interview, configuration, configmap, secret, resources, quota]
---

# Interview - Configuration

## Beginner

**Q: What is a ConfigMap?**

A: A resource for storing non-secret configuration as key/value pairs, injected into Pods as environment variables or mounted files.

**Q: What is the difference between ConfigMap and Secret?**

A: Secrets are intended for sensitive data and are base64-encoded in `data` (with plain `stringData` in manifests). Both are used the same way, but Secrets should be encrypted at rest and managed carefully.

**Q: What is the `immutable` field on ConfigMaps and Secrets?**

A: It makes the resource read-only after creation. You cannot modify or delete a ConfigMap or Secret marked immutable; you must delete and recreate it. This gives the kubelet a guarantee that the mounted data never changes, which improves performance because the kubelet does not need to watch for updates.

**Q: What are resource requests and limits in simple terms?**

A: Requests are the amount of CPU or memory the container is guaranteed and reserved for scheduling. Limits are the maximum the container may use; exceeding a memory limit kills the container (OOMKilled), while exceeding a CPU limit throttles it.

## Intermediate

**Q: How do you inject a Secret into a Pod?**

A: As env vars via `secretKeyRef` or `envFrom`, or mounted as a volume. The token path `/var/run/secrets/kubernetes.io/serviceaccount` is the ServiceAccount token.

**Q: What happens when you change a ConfigMap?**

A: Mounted volumes update after a short delay. Env vars are set at Pod start and do not update until the Pod is recreated.

**Q: `envFrom` vs individual `valueFrom` references - what is the difference?**

A: `envFrom` injects every key of a ConfigMap or Secret into the environment in one shot, which is convenient but conflicts can arise if keys collide or are invalid. Individual `valueFrom` entries map one specific key to one env var name, giving explicit control and renaming. Prefer individual references when keys are not valid identifiers for env vars, or when you need to select only part of the data.

**Q: What is the difference between `data` and `stringData` in a Secret?**

A: `data` holds values that are base64-encoded. `stringData` holds plain text values that the API server encodes at write time; it is never written back to the stored object, so you should not expect to read `stringData` back. Use `data` for programmatic consumers and `stringData` for readability in GitOps manifests.

**Q: What are the common Secret types?**

A: `Opaque` (arbitrary key/value), `kubernetes.io/tls` (for `tls.crt` and `tls.key`), `kubernetes.io/dockercfg` / `kubernetes.io/dockerconfigjson` (registry credentials), `kubernetes.io/basic-auth`, and the service account token type. The type is metadata: it helps controllers and tooling interpret the fields correctly.

**Q: What is a ResourceQuota and what does it enforce?**

A: A ResourceQuota constrains total resource usage in a namespace: aggregate requests and limits across all Pods (e.g. `requests.cpu`, `limits.memory`), plus counts for objects like Pods, Services, PVCs. It is a namespace-level policy creating errors when the sum would exceed the quota. It does not set per-Pod defaults.

**Q: What is a LimitRange, and how does it differ from a ResourceQuota?**

A: A LimitRange applies per-object policies within a namespace: min/max for a single Pod or container, and default requests/limits when a Pod does not set them. A ResourceQuota governs the total across the namespace. They operate at different levels: a LimitRange can add default requests that a ResourceQuota then counts against.

**Q: What is the default environment for a Pod if requests and limits are not set?**

A: If no LimitRange exists, a Pod defaults to no requests and no limits, classified `BestEffort`. It may be scheduled anywhere and can use all node resources, but under memory pressure it is evicted first.

## Advanced

**Q: What are resource units for CPU and memory, and how are they written?**

A: CPU is measured in cores, written as integer or milli-core (`1`, `500m`, `0.5`). Memory uses the binary units `Ki`, `Mi`, `Gi`, `Ti` (2^10 scale) and the decimal SI units `k`, `M`, `G` (10^3 scale); they differ, `1Gi` is 1024 Mi but `1G` is 1000 M. Confusing `Mi` with `M` is a classic source of quota and capacity mismatch surprises.

**Q: What is the `envFrom` vs `env` interplay for Secret and ConfigMap keys?**

A: `envFrom` imports every key as a container env var name. If a key is not a valid identifier (e.g. contains `-`), the invalid keys are skipped silently. With `env` you control the exact mapping through `secretKeyRef`/`configMapKeyRef`. When both set the same env var name, the last entry in the list wins. Mounting the data as files has no such name restrictions, since filenames can hold arbitrary characters.

**Q: Why is a value you changed in a mounted ConfigMap sometimes still old, and how does the kubelet sync period cause it?**

A: Mounted files refresh when the kubelet syncs the Pod from its local cache, driven by the kubelet sync period (default 1 minute) plus a watch or volume refresh. This is not instantaneous, so a change to the ConfigMap can lag up to ~1 minute. Env vars do not update at all until the Pod is recreated.

**Q: How are Pods ordered for eviction when the node hits memory pressure?**

A: Kubelet evicts based on usage above capacity/requests. Priority order is effectively: BestEffort pods are evicted first, then Burstable pods over their requests, then Guaranteed pods last (and even Guaranteed can be evicted when it exceeds its limits). You can tune with `--eviction-hard`. Requests and limits are what sort pods into the QoS buckets.

**Q: What is the exact rule for a Guaranteed QoS Pod?**

A: Both requests and limits must be set for both CPU and memory on every container, and they must be equal (request == limit) for each resource on each container. If a request is omitted, the kubelet defaults it to the limit, so equality can be inferred. Burstable = at least one request or limit set and requests != limits for some resource. A Pod of any QoS class can still be killed when it exceeds a memory limit it set (cgroup OOM), independent of eviction order.

**Q: What is the relationship between a memory limit and OOMKill, and what about CPU limits?**

A: A memory limit creates a cgroup limit; if the container exceeds it the kernel OOM-killer kills a process in that cgroup (`OOMKilled` while other Pods stay healthy). CPU limits are throttling: the cgroup is allowed to use only the limited CPUs, so the process continues but only slower. A container with a CPU limit has no similar kill and pause; the limit is enforced by throttling.

**Q: What happens when a container requests more than the node can offer, or when limits are unset?**

A: If requests exceed what any node can satisfy, the scheduler leaves the Pod `Pending` (Unschedulable), not OOMKilled. If limits are unset but requests are, the container can burst beyond its request and consume spare node capacity until other workloads are evicted; this is why large `requests` without matching limits still keeps the Pod safely schedulable but allows noisy-neighbor spikes.

**Q: Actually, what is the difference between requests and limits on scheduling?**

A: Requests participate in scheduling: the sum of requests across pods must fit on the node, and the scheduler uses them to find a fit. Limits are only enforced after placement, by throttling (CPU) or the cgroup (memory). Increasing limits without increasing requests may place too many pods on a node (oversubscription) and then let them throttle or get killed.

**Q: What is the interplay between `resources.requests` and nodes with allocatable capacity?**

A: Kubelet reports `Allocatable`, which is `capacity` minus reserved system resources; requests are what the scheduler uses to fit Pods onto a node. Setting a request larger than allocatable leaves the node unable to admit the Pod. Requests affect scheduling and eviction, limits affect throttling and OOM - a key distinction to remember.

**Q: What are the differences between `immutable: true` and a normal ConfigMap for running workloads?**

A: With an immutable ConfigMap you trade flexibility for safety: no hot updates, no accidental drift, and the kubelet can skip watch and hash overhead. Any rollout that removes/renames the CM fails with a NotFound until a new CM is created and referenced. Use immutability for long-running config that changes rarely.

**Q: What is "encryption at rest" and what supports it in Kubernetes?**

A: Encryption at rest protects data inside etcd: the kube-apiserver encrypts objects as they are written, using the `EncryptionConfiguration` and an `--encryption-provider-config` file. Providers include `aesgcm`, `aescbc` (legacy), `identity` (no-op), and `kms` (an envelope with a KMS or cloud manager). Secrets, ConfigMaps and other object types are included. This is separate from encryption in transit (TLS): a Secret fetched by `kubectl get secret -o yaml` is base64-encoded, which is *not* encryption, and the value is only protected at rest if etcd encryption (or KMS) is enabled.

**Q: How are ServiceAccount tokens provided to Pods and when is a Secret used?**

A: The kubelet places a projected ServiceAccount token at `/var/run/secrets/kubernetes.io/serviceaccount/token` - a JWT minted by the API server. Modern clusters use a projected, short-lived, bound token; the `kubernetes.io/service-account-token` Secret type is the legacy fallback. Additional secrets can be created and mounted by referencing them in the Pod spec.

**Q: What are best practices for secret rotation and injection?**

A: Prefer volume mounts over env vars so updates are picked up; avoid env-var injection for anything sensitive because any process that can read `/proc/<pid>/environ` (including with `hostPID`) can see the values. Note that trailing whitespace in file-mounted values is stripped. Rotation typically means recreating the Secret and letting the kubelet refresh mounted files, or forcing a rollout.

## Gotchas

**Q: True or False: Base64 encoding makes a Secret encrypted on the wire and at rest.**

A: False. Base64 is encoding, not encryption or obfuscation. Anyone with access to the Secret object (and to etcd when encryption at rest is off) can decode it trivially. Real protection comes from RBAC, encryption at rest, and reducing secret exposure.

**Q: True or False: A Secret can be updated while a Pod mounts it and the container immediately picks up the new values.**

A: False for env vars; only mounted files are refreshed, and only after the kubelet sync. Env-var injection is fixed at Pod creation. If you need live updates, mount the Secret as a volume and re-read it, and account for the kubelet sync period.

**Q: True or False: A Pod with a memory limit can be OOMKilled by the kernel even if the node has free memory.**

A: True, and the common wrong answer is "the node must be out of memory first". A memory limit creates a cgroup boundary for the container; usage at the limit triggers an OOM kill inside that cgroup even when other node memory is idle. The QoS class does not protect against hitting your own container limit.

**Q: True or False: BestEffort pods have no requests and no limits, so they can stick on a node forever.**

A: False on two fronts. BestEffort pods have no requests or limits, but that means they hold no reservation and consume every resource they touch; the kubelet evicts them first under node memory pressure. "Sticks forever" is the opposite of reality - they are cleared first, not last.

**Q: True or False: Adding a CPU limit without a memory limit makes the Pod QoS Guaranteed.**

A: False. Guaranteed requires requests equal to limits for both CPU and memory on every container. A CPU-only limit (or any request != limit) makes the Pod Burstable. Guaranteed is an all-or-nothing classification.

**Q: True or False: A ResourceQuota and a LimitRange do the same job.**

A: False. ResourceQuota caps totals in a namespace (aggregate requests, limits, and object counts). LimitRange sets min/max and defaults for individual Pods and containers. They operate at different levels and you often need both.

**Q: Common wrong answer: "Setting a memory limit too low can crash the host node."**

A: The kernel OOM kill hits the container's cgroup first, not the host. Node-level memory pressure is governed by the sum of requests versus node capacity and the eviction thresholds, not by any single container's low limit.

## Commands and Quick Reference

| Task | Command |
|------|---------|
| List ConfigMaps | `kubectl get configmaps` |
| Detail a ConfigMap | `kubectl describe configmap <name>` |
| Create a ConfigMap from key/value | `kubectl create configmap <name> --from-literal=key=value` |
| Create a ConfigMap from file | `kubectl create configmap <name> --from-file=<path>` |
| List Secrets | `kubectl get secrets` |
| Detail a Secret | `kubectl describe secret <name>` |
| Create a generic Secret | `kubectl create secret generic <name> --from-literal=key=value` |
| Create a TLS Secret | `kubectl create secret tls <name> --cert=cert.crt --key=key.key` |
| Show encoded Secret values | `kubectl get secret <name> -o yaml` |
| List ResourceQuotas | `kubectl get resourcequota` |
| List LimitRanges | `kubectl get limitrange` |
| List Pod resource usage | `kubectl top pod` |
| Check a node's allocatable | `kubectl describe node <node>` |

## Scenario

**Q: Your app is killed with OOMKilled. How do you fix it?**

A: Check memory usage with `kubectl top pod`, raise memory `limits` and `requests` (and the app's heap limits), or investigate a memory leak. Never set a memory limit lower than the app needs.

**Q: You roll out a new ConfigMap version and the app still reads the old config for minutes.**

A: If the app reads it via env vars, the change never applies until Pod recreation, so that explains it. If mounted, the kubelet refreshes files on its sync period (default ~1 minute) and the app must re-read them; many apps cache config, so a restart or a sidecar to watch the config may be needed. Best answer: mount the config, make the app watch the filesystem, or roll the Deployment.

**Q: A new ResourceQuota is applied and a Pod immediately fails with a "exceeded quota" error.**

A: Quota is enforced at admission: the Pod creation is rejected with an `exceeded quota` message. Check `kubectl get resourcequota -o yaml` for `used` vs `hard`, and compare the new Pod's requests/limits against the remaining headroom. A LimitRange can also reject the Pod if the request is outside the allowed min/max, or silently add defaults that push the total over the quota. Each Pod often needs explicit `resources` set.

**Q: One container uses all machine memory. What is the usual reason?**

A: Often the container has no requests and limits (BestEffort) and the app leaks, so over time it devours all node RAM and the kubelet starts evicting other Pods. Set realistic requests and limits and add monitoring. If you set a generous memory limit but forgot requests, the scheduler still only reserves the requests - the limit alone does not protect the node from overcommit.

## Related

- [Revision - Configuration](../revision/configuration.md)
- [Lesson 21 - Resource Management and the OOMKiller (Requests vs Limits)](../docs/06-configuration/lesson-21-resource-requests-limits-and-quotas.md)
- [Lesson 20 - ConfigMaps and Secrets](../docs/06-configuration/lesson-20-configmaps-and-secrets.md)
- [Module 06 - Configuration index](../docs/06-configuration/README.md)

[Back to Interview Index](README.md)