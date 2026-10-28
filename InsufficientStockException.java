/**
 * InsufficientStockException.java
 * Custom checked exception thrown when an operation tries to deduct more stock than available.
 */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
