---
title: Lab 17 - End-to-End Traffic Flow and the 502 Bad Gateway
lesson: 17
module: 04 Networking
tags: [kubernetes, networking, traffic-flow, 502, ingress, debugging]
---

# Lab 17 - End-to-End Traffic Flow and the 502 Bad Gateway

## Objective

In this lab you will deploy an NGINX application behind a ClusterIP Service behind an NGINX Ingress Controller on kind, verify the full end-to-end Layer 4 plus Layer 7 traffic flow, then intentionally break the path between the Service and the Pod to produce a `502 Bad Gateway`. You will then run the full 502 debugging workflow: inspecting the Ingress, checking the Service Endpoints, comparing `targetPort` against `containerPort`, using `curl -v`, and confirming DNS resolution. Finally you will learn to tell a 502 apart from 503, 404, and 504.

## Prerequisites

- A working kind cluster with Ingress support (built in Step 1)
- kubectl installed and configured
- Completion of Lessons 1 through 16
- `curl` available on the host for testing the cluster

## Pre-Lab Checklist

- [ ] kind CLI installed and on your PATH
- [ ] kubectl installed and pointed at the cluster or its kubeconfig
- [ ] Lessons 15 and 16 reviewed, especially the Ingress and Service concepts
- [ ] Ports 80 and 443 are free on the host machine
- [ ] Internet access to download the NGINX Ingress Controller manifest

---

## Step 1: Create a Kind Cluster with Ingress Support

Remove any previous cluster so you start from a clean state:

```bash
kind delete cluster
```

Expected output:

```text
Deleting cluster "kind" ...
```

Create the cluster configuration with the `ingress-ready: true` label on the control-plane node and `extraPortMappings` forwarding host ports 80 and 443 into the node:

```bash
cat <<EOF > kind-config.yaml
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
  - containerPort: 443
    hostPort: 443
    protocol: TCP
EOF
```

Create the cluster from that configuration:

```bash
kind create cluster --config kind-config.yaml
kubectl get nodes
```

Expected output:

```text
NAME                 STATUS   ROLES           AGE   VERSION
kind-control-plane   Ready    control-plane   2m    v1.30.0
```

The `ingress-ready=true` label is what the NGINX Ingress Controller manifest for kind uses in its node selector. Without it, the Ingress Controller Pods will never be scheduled.

## Step 2: Install the NGINX Ingress Controller

Install the kind-specific NGINX Ingress Controller manifest. This single manifest includes the namespace, the controller Deployment, the LoadBalancer Service, and the RBAC:

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```

Expected output begins with:

```text
namespace/ingress-nginx created
serviceaccount/ingress-nginx created
serviceaccount/ingress-nginx-admission created
...
deployment.apps/ingress-nginx-controller created
```

Wait for the controller Pod to become ready:

```bash
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s
```

Expected output:

```text
pod/ingress-nginx-controller-7b9f8b76b4-abc12 condition met
```

Confirm the controller and its default backend are healthy:

```bash
kubectl get pods -n ingress-nginx
```

Expected output:

```text
NAME                                       READY   STATUS      RESTARTS   AGE
ingress-nginx-admission-create-pb9dq       0/1     Completed   0          45s
ingress-nginx-admission-patch-6srm4        0/1     Completed   0          45s
ingress-nginx-controller-7b9f8b76b4-abc12  1/1     Running     0          2m
```

## Step 3: Sample Application YAML (Deployment, ClusterIP Service, Ingress)

Create `full-stack.yaml` with three objects: an NGINX Deployment (`containerPort: 80`), a ClusterIP Service that forwards port 80 to target port 80, and an Ingress that routes path `/` to the Service:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: nginx
        image: nginx:stable-alpine
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-svc
spec:
  type: ClusterIP
  selector:
    app: frontend
  ports:
  - port: 80
    targetPort: 80
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: frontend-ingress
spec:
  ingressClassName: nginx
  rules:
  - http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-svc
            port:
              number: 80
```

Apply it and wait for the rollout to finish:

```bash
kubectl apply -f full-stack.yaml
kubectl rollout status deployment/frontend-app
```

Expected output:

```text
deployment.apps/frontend-app created
service/frontend-svc created
ingress.networking.k8s.io/frontend-ingress created
deployment.apps/frontend-app successfully rolled out
```

## Step 4: Verify the End-to-End Traffic Flow

Test the full flow from the host, through the Ingress, into the Service, and to the Pod:

```bash
curl -v http://localhost/
```

Expected output (trimmed):

```text
*   Trying 127.0.0.1:80...
* Connected to localhost (127.0.0.1) port 80
> GET / HTTP/1.1
> Host: localhost
> User-Agent: ...
< HTTP/1.1 200 OK
< Server: nginx/1.23.x
```

Confirm the traffic path object-by-object. Check that the Ingress points to the right Service and port:

```bash
kubectl describe ingress frontend-ingress
```

Expected output:

```text
Name:             frontend-ingress
Namespace:        default
Address:          localhost
Rules:
  Host        Path  Backends
  ----        ----  --------
  *
  /           frontend-svc:80 (10.244.0.5:80)
```

The `(10.244.0.5:80)` in the backend is the Pod IP the controller resolves from the Service. Confirm the Service is aware of the Pod via Endpoints:

```bash
kubectl get endpoints frontend-svc
```

Expected output:

```text
NAME           ENDPOINTS         AGE
frontend-svc   10.244.0.5:80     3m
```

Confirm the Service resolves via DNS from inside the cluster. Exec into the running Pod and run `nslookup`:

```bash
kubectl exec "$(kubectl get pod -l app=frontend -o jsonpath='{.items[0].metadata.name}')" -- nslookup frontend-svc.default.svc.cluster.local
```

Expected output:

```text
Server:    10.96.0.10
Address:   10.96.0.10#53

Name:      frontend-svc.default.svc.cluster.local
Address:   10.96.0.5
```

The full chain is now verified: Host -> NGINX Ingress (Layer 7) -> ClusterIP Service -> kube-proxy DNAT -> Pod.

## Step 5: Break the Path and Produce a 502 Bad Gateway

Simulate a developer who changed the Service `targetPort` to a port no container is listening on, for example when an application was supposed to move to a new port. Create `broken-svc.yaml` pointing `targetPort` to 8080 while NGINX still listens on 80:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: frontend-svc
spec:
  type: ClusterIP
  selector:
    app: frontend
  ports:
  - port: 80
    targetPort: 8080
```

Apply the broken Service:

```bash
kubectl apply -f broken-svc.yaml
```

Wait roughly 10 seconds for NGINX to reload its upstream configuration, then reproduce the error with verbose output:

```bash
curl -v http://localhost/
```

Expected output (trimmed):

```text
< HTTP/1.1 502 Bad Gateway
< Server: nginx/1.23.x
...
<html>
<head><title>502 Bad Gateway</title></head>
```

You have now reproduced a real production-style `502 Bad Gateway`.

## Step 6: The 502 Debugging Workflow

Work through the flow component by component until you find the broken hop.

### 6.1 Check the Ingress Backend

```bash
kubectl describe ingress frontend-ingress
```

The backend still correctly points to `frontend-svc:80`. The Ingress and its rule are not the problem.

### 6.2 Check the Service Endpoints

```bash
kubectl get endpoints frontend-svc
```

Expected output:

```text
NAME           ENDPOINTS         AGE
frontend-svc   10.244.0.5:8080   5m
```

The Endpoints object mirrors the Service `targetPort`, so the registered port flipped from `:80` to `:8080`. The Pod IP is still there because the selector still matches, which rules out a label mismatch. If the Endpoints list were empty, that would mean a different problem (a 503).

### 6.3 Compare targetPort vs containerPort

Fetch the Service targetPort and the Pod containerPort side by side:

```bash
kubectl get svc frontend-svc -o jsonpath='Service targetPort = {.spec.ports[0].targetPort}{"\n"}'
kubectl get pod -l app=frontend -o jsonpath='Pod containerPort  = {.items[0].spec.containers[0].ports[0].containerPort}{"\n"}'
```

Expected output:

```text
Service targetPort = 8080
Pod containerPort  = 80
```

Here is the bug. NGINX inside the container listens on port 80, but the Service forwards traffic to port 8080, where nothing is listening. The Pod kernel receives the packet on 8080 and refuses the connection, which NGINX reports upstream as a bad gateway.

### 6.4 Confirm the Upstream Hop Fails from the Pod Network

Verify that port 80 works but port 8080 does not, from a separate shell inside a helper Pod in the same cluster:

```bash
POD_IP=$(kubectl get pod -l app=frontend -o jsonpath='{.items[0].status.podIP}')
kubectl run curl-check --image=curlimages/curl --rm -it --restart=Never -- sh -c "curl -s http://$POD_IP:80 | head -n 1; echo '---'; curl -s http://$POD_IP:8080 | head -n 1"
```

Example output:

```text
Welcome to nginx!
---
```

The first curl succeeds because NGINX listens on 80. The second produces no output because the connection was refused on 8080.

### 6.5 Confirm DNS Still Resolves

DNS is not the problem either:

```bash
kubectl exec "$(kubectl get pod -l app=frontend -o jsonpath='{.items[0].metadata.name}')" -- nslookup frontend-svc.default.svc.cluster.local
```

The result is the same working resolution from Step 4.

### 6.6 Conclusion

The flow broke at the Ingress/Pod hop. The Ingress (Layer 7) and the Service routing (Layer 4 via kube-proxy) are intact. The `targetPort` does not match the container listening port, so the upstream connection is refused and NGINX returns `502 Bad Gateway`.

Inspect the controller logs for confirmation if you want the final piece of evidence:

```bash
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller | grep -i "502\|connect() failed" | tail -n 5
```

## Step 7: Fix the Path and Verify

Restore the correct `targetPort` in the Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: frontend-svc
spec:
  type: ClusterIP
  selector:
    app: frontend
  ports:
  - port: 80
    targetPort: 80
```

Apply the fix and wait for NGINX to reload:

```bash
kubectl apply -f full-stack.yaml
sleep 10
curl -v http://localhost/
```

Expected output:

```text
< HTTP/1.1 200 OK
< Server: nginx/1.23.x
```

With the targetPort matching `containerPort`, the full flow works again and the 502 is gone.

## Step 8: Map 502, 503, 404, and 504

These four error classes look similar but indicate a different break.

### 503 Service Unavailable

A 503 means the Ingress has no upstream Pods at all (empty Endpoints), often a selector mismatch or a crashing Pod. Break it by selecting labels:

```bash
cat <<EOF > empty-svc.yaml
apiVersion: v1
kind: Service
metadata:
  name: frontend-svc
spec:
  type: ClusterIP
  selector:
    app: does-not-exist
  ports:
  - port: 80
    targetPort: 80
EOF
kubectl apply -f empty-svc.yaml
kubectl get endpoints frontend-svc
```

Expected output:

```text
NAME           ENDPOINTS   AGE
frontend-svc   <none>      10s
```

Now the request returns 503:

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost/
```

Expected output:

```text
503
```

Restore the correct selector with the original manifest:

```bash
kubectl apply -f full-stack.yaml
```

### 404 Not Found

A 404 means the Ingress rule set matched no rule for the requested host or path. The NGINX Ingress Controller returns 404 when the request reaches the controller but there is no matching Ingress rule, for example an unknown Host header:

```bash
curl -s -o /dev/null -w "%{http_code}\n" -H "Host: unknown.example.com" http://localhost/
```

Expected output:

```text
404
```

### 504 Gateway Timeout

A 504 means the connection succeeded but the upstream Pod did not answer within the proxy timeout (NGINX default is 60 seconds). It happens when the container itself is hung or saturated. A 502 is a refused connection.

### Comparison Table

| HTTP Status | Meaning | Kubernetes Cause | Diagnosis |
|-------------|---------|------------------|-----------|
| 404 Not Found | No rule matches the request or host | Path or hostname typo in the Ingress rule | `kubectl describe ingress` |
| 502 Bad Gateway | Upstream refused the connection or returned an invalid response | `targetPort` mismatch or app bound to localhost | Compare `targetPort` vs `containerPort` |
| 503 Service Unavailable | No upstream Pods available | Empty Endpoints (selector mismatch, no readiness) | `kubectl get endpoints` |
| 504 Gateway Timeout | Upstream accepted but did not respond in time | Hung or overloaded container | pod logs and `proxy-read-timeout` |

## Cleanup

Remove the sample workloads and the cluster:

```bash
kubectl delete -f full-stack.yaml
kubectl delete -f broken-svc.yaml
kubectl delete -f empty-svc.yaml
kind delete cluster
```

Expected output:

```text
deployment "frontend-app" deleted
service "frontend-svc" deleted
ingress.networking.k8s.io "frontend-ingress" deleted
Deleting cluster "kind"
```

---

## Lab Questions

1. In Step 4, which commands proved that each of the three hops (Ingress, Service, kube-proxy) was working before you broke anything?
2. When `targetPort` is 8080 and the container listens on 80, why does `kubectl get endpoints frontend-svc` still show an IP?
3. What is the difference between `port` and `targetPort` on a Service, and which one must match the container's listening port?
4. Is a 502 produced when the Endpoints list is empty, or when the upstream connection is refused? Where does a 503 come from instead?
5. If `kubectl get endpoints frontend-svc` returned no entries, which two files would you inspect first?

## Expected Results

After completing this lab:

- You created a kind cluster with `ingress-ready: true` and extra port mappings and installed the NGINX Ingress Controller
- You can name all hops in the end-to-end flow: Ingress -> Service -> kube-proxy -> Pod
- You can reproduce a 502 by mismatching `targetPort` and `containerPort`
- You can run the full 502 debugging workflow: describe ingress, get endpoints, compare ports, `curl -v`, and `nslookup`
- You can tell 502, 503, 404, and 504 apart and map each to its Kubernetes cause

## Key Commands Reference

| Command | Purpose |
|---------|---------|
| `kind create cluster --config kind-config.yaml` | Create a cluster with Ingress support |
| `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml` | Install the NGINX Ingress Controller for kind |
| `curl -v http://localhost/` | Test and verbose the full traffic path |
| `kubectl describe ingress <name>` | Inspect the Ingress backend Service and port |
| `kubectl get endpoints <svc>` | Check if the Service knows about the Pods |
| `kubectl get svc <svc> -o jsonpath='{.spec.ports[0].targetPort}'` | Read the Service targetPort |
| `kubectl get pod -l app=frontend -o jsonpath='{.items[0].spec.containers[0].ports[0].containerPort}'` | Read the container listening port |
| `kubectl exec <pod> -- nslookup <svc>.default.svc.cluster.local` | Confirm Service DNS resolution |
| `kubectl logs -n ingress-nginx deploy/ingress-nginx-controller` | Inspect controller logs for upstream errors |

## Next

- Return to the Lesson 17 concepts in [lesson-17-end-to-end-traffic-flow-and-the-502-bad-gateway.md](../docs/04-networking/lesson-17-end-to-end-traffic-flow-and-the-502-bad-gateway.md)
- Try the Mini Project: Deploy two namespaces and use host-based Ingress rules to assert that 404 appears when a hostname is not defined
- Proceed to [Lab 18 - Service Mesh](lab-18-service-mesh-istio-and-linkerd.md) to add mTLS and L7 observability on top of this traffic path