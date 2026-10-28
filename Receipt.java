/**
 * Receipt.java
 * Generates and prints clean, professional, formatted receipts for in-store transactions.
 */
public class Receipt {
    private Order order;

    public Receipt(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Cannot generate receipt for null order.");
        }
        this.order = order;
    }

    /**
     * Prints the complete ASCII receipt to the console.
     */
    public void printReceipt() {
        System.out.println(generateReceiptText());
    }

    /**
     * Formats the receipt into a string representation.
     */
    public String generateReceiptText() {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(58);
        String dash = "-".repeat(58);

        sb.append("\n").append(line).append("\n");
        sb.append(centerText("CHECK&BUY", 58)).append("\n");
        sb.append(centerText("124 Fashion Street, Connaught Place - Ph: +91 98765-01234", 58)).append("\n");
        sb.append(line).append("\n");

        sb.append(String.format(" Receipt ID: %-22s Date: %s%n", order.getOrderId(), order.getFormattedDate()));
        sb.append(String.format(" Customer  : %-22s Phone: %s%n",
                order.getCustomer().getName(), order.getCustomer().getPhoneNumber()));
        sb.append(dash).append("\n");

        sb.append(String.format(" %-24s %-6s %-4s %-8s %-9s%n", "Item", "Size", "Qty", "Price", "Total"));
        sb.append(dash).append("\n");

        for (OrderItem item : order.getItems()) {
            String name = item.getProduct().getName();
            if (name.length() > 23) {
                name = name.substring(0, 20) + "...";
            }
            sb.append(String.format(" %-24s %-6s %-4d ₹%-7.2f ₹%-8.2f%n",
                    name, item.getSize(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));

            if (item.isBackorder()) {
                sb.append("   ↳ [SPECIAL ORDER / BACKORDER - Will notify upon arrival]\n");
            }
        }

        sb.append(dash).append("\n");
        sb.append(String.format(" %-40s ₹%.2f%n", "Subtotal:", order.calculateSubtotal()));

        if (order.getDiscountCode() != null) {
            sb.append(String.format(" Promo Code (%s - %.0f%%): %20s -₹%.2f%n",
                    order.getDiscountCode(),
                    order.getDiscountPercentage() * 100,
                    "",
                    order.calculateDiscount()));
        }

        sb.append(String.format(" %-40s ₹%.2f%n", "GST (5%):", order.calculateTax()));
        sb.append(line).append("\n");
        sb.append(String.format(" %-40s ₹%.2f%n", "TOTAL DUE:", order.calculateTotal()));
        sb.append(line).append("\n");

        Payment p = order.getPayment();
        if (p != null && p.isSuccessful()) {
            sb.append(String.format(" Payment Method : %s%n", p.getPaymentMethodName()));
            sb.append(String.format(" Details        : %s%n", p.getPaymentDetailsSummary()));
            sb.append(String.format(" Status         : %s%n", "PAID"));
        } else {
            sb.append(" Payment Status : UNPAID / PENDING\n");
        }

        if (order.hasBackorderItems()) {
            sb.append(dash).append("\n");
            sb.append(" NOTE: You have backordered items in this order.\n");
            sb.append(" Our store associates will contact you via phone when\n");
            sb.append(" stock arrives for in-store pickup or home dispatch.\n");
        }

        sb.append(line).append("\n");
        sb.append(centerText("Thank you for shopping with Check&Buy!", 58)).append("\n");
        sb.append(centerText("Please retain receipt for 30-day exchange.", 58)).append("\n");
        sb.append(line).append("\n");

        return sb.toString();
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int leftPadding = (width - text.length()) / 2;
        return " ".repeat(leftPadding) + text;
    }
}
