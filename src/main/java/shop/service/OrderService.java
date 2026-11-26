package shop.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shop.model.Customer;
import shop.model.Order;
import shop.model.Product;
import shop.repository.ProductRepository;

public class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;
    
    // Метрики
    private final Counter totalOrders;
    private final Counter successOrders;
    private final Counter errorOrders;

    // Конструктор для тестов (использует глобальный реестр)
    public OrderService(ProductRepository productRepository) {
        this(productRepository, Metrics.globalRegistry);
    }

    // Конструктор для внедрения реестра (если понадобится)
    public OrderService(ProductRepository productRepository, MeterRegistry registry) {
        this.productRepository = productRepository;
        
        // Инициализируем метрики
        this.totalOrders = Counter.builder("shop_orders_total")
                .description("Общее количество попыток оформления заказа")
                .register(registry);
                
        this.successOrders = Counter.builder("shop_orders_success_total")
                .description("Количество успешно оформленных заказов")
                .register(registry);
                
        this.errorOrders = Counter.builder("shop_orders_error_total")
                .description("Количество ошибок при оформлении заказа")
                .register(registry);
    }

    public Order createOrder(
            int orderId,
            Customer customer,
            int productId,
            int quantity
    ) {
        totalOrders.increment(); // +1 к общему числу операций
        
        LOG.info("Получен запрос на оформление заказа: orderId={}, customerId={}", 
                orderId, customer.getId());

        try {
            Product product = productRepository.findById(productId);

            if (product == null) {
                LOG.error("Ошибка обработки товара: товар с productId={} не найден", productId);
                throw new IllegalArgumentException("Товар не найден");
            }

            if (quantity <= 0) {
                LOG.warn("Некорректная операция: запрошено quantity={} для productId={}", 
                        quantity, productId);
                throw new IllegalArgumentException(
                        "Кол-во запрашиваемого товара должно быть больше нуля.");
            }

            if (product.getStockQuantity() < quantity) {
                LOG.warn("Недостаточно товара на складе: доступно={}, запрошено={}", 
                        product.getStockQuantity(), quantity);
                throw new IllegalArgumentException("Недостаточное кол-во товара на складе.");
            }

            product.setStockQuantity(product.getStockQuantity() - quantity);
            
            double totalPrice = product.getPrice() * quantity;
            LOG.info("Заказ успешно оформлен: orderId={}, totalPrice={}", orderId, totalPrice);
            
            successOrders.increment(); // +1 к успешным
            return new Order(orderId, customer, product, quantity);
            
        } catch (IllegalArgumentException e) {
            errorOrders.increment(); // +1 к ошибкам
            throw e;
        }
    }
}