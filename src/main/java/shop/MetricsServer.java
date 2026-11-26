package shop;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MetricsServer {

    public static void start() throws IOException {
        // Создаем реестр
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Metrics.addRegistry(registry);

        // Создаем встроенный сервер Java на порту 8090
        HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
        
        // Настраиваем обработчик для пути /metrics
        server.createContext("/metrics", exchange -> {
            // Получаем метрики в текстовом формате Prometheus
            String response = registry.scrape();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            
            // Отправляем ответ браузеру
            exchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        
        server.start();
        System.out.println("Metrics server started on http://localhost:8090/metrics");
    }
}