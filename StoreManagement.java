import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StoreManagement.java
 * Coordinates product catalog, inventory tracking, order creation,
 * checkout processing, and order history.
 */
public class StoreManagement {
    private String storeName;
    private Map<String, Product> catalog; // Product ID -> Product
    private Inventory inventory;
    private List<Order> orderHistory;
    private int orderCounter;

    public StoreManagement(String storeName) {
        this.storeName = storeName;
        this.catalog = new HashMap<>();
        this.inventory = new Inventory();
        this.orderHistory = new ArrayList<>();
        this.orderCounter = 1000;
    }

    /**
     * Registers a new product and initializes its stock by size.
     */
    public void registerProduct(Product product, Map<String, Integer> initialStock) {
        if (product == null) return;
        catalog.put(product.getId().toUpperCase(), product);

        if (initialStock != null) {
            for (Map.Entry<String, Integer> entry : initialStock.entrySet()) {
                inventory.addStock(product.getId().toUpperCase(), entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Finds product by exact ID (case-insensitive).
     */
    public Product getProductById(String id) {
        if (id == null) return null;
        return catalog.get(id.trim().toUpperCase());
    }

    /**
     * Searches products by ID or substring match in name (case-insensitive).
     */
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(catalog.values());
        }
        String cleanQuery = query.trim().toLowerCase();
        List<Product> matches = new ArrayList<>();
        for (Product product : catalog.values()) {
            if (product.getId().toLowerCase().contains(cleanQuery) ||
                product.getName().toLowerCase().contains(cleanQuery)) {
                matches.add(product);
            }
        }
        return matches;
    }

    /**
     * Prints catalog products with prices and descriptions.
     */
    public void displayCatalog() {
        System.out.println("\n==================== STORE CATALOG ====================");
        if (catalog.isEmpty()) {
            System.out.println("No products in catalog.");
        } else {
            for (Product p : catalog.values()) {
                System.out.printf("[%s] %-28s | Price: ₹%.2f | %s%n",
                        p.getId(), p.getName(), p.getPrice(), p.getDescription());
            }
        }
        System.out.println("=======================================================");
    }

    /**
     * Displays size-by-size stock for a product.
     */
    public void displayStockForProduct(String productId) {
        Product p = getProductById(productId);
        if (p == null) {
            System.out.println("[Error] Product ID '" + productId + "' not found.");
            return;
        }
        inventory.displayProductStock(p);
    }

    /**
     * Begins a new customer order.
     */
    public Order createNewOrder(Customer customer) {
        orderCounter++;
        String orderId = "ORD-" + orderCounter;
        return new Order(orderId, customer);
    }

    /**
     * Adds an item to the order. If out of stock, it marks it as a backorder so the customer can still purchase!
     */
    public boolean addItemToOrder(Order order, String productId, String size, int quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            System.out.println("[Error] Cannot add to order: Product '" + productId + "' not found.");
            return false;
        }

        String cleanSize = size.trim().toUpperCase();
        int available = inventory.getStock(product.getId(), cleanSize);

        boolean isBackorder = false;
        if (available < quantity) {
            isBackorder = true;
            System.out.printf("  [NOTICE] %s (%s) is OUT OF STOCK / Insufficient (Available: %d, Wanted: %d).%n",
                    product.getName(), cleanSize, available, quantity);
            System.out.println("  --> Item is being placed as a BACKORDER / SPECIAL ORDER for you.");
        } else {
            System.out.printf("  [OK] %s (%s) is IN STOCK (Available: %d). Adding to cart.%n",
                    product.getName(), cleanSize, available);
        }

        OrderItem item = new OrderItem(product, cleanSize, quantity, isBackorder);
        order.addItem(item);
        return true;
    }

    /**
     * Completes checkout: Processes payment, deducts inventory for in-stock items,
     * saves to order history, and returns the printable receipt.
     */
    public Receipt checkout(Order order, Payment payment) {
        if (order == null || order.getItems().isEmpty()) {
            System.out.println("[Checkout Failed] Order is empty.");
            return null;
        }

        double totalAmount = order.calculateTotal();
        System.out.printf("%nInitiating payment of ₹%.2f via %s...%n", totalAmount, payment.getPaymentMethodName());

        boolean paymentSuccess = payment.processPayment(totalAmount);
        if (!paymentSuccess) {
            System.out.println("[Checkout Aborted] Payment could not be processed.");
            return null;
        }

        order.setPayment(payment);

        // Deduct inventory for items that were in stock
        for (OrderItem item : order.getItems()) {
            if (!item.isBackorder()) {
                try {
                    inventory.deductStock(item.getProduct().getId(), item.getSize(), item.getQuantity());
                } catch (InsufficientStockException e) {
                    System.err.println("[Inventory Warning] " + e.getMessage());
                }
            } else {
                System.out.printf("[Backorder Queued] Product %s (%s) x%d sent to store restocking queue.%n",
                        item.getProduct().getName(), item.getSize(), item.getQuantity());
            }
        }

        // Record into history
        orderHistory.add(order);
        System.out.println("✓ Order " + order.getOrderId() + " successfully placed and paid!");

        return new Receipt(order);
    }

    /**
     * Displays all past orders in store history.
     */
    public void displayOrderHistory() {
        System.out.println("\n==================== ORDER HISTORY ====================");
        if (orderHistory.isEmpty()) {
            System.out.println("No orders processed yet.");
        } else {
            for (Order o : orderHistory) {
                System.out.printf("Order ID: %-10s | Date: %s | Customer: %-18s | Items: %d | Total: ₹%.2f | Status: %s%n",
                        o.getOrderId(),
                        o.getFormattedDate(),
                        o.getCustomer().getName(),
                        o.getItems().size(),
                        o.calculateTotal(),
                        o.getStatus());
            }
        }
        System.out.println("=======================================================");
    }

    /**
     * Low stock alert report (optional enhancement).
     */
    public void checkLowStockAlerts(int threshold) {
        System.out.printf("%n=== LOW STOCK ALERTS (Threshold: <= %d units) ===%n", threshold);
        boolean foundAny = false;
        for (Product product : catalog.values()) {
            Map<String, Integer> sizes = inventory.getStockBySizes(product.getId());
            for (Map.Entry<String, Integer> entry : sizes.entrySet()) {
                if (entry.getValue() <= threshold) {
                    System.out.printf("  [ALERT] %s (%s) - Size %-3s : %d remaining %s%n",
                            product.getName(), product.getId(), entry.getKey(), entry.getValue(),
                            entry.getValue() == 0 ? "(OUT OF STOCK)" : "(LOW STOCK)");
                    foundAny = true;
                }
            }
        }
        if (!foundAny) {
            System.out.println("  All products have healthy inventory levels.");
        }
        System.out.println("=================================================");
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Order> getOrderHistory() {
        return Collections.unmodifiableList(orderHistory);
    }

    public String getStoreName() {
        return storeName;
    }
}
