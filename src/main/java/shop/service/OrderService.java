package shop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shop.model.Customer;
import shop.model.Order;
import shop.model.Product;
import shop.repository.ProductRepository;

public class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Order createOrder(
            int orderId,
            Customer customer,
            int productId,
            int quantity
    ) {
        LOG.info("Получен запрос на оформление заказа: orderId={}, customerId={}", 
                orderId, customer.getId());

        Product product = productRepository.findById(productId);

        if (product == null) {
            LOG.error("Ошибка обработки товара: товар с productId={} не найден", productId);
            throw new IllegalArgumentException("Товар не найден");
        }

        if (quantity <= 0) {
            LOG.warn("Некорректная операция: запрошено quantity={} для productId={}", 
                    quantity, productId);
            throw new IllegalArgumentException("Кол-во запрашиваемого товара должно быть больше нуля.");
        }

        if (product.getStockQuantity() < quantity) {
            LOG.warn("Недостаточно товара на складе: доступно={}, запрошено={}", 
                    product.getStockQuantity(), quantity);
            throw new IllegalArgumentException("Недостаточное кол-во товара на складе.");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        
        double totalPrice = product.getPrice() * quantity;
        LOG.info("Заказ успешно оформлен: orderId={}, totalPrice={}", orderId, totalPrice);

        return new Order(orderId, customer, product, quantity);
    }
}