import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Inventory.java
 * Manages real-time stock levels organized by Product ID and Size/Variant (e.g., S, M, L, XL).
 */
public class Inventory {
    // Maps Product ID -> (Size/Variant -> Quantity)
    private Map<String, Map<String, Integer>> stockMap;

    public Inventory() {
        this.stockMap = new HashMap<>();
    }

    /**
     * Adds or replenishes stock for a specific product and size.
     */
    public void addStock(String productId, String size, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        stockMap.putIfAbsent(productId, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        Map<String, Integer> sizeMap = stockMap.get(productId);
        int currentQty = sizeMap.getOrDefault(size, 0);
        sizeMap.put(size, currentQty + quantity);
    }

    /**
     * Gets available quantity for a product and size. Returns 0 if not found.
     */
    public int getStock(String productId, String size) {
        Map<String, Integer> sizeMap = stockMap.get(productId);
        if (sizeMap == null) {
            return 0;
        }
        return sizeMap.getOrDefault(size, 0);
    }

    /**
     * Returns an unmodifiable view of all sizes and stock counts for a product.
     */
    public Map<String, Integer> getStockBySizes(String productId) {
        Map<String, Integer> sizeMap = stockMap.get(productId);
        if (sizeMap == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(sizeMap);
    }

    /**
     * Checks if stock is available for the given quantity.
     */
    public boolean isAvailable(String productId, String size, int quantity) {
        return getStock(productId, size) >= quantity;
    }

    /**
     * Deducts stock upon confirmed order.
     * @throws InsufficientStockException if requested quantity exceeds available stock.
     */
    public void deductStock(String productId, String size, int quantity) throws InsufficientStockException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Deduct quantity must be greater than zero.");
        }

        int current = getStock(productId, size);
        if (current < quantity) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for Product '%s' [%s]. Requested: %d, Available: %d",
                    productId, size, quantity, current)
            );
        }

        stockMap.get(productId).put(size, current - quantity);
    }

    /**
     * Prints a formatted stock matrix for a given product.
     */
    public void displayProductStock(Product product) {
        System.out.println("--------------------------------------------------");
        System.out.println("Stock Details for: " + product.getName() + " (" + product.getId() + ")");
        System.out.println("--------------------------------------------------");
        Map<String, Integer> sizes = getStockBySizes(product.getId());
        if (sizes.isEmpty()) {
            System.out.println("  No sizes registered in inventory.");
        } else {
            for (Map.Entry<String, Integer> entry : sizes.entrySet()) {
                String size = entry.getKey();
                int qty = entry.getValue();
                String status = qty > 0 ? (qty + " in stock") : "OUT OF STOCK (Eligible for Backorder)";
                System.out.printf("  Size: %-4s | %s%n", size, status);
            }
        }
        System.out.println("--------------------------------------------------");
    }
}
