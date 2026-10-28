/**
 * UpiPayment.java
 * Demonstrates Inheritance: Extends Payment for UPI/Digital QR payment methods.
 */
public class UpiPayment extends Payment {
    private String upiId;

    public UpiPayment(String upiId) {
        super();
        this.upiId = upiId;
    }

    @Override
    public boolean processPayment(double amountToPay) {
        this.amount = amountToPay;
        if (upiId == null || !upiId.contains("@")) {
            System.out.println("[Payment Failed] Invalid UPI ID format.");
            this.isSuccessful = false;
            return false;
        }

        // Simulate instant network settlement
        this.isSuccessful = true;
        this.transactionReference = "UPI-REF-" + (int)(Math.random() * 89999999 + 10000000);
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "UPI / QR Transfer";
    }

    @Override
    public String getPaymentDetailsSummary() {
        return String.format("VPA/UPI ID: %s | UTR: %s", upiId, transactionReference);
    }
}
