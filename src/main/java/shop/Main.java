package shop;

import shop.model.Customer;
import shop.model.Product;
import shop.repository.InMemoryProductRepository;
import shop.repository.ProductRepository;
import shop.service.OrderService;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Запускаем сервер метрик
        MetricsServer.start();

        ProductRepository productRepository = new InMemoryProductRepository();
        Product laptop = new Product(1, "Ноутбук", 2500.0, 5);
        productRepository.save(laptop);

        Customer customer = new Customer(1, "Анна");
        OrderService orderService = new OrderService(productRepository);

        // 2. Имитируем работу приложения
        // Успешный заказ
        orderService.createOrder(1, customer, 1, 2);
        // Еще один успешный заказ
        orderService.createOrder(2, customer, 1, 1);

        // Ошибочный заказ (нехватка товара)
        try {
            orderService.createOrder(3, customer, 1, 10);
        } catch (IllegalArgumentException e) {
            System.out.println("Ожидаемая ошибка: " + e.getMessage());
        }

        // Ошибочный заказ (товар не найден)
        try {
            orderService.createOrder(4, customer, 999, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Ожидаемая ошибка: " + e.getMessage());
        }

        System.out.println("Приложение работает. Метрики доступны на http://localhost:8080/metrics");
        
        // Держим приложение запущенным, чтобы Prometheus мог собирать метрики
        Thread.sleep(600000); // 10 минут
    }
}