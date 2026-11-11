---
title: Revision - Networking
module: 04 Networking
status: Complete
tags: [revision, networking, service, ingress, network-policy, cni, dns]
---

# Revision - Networking

## 1. The Mental Model

Kubernetes networking is five layers that each solve one problem:

1. **The CNI** builds the roads and gives every Pod its own unique IP address, so any Pod can reach any other directly (a flat network).
2. **The Service** is the receptionist. Pod IPs change, so it provides a stable virtual IP (ClusterIP) and DNS name in front of the shifting Pods.
3. **kube-proxy** is the mail sorter. It programs kernel rules (iptables/IPVS) that rewrite a packet's destination from the Service IP to a real Pod IP.
4. **The Ingress Controller** is the front-door for HTTP/HTTPS. It reads the Host header and URL path and routes each request to the right Service.
5. **NetworkPolicy** is the firewall. Kubernetes is allow-by-default, so restrict which Pods can talk to which by enabling policies via the CNI.

Traffic flows in order: Client -> Cloud LoadBalancer -> Ingress Controller (L7) -> Service (L4) -> kube-proxy DNAT -> Pod. When a hop breaks, the client sees an HTTP error like 502 or 503; your job is to walk each hop and find where it broke.

## 2. Core Concepts

### CNI and Pod IPs

The CNI is a plugin the kubelet calls when a Pod is created. It assigns the Pod its IP (IPAM), creates a virtual interface inside the Pod's network namespace, and programs the routing that lets Pods on different nodes talk. Common plugins:

- **Calico** - policy-focused, iptables or eBPF, strong NetworkPolicy support.
- **Cilium** - eBPF-based, fast, supports L7 NetworkPolicies and can even replace kube-proxy.
- **Flannel** - simple overlay only; does NOT enforce NetworkPolicies on its own.
- **kindnetd** - kind's minimal CNI for local clusters.

Key idea: Kubernetes does not route packets itself. The Linux kernel does; the CNI and kube-proxy only program the kernel's tables.

### Services and kube-proxy

A Service is not a physical proxy. It is a virtual IP plus distributed kernel rules on every node. The **EndpointController** watches for Pods whose labels match the Service selector and stores their IPs in an **Endpoints** object (scaled up via **EndpointSlices** in large clusters). **kube-proxy** watches those endpoints and writes rules:

- **iptables mode** (default): a `KUBE-SVC-XXXX` chain per Service matches the ClusterIP, then the `statistic` module picks a random `KUBE-SEP-YYYY` chain that performs DNAT (rewrites the destination to a real Pod IP). The literal rule looks like `-m statistic --mode random --probability 0.5 -j KUBE-SEP-...` - for two replicas, a 50% probability to each Endpoint. Evaluation is sequential, so huge clusters get slow.
- **IPVS mode**: Linux Virtual Server hash tables for O(1) lookups, faster for large clusters.
- **eBPF (Cilium)**: bypasses iptables entirely, the fastest option.

iptables mode is fine up to a few thousand Services. Once you cross roughly **5,000 Services**, plan to switch to IPVS or to an eBPF CNI such as Cilium, because the kernel walks every rule sequentially and CPU usage spikes.

When a Service has **no Endpoints**, kube-proxy writes a REJECT rule, so the client gets an instant "Connection Refused" rather than a timeout. This is the counter-myth to "no endpoints always times out": you only see a timeout when a NetworkPolicy is silently dropping packets, so diagnose a suspected timeout against the NetworkPolicy, not the Service.

kube-proxy rules live **only in memory**. A crashed kube-proxy leaves stale rules: new Services never route and dead Pods keep receiving traffic. A node reboot forces a full ruleset rebuild, and during that window Services are unreachable. Monitor kube-proxy health (`kubectl logs -n kube-system <kube-proxy-pod>`, confirm RBAC and API Server connectivity) so stale-rules regressions surface early.

Deleting a Service flushes its rules and returns the ClusterIP to the pool for reuse.

Service types:

- **ClusterIP** (default) - internal-only virtual IP; use for microservice-to-microservice calls.
- **NodePort** - opens the same port from 30000-32767 on every node; use for debugging or as an Ingress backend, not for production web apps.
- **LoadBalancer** - provisions a cloud load balancer; one per Service, so expensive.
- **ExternalName** - returns a CNAME alias to an external DNS name; no ClusterIP, no selector.
- **Headless** (`clusterIP: None`) - no virtual IP; DNS resolves the actual Pod IPs, giving each Pod its own DNS name like `pod-0.db.default.svc.cluster.local`. This is how StatefulSets find their members.

### DNS and CoreDNS

CoreDNS runs in `kube-system` (as the `kube-dns` Service). It creates a record for every Service: `<name>.<namespace>.svc.cluster.local` resolves to the ClusterIP. Within a namespace use the short name `web-svc`; across namespaces use `web-svc.otherns.svc.cluster.local`.

### Ingress and Ingress Controllers

Ingress is a rules object; it does nothing by itself. You must install an **Ingress Controller** (NGINX, Traefik, AWS ALB, etc.) that watches for Ingress objects and programs its proxy. The controller routes by Host header and URL path (Layer 7) to a **Service** (Layer 4), which then routes to Pods. Ingress centralizes external HTTP/HTTPS access, TLS termination, and routing so you avoid one cloud load balancer per service.

Internally the Ingress object is stored in **etcd**; the controller watches for it via a **SharedInformer** on the API Server. When an Ingress is created, the controller generates its native proxy configuration (NGINX writes an `nginx.conf` server/location block that `proxy_pass`es to the backend Service) and reloads it. Because the rules are generated, you can swap controllers - NGINX to Traefik, or to a cloud-native controller - by changing a single field, `spec.ingressClassName`, without touching the Ingress rules.

Routing all external traffic through the Ingress also centralizes **security**: apply WAF, rate-limiting, and SSL inspection in one place instead of per Service. One limitation: plain Ingress terminates HTTP/2 and cannot pass **gRPC** through; use controller annotations or an L4 LoadBalancer passthrough for gRPC. On kind, remember the node needs the label `ingress-ready=true` plus `extraPortMappings` so host port 80 reaches the cluster.

### NetworkPolicy

A NetworkPolicy is enforced by the **CNI** (not the API server). The network is **allow all** by default. Listing a direction with no matching rules becomes **deny all** for that direction. `policyTypes: [Ingress]` governs incoming traffic to the selected Pods; `[Egress]` governs outgoing.

- **Lifecycle:** the moment a policy matches a Pod, that Pod is firewalled, including Pods created after the policy. Deleting the policy reverts those Pods to default-allow, so removing a policy reopens traffic.
- **DROP vs REJECT**: NetworkPolicies silently **drop** packets (the client sees a Timeout) - they do not **reject** (an instant Connection Refused, which means the route worked but no app is listening). Learn both so a hang and a refusal point you to the firewall versus the app.
- **Use cases:** multi-tenant clusters, PCI/HIPAA micro-segmentation, and zero-trust architectures, where a compromised Pod must be unable to scan or reach sensitive Pods.
- **Scale:** iptables-based CNIs evaluate policies sequentially, so latency grows with the number of policies. eBPF-based CNIs (Cilium) enforce policies with O(1) hash lookup.
- A timeout usually means a policy is silently dropping packets; Connection Refused usually means the app is not listening.

### Service Mesh (Istio, Linkerd)

A Service Mesh moves network logic out of application code. An **init container** writes iptables rules that force all Pod traffic through a **sidecar proxy** (Envoy in Istio; a Rust proxy in Linkerd). The **data plane** is the proxies; the **control plane** (Istiod) distributes routing config and acts as a certificate authority. Features:

- **mTLS** - both client and server verify each other's certificates, so inter-service traffic is encrypted and authenticated (zero trust).
- **Weighted routing** - a VirtualService can send 90% to v1 and 10% to v2 for canary deployments. The `v1`/`v2` subsets it references are defined separately in a **DestinationRule** that matches Pod labels (`version: v1`, `version: v2`).
- **Retries, timeouts, and tracing** - handled by the proxy, not the app code.
- **Injection mechanics** - label a namespace (`istio-injection=enabled`); a **MutatingWebhookConfiguration** then injects the `istio-init` container (writes the iptables redirections) and the `istio-proxy` (Envoy) container into every new Pod. Roll injection out **namespace by namespace**, not cluster-wide, to control blast radius.
- **Config delivery** - proxies fetch routing rules and certificates from the control plane over a gRPC **xDS/SDS** channel on port 15012.
- **External exposure** - traffic enters the mesh through an **Istio Gateway** fronted by the `istio-ingressgateway` load balancer, separate from the standard Ingress object.

Mesh vs standard networking at a glance:

| Feature | Standard K8s | Service Mesh (Istio) |
|---------|--------------|----------------------|
| Traffic routing | Random (kube-proxy) | Weighted, path/host/header-based |
| Security | Plain text | mTLS (zero trust) |
| Tracing | Manual app code | Automatic (Envoy) |
| Retries/timeouts | App code | Infrastructure (Envoy) |

Start with PERMISSIVE mTLS (accepts encrypted and plain text) and move to STRICT only after every workload has a sidecar. The proxy adds 1-2ms latency and CPU/RAM per Pod, so it pays off mainly with many microservices. Validate config with `istioctl analyze` and inspect Envoy routing with `istioctl proxy-config routes <pod>`; tear down with `istioctl x uninstall --purge`.

## 3. Key Commands

```bash
# Services and Endpoints
kubectl get svc
kubectl get endpoints web-svc          # empty means selector mismatch or unhealthy Pods
kubectl describe svc web-svc
kubectl expose deployment web-deploy --port=80 --target-port=8080

# Test connectivity from a temporary client Pod
kubectl run test --rm -it --image=alpine -- wget -qO- http://web-svc

# DNS
kubectl exec <pod> -- nslookup web-svc.default.svc.cluster.local
kubectl get pods -n kube-system -l k8s-app=kube-dns

# kube-proxy internals (run inside a node, e.g. docker exec into a kind node)
docker exec -it <kind-node> bash
iptables-save | grep KUBE-SVC
iptables-save | grep KUBE-SEP
kubectl get configmap -n kube-system kube-proxy -o yaml | grep mode

# kube-proxy health: a crashed proxy leaves stale rules and dead Pods still get traffic
kubectl get pods -n kube-system -l k8s-app=kube-proxy
kubectl logs -n kube-system <kube-proxy-pod>          # RBAC / API connectivity

# Ingress
kubectl get ingress
kubectl describe ingress my-ingress
kubectl get pods -n ingress-nginx
kubectl logs -n ingress-nginx <controller-pod>

# NetworkPolicy
kubectl get networkpolicy -A
kubectl describe networkpolicy api-policy
kubectl exec <pod> -- wget -O- <pod-ip>               # hang = drop, refused = no app

# 502 flow
curl -v http://localhost/
kubectl get endpoints <svc>
kubectl get pod -l app=web -o yaml | grep containerPort
```

Switch kind to IPVS so kube-proxy uses hash tables instead of iptables:

```yaml
# kind-config-ipvs.yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
kubeProxyMode: ipvs
```

```bash
kind create cluster --config kind-config-ipvs.yaml
docker exec -it <kind-node> bash
ipvsadm -L      # inspect IPVS rules; compare against the iptables rules from Lesson 13
```

kind with Ingress needs the node labeled `ingress-ready=true` plus a host-port mapping:

```yaml
# kind-config-ingress.yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  kubeadmConfigPatches:
  - |
    kind: InitConfiguration
    nodeRegistration:
      kubeletExtraArgs:
        node-labels: "ingress-ready=true"
  extraPortMappings:
  - containerPort: 80
    hostPort: 80
    protocol: TCP
```

Service mesh (Istio):

```bash
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled
istioctl analyze
istioctl proxy-config routes <pod>
istioctl x uninstall --purge
```

## 4. YAML Patterns (Service, Ingress, NetworkPolicy) with Field-by-Field Explanation

### Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: web
spec:
  type: ClusterIP
  selector:
    app: web
  ports:
  - port: 80
    targetPort: 80
```

- `spec.type` - `ClusterIP` (default), `NodePort`, `LoadBalancer`, `ExternalName`, or headless via `clusterIP: None`.
- `spec.selector` - the labels that identify the backing Pods. This is what populates the Endpoints list; a mismatch leaves Endpoints empty.
- `spec.ports[].port` - the port clients connect to on the Service IP.
- `spec.ports[].targetPort` - the port on the Pod the traffic is forwarded to. It must match the container's real listening port, or you get a 502. Prefer a named port (`targetPort: http`) matched against a named `containerPort` in the container; then you can change the real port number later without touching the Service.

### Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: web
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /$1
spec:
  ingressClassName: nginx
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: web
            port:
              number: 80
  tls:
  - hosts:
    - app.example.com
    secretName: web-tls
```

- `spec.ingressClassName` - selects which installed controller processes this Ingress.
- `spec.rules[].host` - optional; if present, only requests with that Host header match. Omit it to match all hosts.
- `path` and `pathType` - `Prefix` matches any URL starting with the path; `Exact` matches only that path.
- `backend.service.name` and `backend.service.port.number` - the target Service and its `port`. Ingress always routes to a Service, never directly to Pods.
- `spec.tls` - the host and the Kubernetes Secret holding the certificate; the controller terminates TLS here.
- `annotations` - controller hints; `rewrite-target: /$1` strips a captured path prefix before forwarding.

### NetworkPolicy

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-policy
spec:
  podSelector:
    matchLabels:
      app: api
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: web
    ports:
    - protocol: TCP
      port: 8080
```

- `spec.podSelector` - the Pods this policy applies to. `{}` (empty) selects ALL Pods in the namespace.
- `spec.policyTypes` - the directions governed. If `Ingress` is listed but no `ingress` rules exist, incoming traffic is denied. Same logic for `Egress`.
- `spec.ingress[].from` - the allowed sources: `podSelector`, `namespaceSelector`, or `ipBlock`.
- `spec.ingress[].ports` - the allowed protocols and ports. If omitted from an ingress entry, all ports from the matched source are allowed.

To default-deny a whole namespace:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
```

### Istio VirtualService and DestinationRule (weighted canary)

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: reviews
spec:
  host: reviews
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
---
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: reviews
spec:
  hosts:
  - reviews
  http:
  - route:
    - destination:
        host: reviews
        subset: v1
      weight: 90
    - destination:
        host: reviews
        subset: v2
      weight: 10
```

- `DestinationRule.spec.subsets` - the named subsets (`v1`, `v2`) that select Pods by label (`version`). The VirtualService only references them; the routing decision lives in the VirtualService.
- `VirtualService.spec.hosts` - the Service the rules apply to.
- `VirtualService.spec.http[].route[]` - weighted routes; `weight` percentages must sum to 100 (90/10 = a canary).

## 5. How It Fits Together (traffic flow: pod->service->ingress, and DNS resolution)

### Pod to Service (internal)

1. Pod A asks CoreDNS for `web-svc.default.svc.cluster.local` and receives the ClusterIP (for example, 10.96.0.10).
2. Pod A sends a packet to 10.96.0.10:80. The kernel's PREROUTING hook intercepts it, matches the `KUBE-SVC` chain for that ClusterIP, and DNATs the destination to one of the Pod IPs from the Endpoints list.
3. The CNI routes the rewritten packet to the target Pod, which replies (source NAT returns the reply).

### External request through Ingress

Upstream/downstream: the client (browser) is the **downstream**; the Pod behind the Service is the **upstream** the Ingress or controller forwards to.

1. Client resolves `app.example.com` and sends HTTP to the cloud load balancer, which forwards port 80/443 to the Ingress Controller Pods.
2. The controller reads the Host header and URL path, matches the Ingress rule, and proxies the request to the backend Service (`web:80`).
3. kube-proxy DNATs the ClusterIP to a healthy Pod; the Pod only receives traffic once it passes its readiness probe.

Connection lifecycle per hop (client to Ingress, Ingress to Pod):

1. TCP handshake opens the connection.
2. The HTTP request is sent over it.
3. The backend responds.
4. The connection is torn down (or kept alive and reused).

Two safeguards for zero-downtime deployments: (1) give every Deployment a Service so consumers always have a stable name; (2) connect via the Service **DNS name**, never a hardcoded ClusterIP, because ClusterIPs can change. During a rolling update the Ingress may briefly hold a stale, terminating Pod as upstream; connection draining plus readiness probes on the new Pod let the old Pod finish in-flight requests before it is replaced, avoiding 502 blips.

Use `targetPort` by a **name** (`targetPort: http`) that maps to a named `containerPort`, so a port change later does not break the Service or Ingress.

### DNS resolution chain

`Pod -> CoreDNS -> A record -> ClusterIP -> kube-proxy DNAT -> Pod IP`. If any egress NetworkPolicy blocks UDP/TCP port 53, DNS breaks and Pods cannot resolve Service names at all.

## 6. Common Mistakes and Gotchas

- **Selector mismatch** - Service selector does not match the Pod labels, so Endpoints is empty.
- **`targetPort` mismatch** - the Service targets a port the container does not listen on; the Pod refuses it and the Ingress returns 502.
- **App bound to `localhost`** - only accepts intra-container traffic; bind to `0.0.0.0` instead.
- **No Ingress Controller installed** - an Ingress object without a controller silently does nothing.
- **`ingressClassName` missing** - modern controllers may ignore Ingress resources without a class.
- **Default-deny Egress without DNS** - blocking all egress breaks CoreDNS on port 53; always allow DNS to kube-system.
- **Services do not provide security** - Services only load-balance; use NetworkPolicy to filter traffic.
- **Namespaces do not isolate traffic** - they are logical grouping, not a network boundary; Pods in different namespaces can talk unless a policy blocks them.
- **Selectors are namespace-scoped** - a Service only selects Pods in its own namespace; cross-namespace access needs the full DNS name.
- **Timeout vs Connection Refused** - a timeout is a firewall (NetworkPolicy) drop; Connection Refused means no app is listening.
- **iptables at scale** - sequential O(n) evaluation slows with many Services; use IPVS or Cilium eBPF.
- **STRICT mTLS too early** - STRICT drops plain-text traffic before every workload has a sidecar.
- **Ingress as a single bottleneck** - run multiple controller replicas with resource requests and limits.
- **DB behind NodePort** - exposing a database via NodePort puts it on every node's public port; keep databases on ClusterIP and restrict them with a NetworkPolicy.
- **Deleting a Service mid-life** - a deleted Service has its rules flushed and its ClusterIP returned to the pool; anything still pointing at the old name or IP breaks.
- **Istio STRICT before all sidecars** - same risk as above; enable STRICT mTLS only after every workload in the namespace has a sidecar.

## 7. Quick Troubleshooting (symptom -> cause -> fix, especially Services and 502)

| Status | Meaning | Typical K8s cause |
|--------|---------|-------------------|
| 404 Not Found | No matching rule | Ingress path/host mismatch |
| 502 Bad Gateway | Reached upstream but it refused | `targetPort` mismatch, app on `localhost`, or kube-proxy out of sync |
| 503 Service Unavailable | No healthy upstream | Service Endpoints empty |
| 504 Gateway Timeout | Upstream accepted but answered too slowly | App hung or overwhelmed |
| Timeout / hang | Packet dropped | NetworkPolicy silently dropping |
| Connection Refused (no app) | Port closed | No listener on the target port |

**Symptom: `kubectl get endpoints web-svc` shows `<none>`**

- Cause: Service selector does not match the Pod labels, or the Pods are not Ready.
- Fix: compare `kubectl describe svc <name> | grep Selector` against `kubectl get pods --show-labels`, then align labels.

**Symptom: connection to a Service hangs then times out**

- Cause: Endpoints exist but Pods are not Ready, or a NetworkPolicy is silently dropping packets.
- Fix: check `kubectl get endpoints` and Pod readiness; review the namespace's NetworkPolicies.

**Symptom: DNS resolution fails inside a Pod**

- Cause: CoreDNS is down, or an egress policy blocks port 53.
- Fix: `kubectl get pods -n kube-system -l k8s-app=kube-dns`; add an egress rule allowing UDP/TCP 53 to kube-system.

**Symptom: 502 Bad Gateway from the Ingress**

- Cause: the Ingress reached the Pod but the Pod refused the connection, usually a `targetPort` mismatch or the app bound to localhost.
- Fix flow: (1) `kubectl describe ingress <name>` - confirm the right Service and port; (2) `kubectl get endpoints <svc>` - if populated, routing reaches the Pod; (3) compare Service `targetPort` with the Pod's real listening port; (4) exec into a utility Pod and `curl <pod-ip>:<port>` to prove the app is reachable.

**Symptom: 503 Service Unavailable from the Ingress**

- Cause: the Service has no healthy Endpoints (selector mismatch, crashing Pods, or failing readiness probe), so the controller has no upstream.
- Fix: fix the selector or the Pods; only Ready Pods join Endpoints.

**Symptom: 404 Not Found from the Ingress**

- Cause: no Ingress rule matches the requested path or host.
- Fix: check `path`, `pathType`, and `host` in the Ingress rules; confirm the controller picked up the change.

**Symptom: Ingress does nothing after applying YAML**

- Cause: no Ingress Controller installed.
- Fix: install NGINX or Traefik and confirm `kubectl get pods -n ingress-nginx` is Running.

**Symptom: traffic flows even after applying a NetworkPolicy**

- Cause: the CNI does not enforce NetworkPolicies (for example, Flannel alone).
- Fix: install a policy-capable CNI such as Calico or Cilium.

**Symptom: 503 inside a service mesh**

- Cause: missing sidecar (namespace not labeled) or STRICT mTLS with a workload that has no proxy.
- Fix: `kubectl get pod <pod> -o yaml | grep istio-proxy`; run `istioctl analyze`; start with PERMISSIVE mTLS.

**Symptom: Endpoints populated but connections are refused**

- Cause: `kubectl get endpoints` shows the Pod IPs (the EndpointController is healthy), so the problem is on the client node: kube-proxy is out of sync or crashed and its iptables/IPVS rules do not resolve the ClusterIP to a live Pod.
- Fix: `kubectl logs -n kube-system <kube-proxy-pod>` - look for API access errors; confirm RBAC and that the pod can reach the API Server.

**Symptom: Connection Refused hitting the Ingress entry point**

- Cause: the Ingress Controller is not running, so nothing listens on the exposed port.
- Fix: `kubectl get pods -n ingress-nginx` (expect a Running controller) and `kubectl get svc -n ingress-nginx`; reinstall or scale the controller if needed.

## 8. 30-Second Recap

- CNI gives every Pod a unique IP and a flat network; the kernel routes, plugins program.
- Services give Pods a stable ClusterIP and DNS name; Endpoints list the real Pod IPs; kube-proxy DNATs via iptables (or IPVS/eBPF).
- Service types: ClusterIP (internal), NodePort (30000-32767 on every node), LoadBalancer (cloud LB), ExternalName (CNAME), headless (`clusterIP: None` for StatefulSets).
- DNS: `<name>.<namespace>.svc.cluster.local` resolved by CoreDNS.
- Ingress = L7 rules; Ingress Controller = the software (NGINX/Traefik) that executes them; always routes to a Service.
- NetworkPolicy = CNI-enforced firewall; default is allow-all; `podSelector: {}` + `policyTypes: [Ingress, Egress]` = default deny.
- Service Mesh = sidecar proxies (data plane) + control plane; provides mTLS and weighted canary routing.
- 502 = Pod refused the connection (check `targetPort`); 503 = no Endpoints; 504 = Pod answered too slowly or hung; timeout = policy drop; 404 = no matching Ingress rule.

## Related Lessons

- [Lesson 13 - Networking Fundamentals and the CNI](../docs/04-networking/lesson-13-networking-fundamentals-and-the-cni.md)
- [Lesson 14 - Services and Cluster Networking](../docs/04-networking/lesson-14-services-and-cluster-networking.md)
- [Lesson 15 - Ingress and Ingress Controllers](../docs/04-networking/lesson-15-ingress-and-ingress-controllers.md)
- [Lesson 16 - Network Policies](../docs/04-networking/lesson-16-network-policies.md)
- [Lesson 17 - End-to-End Traffic Flow and the 502 Bad Gateway](../docs/04-networking/lesson-17-end-to-end-traffic-flow-and-the-502-bad-gateway.md)
- [Lesson 18 - Service Mesh (Istio and Linkerd)](../docs/04-networking/lesson-18-service-mesh-istio-and-linkerd.md)

## Related Material

- [Networking Cheat Sheet](../cheatsheets/networking-cheatsheet.md)
- [Interview - Networking](../interview/networking.md)

[Back to Revision Index](README.md)
