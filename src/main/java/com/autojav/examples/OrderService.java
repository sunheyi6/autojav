package com.autojav.examples;

import java.util.List;
import java.util.ArrayList;

public class OrderService {
    
    private OrderRepository orderRepository;
    private PaymentService paymentService;
    
    public OrderService(OrderRepository orderRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }
    
    public Order getOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        
        if (order == null) {
            return null;
        }
        
        return order;
    }
    
    public String getOrderStatus(String orderId) {
        Order order = orderRepository.findById(orderId);
        
        return order.getStatus();
    }
    
    public double getOrderTotal(String orderId) {
        Order order = orderRepository.findById(orderId);
        
        List<OrderItem> items = order.getItems();
        double total = 0;
        
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        
        return total;
    }
    
    public boolean processPayment(String orderId) {
        Order order = orderRepository.findById(orderId);
        
        if (order.getStatus().equals("PAID")) {
            return false;
        }
        
        return paymentService.process(order);
    }
    
    public void updateOrderAddress(String orderId, String address) {
        Order order = orderRepository.findById(orderId);
        
        order.setShippingAddress(address);
        orderRepository.save(order);
    }
    
    public List<Order> getOrdersByUser(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        
        List<Order> activeOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus().equals("ACTIVE")) {
                activeOrders.add(order);
            }
        }
        
        return activeOrders;
    }
    
    static class Order {
        private String id;
        private String status;
        private String shippingAddress;
        private List<OrderItem> items;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getShippingAddress() {
            return shippingAddress;
        }
        
        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }
        
        public List<OrderItem> getItems() {
            return items;
        }
        
        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }
    
    static class OrderItem {
        private String productId;
        private double price;
        private int quantity;
        
        public double getPrice() {
            return price;
        }
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
    
    static class OrderRepository {
        public Order findById(String orderId) {
            return null;
        }
        
        public List<Order> findByUserId(String userId) {
            return null;
        }
        
        public void save(Order order) {
        }
    }
    
    static class PaymentService {
        public boolean process(Order order) {
            return true;
        }
    }
}