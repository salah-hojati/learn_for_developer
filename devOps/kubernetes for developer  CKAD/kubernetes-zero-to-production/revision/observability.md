---
title: Revision - Observability
module: 08 Observability
status: Complete
tags: [revision, observability, prometheus, grafana, loki, promtail, probes, metrics, monitoring]
---

# Revision - Observability

This file is a self-contained refresher for Module 08. If you forgot everything, read only this file and you will re-learn the important parts: the three pillars, Prometheus, kube-state-metrics, Grafana, Loki + Promtail, Kubernetes logging, and liveness/readiness/startup probes.

## 1. The Mental Model

Think of a production app as a complex machine. To run it confidently you need to answer three questions:

- **Metrics (Prometheus) - WHAT and HOW MUCH:** "CPU spiked to 90% at 3:05 AM. What changed?" Numbers stored as time series.
- **Logs (Loki) - WHY:** "The backend restarted at 3:05 AM. Why?" Text lines with timestamps giving the reason.
- **Traces (Tempo/Jaeger) - WHERE:** "The checkout request failed. Where in the chain of 5 services did it break?" The journey of one request across services.

Holding that in your head is the whole module:

```
Metrics answer "what" and "how much", logs answer "why", traces answer "where".
Kubernetes gives you, for free, the first layer: Metrics Server (snapshot) + kubectl logs (one Pod).
Production adds the full stack: Prometheus (history) + Grafana (dashboards) + Loki (cluster-wide logs).
Probes keep the car on the road by checking whether each container is alive and ready.
```

## 2. Core Concepts

### The Three Pillars
| Pillar | Tool | Question it answers | Storage |
|--------|------|--------------------|---------|
| Metrics | Prometheus (TSDB) | What / how much | Time series, months of history |
| Logs | Loki (via Promtail) | Why | Compressed chunks, metadata index |
| Traces | Tempo / Jaeger | Where (request journey) | Spans correlated by trace ID |

### Prometheus and Metrics Server (they are NOT the same)
| Feature | Metrics Server | Prometheus |
| --- | --- | --- |
| Storage | In-memory only (no history) | Time-series DB (months of history) |
| Use case | `kubectl top`, feeds the HPA | Dashboards, alerting, SLOs |
| Scrape interval | Every 60 seconds | Configurable, usually 15 seconds |
| Query language | None | PromQL |
| Who consumes it | The Horizontal Pod Autoscaler | Humans and Alertmanager |

### The Metrics Server and the Metrics API
- **Metrics API (`metrics.k8s.io`):** the API that `kubectl top` and the HPA query. It is served by the Metrics Server.
- **API Aggregation:** the Metrics Server registers as an aggregated API server. `kubectl top` sends its request to the API server, which proxies it to the Metrics Server; this makes `kubectl top` feel like a native kubectl command even though the data lives in a separate Pod.
- **Source of numbers:** the Metrics Server polls every kubelet's Summary API at `/metrics/resource/v1beta1` every 60 seconds; the kubelet reads each container's cgroup stats (CPU accounting, memory pages) and returns JSON, and the Metrics Server keeps only the latest snapshot in memory.
- **The "never grows etcd" rationale:** metrics are not persisted, precisely so the core etcd database stays small and fast. If you need history, use Prometheus.
- **Scale/reliability:** the Metrics Server is lightweight, but a single instance cannot keep up with 1,000+ nodes. Scale it out or use a cloud-native equivalent (CloudWatch Container Insights, Google Cloud Monitoring, Azure Monitor) at that size.

### Prometheus architecture terms
- **Pull model:** Prometheus makes an HTTP GET to each app's `/metrics` endpoint every scrape interval. If the app does not expose `/metrics`, Prometheus cannot see it. (Push is the exception, via the Pushgateway, for batch jobs that finish before a scrape.)
- **Targets:** The list of endpoint addresses Prometheus scrapes. A target is `Up` (value 1) if the last scrape succeeded, `Down` (0) if it failed.
- **Labels:** Key/value dimensions attached to every metric (e.g. `namespace`, `pod`, `status`). Low cardinality keeps Prometheus fast; high-cardinality labels (every HTTP URL, every user) blow up storage and slow queries.
- **Metric types:** Counter (only increases, e.g. `http_requests_total`), Gauge (can go up/down, e.g. CPU), Histogram/Summary (latency distributions for percentiles).
- **kube-state-metrics:** an exporter that turns Kubernetes object state into metrics (`kube_pod_status_ready`, `kube_deployment_status_replicas`). It answers "how many ready replicas do I have right now" as a time series.
- **Node Exporter:** a DaemonSet agent exposing host-level metrics (`node_cpu_seconds_total`, `node_memory_MemTotal_bytes`) so you see node memory pressure, not just container CPU.
- **Grafana:** the "TV screen" that queries Prometheus/Loki and draws dashboards.
- **Alertmanager:** fires notifications (Slack, PagerDuty) when a PromQL expression crosses a threshold.

### PromQL basics
PromQL is the query language. Learn these three patterns:
```text
up                                        # 1 if every target scraped OK, 0 if a target is down
rate(http_requests_total[5m])             # per-second rate of a counter over 5 minutes
sum(rate(http_requests_total{status=~"5.."}[5m]))
  / sum(rate(http_requests_total[5m]))    # error ratio: 5xx over all requests
histogram_quantile(0.99, rate(latency_bucket[5m]))   # p99 latency
```

### Loki and Promtail (logging)
- **Promtail:** a DaemonSet (one Pod per node) that tails the container log files under `/var/log/pods/`, attaches Kubernetes metadata as labels (`namespace`, `pod`), and **pushes** them to Loki.
- **Loki:** a log aggregation database created by Grafana Labs. Key idea: it indexes only the **labels (metadata)**, not the full log text, so it is cheap to store and fast to query. It stores logs as **compressed chunks** in object storage.
- **LogQL** is Loki's query language, similar in spirit to PromQL but filtering text: `{namespace="default"} |= "ERROR"`.
- Internal split: **Distributor** validates/incoming, **Ingester** builds chunks in memory and flushes to storage, **Querier** executes LogQL on read.
- Unlike Prometheus (pull), shipping logs is a **push** model: Promtail pushes to Loki.
- **Security/production:** encrypt transport (TLS) between Promtail and Loki when they cross a network boundary, and apply RBAC so only the Grafana service account can query the Loki API. For shared Loki instances, use tenant/namespace separation (Loki multitenancy) so team A cannot read team B's logs.
- **Pre-configure Grafana with the Loki datasource** during the Grafana Helm install via `--set datasources."datasources\.yaml".apiVersion=1` etc., instead of adding it by hand afterwards.
- **Promtail targets:** `kubectl port-forward svc/loki-promtail 9080:9080 -n loki` and open `http://localhost:9080/targets` to preview what Promtail is tailing on this node.
- **Keep streams clean:** use Promtail `relabel_configs` `drop` rules to cut high-cardinality/noisy labels before they reach Loki (cardinality = number of distinct label streams).

### Kubernetes logging strategies
1. **stdout/stderr (default + required):** apps stream logs to stdout/stderr; the container runtime writes them to JSON files at `/var/log/pods/<ns>_<pod>_<uid>/<container>/<count>.log`. This is the Twelve-Factor App pattern and the only stream `kubectl logs` can read.
2. **Centralized via a DaemonSet shipper (Promtail, Fluentd, Fluent Bit, Vector):** one shipper per node reads `/var/log/pods/` and forwards to a central store (Loki/ELK). This is mandatory in production: search across every node, months of history, survives Pod deletion.
3. **Sidecar pattern:** for legacy apps that must write to a file, a sidecar container tails the shared file and prints it to its own stdout so the shipper can see it.
4. **Structured logs:** emit JSON lines (`{"level":"error","message":"DB down"}`) so LogQL can parse fields (`{namespace="prod"} | json | level="error"`) instead of grepping text. Never print secrets, PII, or card numbers to stdout.

### How `kubectl logs` reaches the logs (transport)
- `kubectl logs` does not read the file itself. The API Server proxies the request to the kubelet on the Pod's node via the kubelet's gRPC **LogHandler**; the kubelet streams the JSON log file back and kubectl renders it as text.
- On a multi-container Pod you must pass `-c <container>`. Without it you get `error: a container name must be specified for pod <pod>, choose one of: [...]`.

### Log rotation and the "missing file" gotcha
- The `json-file` driver writes each stdout/stderr line as a JSON entry to `/var/log/pods/<ns>_<pod>_<uid>/<container>/<count>.log` (`0.log`, then `1.log` after a restart).
- Rotation is size-based with kubelet flags `containerLogMaxSize` (default 10MB) and `containerLogMaxFiles`: when `0.log` hits the max size it rotates to `0.1.log` and a fresh `0.log` starts, so a noisy app cannot fill the node disk.
- `unable to retrieve container logs` means the file is missing, e.g. the container was killed before it could flush its output buffers. Centralized logging solves this.

### The three probes (health checks)
| Probe | On failure | Purpose | Use for |
| --- | --- | --- | --- |
| **startupProbe** | Gates liveness/readiness until it passes once | Slow boot (Java >10s) | Apps that need 30-60s to initialize |
| **livenessProbe** | Restarts the container | Process alive but app deadlocked | Detect unrecoverable hangs |
| **readinessProbe** | Removes the Pod from Service Endpoints (no traffic) | App busy/loading, deps down | Any web server |

Key rules:
- Success conditions: `httpGet` = status 200-399; `tcpSocket` = port open; `exec` = exit code 0.
- A failed `livenessProbe` restarts the container **on the same node**, it does NOT move the Pod.
- A failed `readinessProbe` never restarts anything; it only unplugs traffic.
- Never point liveness at a dependency (DB). Restarting the app will not fix the database: use readiness for that, liveness only for "am I unrecoverable".

### Probe internals (implemented by the kubelet)
- The kubelet's **ProbeManager** runs one goroutine per configured probe: `httpGet` uses the Go HTTP client against the Pod IP/path, `exec` goes through the CRI.
- Each result is written to the Pod's `.status.containerStatuses` and `.status.conditions` (e.g. `Ready=False` when readiness fails).
- The **EndpointController** in the control plane watches the Ready condition; when it flips to `False`, it removes the Pod IP from the Service's Endpoints so new traffic stops.
- On the `failureThreshold`-th consecutive liveness failure the kubelet sends **SIGTERM** to the container process (restart on the same node).

### Probe tuning rules
- Keep `periodSeconds` between 5-10s; `1s` hammers the app and skews metrics/CPU.
- Readiness/health endpoints must **not require authentication** (the kubelet's probe cannot log in) and must be lightweight: a fast 200, no heavy checks, or every probe delays traffic.
- Use separate endpoints: `/healthz` for liveness (dead -> restart), `/readyz` for readiness (deps/thread pools).

### Desired components (what "full observability" means)
- Metrics Server (HPA + `kubectl top`), Prometheus + node-exporter + kube-state-metrics (history), Grafana (UI), Alertmanager (alerts), Promtail + Loki (logs), Tempo/Jaeger (traces), plus `startup*/*probe` on every workload.

### The kube-prometheus-stack in practice
- One Helm install (`kube-prometheus-stack`) brings Prometheus, Grafana, Alertmanager, node-exporter, and kube-state-metrics plus the Prometheus CRDs.
- The default Grafana/Prometheus login is `admin / prom-operator`; change it immediately.
- In addition to **ServiceMonitor** there are two more scrape/rule CRDs: **PodMonitor** (selects Pods directly when there is no Service) and **PrometheusRule** (defines alerting rules evaluated by Prometheus).
- Secure the UIs: Grafana and Alertmanager expose sensitive infrastructure, so put them behind SSO/OIDC or a VPN; never expose them to the public internet.
- Give Prometheus its own resources (requests with reasonable limits) so the scheduler never treats it as invokable and OOM-kills the metrics source.
- Watch the scrape cost: ~1000 Pods means ~1000 HTTP GET `/metrics` per scrape interval. Tune scrape intervals in large clusters and remove high-cardinality labels.

## 3. Key Commands

```bash
# Metrics
kubectl top nodes
kubectl top pods
kubectl top pod -l app=myapp
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts && helm repo update
helm install monitoring prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace
kubectl get secret -n monitoring monitoring-grafana -o jsonpath='{.data.admin-password}' | base64 -d   # default login: admin / prom-operator
kubectl port-forward svc/monitoring-grafana 3000:80 -n monitoring
kubectl port-forward svc/monitoring-kube-prometheus-prometheus 9090:9090 -n monitoring

# Logs
kubectl logs <pod> -c <container>         # multi-container: -c required; else "a container name must be specified"
kubectl logs <pod> --previous             # crash logs (reads <count-1>.log)
kubectl logs -f -l app=myapp --tail=200
kubectl logs <pod> --since=10m
helm repo add grafana https://grafana.github.io/helm-charts && helm repo update
helm install loki grafana/loki --namespace loki --create-namespace --set service.type=ClusterIP
helm install promtail grafana/promtail --namespace loki
helm install my-grafana grafana/grafana --namespace loki \
  --set admin.user=admin --set admin.password=admin \
  --set datasources."datasources\.yaml".apiVersion=1 \
  --set datasources."datasources\.yaml".datasources[0].name=Loki \
  --set datasources."datasources\.yaml".datasources[0].type=loki \
  --set datasources."datasources\.yaml".datasources[0].url=http://loki:3100 \
  --set datasources."datasources\.yaml".datasources[0].access=proxy
kubectl port-forward svc/loki-promtail 9080:9080 -n loki   # http://localhost:9080/targets shows what Promtail tails

# Probes / debug
kubectl get pod <name>                    # watch RESTARTS column (liveness)
kubectl describe pod <name>               # read the Events: "Liveness probe failed..."
kubectl get endpoints <svc>               # empty => readiness failing
kubectl get events -A --sort-by='.lastTimestamp'
```

## 4. YAML Patterns

### a) A Pod with all three probes
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: api
  labels:
    app: api
spec:
  containers:
  - name: app
    image: myapi:1.0
    ports:
    - containerPort: 8080
      name: http
    startupProbe:            # allows 60s boot; gates liveness/readiness until it passes
      httpGet:
        path: /healthz
        port: http
      periodSeconds: 1
      failureThreshold: 60
    livenessProbe:           # if the app deadlocks, restart it
      httpGet:
        path: /healthz
        port: http
        initialDelaySeconds: 5
        periodSeconds: 10
        failureThreshold: 3
    readinessProbe:          # route traffic only when dependencies are ready
      httpGet:
        path: /readyz
        port: http
      initialDelaySeconds: 5
      periodSeconds: 5
      failureThreshold: 1
```
Explanation: `startupProbe` gives the JVM 60 seconds to boot; only after it passes do `liveness` and `readiness` run. `liveness` restarts a deadlocked container after 3 consecutive failures. `readiness` keeps the Pod out of the Service Endpoints until `/readyz` (which checks the DB and thread pools) returns 200-399.

### b) Prometheus Service + ServiceMonitor (a "scrape come here")
```yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp
  labels:
    app: myapp
spec:
  selector:
    app: myapp
  ports:
  - port: 8080
    targetPort: 8080
    name: metrics
---
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: myapp
  namespace: monitoring
spec:
  selector:
    matchLabels:
      app: myapp
  endpoints:
  - port: metrics
    path: /metrics
    interval: 15s
```
Explanation: The `ServiceMonitor.selector.matchLabels.app: myapp` must match the Service's `labels` (not its selector). The endpoint `port: metrics` references the Service's **named** port, resolving to `http://<pod-ip>:8080/metrics`. Prometheus (via the Prometheus Operator) watches this CRD and adds the App Pod as a scrape target every 15 seconds.

### c) Promtail pipeline snippet
```yaml
scrape_configs:              # inside the Promtail daemonset config
- job_name: kubernetes-pods
  kubernetes_sd_configs:
  - role: pod
  relabel_configs:
  - source_labels: [__meta_kubernetes_namespace]
    target_label: namespace
  - source_labels: [__meta_kubernetes_pod_name]
    target_label: pod
  - source_labels: [__meta_kubernetes_pod_container_name]
    target_label: container
  - action: drop
    regex: ^(debug|audit)\.log$
    source_labels: [__path__]      # drop noisy streams before they reach Loki
```
Explanation: Promtail runs as a DaemonSet (one per node) so it can read `/var/log/pods/`. `kubernetes_sd_configs.role: pod` tells it to discover every Pod on the node via the API. `relabel_configs` copies internal discovery labels `__meta_kubernetes_namespace` and `__meta_kubernetes_pod_name` onto the final `namespace` and `pod` labels you then filter with LogQL (`{namespace="default"}`). The `action: drop` rule removes high-cardinality/noisy file streams (here debug/audit logs) before they are pushed, keeping the label/stream cardinality low. Because `serviceAccountName` is set on the DaemonSet, Promtail may call the API for discovery.

## 5. How It All Fits Together

One pipeline from signal to action:

```text
instrument -> scrape -> store -> query -> alert
   |              |        |        |       |
 app exposes  Prometheus  TSDB  Grafana/  Alertmanager
 /metrics      pulls       chunk  PromQL/LogQL   fires Slack
 (app node)    /metrics
```

For each pillar:
- **Metrics:** every app exposes `/metrics`; Prometheus scrapes it (and node-exporter + kube-state-metrics container, object metrics they export 24/7 PMF). Data goes into the TSDB for months; Grafana queries it for dashboards; PromQL rules run and Alertmanager pages you when, say, p99 latency > 100ms. Then read until you fire a dead, not a petting zoo nobody watches. Dashboard.
- **Logs:** every app logs json to stdout; containerd writes it to files on the node; a Promtail (Fluentd) DaemonSet tails those files, adds namespace/pod labels, and pushes to Loki for months of searchable history.
- **Traces:** each library sends spans to Tempo/Jaeger, keyed by a trace ID threaded through services, so you can reconstruct the failing request's journey.
- **Probes keep Pods healthy within that flow:** startupProbe gates slow boots, livenessProbe kills deadlocked containers so a fresh replica starts, readinessProbe drops unhealthy Pods out of Service Endpoints. Probes are the feedback loop that makes a Deployment actually self-heal before routing you into algorithmic dashboards and alerts.

## 6. Common Mistakes and Gotchas

| Mistake | Why it happens | How to avoid it |
| --- | --- | --- |
| App has no `/metrics` endpoint | Developers never instrument | Add `/metrics` to every service |
| Forgetting the ServiceMonitor | Prometheus has nothing to scrape | Create a ServiceMonitor whose labels match the Service |
| `kubectl logs` on a multi-container Pod | Forgot `-c` | Always pass `-c <container>` |
| Looking at current logs for a crash | New container has fresh logs | Use `kubectl logs <pod> --previous` |
| App writes to a file, not stdout | Legacy logging | Print to stdout; use structured JSON |
| HPA shows `<unknown>` | Metrics Server not installed | Install Metrics Server before the HPA |
| Expecting Prometheus-less history from `kubectl top` | Confusing snapshot vs history | Use Prometheus for historical/alerting |
| Liveness probing a DB | Restarting app won't fix the DB | Check only "am I unrecoverable dead"; use readiness for deps |
| Startup Probe (or high initialDelay) ignored | Java needs 3-60s to boot | Use `startupProbe` to gate liveness/readiness |
| Low cardinality control ignored | Per-URL labels explode the TSDB | Keep label sets small and stable |
| No Prometheus retention/PV | Disk fills, node crashes | Set `--storage.tsdb.retention.time`, attach PV |
| No Loki retention / object storage | Disk fills, logs lost | Set `retention_period`, point Loki at S3/GCS |
| `--kubelet-insecure-tls` in production | Copied from kind setup | Only for local dev; real clusters trust via TLS |

## 7. Quick Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| `kubectl top` -> `metrics not available yet` | Metrics Server not ready | Install/wait; `kubectl get pods -n kube-system \| grep metrics` |
| HPA shows `<unknown>` | Metrics Server missing | Install Metrics Server |
| Pod `CrashLoopBackOff` | Liveness probe fails during boot | Increase threshold `startupProbe` |
| Pod Running (RESTARTS low) but no traffic | Readiness root | `kubectl get endpoints <svc>`: empty => `/readyz` failing |
| Prometheus targets all `down` (up==0) | Target unreachable or no `/metrics` | Prometheus -> Status -> Targets; check the error, fix `/metrics` + labels |
| Grafana shows no data | Wrong time range or targets Down | Set "Last 5 min"; run `up` and `rate()` |
| Loki Explorer empty | App logs to a file | confirm `kubectl logs`; if empty, app doesn't, use stdout/sidecar |
| Promtail has no targets | DaemonSet not on node or path wrong | Check Promtail `/targets`; verify mounted `/var/log` |
| `{namespace="x"}` finds nothing | Logs exist but out of time window | Widen Grafana time range; deleted Pods still in old chunks |

## 8. 30-Second Recap

- Observability = Metrics (what) + Logs (why) + Traces (where).
- Prometheus **pulls** `/metrics` every 15s into a TSDB; Grafana visualizes; Alertmanager alerts; kube-state-metrics + node-exporter give object and node metrics.
- Loki = "Prometheus of logs": indexes labels, push from Promtail DaemonSet, query with LogQL `{namespace="x"} |= "ERROR"`.
- Logging: stdout/stderr -> `/var/log/pods/` -> shipper -> centralized store. Never write to files; best shared a sidecar.
- Probes: startup gates slow boot, liveness restarts the hung container, readiness removes traffic. httpGet closure 200-399.
- Vertical: prometheus + node-exporter + kube-state-metrics + alertmanager + grafana + promtail + loki (+ tempo) + probes on every workload.

## Related Lessons

- [Lesson 24 - Monitoring and Metrics](../docs/08-observability/lesson-24-monitoring-and-metrics.md)
- [Lesson 25 - Logging](../docs/08-observability/lesson-25-logging.md)
- [Lesson 26 - Probes and Health Checks](../docs/08-observability/lesson-26-probes-and-health-checks.md)
- [Lesson 27 - Observability Deep Dive (Prometheus and Grafana)](../docs/08-observability/lesson-27-observability-deep-dive-prometheus-and-grafana.md)
- [Lesson 28 - Centralized Logging (Loki and Promtail)](../docs/08-observability/lesson-28-centralized-logging-loki-and-promtail.md)

## Related Material

- [Observability Cheat Sheet](../cheatsheets/observability-cheatsheet.md)
- [Interview - Observability](../interview/observability.md)

[Back to Revision Index](README.md)