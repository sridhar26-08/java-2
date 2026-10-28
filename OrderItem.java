/**
 * OrderItem.java
 * Represents an individual line item in an order, capturing product, size, quantity,
 * and whether the item was placed as a backorder (due to being out of stock).
 */
public class OrderItem {
    private Product product;
    private String size;
    private int quantity;
    private double unitPrice;
    private boolean isBackorder;

    public OrderItem(Product product, String size, int quantity, boolean isBackorder) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.product = product;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.isBackorder = isBackorder;
    }

    public Product getProduct() {
        return product;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public boolean isBackorder() {
        return isBackorder;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        String status = isBackorder ? " [BACKORDER - To Ship/Restock]" : " [IN-STORE PICKUP]";
        return String.format("%s (%s) x%d @ ₹%.2f = ₹%.2f%s",
                product.getName(), size, quantity, unitPrice, getSubtotal(), status);
    }
}
