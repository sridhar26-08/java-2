/**
 * CardPayment.java
 * Demonstrates Inheritance: Extends Payment for Credit/Debit cards with masked card numbers.
 */
public class CardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;

    public CardPayment(String cardNumber, String cardHolderName) {
        super();
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public boolean processPayment(double amountToPay) {
        this.amount = amountToPay;
        // Basic card validation simulation
        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 12) {
            System.out.println("[Payment Failed] Invalid Card Number.");
            this.isSuccessful = false;
            return false;
        }

        // Simulate successful authorization with bank
        this.isSuccessful = true;
        this.transactionReference = "TXN-AUTH-" + (int)(Math.random() * 900000 + 100000);
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit/Debit Card";
    }

    private String getMaskedCardNumber() {
        String clean = cardNumber.replaceAll("\\s+", "");
        if (clean.length() <= 4) return clean;
        return "****-****-****-" + clean.substring(clean.length() - 4);
    }

    @Override
    public String getPaymentDetailsSummary() {
        return String.format("Card: %s | Name: %s | Auth Ref: %s",
                getMaskedCardNumber(), cardHolderName, transactionReference);
    }
}
