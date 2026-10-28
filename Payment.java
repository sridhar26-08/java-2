/**
 * Payment.java
 * Abstract base class demonstrating OOP Abstraction and Polymorphism.
 * Specific payment gateways (Cash, Card, UPI) extend this class and implement processPayment().
 */
public abstract class Payment {
    protected double amount;
    protected boolean isSuccessful;
    protected String transactionReference;

    public Payment() {
        this.amount = 0.0;
        this.isSuccessful = false;
        this.transactionReference = "";
    }

    public abstract boolean processPayment(double amountToPay);
    public abstract String getPaymentMethodName();
    public abstract String getPaymentDetailsSummary();

    public double getAmount() {
        return amount;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
}
