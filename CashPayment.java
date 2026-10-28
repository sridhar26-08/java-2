/**
 * CashPayment.java
 * Demonstrates Inheritance: Extends Payment for in-person cash transactions.
 * Calculates change due.
 */
public class CashPayment extends Payment {
    private double cashTendered;
    private double changeDue;

    public CashPayment(double cashTendered) {
        super();
        this.cashTendered = cashTendered;
        this.changeDue = 0.0;
    }

    @Override
    public boolean processPayment(double amountToPay) {
        this.amount = amountToPay;
        if (cashTendered < amountToPay) {
            System.out.printf("[Payment Failed] Insufficient cash tendered. Needed: ₹%.2f, Given: ₹%.2f%n",
                    amountToPay, cashTendered);
            this.isSuccessful = false;
            return false;
        }

        this.changeDue = cashTendered - amountToPay;
        this.isSuccessful = true;
        this.transactionReference = "CASH-" + System.currentTimeMillis() % 100000;
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Cash";
    }

    @Override
    public String getPaymentDetailsSummary() {
        return String.format("Cash Tendered: ₹%.2f | Change Due: ₹%.2f (Ref: %s)",
                cashTendered, changeDue, transactionReference);
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public double getChangeDue() {
        return changeDue;
    }
}
