---
title: Interview - Networking
module: 04 Networking
status: Complete
tags: [interview, networking, service, ingress, network-policy, dns]
---

# Interview - Networking

## Beginner

**Q: How does a Pod get its IP?**

A: The CNI plugin assigns each Pod an IP on a flat network namespace via, e.g., Calico or Cilium.

**Q: What is a Service?**

A: A stable network abstraction (ClusterIP) in front of Pods, so their IPs can change without breaking callers. kube-proxy implements traffic forwarding.

## Intermediate

**Q: Traffic reaches a Service but fails. Walk me through the debug flow.**

A: First `kubectl get endpoints <svc>` - empty means the selector does not resolve or Pods are not Ready. Then check Pods are running and Ready, exec into a pod and `curl <pod-ip>:<port>`, and check the targetPort matches the container. Verify DNS and NetworkPolicy along the path.

**Q: What is a headless service and when is it used?**

A: A Service with `clusterIP: None`. It returns Pod IPs directly and creates per-Pod DNS A records (e.g. `db-0.db.svc.cluster.local`) so StatefulSet peers can address each other directly instead of random load balancing.

**Q: What does the Ingress Controller do?**

A: An Ingress manifest holds routing rules; the controller (NGINX, Traefik) runs them, terminating TLS and routing HTTP(S) to the right Service by host/path. Ingress is only a rule without a controller.

## Advanced

Q: NetworkPolicy refused. What is its effect?

A: It filters Pod-to-Pod traffic enforced by the CNI. `podSelector: {}` with `policyTypes: [Ingress, Egress]` applies default-deny to a namespace; you then allow specific sources/destinations.

**Q: What are the four Service types and when do you use each?**

A: ClusterIP exposes a stable virtual IP reachable only from inside the cluster; it is the default and the backing for NodePort and LoadBalancer. NodePort allocates a port in the 30000-32767 range on every node for access without a cloud LB (on-prem clusters, demos, control-plane access). LoadBalancer asks the cloud provider for an external IP (or MetalLB on bare metal) and fronts the NodePorts. ExternalName maps to an external DNS CNAME and performs no proxying at all. The ClusterIP is stable for the Service lifetime; it changes only if the Service is deleted and recreated.

**Q: What are kube-proxy's operating modes?**

A: Userspace (legacy proxy mode - a user-space process in kube-proxy listens on the Service IP:port and proxies to one backend at a time, slow and outdated), iptables (the default - kernel NAT rules evaluated linearly per packet), and IPVS (a kernel-space L4 load balancer). iptables is fine for small and mid-size clusters, but every new Service and endpoint appends more chains and each packet walks them in order, so cost grows with cluster scale; IPVS uses kernel hash tables and is preferred for high-scale, node-heavy clusters.

**Q: What features does IPVS add over iptables mode?**

A: IPVS offers multiple scheduling algorithms (round-robin, least-connection, shortest expected delay, source-hash), built-in connection sticky (via persistent connections) and far higher throughput under thousands of Services because lookups are hash-based rather than rule-walk. It is enabled with `--proxy-mode=ipvs` and needs the `ip_vs`, `ip_vs_rr`, `ip_vs_wrr`, and `ip_vs_sh` kernel modules loaded; inspect its rules with `ipvsadm -L -n`.

**Q: What are EndpointSlices and when do you prefer them over Endpoints?**

A: EndpointSlices break the endpoint list into slices of at most 100 entries each (a legacy `Endpoints` object caps at 1000) and can describe multiple address types (IPv4, IPv6, FQDN) per slice. Every slice carries the label `kubernetes.io/service-name` so kube-proxy and CoreDNS select exactly the slices that matter. They are the modern default for publishing Service backends; the `Endpoints` object is still generated for compatibility, and headless Services read these slices to produce per-Pod A records.

**Q: Give the full semantics of a Headless Service, including DNS.**

A: `clusterIP: None` removes the virtual IP; the Service never load-balances and has no NodePort/LoadBalancer entries. A DNS lookup on the Service name returns the set of backing Pod IPs directly, while normal Services return the ClusterIP. StatefulSet Pods additionally get per-Pod A records `<pod-name>.<svc-name>.<namespace>.svc.cluster.local` (e.g. `db-0.db.default.svc.cluster.local`); with `publishNotReadyAddresses: true` unready Pods are published too. Use headless when clients must reach specific named Pods - voting/raft quorums, MySQL/Postgres replicas, sharding - not when they only need a load-balanced name. If callers only need load balancing, use a normal Service.

**Q: What is the NodePort range and what does `ExternalTrafficPolicy` change?**

A: The default `--service-node-port-range` is 30000-32767. With `externalTrafficPolicy: Cluster` (default), an entry node SNATs the packet and may forward to a backend Pod on a different node, so the Pod sees a node IP and replies may return via any node. With `externalTrafficPolicy: Local`, a node only routes to backends living on that node, preserving the true client source IP, at the cost of uneven distribution when Pods are sparse and dropped packets on nodes without a local backend. Client-IP preservation and reliable `sessionAffinity: ClientIP` stickiness both require Local mode, because with Cluster mode every request is SNATed to a node IP and affinity keyed on the real client becomes meaningless.

**Q: How are Ingress path types, wildcards, and default backends evaluated?**

A: `pathType` is `Prefix` (matches by path segments, e.g. `/` or `/api/`), `Exact` (the URI must match exactly), or `ImplementationSpecific` (behavior delegated to the controller). Hosts support wildcards such as `*.example.com`. Requests that match no rule fall through to the `defaultBackend`, which you can set or leave blank. The Ingress resource is declarative only - it has zero effect until a controller such as NGINX Ingress, HAProxy, or Traefik watches it and materializes rules into real proxy config.

**Q: What is the classic NetworkPolicy default-deny trap?**

A: A NetworkPolicy only enforces the directions listed in `policyTypes`. An empty `podSelector: {}` with no rules and no `policyTypes` is a no-op - nothing is blocked. An explicit `policyTypes: [Egress]` with no ingress rules allows all Ingress traffic by default. To fully default-deny a namespace you must state both directions:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-all
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
```

Every blanket rule then becomes an allow-list you reconstruct with explicit allows: CoreDNS lives in `kube-system` and the cluster DNS Service is `kube-dns` at 10.96.0.10; default-deny breaks DNS unless you allow Egress to TCP/UDP 53 for the kube-system namespace plus its return traffic, and calls to Services in the same namespace still need an egress rule for the backend Pod IPs. Remember the enforcement point is the in-place CNI (Calico, Cilium, Weave, and others) - `kubectl get netpol` confirms the object exists, not that it is enforced.

**Q: How does Pod DNS work and what is the ndots trap?**

A: Every Pod gets a `/etc/resolv.conf` with search suffixes (`<ns>.svc.cluster.local`, `svc.cluster.local`, `cluster.local`) and `ndots: 5`. CoreDNS serves `cluster.local` at the `kube-dns` Service IP 10.96.0.10. A short name such as `api` has fewer than 5 dots, so the resolver tries `api.<ns>.svc.cluster.local` before falling back, each miss costing a round-trip and UDP timeout - the classic "DNS is slow" latency trap. The search list is namespace-scoped, so the same short name resolves from inside its own namespace but not from others; always query the FQDN cross-namespace. Debug from inside a Pod (`kubectl exec`) with `nslookup <svc>.<ns>.svc.cluster.local`; if DNS is broken, `getent hosts` or `cat /etc/resolv.conf` tells the story faster. CoreDNS itself runs as Pods in `kube-system` fronted by the ClusterIP `kube-dns`, and apparent DNS propagation delay after a Service rename comes from caching and TTL, not the control plane.

**Q: How does kube-proxy plus conntrack implement a Service?**

A: kube-proxy writes table rules (iptables or IPVS) that DNAT the ClusterIP:port to a selected backend Pod IP:targetPort, placing an entry in the conntrack table so the reply is rewritten back to the original destination and the return packet reaches the client. For external traffic the client source address is also SNATed to the entry node unless `externalTrafficPolicy: Local`. `sessionAffinity: ClientIP` pins a client to a backend by keeping a book on IP->Pod mapping, nothing else, and works only when that source IP survives NAT.

**Q: How do two Pods on different nodes communicate?**

A: The CNI grants every Pod an IP from the cluster Pod CIDR (e.g. 10.244.0.0/16) and wires a local bridge on each node for same-node traffic. For cross-node traffic the CNI must spread the route: either an overlay - VXLAN encapsulates the packet inside UDP over the node's real NIC (default with Flannel or Cilium in VXLAN mode), or routing - Calico BGP peering advertises Pod CIDRs to the fabric. Either way the result is one flat IP space where any Pod can reach any Pod.

**Q: Walk me through `port` vs `targetPort` and how a Service discovers Pods.**

A: `port` is what callers hit on the Service VIP; `targetPort` is the container port the traffic is forwarded to (defaults to `port` if omitted), and the `selector` must match the Pod labels. kube-proxy watches EndpointSlices, which are built from matching, Ready Pods, and programs NAT. For a headless Service it creates no VIP; DNS returns the Pod IPs and names and the client must do its own load balancing.

## Gotchas

**Q: Does applying a NetworkPolicy instantly kill existing traffic?**

A: No. NetworkPolicies gate new connections; flows already tracked in conntrack continue until they finish. New connections are refused immediately, which is why "I denied everything but traffic still flows" is usually someone testing an established connection.

**Q: I switched kube-proxy to `mode: ipvs` but `iptables -L` still shows a full rule set. Is the switch working?**

A: kube-proxy needs a restart to pick up the flag, and the `ip_vs`, `ip_vs_rr`, `ip_vs_wrr` kernel modules must be loaded or the fallback silently re-enables iptables. Verify with `ipvsadm -L -n|head` - rules present in IPVS means the mode is active; a leftover full iptables footprint suggests the fallback.

**Q: Why does the Pod behind a LoadBalancer see a node IP instead of the browser's IP?**

A: Cloud LBs terminate into a NodePort; with `externalTrafficPolicy: Cluster` kube-proxy SNATs the source to the entry node, so the Pod only ever sees node IPs. Set `externalTrafficPolicy: Local` and unwrap `X-Forwarded-For` (or PROXY protocol) in the LB, but remember only nodes that host a backend Pod answer - a LB health check or request to a node with no local Pod is dropped. That is the Local-mode tradeoff.

**Q: I set `sessionAffinity: ClientIP` but requests still hop between Pods.**

A: With `externalTrafficPolicy: Cluster` the node SNATs every request to the same node IP, so the backend cannot distinguish real clients and stickiness degrades to per-node randomness. Pair ClientIP affinity with `externalTrafficPolicy: Local` (or terminate at the LB that preserves source) so the affinity key is the true client address.

**Q: The same Service works from a few jump hosts but drops for the rest, right after a scale-out.**

A: Under `externalTrafficPolicy: Cluster` the entry node may forward to a backend on a different node, applying an SNAT source that some host network rules (return-path filtering, firewall ACLs, security group) reject. `externalTrafficPolicy: Local` removes the extra hop entirely; otherwise allow the SNAT (node) source.

**Q: Why does `kubectl port-forward` work but HPA never scales my Deployment?**

A: `kubectl port-forward` tunnels through the Kubernetes API server to the Pod IP, so the load it generates never hits a Service in the data plane and - more importantly - HPA does not watch ports at all. HPA scales on Pod resource metrics (CPU/memory from `metrics-server`). It will not react to port-forward traffic, and port-forward can only target in-cluster Pod or Service IPs, never an arbitrary public IP.

**Q: I allowed egress to ports, but DNS still times out in a default-deny namespace.**

A: DNS is UDP, a tiny window of fragments, and TCP fallback; conntrack must return replies. You need an egress allow rule to the `kube-dns` Service (namespace `kube-system`, TCP and UDP 53) and allow the return traffic (`state: established`) or the answer never reaches the Pod. This is the single most common DNS believes in namespaces with restrictive policies.

## Scenario

**Q: A web app returns 502 via the Ingress. How do you fix it?**

A: It is a Gateway server error, likely the backend Service has no healthy endpoints. Check `kubectl get endpoints`, Pod readiness, then confirm the app is listening on the expected targetPort (curl the Pod IP).

**Q: A Pod in the `web` namespace cannot resolve `api` from the `backend` namespace.**

A: Always use the fully qualified name `api.backend.svc.cluster.local` from outside the namespace. Confirm the Service exists (`kubectl get svc -n backend`) and has endpoints. Then test from inside the Pod - if `nslookup` times out but the service exists, the block is a NetworkPolicy on egress: either no `Egress` DNS rule to `kube-dns`, or an egress allow that does not include the `web` namespace's CIDR or namespaceSelector.

**Q: An Ingress returns 404 even though the Service endpoints are healthy.**

A: 404 from an ingress controller means the routing rule did not match the request. Check the `Host` header matches the Ingress host (`curl -H "Host: app.example.com"`), the path matches the `pathType` (e.g. `Prefix` vs `Exact`), and that traffic not matching falls to the `defaultBackend`. Also verify the Ingress resource and Service are reconciled in the same namespace and the controller (IngressClass) is the one you expect.

**Q: NodePort connections intermittently RST even though backend Pods are healthy.**

A: With `externalTrafficPolicy: Cluster` the connection can be forwarded to a different node, and subsequent route/firewall problems there or the extra NAT cause the reset. Set `externalTrafficPolicy: Local` so a node only answers for Pods it hosts, or confirm the SNAT node IP is permitted. `kubectl get svc -o yaml` shows the current policy.

## Commands and Quick Reference

| Command | Purpose |
|---------|---------|
| `kubectl get svc` / `kubectl get svc -o yaml` | List Services and full spec, ports, selectors |
| `kubectl describe svc <name>` | Endpoints, events, port mapping, `ExternalTrafficPolicy` |
| `kubectl get endpoints <name>` / `kubectl get ep` | Check backing Pods (empty means selector or readiness failed) |
| `kubectl get endpointslices -l kubernetes.io/service-name=<name>` | Inspect EndpointSlices behind a Service |
| `kubectl get netpol -n <ns>` | List NetworkPolicies in a namespace |
| `kubectl get svc --sort-by=.spec.clusterIP` | Sort Services by ClusterIP |
| `kubectl port-forward svc/<name> 8080:80` | Tunnel to a Service from localhost via the API server |
| `kubectl exec -it <pod> -- nslookup <svc>.<ns>.svc.cluster.local` | Resolve a Service DNS name from inside the cluster |
| `kubectl exec -it <pod> -- curl <svc-ip>:<port>` | End-to-end connectivity through a Service |
| `kubectl exec -it <pod> -- cat /etc/resolv.conf` | Show `nameserver 10.96.0.10`, search list, `ndots: 5` |
| `nc -vz <pod-ip> <port>` | Direct L4 connectivity check to a Pod |
| `ipvsadm -L -n` or `iptables -t nat -L -n` | Inspect kube-proxy forwarding rules (IPVS / iptables modes) |
| `kubectl get svc --all-namespaces --field-selector spec.type=LoadBalancer` | Filter Services by type cluster-wide |
| `kubectl get endpoints -A` | Spot Services with no healthy endpoints cluster-wide |
| `kubectl run tmp --image=busybox --restart=Never --rm -it -- wget -qO- http://<svc>` | One-shot probe Pod to test Service connectivity in-cluster |
| `kubectl get pods -n kube-system -l k8s-app=kube-dns` | Confirm CoreDNS pods are Running and Ready |

## Related

- [Revision - Networking](../revision/networking.md)
- [Lesson 13 - Networking Fundamentals and the CNI](../docs/04-networking/lesson-13-networking-fundamentals-and-the-cni.md)
- [Lesson 14 - Services and Cluster Networking](../docs/04-networking/lesson-14-services-and-cluster-networking.md)
- [Lesson 15 - Ingress and Ingress Controllers](../docs/04-networking/lesson-15-ingress-and-ingress-controllers.md)
- [Lesson 16 - Network Policies](../docs/04-networking/lesson-16-network-policies.md)
- [Lesson 17 - End-to-End Traffic Flow and the 502 Bad Gateway](../docs/04-networking/lesson-17-end-to-end-traffic-flow-and-the-502-bad-gateway.md)
- [Lesson 18 - Service Mesh: Istio and Linkerd](../docs/04-networking/lesson-18-service-mesh-istio-and-linkerd.md)

[Back to Interview Index](README.md)