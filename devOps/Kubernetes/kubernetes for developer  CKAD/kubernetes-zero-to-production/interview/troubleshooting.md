---
title: Interview - Troubleshooting
module: 13 Troubleshooting
status: Complete
tags: [interview, troubleshooting, debugging, logs, diagnosis]
---

# Interview - Troubleshooting

## Beginner

**Q: Where do you start when a Pod is failing?**

A: `kubectl get pods`, then `kubectl describe pod <name>` for events and conditions, then `kubectl logs <pod>`. Work outside-in: Service -> endpoints -> Pod -> container.

**Q: What does `kubectl logs --previous` do?**

A: For a CrashLoopBackOff container, it reads the logs of the previous (crashed) instance. The current logs are empty because the container just restarted, so the real error is in the previous logs.

## Intermediate

**Q: A Service is not routing traffic. How do you debug?**

A: Check `kubectl get endpoints <svc>` (empty = selector mismatch or Pods not Ready), verify Pods are Running/Ready, confirm the Service `targetPort` matches the container port, then probe the Pod IP directly and check NetworkPolicy/DNS.

**Q: A node is NotReady with DiskPressure. What is happening?**

A: The node's disk is over the eviction threshold. The kubelet is evicting Pods. Investigate large volumes, logs, and images; then compact/clean up or add capacity.

**Q: How do you identify which Pods are on a node?**

A: `kubectl get pods -A -o wide | grep <node>` or `--field-selector spec.nodeName=<node>`.

## Scenario

Q: The API server is reachable but a Service suddenly 502s. Step by step.

A: Check the Ingress/Service endpoints and the backing deployment; see if Pods crashed (`describe`), the targetPort is correct, and the app is actually listening. Use `kubectl get endpoints`, `kubectl top pod`, and event logs.

## Advanced

**Q: A Pod is stuck in `Terminating` for 15 minutes despite `kubectl delete pod <name>` having run. What can block deletion?**

A: A terminating Pod survives until two things settle: every blocking finalizer is cleared, and the kubelet confirms the container has actually stopped.

1. **Finalizers.** Check `kubectl get pod <name> -o jsonpath='{.metadata.finalizers}'`. An operator or admission webhook can add a finalizer that blocks deletion (for example `kubernetes.io/pv-protection` on a PV-bound Volume). The object stays stuck until that controller clears the finalizer. If the controller is dead, force-clear the list only after you prove it cannot come back:

```bash
kubectl patch pod <name> -p '{"metadata":{"finalizers":[]}}' --type=merge
```

2. **Owner references and garbage collection.** Inspect `kubectl get pod <name> -o jsonpath='{.metadata.ownerReferences}'`. If the parent Deployment/StatefulSet was deleted with an `Orphan` deletion propagation policy (or the GC controller is stuck), the children are not cascaded and can be stranded. `kubectl get pods -A --show-owner` shows the whole ownership graph at a glance.
3. **A dead or unreachable node cannot confirm termination.** When its kubelet never answers the deletion call, the API server keeps the pod "Terminating" for a long grace window. Prove the container actually stopped at the runtime level (`crictl ps -a` below) before forcing.
4. **Last resort, with evidence captured:**

```bash
kubectl delete pod <name> --grace-period=0 --force
```

Run this last; force-delete skips grace period and runtime teardown, so it can orphan containers on nodes.

**Q: A Deployment is stuck in ImagePullBackOff even though "the image exists." Build the triage checklist.**

A: ImagePullBackOff means the container runtime never succeeded in pulling the image. `kubectl describe pod <name>` gives the exact reason in the Events block.

1. **Read the actual pull error.** `Failed to pull image ... unauthorized` (registry auth), `manifest unknown`/`not found` (bad tag or missing arch), `invalid reference format` (typo in the tag). Match the follow-up to the message instead of guessing.
2. **Registry authentication.** A private registry image (Docker Hub private, ECR, GCR) without `imagePullSecrets` fails with `unauthorized`/`access denied`. Create a `kubernetes.io/dockerconfigjson` Secret and reference it in the pod spec:

```yaml
spec:
  template:
    spec:
      imagePullSecrets:
      - name: registry-creds
```

3. **Tag and digest correctness.** Copy the exact image string the pod uses:

```bash
kubectl get deployment <name> -o jsonpath='{.spec.template.spec.containers[0].image}'
```

Note whether it is a tag (`:latest`) or a digest (`@sha256:...`). A digest that was deleted or never pushed fails with manifest unknown while the old tag works. Prefer digest pinning in production.
4. **Architecture mismatch.** A single-arch image (linux/amd64) pulled on an arm64/s390x node returns `not found` (the registry has no matching manifest entry) and a multi-arch image can start then die with `exec format error`. Check node arch with `kubectl get nodes -o wide` (ARCH column).
5. **Registry reachability.** Egress-firewalled nodes cannot reach the registry. Prove runtime-level connectivity on the node: `crictl pull <image>` inside the node tells you immediately.
6. Fix the root cause, then re-trigger only the rollout (`kubectl rollout restart deployment/<name>`); do not hand-delete pods one by one.

**Q: A Pod is in CrashLoopBackOff, `kubectl logs` is empty. How do you map the exit code to the cause?**

A: Every restart records a container exit code; the current shell sees empty logs because the container has not repopulated them. Read the `--previous` logs and the terminated state directly:

```bash
kubectl logs <name> --previous --tail=200
kubectl get pod <name> -o jsonpath='{range .status.containerStatuses[*]}{.name} exit={.state.terminated.exitCode} reason={.state.terminated.reason}{"\n"}{end}'
```

| Exit | Signal / meaning | Typical cause |
|------|--------------------|---------------|
| 0 | Clean own exit | Batch one-shot that a Deployment mindlessly restarts; probe killed a healthy process |
| 1 | Generic application error | Code path threw; read `--previous` logs for the stack trace |
| 126 | Cannot execute | Entrypoint exists but lacks executable permission or has no shebang |
| 127 | Command not found | Entrypoint/path typo; the binary is missing from the image |
| 137 | SIGKILL | Usually OOM: the kernel killed the container above its memory limit. Also used by forced eviction/drain kills |
| 143 | SIGTERM | Graceful SIGTERM: drain/shutdown signal, or the process itself handled it |

Note: 137 is over-indexed to OOM, but a node drain also delivers SIGKILL. Confirm with the `reason` field (`OOMKilled` vs `ContainerCannotRun`/`Killing`) and node events before raising memory limits.

**Q: An app starts correctly locally but its Pod is crash-looping in the cluster. Could a probe be the killer?**

A: Yes - livenessProbe evaluates from the container start (plus `initialDelaySeconds`) regardless of whether the process has finished booting. A JVM or slow ORM startup that exceeds the probe interval gets SIGKILL while technically healthy.

The correct control for slow start is a `startupProbe`: it runs first, and liveness/readiness are gated off until it succeeds. It also keeps the container from being restarted while the image warms up:

```yaml
startupProbe:
  httpGet:
    path: /healthz
    port: 8080
  failureThreshold: 30   # 30 x 5s = up to 150s startup window
  periodSeconds: 5
```

Then distinguish: `kubectl logs --previous` still shows whether the container died from the probe (`reason=Killing`, exit 137) or from an app crash (exit 1). Do not raise memory limits first; establish probe and process time first.

**Q: A worker node goes NotReady. `kubectl describe node` shows only "Ready=False". What is the actual diagnosis path?**

A: Ready is set by the controller manager's node-monitor based on the kubelet heartbeat; after `node-monitor-grace-period` (40s default) the node flips NotReady. A NotReady node almost always means the kubelet, its runtime, or the CNI is failing (net reachability to the API server also qualifies).

1. **Kubelet process.** On the node: `systemctl status kubelet` and `journalctl -u kubelet -f`. Corruption of `/var/lib/kubelet/config.yaml` or a flag change frequently crashes the kubelet; the journal shows the parse or cert error.
2. **Container runtime down.** The kubelet depends on the CRI. If containerd/CRI-O is dead, kubelet logs show `failed to connect to runtime` and `crictl ps` on the node fails too. Restart the runtime before the kubelet.
3. **/var/lib/kubelet state.** Logs and volume data live here; a full disk or unwritable mount stops the kubelet from writing. Check `df -h /var/lib/kubelet`. `imagefs.available` and `nodefs.available` drive the `DiskPressure` condition.
4. **Certificates.** The kubelet talks to the API server on 10250 using certs in `/var/lib/kubelet/pki`. An expired cert or a recreated node makes readiness fail; the journal shows `x509` errors.
5. **CNI dead.** Pods cannot get IPs, the kubelet cannot finalize sandboxes, and the node stays NotReady. Check the CNI DaemonSet pods (Calico/Cilium/Flannel) and their logs.
6. **Fallback.** When kubelet and runtime are both healthy, the failure is usually connectivity to the control plane (firewall, DNS, or the API server address).

Drain before reboot: `kubectl drain <node> --ignore-daemonsets --delete-emptydir-data` so the API does not keep scheduling onto a node about to come down.

**Q: Pod A reaches Pod B by IP, but the Service ClusterIP times out. Which layer should you drill into?**

A: Pod-to-Pod traffic passes through the CNI data plane only; the Services layer is added by kube-proxy, which programs iptables/ipvs/nftables (or the CNI handles service routing). Working Pod-to-Pod IP traffic nearly proves the CNI is fine, so the fault sits in the proxy path.

1. **Missing endpoints first:** `kubectl get endpoints <svc>`. An empty endpoint list fails any proxy, and its cause is selector/readiness, not kube-proxy.
2. **Kube-proxy process:** `kubectl get pods -n kube-system -l k8s-app=kube-proxy` and its logs (`Back-off restarting failed container` usually means its API access or ConfigMap is broken).
3. **NAT rules on the node:** in iptables mode `iptables-save -t nat | grep <clusterIP>` or `iptables -t nat -L KUBE-SERVICES -n`; in ipvs mode `ipvsadm -L -n`. Missing rules means kube-proxy never programmed the Service.
4. **Packet trace:** on the destination node run `tcpdump -ni <iface> tcp port <targetPort>` while the client curls the ClusterIP. Packets arriving but no reply: the app listens on a different port or the `targetPort`/`containerPort` mismatches. No packets: the DNAT rule is absent (rule check above) or a node-level `rp_filter`/firewall is dropping forwarded traffic.

**Q: Every pod in the cluster fails to resolve in-cluster DNS names, while the API server stays reachable. Where do you look?**

A: DNS is served by CoreDNS (Deployment, usually two total replicas) in kube-system.

1. **Deployment health:** `kubectl get pods -n kube-system | grep coredns`. A CoreDNS pod in CrashLoopBackOff usually points at a bad CoreDNS ConfigMap; read the events with `kubectl describe pod -n kube-system -l k8s-app=kube-dns` and check `kubectl logs -n kube-system <coredns-pod>`.
2. **Endpoints:** `kubectl get endpoints -n kube-system kube-dns`. Zero endpoints means no CoreDNS pod is Ready, so the Service has nothing to forward to.
3. **Resolve from the inside:** run

```bash
kubectl run dns-test --rm --restart=Never -i --image=busybox:1.36 -- nslookup kubernetes.default.svc.cluster.local
```

If the test succeeds but the app still fails to resolve, diff `dnsPolicy` (`Default` points the pod at the host resolver and skips CoreDNS entirely) and the kubelet `--cluster-dns`/`--cluster-domain` flags.
4. A flapping coredns that restarts under load: check `coredns_dns_requests_total`/`coredns_panics_total` in Prometheus, then raise `resources.limits` or `replicas` from the Deployment.

**Q: `kubectl describe pod` shows no events, but the container still fails. Where do you go when Describe is empty?**

A: Events surface Pod-level actions and their failures (scheduling, pull, mount, probes). Once the image is pulled, volumes are mounted, and the process spawns, a crash inside the container produces no new event; the record is the log and the exit code. "No events" is normal for an app-level bug.

1. **Logs first:** `kubectl logs <name> --previous --tail=200`, then the jsonpath for exit code/reason rather than the spec.
2. **Resource vs container:** an OOMKilled container exits 137 with `reason=OOMKilled`, and that IS an event; an exit 1 with no event is almost always an app or config bug. Check `kubectl top pod` to compare the app's RSS to `resources.limits.memory`.
3. Pull insights at the **runtime level** - the kubelet merely reports; the runtime owns the container. On the node:

```bash
crictl ps -a                              # status of every container incl. exited
crictl logs <container-id>                # runtime logs (may exist beyond kubectl)
crictl exec -it <container-id> sh         # exec into a running container
```

nerdctl, the containerd front-end, reads the same state: `nerdctl ps -a --namespace k8s.io`.
4. **Scoop anything that missed the pod's tail** by filtering events directly: `kubectl get events --field-selector involvedObject.name=<pod>,involvedObject.kind=Pod --sort-by=.lastTimestamp` in the pod's namespace.
5. **Debug with temporary tools:** if the container lacks `tcpdump`/`curl` or cannot start at all, add an ephemeral container with the tools you need: `kubectl debug pod/<name> -it --image=nicolaka/netshoot --target=<container>` (no spec change). For node access: `kubectl debug node/<node> -it --image=busybox:1.36 -- chroot /host`.

**Q: `kubectl get` hangs. The API server port answers TCP, but read/write requests stall. How do you split "API server process is up" from "API server storage is broken"?**

A: The process being listenable is not the cluster being usable. Use the fine-grained health endpoints; `/healthz` is liveness of the process, `/livez` and `/readyz` allow you to separate where the chain breaks:

```bash
kubectl get --raw='/livez?verbose'        # apiserver-only; unaffected by etcd
kubectl get --raw='/readyz?exclude=etcd'  # readiness of everything minus etcd
```

- `/livez?verbose` OK but `/readyz` fails: apiserver is up, etcd is the dependency that is down — likely members lost quorum (a majority, `n/2+1`, must be reachable), etcd peering broken, or certificates expired.
- The apiserver fails as well: crashloop of the `kube-apiserver` static pod (`kubectl logs -n kube-system kube-apiserver-<node>`) or it cannot reach etcd.

**etcd and certificate failures** deserve a specific section callout:

- Verify the client cert's expiry used by the apiserver for etcd access:

```bash
openssl x509 -in /etc/kubernetes/pki/apiserver-etcd-client.crt -noout -enddate
```

An expired or mismatched `--etcd-cafile`/`--etcd-certfile` produces the classic log line `Unable to connect to etcd: x509: certificate has expired...`. Reissue the client cert and reconcile the CA.
- Even after the certs are OK, check etcd health and membership:

```bash
ETCDCTL_API=3 etcdctl --endpoints=https://127.0.0.1:2379 \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  endpoint health
```

Loss of quorum (a majority, `n/2+1`, of etcd members offline) makes the entire API read+write path fail; restore from backups before making changes. Only reach for `--force-new-cluster` as a last-resort data-loss trade.

## Gotchas

- Events are short-lived: they are retained about 1 hour (then evicted from the API). Capture `kubectl get events` early; do not expect describe to keep history after a long wait.
- A silent Describe is not no problem. Container crashes, probe deaths, and app errors write to the log and exit code, not to the Events block. Later fix path: `logs --previous`.
- `kubectl logs --previous` only works if the container has already restarted; on a first-tick crash run a normal `logs` with no previous.
- Probe false alarms: a slow cold start (JVM, warm caches) belongs in the `startupProbe`, not more `initialDelaySeconds` on liveness, or the kubelet keeps restarting a healthy pod.
- Port-forward proves only ONE link, the path node -> pod. `port-forward pod` working while `port-forward svc` fails means the app is fine but the Service selector/endpoints (or kube-proxy) are broken; `port-forward svc` working but the Ingress 502 means ingress routes to a wrong `backend.service.name`/port.
- Drain hanging: `kubectl drain` stuck on a node is frequently a PodDisruptionBudget holding it up. `kubectl get pdb -A`; a pod that cannot meet the PDB count will not evict. Fix by scaling the Deployment up, relaxing the PDB, or (last) `--disable-eviction`.
- `kubectl get pods` Running is not ready-to-serve: readiness and liveness are judged separately, so always confirm the app port is actually bound inside the pod.

## Commands and Quick Reference

```bash
# Wide overview first
kubectl get nodes -o wide
kubectl get pods -A -o wide
kubectl get events -A --sort-by='.lastTimestamp'

# Drill into a pod
kubectl describe pod <name>
kubectl logs <name> --previous --tail=200      # crashed instance
kubectl logs <name> -c <container> --tail=50   # per-container when the pod is multi
kubectl get pod <name> -o jsonpath='{.status.containerStatuses[*].state.terminated.exitCode}'
kubectl get events --field-selector involvedObject.name=<name> --sort-by=.lastTimestamp

# Networking debug
kubectl get endpoints <svc>
kubectl get svc -o wide
kubectl port-forward svc/<svc> 8080:80                  # svc-layer proof
kubectl port-forward pod/<pod> 8080:<containerPort>     # pod-layer proof
kubectl run dbg --rm -it --image=nicolaka/netshoot --restart=Never -- bash

# On-node runtime ground truth (run on the worker)
crictl ps -a
crictl logs <id>
crictl rm <id>            # clean a dead container
nerdctl ps -a --namespace k8s.io    # containerd front-end equivalent

# Ephemeral debug containers (no spec change)
kubectl debug pod/<name> -it --image=nicolaka/netshoot --target=<container>
kubectl debug node/<node> -it --image=busybox:1.36 -- chroot /host

# APIServer / etcd health
kubectl get --raw='/livez?verbose'
kubectl get --raw='/readyz?verbose'
openssl x509 -in /etc/kubernetes/pki/apiserver-etcd-client.crt -noout -enddate
```

## Related

- [Revision - Troubleshooting](../revision/troubleshooting.md)
- [Lesson 17 - End-to-End Traffic Flow and the 502 Bad Gateway](../docs/04-networking/lesson-17-end-to-end-traffic-flow-and-the-502-bad-gateway.md)

[Back to Interview Index](README.md)