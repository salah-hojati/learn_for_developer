---
title: Interview - Fundamentals
module: 01 Fundamentals
status: Complete
tags: [interview, fundamentals, containers, namespaces, cgroups, kubectl]
---

# Interview - Fundamentals

## Beginner

**Q: What is a container image and what is an image layer?**

A: An image is a read-only template made of layers. Each layer is a set of filesystem changes produced by one instruction in the Dockerfile (RUN, COPY, ADD). Layers are shared between images and cached, which speeds up pulls and shrinks registry bandwidth.

**Q: What is the difference between `docker run`, `docker build`, and `docker push`?**

A: `docker run` creates and starts a container from an image, `docker build` builds an image from a Dockerfile and an optional build context, and `docker push` uploads a local image to a registry so it can be pulled elsewhere.

**Q: What does a container format (image manifest, annotations, content-addressable storage) enable?**

A: The OCI image format makes an image portable across registries and runtimes. The manifest names layers by content digests, so images are tamper-evident and can be cached and reused across tools without depending on a single vendor implementation.

**Q: Why is `kubectl get pods` fast but `kubectl get events` useful in a different way?**

A: `get pods` returns the current desired-and-observed state from the API in one snapshot. `get events` gives the ordered timeline of transitions and errors, which shows *why* a Pod is stuck (FailedScheduling, FailedMount, BackOff) rather than only what the state is.

**Q: What is the difference between a container and a virtual machine?**

A: A VM virtualizes hardware and runs a full guest OS. A container is a Linux process isolated by namespaces and constrained by cgroups, sharing the host kernel. Containers start faster and have a smaller footprint.

**Q: What is the smallest unit you schedule in Kubernetes?**

A: A Pod. It wraps one or more containers that share a network namespace, IP address, and volumes.

**Q: What role does the container runtime play?**

A: It runs containers using the CRI protocol (containerd is the default). The kubelet asks the runtime to pull images and start/stop containers.

## Intermediate

**Q: How do namespaces and cgroups differ?**

A: Namespaces isolate what a process can see (processes, network, filesystem, mounts). Cgroups constrain what it can use (CPU, memory, I/O). Together they provide container isolation and resource limiting.

**Q: What is a kubeconfig and what is a context?**

A: A kubeconfig stores connection information in `~/.kube/config`. A context bundles a cluster, a user, and a namespace; you switch contexts with `kubectl config use-context`.

**Q: What does `docker commit` do and why is it discouraged in production?**

A: `docker commit` snapshots a running container's writable layer into a new image. It is discouraged because the resulting image lacks the build instructions and reproducible steps of a Dockerfile, making it hard to audit, replicate, and rebuild.

## Advanced

**Q: What changed between cgroups v1 and v2, and why does it matter for Kubernetes?**

A: cgroups v1 exposed a single hierarchy per controller (cpu, memory, io) with the "no internal processes" rule, which forced extra child cgroups and made shared-hierarchy accounting messy; it also lacked kernel-memory accounting and pressure-stall (PSI) signals. cgroups v2 unified everything into one hierarchy, keeps kernel memory and usage accounting accurate, adds `memory.high` throttling, and exposes PSI so the kubelet can evict on memory pressure more gracefully. It matters operationally because the kubelet and the container runtime must use the same cgroup driver as the host (modern distros are v2, typically with `--cgroup-driver=systemd`); a mismatch means limits silently do not apply.

**Q: What is the OCI runtime spec / runc, and how does containerd interact with it?**

A: runc is a low-level runtime that implements the OCI Runtime Spec: it creates namespaces, applies cgroup placement, sets rootfs, and spawns the container's process; this is called "runC (OCI runtime)". containerd is the higher-level runtime that handles pulling, unpacking, storage, and image lifecycle, then calls runc (via an OCI runtime shim) to actually run the container. Kubernetes only talks to containerd over CRI (CRI-O is an alternative), so the kubelet never invokes runc directly.

**Q: What is the relationship between docker, containerd, and CRI?**

A: Docker uses a built-in containerd, so containerd is still present. But the kubelet talks to a CRI (Container Runtime Interface) plugin, not to Docker. The dockershim no longer exists since 1.24, so a cluster must use a CRI-compatible runtime directly (containerd or CRI-O). `crictl` is the debugging CLI that speaks CRI to the runtime.

**Q: How do entrypoint and cmd (or the OCI `Entrypoint`/`Cmd`) differ, and what overrides what?**

A: The image sets `Entrypoint` (the process to run) and `Cmd` (the default arguments). On `docker run image cmd...`, the trailing args override `Cmd`, and `--entrypoint` overrides `Entrypoint`. In Kubernetes, a container spec's `command` overrides the image `Entrypoint`, and `args` overrides `Cmd`. If you define only `args`, they append to the image's `Entrypoint`. This distinction is a frequent source of failed containers.

**Q: Why does `kubectl port-forward` work even without a Service, and when does it fail?**

A: `port-forward` asks the API server to establish a narrow tunnel to a specific Pod (or Service, but the pod-level path talks directly to the Pod IP), bypassing Service load balancing entirely. It is fine for local debugging but not for production traffic, because it is per-pod, single-client in practice, and hits the API server proxy, adding load to the control plane.

**Q: What is client-side apply vs server-side apply (SSA)?**

A: Client-side apply (the kubectl default until 1.18) does a local three-way merge and then a full PUT; a client does not know which fields other controllers keep, so concurrent editors race and can clobber each other. Server-side apply (SSA) sends a PATCH that carries the intended value with an ownership signature; the API server records `managedFields` per field, letting multiple controllers and tools co-own a resource without overwriting one another — much safer for GitOps and for `kubectl apply --server-side`.

**Q: What does `kubectl apply --dry-run=client -o yaml` actually produce and not produce?**

A: It produces a dry-run response and prints the object as YAML — but it does NOT persist anything; `-o yaml` prints the input manifest (or the generated object when used with generators/`--validate`). It is handy for validating YAML and inspecting generated UUIDs, but does not run admission controllers or dry-run server-side logic unless you pass `--dry-run=server`, which is much closer to actually applying policy: admission + validation but no persistence.

**Q: What is an "owner reference" and why does orphaned cleanup matter in `kubectl delete`?**

A: An owner reference sets the relationship (e.g., a ReplicaSet owns Pods). Deleting the owner's garbage collection removes owned objects. `kubectl delete --cascade=foreground` (default is foreground) makes the owner wait for children before full deletion; `--cascade=orphan` leaves children behind, which is useful for pre-upgrade snapshots but can leak Pods/ReplicaSets if misused.

**Q: How do image layers and overlayfs work under the hood?**

A: A container image is a stack of read-only layers; the runtime (containerd) downloads and verifies them by digest, then mounts them using an overlay filesystem (overlay2) where lowerdir holds the layers and upperdir is the writable container layer. Reads are served from the layers bottom-up, writes go to the upperdir with copy-on-write for modified files. Deleting a file in a lower layer creates a whiteout in the upper dir; that is why image layer order and `docker build` caching change the final disk footprint.

**Q: Why does a container's PID 1 matter, and what is zombie reaping?**

A: PID 1 in a container is the process that must reap orphaned/zombie children — when a child is killed, it becomes a zombie until its parent calls wait(); if PID 1 does not do this, zombies accumulate. Most images run an init like `tini` or a signal-aware entrypoint precisely to trap SIGTERM, forward it to the app, and reap grandchildren. If your container ignores SIGTERM or does not reap, Pods take the full `terminationGracePeriodSeconds` before SIGKILL.

**Q: What is the difference between a Namespace in Kubernetes and a namespace in Linux?**

A: Kubernetes Namespace (capital N) is a logical partition of cluster objects (RBAC, quotas, policies); Linux namespaces (lowercase, e.g., pid, net, mount) are kernel isolation features that containers use. They are unrelated; an interviewer may conflate them to see whether you can separate the abstraction layers.

**Q: What is an init container and how does it differ from a regular container?**

A: An init container runs to completion before the main containers start, one at a time and in order. It can have its own image, volume mounts, and security context, but it cannot run probes, has no readiness, and is not restarted when it fails unless the Pod restarts. It is the standard way to prepare the environment (wait-for-db, fetch config, chown volumes) without baking logic into the app image.

## Gotchas

**Q: "`kubectl apply -f deploy.yaml` after editing a Deployment does not restart Pods." True or False?**

A: False if (and only if) the change touches `.spec.template` — e.g., the image tag or `resources.requests` — which triggers a rolling update and replaces Pods. The trap is the flip: fields that *do not* touch the template, like `replicas` or the update `strategy`, never change the Pod spec, and editing a live Pod directly (`kubectl edit pod`) is a no-op because a Pod's spec is immutable. So the answer is "it depends on which field changed": template changes roll, others do not.

**Q: "The PostStart lifecycle hook always runs before the container's readiness probes."**

A: False. PostStart is executed asynchronously by the kubelet: it is *not* guaranteed to complete before the entrypoint starts and it has no fixed ordering with probes. Only the ordering contract is that a hook failure (a non-zero exit, or the hook never returning) marks the container as failed. A blocking hook delays readiness; probe and hook ordering is best-effort, so design services so neither depends on the other's ordering.

**Q: "A Node is a namespaced object."**

A: Trap. Nodes are cluster-scoped: `kubectl get nodes` is not tied to any namespace, and neither are PVs, StorageClasses, or Namespaces themselves. Pods, Services, and ConfigMaps are namespaced. Many candidates mix these up when someone asks "which objects live in a namespace", so enumerate the cluster-scoped set from memory.

**Q: "The default `kubectl exec` gives you a root shell in a container."**

A: Trap. `kubectl exec` runs a process inside the container as whatever user the container is configured with — the image's `USER` or the Pod's `securityContext.runAsUser`, NOT automatically root. It also does not give you the node's capabilities: the debug process is confined by the container's namespaces and cgroup, so it cannot see the node or other Pods. To truly inspect the node you need `kubectl debug node/NODE`, which runs a privileged host-namespace Pod.

**Q: `docker run -it ubuntu` gives a shell, so `kubectl exec` should also give an interactive TTY?"**

A: True-ish only if the Pod is `stdin: true` and `tty: true` in the container spec. Otherwise `kubectl exec -it` errors because the container was not started with an interactive TTY attached. This is why `kubectl exec` on most server containers needs `-i` and the container spec must allocate a TTY to get a prompt.

## Commands and Quick Reference

| Command | Why you run it |
| --- | --- |
| `kubectl auth can-i list pods --as=dev` | Check whether a user/ServiceAccount is allowed to perform the action before troubleshooting RBAC. |
| `kubectl get --raw /readyz` | Hit the API server health endpoint directly to confirm the front door and etcd dependency. |
| `kubectl config current-context` and `kubectl config get-contexts` | See which cluster/user/namespace kubectl is pointed at when a command hits the wrong cluster. |
| `kubectl get pods -o wide` | Get the node, IP, and node name in one line for networking and scheduling diagnosis. |
| `kubectl describe node NODE` | See which pods run there, the node Conditions, and the Capacity/Allocatable split that affects scheduling. |
| `kubectl api-resources` | Discover all API groups, the Kinds and whether each is namespaced. |
| `kubectl explain` | Get the field-level documentation for a resource from the server's OpenAPI schema. |
| `kubectl debug node/NODE` | Create a temporary pod on the node for a shell into the node's namespaces. |
| `kubectl port-forward pod/POD host:container` | Tunnel local to a single pod's container port, for debugging without a Service. |
| `kubectl edit` + `--validate=false` / `kubectl replace --force` | Remove an immutable field (like pod template) or force-recreate an object when edit fails. |
| `kubectl apply --server-side` | Apply with server-side merge handling for predictable multi-controller ownership. |
| `kubectl -o wide` and `kubectl get nodes -o jsonpath` | Extract structured info for scripts (e.g., node internal IPs) in a pipeline. |

## Scenario

**Q: You run `kubectl get pods` and get `Unable to connect to the server: dial tcp`. What do you check?**

A: Check the current context (`kubectl config current-context`), the API server address/port in the kubeconfig, network reachability, and whether the API server is healthy. Verify `kubectl cluster-info`.

**Q: You change the image tag of a Deployment and `kubectl rollout status` hangs. The new Pods stay `CrashLoopBackOff`. What do you do first?**

A: Look at the crash reason with `kubectl logs`, the exit code, and `kubectl describe pod`. Check whether the image exists in the registry that the worker can reach, if the tag is valid, and test with `docker pull` (or crictl on the node). Then decide: fix the image/tag, or `kubectl rollout undo deployment/<name>` to roll back cleanly. Keep `revisionHistoryLimit` in mind to know how far back you can roll.

**Q: A user says "I can `kubectl get pods` but not `kubectl logs`". What's the most common cause?**

A: RBAC: `get pods` only needs `pods/get` (or list/watch), while `pods/log` is a separate subresource and must be present in the Role/ClusterRole. Mirror the check: what verb resources the role grants, and verify with `kubectl auth can-i get pods/log --as=<user>`.

**Q: A Pod is created but `kubectl get pods` shows `ContainerCreating` for minutes. What is the trace?**

A: ContainerCreating usually hangs on image pull, volume mount, or CNI. Use `kubectl describe pod` for the events, `kubectl get events --field-selector involvedObject.name=...`, check on the node with `crictl ps -a` for the CRI state, and `journalctl -u kubelet` for the underlying message. If you see `ImagePullBackOff` or the CNI `InvalidArgument`, fix the registry credentials or the CNI config that the node's kubelet can't use.

**Q: After `kubectl delete service foo`, `kubectl get svc foo` still shows the old ClusterIP. What's going on?**

A: This is a classic "why is deletion stuck" trap: the object has a `metadata.deletionTimestamp` and `metadata.finalizers` still present. Something — often a controller webhook (e.g., a load balancer controller) or a protection finalizer — must remove its finalizer before the API server garbage-collects the object. Inspect `kubectl get svc foo -o yaml` for `finalizers`, check `kubectl get events`, and if the owner is gone, remove the finalizer by patching `metadata.finalizers` to an empty list. The same pattern (`kubernetes.io/pvc-protection`) keeps a PVC in `Terminating` while a Pod still mounts it.

## Related

- [Revision - Fundamentals](../revision/fundamentals.md)
- [kubectl Cheat Sheet](../cheatsheets/kubectl-cheatsheet.md)
- [Lesson 01 - Anatomy of a Container](../docs/01-fundamentals/lesson-01-anatomy-of-a-container.md)
- [Lesson 02 - Namespaces and Contexts](../docs/01-fundamentals/lesson-02-namespaces-and-contexts.md)

[Back to Interview Index](README.md)