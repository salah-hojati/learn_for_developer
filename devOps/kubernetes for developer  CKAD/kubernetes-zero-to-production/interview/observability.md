---
title: Interview - Observability
module: 08 Observability
status: Complete
tags: [interview, observability, prometheus, grafana, loki, probes]
---

# Interview - Observability

## Beginner

**Q: What are the three pillars of observability?**

A: Metrics (what happened), logs (why it happened), and traces (where the request went through distributed systems).

**Q: What do readiness and liveness probes do?**

A: Readiness determines if a Pod should receive traffic (failing removes it from Service endpoints). Liveness determines if a container should be restarted.

## Intermediate

**Q: How does Prometheus collect metrics?**

A: It scrapes HTTP `/metrics` endpoints on a schedule. Exporters (node-exporter for host metrics, app client libraries for application metrics) expose them; Prometheus stores them in a time-series DB and evaluates queries and alert rules.

**Q: What is the difference between Prometheus and Loki?**

A: Prometheus stores numeric time-series metrics; Loki stores logs. Promtail ships pod logs to Loki. Grafana visualizes both.

**Q: A container keeps restarting but readiness is green. What now?**

A: Check liveness probe and app logs (`kubectl logs --previous`). Readiness green means the app is ready; liveness failing means the health endpoint reports bad state or the probe is misconfigured.

## Advanced

Q: Give a PromQL example that alerts on high error rate.

A: `sum(rate(http_requests_total{status=~"5.."}[5m])) / sum(rate(http_requests_total[5m])) > 0.05`.

**Q: Explain the four Prometheus metric types, and what makes Prometheus subject to cardinality explosion?**

A: Prometheus stores metrics as time series: each metric name plus every label/value combination is a separate series. The four metric types are:

- Counter: only increases (requests, errors, bytes). Use `rate()` or `increase()`; never read the raw value as a number.
- Gauge: goes up and down (CPU percent, memory in use, queue depth). Read the current value directly.
- Histogram: buckets an observation such as latency; exposes `_bucket` series with `le` (less than or equal) bounds plus `_sum` and `_count`. Required for `histogram_quantile` and for aggregation across Pods via `sum by (le)`.
- Summary: computes percentiles server side and exposes `_quantile` series. Quantiles of a Summary cannot be aggregated across replicas, so prefer Histogram for latency.

Cardinality explosion happens when a metric carries high-cardinality labels such as request URLs, user IDs, IP addresses, or container IDs. Every unique value creates a new series, growing TSDB memory, slowing rule evaluation and dashboards, and inflating scrape size. Mitigate by keeping labels bounded (namespace, deployment, pod, endpoint), moving per-request detail into logs, and dropping or rewriting labels in relabeling rules.

**Q: What is the difference between `rate`, `increase`, and `irate`?**

A: All three operate on counters and need a range vector such as `metric[5m]`.

- `rate(metric[5m])`: per-second average increase over the window. Smooth, and the standard choice for alerting.
- `increase(metric[5m])`: the total counter increase over the window, extrapolated. Use for "how many errors in the last 5 minutes".
- `irate(metric[5m])`: per-second rate from the last two samples in the window. Most reactive, good for real-time dashboards of spiky data, but too noisy for alerting.

They all handle counter resets. If the counter has too few samples in the window the result is unreliable, which is why a 3x scrape interval is the practical minimum rule for `rate()`.

**Q: What are the best practices for Prometheus alerting rules, and what does the `for` clause do?**

A: `for: 5m` in an alerting rule requires the condition to remain true for 5 continuous minutes before the alert fires. Prometheus marks the rule Pending first and only fires when it stays true, which filters single-scrape spikes and flapping.

Alert only on symptoms with real impact, such as the error ratio over the last 5 minutes or p99 above the SLO, and never on bare counter volatility. Set `for` to at least two or three times the scrape interval; 5 minutes is the practical default. Use longer `for` durations for slow-burn problems such as disk filling up, and shorter ones only for rollout events. Annotate every rule with the current value, the affected workload, and a runbook link. Keep warnings and criticals as distinct alerts.

**Q: What are recording rules and when do you use them?**

A: A recording rule precomputes an expensive query at the evaluation interval and stores it as a new metric. Alerts and dashboards then query the precomputed series instead of re-aggregating thousands of raw series:

```yaml
groups:
  - name: recording.rules
    rules:
      - record: job:http_error_ratio:rate5m
        expr: sum(rate(http_requests_total{status=~"5.."}[5m])) by (job) / sum(rate(http_requests_total[5m])) by (job)
```

Rules keep dashboards and alert queries fast; the trade-offs are that the result only reflects the last evaluation interval and that every rule adds series, so record only expressions that are queried repeatedly.

**Q: How do you handle Prometheus retention, downsampling, and high availability in single and multi-cluster setups?**

A: Retention: Prometheus prunes the local TSDB by time (`--storage.tsdb.retention.time`, default 15 days) and optionally by size (`--storage.tsdb.retention.size`). It compacts TSDB blocks but does not downsample retained samples; long-term, coarse resolution is left to a store like Thanos or Mimir.

High availability: run at least two identical replicas scraping the same targets with the same rule files; label every replica's data with `external_labels` (e.g. `cluster="prod-a"`), let Grafana query both and deduplicate, and have Alertmanager deduplicate the identical alerts. If metrics can be missed, add Alertmanager HA with a mesh.

Multi-cluster: use `remote_write` from each cluster Prometheus to a central writable store (Mimir, VictoriaMetrics, Grafana Cloud), or deploy Thanos with sidecars that upload local blocks to an object store, and a Thanos Querier that answers with a global view plus downsampled blocks.

**Q: How does Kubernetes service discovery work, and how do relabeling, scrape interval, and sample interval relate?**

A: `kubernetes_sd_configs` lets Prometheus watch the cluster API and build targets from roles: `pod`, `service`, `endpoints`, `node`, `ingress`. Each discovered target arrives with `__meta_kubernetes_*` labels (namespace, pod name, annotations). `relabel_configs` then:

- Drop or ignore non-monitored Pods, for example via the `prometheus.io/scrape` annotation.
- Create clean target labels (`namespace`, `pod`) from the meta labels.
- Override `__metrics_path__`, `__scheme__` (http/https), and `__address__`, for example to scrape per-endpoint instead of every Pod.

The `scrape_interval` is how often Prometheus pulls a target (default 60s). The `sample_interval` is how often the application or exporter internally records metric values. A scrape interval shorter than the sample interval only returns the same value again; scraping much faster than needed only adds CPU and storage cost.

**Q: Give PromQL for a per-Pod error ratio, top/bottom workloads, and latency percentiles.**

```promql
# 5xx ratio broken down per Pod
sum by (pod) (rate(http_requests_total{status=~"5.."}[5m]))
  / sum by (pod) (rate(http_requests_total[5m]))

# Top N Pods by request rate (bottomk N reverses)
topk(5, sum by (pod) (rate(http_requests_total[5m])))

# p99 latency from a Histogram, aggregated across Pods
histogram_quantile(0.99, sum by (le) (rate(http_request_duration_seconds_bucket[5m])))

# Empirical p99 of a Gauge over the last 5 minutes
quantile_over_time(0.99, my_gauge[5m])
```

- `sum by (pod)` keeps one series per Pod so the `/` operator computes a ratio per Pod.
- `histogram_quantile` requires all `le` buckets for `sum by (le)`; percentiles computed over inputs or summaries are misleading.
- `quantile_over_time` works on instant-vector series, typically gauges, not on counters or histogram bucket series.

**Q: What is the difference between kube-state-metrics, cAdvisor, node-exporter, and blackbox-exporter?**

A:

- cAdvisor: embedded in the kubelet and served on `/metrics/cadvisor`. Exposes per-container and per-Pod CPU, memory, network, and disk I/O, mostly derived from cgroups. This is the basis of what `kubectl top` reports through the Metrics Server.
- node-exporter: a DaemonSet on every node exposing host OS metrics: CPU idle, memory, disk and network stats, filesystem usage, load. Answering "is this node healthy and at capacity".
- kube-state-metrics: metrics about the state of cluster objects: `kube_deployment_status_replicas`, `kube_pod_container_status_restarts`, `kube_node_info`, PVC/PV state, Pod conditions. It measures the cluster configuration and events rather than the OS/app runtime.
- blackbox exporter: probes protocols (HTTP, HTTPS, TCP, ICMP/DNS) of external endpoints, capturing status codes, TLS metadata, and latency. Use it for "user-visible" endpoints rather than scraping app internals.

Use node-exporter + cAdvisor for host utilization, kube-state-metrics for cluster-health dashboards, and blackbox for dependencies or CDN.

**Q: Walk through the logging architecture: stdout/stderr to files, `kubectl logs --previous`, sidecar vs node agent, structured logs, Promtail, Loki, Alloy, OTel.**

A: Containers write to stdout/stderr; the CRI runtime such as containerd captures those streams and writes each container's output under `/var/log/pods/<namespace>/<pod>/<container>/` in JSON files with metadata. `kubectl logs` reads those files through the kubelet API. `kubectl logs <pod> --previous` shows the logs of the previous, now-terminated container, which is the fastest way to diagnose a CrashLoopBackOff.

To get logs out of the node: run a per-node agent as a DaemonSet (Promtail, Fluent Bit, Vector, or Grafana Alloy), which tails the same `/var/log/pods` files, attaches labels such as namespace, pod, and container, and pushes to Loki or another store. A logging sidecar is the alternative: it tails a specific file in a shared volume and prints it to stdout, but it costs one more container per Pod. Use the node-level agent for normal workloads; use a sidecar only when the application writes only to a file and needs converting (custom format, rotation), which you have to balance against the cost.

Structured logging matters. Emit JSON or logfmt lines, for example `{"level":"error","trace_id":"..."}` or `level=error trace_id=...`, so the log pipeline can parse the fields into labels in the collector or at query time. Plain text can still be searched using LogQL filters (`|= "error"`), but it loses structured fields and dashboards.

The modern fleet: Grafana Alloy is the drop-in evolution of Promtail, whose config stays compatible. OpenTelemetry Collector is the standard exporter for traces and metrics in the ecosystem; it can also forward logs (OTLP) into Loki, but the typical local shipping agent remains Promtail/Alloy.

**Q: How does Loki represent logs, and why do streams and labels follow constraints?**

A: Loki does not index the content of a log line. Instead it stores log lines in compressed streams and uses an index on stream labels and timestamps to find them. A stream is the set of log lines that share the same label values. This keeps indexing cheap and lets Loki hold large volumes, at the price of retrieving whole lines and filtering them at query time.

- Label cardinality defines the stream set. Put bounded labels such as `namespace`, `app`, and `container`; a unique label value per Pod, such as a container ID or UUID, creates that many streams, bloats the index, and hurts queries.
- Prefer the line filter (`|= "error"`) and the structured pipeline stages over fine-grained labels.
- Promtail adds the Pod metadata labels automatically; Alloy keeps the same behavior, and OTel can ship logs with the same label token.

**Q: What are Kubernetes events for, what is their retention, and how do you read them safely?**

A: Events are cluster object descriptions of what a controller or tool did: the scheduler raises on schedule events, kubelet on start/mount, HPA and so on. They are stored in etcd and are short-lived: the default TTL is about 1 hour, and on busy clusters old events are pruned before that. `kubectl get events -A`, `kubectl get events --watch`, or the Events section of `kubectl describe` reads them.

The most useful reasons to look for a failing deployment:

- `FailedScheduling`: the scheduler rejects the Pod, for capacity, taints, or affinity.
- `FailedMount`: the volume, PVC, or secret can't be bound or mounted.
- `OOMKilled`: the kernel terminated the container because it exceeded its memory limit; the event often appears together with `CrashLoopBackOff` and a failing liveness probe.
- `ImagePullBackOff`: the image pull fails: bad tag, auth, private repo, rate limit.

Events are diagnostic and transient, not telemetry. Use them to understand "what just happened to this object"; do not build alerts on the event volume because events expire, they are not guaranteed, and they are namespace-scoped. Reading events requires RBAC: Role/RoleBinding with `get`/`list`/`watch` on the `events` resource in the namespace (the default `view` ClusterRole already has it).


**Q: How do SLI/SLO, error budget, dashboards, and `kubectl top` metric-server limits connect?**

A: An SLI is a measurable indicator, for example p99 of request latency or the HTTP error rate. An SLO is the target the team commits to, such as "p99 latency under 200 ms over the last 30 days". The error budget is the remaining tolerance before the SLO is missed. A burn-rate alert fires when the budget is being consumed faster than expected over a window (for example, 5% of the budget gone in 1 hour), so the page goes out before the budget is exhausted.

Dashboards:

- Cluster capacity: node CPU, memory, disk pressure, evictions, `kubectl top node` output, cAdvisor.
- Workload level: per-namespace and per-Deployment error rate, p99 latency, and throughput.

`kubectl top pod` and `kubectl top node` read the **Metrics API** (`metrics.k8s.io`) served by the Metrics Server, which aggregates the kubelet's cgroup/cAdvisor data. Caveats: the Metrics Server only keeps a short in-memory window (a few minutes), it does not persist history, it provides no arbitrary label aggregation, and HPA only works for containers that declare resource `requests`. For durable dashboards, SLO reporting, and burn-rate alerts, query Prometheus with node-exporter and kube-state-metrics instead.

**Q: How do OpenTelemetry and tracing fit with metrics and logs?**

A: The application is instrumented with the OpenTelemetry SDK, which produces spans, metrics, and logs; they are sent over OTLP to the OpenTelemetry Collector and forwarded to a trace backend such as Jaeger, Tempo, or Grafana. A `traceID` is propagated in a `traceparent` header down the call chain, so every service contributes a span to the same trace and you can see the full request path and per-hop latency.

Latency is captured as a histogram with a standard bucket like `http.server.request.duration`, which is turned into `histogram_quantile` at query time. To correlate: put the `trace_id` into the structured JSON log line; Loki indexes it as a field, so you can search the trace ID against `metrics` (p99, error rate) and join into the trace backend to inspect the spans.

**Q: Walk through the probe types (`httpGet`, `exec`, `tcpSocket`, `grpc`) and the configuration fields; when do you pick one over the other?**

A: Each container can have three probes: `livenessProbe` restarts the container, `readinessProbe` gates traffic from Services and endpoints, and `startupProbe` covers slow startups before liveness and readiness begin.

Probe implementations:

- `httpGet`: the kubelet performs a GET on `http://<pod-ip>:<port>/<path>` with optional `host` and `httpHeaders`; success is a 2xx/3xx status. The strongest check because it exercises the application.
- `exec`: the kubelet executes a command in the container; success is exit code 0. Works for containers without HTTP, e.g. a postgres container with `pg_isready`.
- `tcpSocket`: the kubelet opens a TCP connection to the port; success is a successful dial. It only proves a port is listening, not that the process is healthy; use as a last resort.
- `grpc`: calls the gRPC health check protocol and requires the application to implement the health server; suited for gRPC-native services.

Tuning fields: `initialDelaySeconds` (grace at start), `periodSeconds` (default 10), `timeoutSeconds` (default 1), `failureThreshold` (default 3), `successThreshold` (default 1). A `startupProbe` uses the same fields but its default failureThreshold is much larger, effectively giving up to about 60 seconds of startup grace before the liveness probe is considered failed; set it explicitly for slow JVMs or cache warmup.

Enforcement rules: do not share the readiness and liveness paths. If an endpoint is transiently slow, readiness should remove the Pod from endpoints without restarting it; a shared liveness endpoint restarts the whole Pod during a burst. Liveness must only depend on the app itself, never on a peer DB or cache, because a dependency outage would otherwise kill the Pod repeatedly instead of reporting degraded health. Cross-check the endpoint response time with `curl` and look at `kubectl top pod` CPU/memory before treating a slow probe (beyond `timeoutSeconds`) as a dead container.

## Scenario

Q: The Metrics Server is not reporting. What breaks?

A: `kubectl top` fails and the HPA shows `<unknown>` targets. Fix by checking the `metrics-server` deployment, its API, and that Pods have resource requests.

**Q: `kubectl top` errors, and HPA reports `<unknown>` for all workloads. Diagnostic steps?**

A: The error comes from the Metrics API, not from the workload:

```bash
kubectl get --raw /apis/metrics.k8s.io/v1beta1/nodes
kubectl get apiservice v1beta1.metrics.k8s.io
```

1. If the apiservice is `False`, look at `kubectl logs -n kube-system deployment/metrics-server`: the usual causes are the kubelet TLS trust (the certificate is signed by a CA the server cannot verify, fixed with `--kubelet-certificate-authority`, or `--kubelet-insecure-tls` in development) and the preferred node address type (`--kubelet-preferred-address-types=InternalIP`).
2. Verify the raw API returns JSON with `cpu`/`memory` values; empty data means the metrics-server cannot reach the kubelet on port 10250 (network policy, RBAC, or certificate), even though the API itself is up.
3. `kubectl top` for a single Pod can be empty when the Pod just started or when the node's kubelet is not reporting; wait for the next window and check node `Ready` status and RBAC.

## Gotchas

- Prometheus scrapes exactly the `metrics_path` you configure (default `/metrics`). An exporter that exposes the data on another path makes the scrape return a 404, and the target shows `up == 0` with no data; do not assume every exporter uses the default path.
- `kubectl top` requires a working Metrics API. Without it, HPA shows `<unknown>` and `kubectl top` errors out; that API error does not mean the workloads are unhealthy.
- A probing mistake: using the same endpoint for readiness and liveness. A slow but live app restarts (liveness fails) instead of being removed from the endpoints; keep the two paths separate.
- PromQL: `rate()` requires a range vector (`metric[5m]`); `rate(metric)` raises a parse error, and applying `rate` to a gauge gives misleadingly stable numbers.
- Loki: streams are the label combination; unique per request/Pod labels explode the number of streams and impact the index; keep bounded labels and search in the content.
- Kubernetes events have a ~1 hour TTL, so they must not be an alert source. Watch events in the short window `kubectl get events -A --watch` and pair them with metrics.

## Commands and Quick Reference

| Task | Command |
|------|---------|
| Pod CPU/memory usage | `kubectl top pod -n <namespace> <pod>` |
| Node CPU/memory usage | `kubectl top node` |
| Metrics API raw output | `kubectl get --raw /apis/metrics.k8s.io/v1beta1/nodes` |
| Aggregated API status | `kubectl get apiservice v1beta1.metrics.k8s.io` |
| Logs of the current container | `kubectl logs <pod> -f --tail=200` |
| Logs of the previous (crashed) container | `kubectl logs <pod> --previous` |
| Events, watch or sorted | `kubectl get events --watch`, `kubectl get events --sort-by=.lastTimestamp` |
| Scrape a target manually | `curl -v http://<pod-ip>:<port>/metrics` |
| Pod wide + field selector | `kubectl get pods -o wide --field-selector=status.phase=Running` |
| Debug into a Node or Pod | `kubectl debug node/<node>` or `kubectl debug <pod> --copy-to=<tmp>` |

## Related

- [Revision - Observability](../revision/observability.md)
- [Lesson 24 - Monitoring and Metrics](../docs/08-observability/lesson-24-monitoring-and-metrics.md)
- [Lesson 25 - Logging](../docs/08-observability/lesson-25-logging.md)
- [Lesson 26 - Probes and Health Checks](../docs/08-observability/lesson-26-probes-and-health-checks.md)
- [Lesson 27 - Observability Deep Dive (Prometheus and Grafana)](../docs/08-observability/lesson-27-observability-deep-dive-prometheus-and-grafana.md)
- [Lesson 28 - Centralized Logging (Loki and Promtail)](../docs/08-observability/lesson-28-centralized-logging-loki-and-promtail.md)

[Back to Interview Index](README.md)