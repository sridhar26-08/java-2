import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order.java
 * Encapsulates order details, line items, customer info, pricing calculations,
 * discount code handling, and payment association.
 */
public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> items;
    private LocalDateTime timestamp;
    private String discountCode;
    private double discountPercentage; // e.g., 0.10 for 10%
    private double taxRate;             // e.g., 0.05 for 5%
    private Payment payment;
    private String status;              // "PENDING", "PAID", "CANCELLED"

    public Order(String orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer != null ? customer : new Customer("Walk-in Customer", "N/A");
        this.items = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
        this.discountCode = null;
        this.discountPercentage = 0.0;
        this.taxRate = 0.05; // 5% standard sales tax
        this.status = "PENDING";
    }

    public void addItem(OrderItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * Applies promotional discount codes.
     * Optional enhancement: "STUDENT10" (10%), "RETAIL20" (20%), "FIRSTBUY" (15%).
     */
    public boolean applyDiscountCode(String code) {
        if (code == null) return false;
        String clean = code.trim().toUpperCase();
        switch (clean) {
            case "STUDENT10":
                this.discountCode = clean;
                this.discountPercentage = 0.10;
                return true;
            case "RETAIL20":
                this.discountCode = clean;
                this.discountPercentage = 0.20;
                return true;
            case "FIRSTBUY":
                this.discountCode = clean;
                this.discountPercentage = 0.15;
                return true;
            default:
                return false;
        }
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (OrderItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double calculateDiscount() {
        return calculateSubtotal() * discountPercentage;
    }

    public double calculateTax() {
        double taxableAmount = calculateSubtotal() - calculateDiscount();
        return taxableAmount * taxRate;
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double discount = calculateDiscount();
        double tax = (subtotal - discount) * taxRate;
        return (subtotal - discount) + tax;
    }

    public boolean hasBackorderItems() {
        for (OrderItem item : items) {
            if (item.isBackorder()) {
                return true;
            }
        }
        return false;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null && payment.isSuccessful()) {
            this.status = "PAID";
        }
    }

    public String getStatus() {
        return status;
    }
}
