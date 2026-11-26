package shop;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;

public class MetricsServer {

    public static void start() throws IOException {
        // Создаем реестр Prometheus
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        
        // Привязываем его к глобальному реестру Micrometer
        Metrics.addRegistry(registry);
        
        // Запускаем HTTP-сервер на порту 8090
        HTTPServer server = new HTTPServer(8090);
        System.out.println("Metrics server started on http://localhost:8090/metrics");
    }
}