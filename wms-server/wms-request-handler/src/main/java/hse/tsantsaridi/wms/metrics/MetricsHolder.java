package hse.tsantsaridi.wms.metrics;


import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.core.instrument.*;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public final class MetricsHolder {

    /** Регистр Prometheus, экспортируется на /metrics */
    public static final PrometheusMeterRegistry PROM_REGISTRY =
            new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);


    private static final AtomicInteger inflightCurrent = new AtomicInteger(0);
    private static final AtomicInteger inflightMax     = new AtomicInteger(0);

    public static final Gauge HTTP_INFLIGHT_CURRENT = Gauge
            .builder("http_requests_inflight_current", inflightCurrent, AtomicInteger::get)
            .tag("component", "central")
            .register(PROM_REGISTRY);

    public static final Gauge HTTP_INFLIGHT_MAX = Gauge
            .builder("http_requests_inflight_max", inflightMax, AtomicInteger::get)
            .tag("component", "central")
            .register(PROM_REGISTRY);

    /* Счетчики ответов 2xx и 5xx */
    public static final Counter HTTP_GET_CAPABILITY_2XX = Counter.builder("http_requests_total")
            .tags("component", "central", "request", "GET_CAPABILITY", "status", "200")
            .register(PROM_REGISTRY);

    public static final Counter HTTP_GET_MAP_2XX = Counter.builder("http_requests_total")
            .tags("component", "central", "request", "GET_MAP", "status", "200")
            .register(PROM_REGISTRY);

    public static final Counter HTTP_GET_MAP_5XX = Counter.builder("http_requests_total")
            .tags("component", "central", "request", "GET_MAP", "status", "500")
            .register(PROM_REGISTRY);

    public static final Counter HTTP_GET_CAPABILITY_5XX = Counter.builder("http_requests_total")
            .tags("component", "central", "request", "GET_CAPABILITY", "status", "500")
            .register(PROM_REGISTRY);

    /* Таймер латентности */
    public static final Timer HTTP_LATENCY = Timer.builder("http_request_duration_seconds")
            .tags("component", "central", "request", "GET_MAP")
            .publishPercentileHistogram()
            .sla(Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(500),
                    Duration.ofSeconds(1))
            .register(PROM_REGISTRY);


    public static void incInFlight() {
        int cur = inflightCurrent.incrementAndGet();
        inflightMax.updateAndGet(prev -> Math.max(prev, cur));
    }

    public static void decInFlight() {
        inflightCurrent.decrementAndGet();
    }
}
