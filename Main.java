import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main.java
 * Complete demonstration of the In-Store Inventory & Ordering System.
 * 
 * Features demonstrated:
 * 1. Product catalog browsing & search
 * 2. Real-time stock availability check by size/variant
 * 3. Ordering in-stock items AND placing orders for out-of-stock items (backorders)
 * 4. Automatic inventory deduction upon successful payment
 * 5. Promo/discount code application
 * 6. Polymorphic payment processing (Cash, Card, UPI)
 * 7. Professional formatted receipt generation
 * 8. Order history and low-stock alert monitoring
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("       IN-STORE INVENTORY & RETAIL COUNTER ORDERING SYSTEM       ");
        System.out.println("=================================================================");

        // 1. Initialize Store
        StoreManagement store = new StoreManagement("Check&Buy");

        // 2. Load Sample Data (3 clothing products with sizes S, M, L, XL)
        seedSampleData(store);

        // Check if user requested CLI mode specifically
        boolean isCliMode = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-cli".equalsIgnoreCase(arg)) {
                isCliMode = true;
                break;
            }
        }

        // Launch GUI by default if display environment is available
        if (!isCliMode && !java.awt.GraphicsEnvironment.isHeadless()) {
            System.out.println("Starting Graphical User Interface (GUI)...");
            System.out.println("(Tip: Run with 'java Main --cli' if you want console-only mode)");
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
                StoreAppUI ui = new StoreAppUI(store);
                ui.setVisible(true);
            });
            return;
        }

        // Run full automated demonstration showcasing all requirements in CLI
        runAutomatedWalkthrough(store);

        // Interactive mode check
        System.out.println("\nWould you like to enter Interactive Counter Mode? (y/N): ");
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        if (scanner.hasNextLine()) {
            choice = scanner.nextLine().trim();
        }
        if (choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")) {
            runInteractiveMode(store, scanner);
        } else {
            System.out.println("\nDemonstration concluded. Thank you!");
        }
    }

    /**
     * Seeds initial product catalog and size-wise inventory.
     * Contains all 12 clothing store products with exact sizes and quantities.
     */
    private static void seedSampleData(StoreManagement store) {
        registerItem(store, "P001", "Summer Dress", 1500.00, "Floral lightweight breathable summer dress",
                "XS", 5, "S", 8, "M", 3, "L", 6, "XL", 4);

        registerItem(store, "P002", "Cotton T-Shirt", 599.00, "100% combed cotton round-neck tee",
                "S", 12, "M", 15, "L", 10, "XL", 7, "XXL", 5);

        registerItem(store, "P003", "Slim Fit Jeans", 2499.00, "Stretch denim dark indigo slim jeans",
                "28", 6, "30", 10, "32", 8, "34", 4, "36", 3);

        registerItem(store, "P004", "Traditional Kurta", 1299.00, "Ethnic straight-fit handcrafted cotton kurta",
                "S", 4, "M", 9, "L", 7, "XL", 5);

        registerItem(store, "P005", "Cotton Saree", 2999.00, "Pure handloom traditional woven saree",
                "Free Size", 8);

        registerItem(store, "P006", "Formal Shirt", 1899.00, "Wrinkle-resistant premium office formal shirt",
                "S", 7, "M", 11, "L", 9, "XL", 6);

        registerItem(store, "P007", "Casual Shorts", 899.00, "Comfortable cotton drawstring summer shorts",
                "S", 10, "M", 12, "L", 8, "XL", 5);

        registerItem(store, "P008", "Sports Tshirt", 799.00, "Quick-dry moisture-wicking active athletic tee",
                "XS", 6, "S", 14, "M", 16, "L", 9, "XL", 7);

        registerItem(store, "P009", "Denim Jacket", 3499.00, "Classic washed rugged indigo trucker jacket",
                "S", 3, "M", 5, "L", 4, "XL", 2);

        registerItem(store, "P010", "Cotton Lehenga", 3999.00, "Traditional flared festive ethnic lehenga skirt",
                "Free Size", 4);

        registerItem(store, "P011", "Polo Shirt", 1299.00, "Classic pique cotton ribbed-collar polo",
                "S", 8, "M", 10, "L", 7, "XL", 5);

        registerItem(store, "P012", "Palazzo Pants", 1599.00, "Wide-leg relaxed breathable ethnic palazzo",
                "S", 6, "M", 9, "L", 8, "XL", 4);
    }

    private static void registerItem(StoreManagement store, String id, String name, double price, String desc, Object... sizeQtys) {
        Product p = new Product(id, name, price, desc);
        Map<String, Integer> stock = new HashMap<>();
        for (int i = 0; i < sizeQtys.length; i += 2) {
            stock.put((String) sizeQtys[i], (Integer) sizeQtys[i + 1]);
        }
        store.registerProduct(p, stock);
    }

    /**
     * Executes an end-to-end automated walkthrough covering all project brief requirements.
     */
    private static void runAutomatedWalkthrough(StoreManagement store) {
        System.out.println("\n>>> [STEP 1] DISPLAYING PRODUCT CATALOG <<<");
        store.displayCatalog();

        System.out.println("\n>>> [STEP 2] REAL-TIME PRODUCT SEARCH <<<");
        System.out.println("Searching for keyword 'Shirt':");
        List<Product> searchResults = store.searchProducts("Shirt");
        for (Product p : searchResults) {
            System.out.println("  Found: " + p);
        }

        System.out.println("\n>>> [STEP 3] CHECKING REAL-TIME STOCK BY SIZE/VARIANT <<<");
        store.displayStockForProduct("P001");
        store.displayStockForProduct("P009");

        System.out.println("\n>>> [STEP 4] CREATING CUSTOMER ORDER & HANDLING OUT-OF-STOCK ITEMS <<<");
        Customer customer = new Customer("Aarav Sharma", "+91 98765-43210");
        System.out.println("Customer at Counter: " + customer);

        Order order = store.createNewOrder(customer);
        System.out.println("New Order Initialized: " + order.getOrderId());

        System.out.println("\nAdding Item 1: In-stock item (P001, Size M, Qty 2)...");
        store.addItemToOrder(order, "P001", "M", 2);

        System.out.println("\nAdding Item 2: Backorder demo (P001, Size XL, Qty 10 - exceeds stock of 4)...");
        store.addItemToOrder(order, "P001", "XL", 10);

        System.out.println("\nAdding Item 3: In-stock item (P009, Size S, Qty 1)...");
        store.addItemToOrder(order, "P009", "S", 1);

        System.out.println("\n>>> [STEP 5] APPLYING DISCOUNT PROMOTIONAL CODE <<<");
        String promo = "STUDENT10";
        if (order.applyDiscountCode(promo)) {
            System.out.printf("✓ Applied Promo '%s' (10%% discount applied)%n", promo);
        }

        System.out.println("\n>>> [STEP 6] PROCESSING PAYMENT (POLYMORPHISM DEMO) <<<");
        // We demonstrate Card payment (or UPI/Cash)
        Payment payment = new CardPayment("4532 8923 1122 7890", "Aarav Sharma");
        Receipt receipt = store.checkout(order, payment);

        System.out.println("\n>>> [STEP 7] GENERATING AND PRINTING RECEIPT <<<");
        if (receipt != null) {
            receipt.printReceipt();
        }

        System.out.println(">>> [STEP 8] VERIFYING INVENTORY DEDUCTION <<<");
        System.out.println("Notice that Size 'M' decreased from 3 to 1, while 'XL' backorder was recorded:");
        store.displayStockForProduct("P001");

        System.out.println("\n>>> [STEP 9] LOW STOCK ALERTS REPORT <<<");
        store.checkLowStockAlerts(3);

        System.out.println("\n>>> [STEP 10] TRACKING ORDER HISTORY <<<");
        store.displayOrderHistory();
    }

    /**
     * Interactive console mode allowing user to test any operation.
     */
    private static void runInteractiveMode(StoreManagement store, Scanner scanner) {
        while (true) {
            System.out.println("\n--- COUNTER TERMINAL MENU ---");
            System.out.println("1. View Store Catalog");
            System.out.println("2. Search Product");
            System.out.println("3. Check Stock by Size");
            System.out.println("4. Create New Order & Checkout");
            System.out.println("5. View Low Stock Alerts");
            System.out.println("6. View Order History");
            System.out.println("7. Exit");
            System.out.print("Select an option (1-7): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    store.displayCatalog();
                    break;
                case "2":
                    System.out.print("Enter search term (ID or Name): ");
                    String term = scanner.nextLine();
                    List<Product> found = store.searchProducts(term);
                    if (found.isEmpty()) {
                        System.out.println("No matching products found.");
                    } else {
                        for (Product p : found) {
                            System.out.println("  " + p);
                        }
                    }
                    break;
                case "3":
                    System.out.print("Enter Product ID: ");
                    String pId = scanner.nextLine();
                    store.displayStockForProduct(pId);
                    break;
                case "4":
                    handleInteractiveCheckout(store, scanner);
                    break;
                case "5":
                    System.out.print("Enter low stock threshold: ");
                    try {
                        int thresh = Integer.parseInt(scanner.nextLine().trim());
                        store.checkLowStockAlerts(thresh);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                    break;
                case "6":
                    store.displayOrderHistory();
                    break;
                case "7":
                    System.out.println("Exiting terminal. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleInteractiveCheckout(StoreManagement store, Scanner scanner) {
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Customer Phone: ");
        String phone = scanner.nextLine();

        Customer cust = new Customer(name, phone);
        Order order = store.createNewOrder(cust);

        while (true) {
            System.out.print("Enter Product ID to add (or 'done' to finish): ");
            String id = scanner.nextLine().trim();
            if (id.equalsIgnoreCase("done")) break;

            Product p = store.getProductById(id);
            if (p == null) {
                System.out.println("Product not found! Check catalog.");
                continue;
            }

            store.displayStockForProduct(id);
            System.out.print("Enter Size: ");
            String size = scanner.nextLine().trim();
            System.out.print("Enter Quantity: ");
            try {
                int qty = Integer.parseInt(scanner.nextLine().trim());
                store.addItemToOrder(order, id, size, qty);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity number.");
            }
        }

        if (order.getItems().isEmpty()) {
            System.out.println("No items added. Order cancelled.");
            return;
        }

        System.out.print("Enter promo code (e.g. STUDENT10, RETAIL20, or press Enter to skip): ");
        String promo = scanner.nextLine().trim();
        if (!promo.isEmpty()) {
            if (order.applyDiscountCode(promo)) {
                System.out.println("Promo code applied successfully!");
            } else {
                System.out.println("Invalid promo code. Proceeding without discount.");
            }
        }

        System.out.printf("Total to Pay: ₹%.2f%n", order.calculateTotal());
        System.out.println("Select Payment Method: 1) Cash  2) Card  3) UPI");
        String pMethod = scanner.nextLine().trim();
        Payment payment;
        if (pMethod.equals("1")) {
            System.out.print("Enter cash amount tendered (₹): ");
            double cash = Double.parseDouble(scanner.nextLine().trim());
            payment = new CashPayment(cash);
        } else if (pMethod.equals("2")) {
            System.out.print("Enter Card Number: ");
            String cardNum = scanner.nextLine();
            payment = new CardPayment(cardNum, cust.getName());
        } else {
            System.out.print("Enter UPI ID (e.g., name@okbank): ");
            String upi = scanner.nextLine();
            payment = new UpiPayment(upi);
        }

        Receipt receipt = store.checkout(order, payment);
        if (receipt != null) {
            receipt.printReceipt();
        }
    }
}
