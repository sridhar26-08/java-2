import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

/**
 * StoreAppUI.java
 * Modern Java Swing Graphical User Interface (GUI) for the In-Store Inventory & Ordering System.
 * 
 * Features:
 * - Real-time product search and variant stock checking (S, M, L, XL)
 * - Visual badges for in-stock and out-of-stock / backorder items
 * - Live shopping cart with item addition, removal, and calculation
 * - Customer info capture (name, phone)
 * - Promo code application (STUDENT10, RETAIL20, FIRSTBUY)
 * - Polymorphic payment selection (Cash with live change calculator, Card, UPI)
 * - Professional printable receipt popup dialog
 * - Dedicated tabs for Live Inventory Management (with restocking) and Order History
 */
public class StoreAppUI extends JFrame {

    private StoreManagement store;
    private Order currentOrder;
    private Customer currentCustomer;

    // GUI Components - POS Counter
    private JTextField searchField;
    private DefaultListModel<Product> productListModel;
    private JList<Product> productJList;
    private JComboBox<String> sizeComboBox;
    private JLabel stockStatusLabel;
    private JSpinner quantitySpinner;
    private JButton addToCartBtn;

    // Cart Components
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JTextField promoCodeField;
    private JButton applyPromoBtn;
    private JLabel promoFeedbackLabel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;

    // Payment Components
    private JRadioButton cashRadio;
    private JRadioButton cardRadio;
    private JRadioButton upiRadio;
    private JPanel paymentDetailsCardPanel;
    private CardLayout paymentCardLayout;
    private JTextField cashTenderedField;
    private JLabel changeDueLabel;
    private JTextField cardNumberField;
    private JTextField upiIdField;
    private JButton checkoutBtn;

    // Inventory Tab Components
    private DefaultTableModel inventoryTableModel;
    private JTable inventoryTable;
    private JComboBox<Product> restockProductCombo;
    private JComboBox<String> restockSizeCombo;
    private JSpinner restockQtySpinner;
    private JButton restockBtn;
    private JSpinner lowStockThresholdSpinner;

    // History Tab Components
    private DefaultTableModel historyTableModel;
    private JTable historyTable;
    private JButton viewHistoryReceiptBtn;

    // Modern Color Palette
    private static final Color PRIMARY_COLOR = new Color(30, 41, 59);    // Slate Dark
    private static final Color ACCENT_COLOR = new Color(37, 99, 235);    // Royal Blue
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);  // Emerald Green
    private static final Color DANGER_COLOR = new Color(239, 68, 68);    // Crimson Red
    private static final Color WARNING_COLOR = new Color(245, 158, 11);  // Amber
    private static final Color CARD_BG = new Color(248, 250, 252);       // Light Slate

    public StoreAppUI(StoreManagement store) {
        this.store = store;
        this.currentCustomer = new Customer("Walk-in Customer", "N/A");
        this.currentOrder = store.createNewOrder(currentCustomer);

        setTitle("Check&Buy - In-Store POS & Inventory System");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());

        // 1. Header Banner
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Navigation
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel posPanel = createPosPanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel historyPanel = createHistoryPanel();

        tabbedPane.addTab("🛒 POS Counter", posPanel);
        tabbedPane.addTab("📦 Live Inventory & Restock", inventoryPanel);
        tabbedPane.addTab("📋 Order History", historyPanel);

        // Refresh inventory tab when switched to it
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                refreshInventoryTable();
            } else if (tabbedPane.getSelectedIndex() == 2) {
                refreshHistoryTable();
            }
        });

        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(rootPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel titleLabel = new JLabel("CHECK&BUY");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("In-Store Counter Terminal & Real-Time Stock Management");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLabel.setForeground(new Color(203, 213, 225));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subLabel);

        JLabel statusBadge = new JLabel(" ● SYSTEM ONLINE ");
        statusBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(SUCCESS_COLOR);
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(6, 12, 6, 12));

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(statusBadge, BorderLayout.EAST);
        return panel;
    }

    // ==========================================
    // TAB 1: POS COUNTER & ORDERING
    // ==========================================
    private JPanel createPosPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Left: Catalog & Product Selection
        JPanel leftPanel = createCatalogPanel();
        // Right: Cart, Customer & Payment
        JPanel rightPanel = createCartAndCheckoutPanel();

        panel.add(leftPanel);
        panel.add(rightPanel);
        return panel;
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                "Product Catalog & Variant Stock",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                PRIMARY_COLOR));

        // Search Bar at Top
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(new EmptyBorder(4, 8, 4, 8));
        JLabel searchIcon = new JLabel("🔍 Search:");
        searchIcon.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Filter by Product ID or Name...");

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(); }
        });

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Product List
        productListModel = new DefaultListModel<>();
        for (Product p : store.searchProducts("")) {
            productListModel.addElement(p);
        }

        productJList = new JList<>(productListModel);
        productJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productJList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        productJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    Product p = (Product) value;
                    label.setText(String.format("<html><b>[%s]</b> %s <font color='#2563EB'><b>₹%.2f</b></font><br><font color='#64748B'>%s</font></html>",
                            p.getId(), p.getName(), p.getPrice(), p.getDescription()));
                    label.setBorder(new EmptyBorder(8, 8, 8, 8));
                }
                return label;
            }
        });

        productJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedProductVariants();
            }
        });

        JScrollPane listScroll = new JScrollPane(productJList);
        panel.add(listScroll, BorderLayout.CENTER);

        // Product Variant & Add to Cart Panel (Bottom)
        JPanel variantPanel = new JPanel(new GridBagLayout());
        variantPanel.setBackground(CARD_BG);
        variantPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Size Selector
        gbc.gridx = 0; gbc.gridy = 0;
        variantPanel.add(new JLabel("Select Size:"), gbc);
        sizeComboBox = new JComboBox<>();
        sizeComboBox.addActionListener(e -> updateStockBadge());
        gbc.gridx = 1; gbc.gridy = 0;
        variantPanel.add(sizeComboBox, gbc);

        // Real-Time Stock Status
        gbc.gridx = 2; gbc.gridy = 0;
        stockStatusLabel = new JLabel("Select an item");
        stockStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        variantPanel.add(stockStatusLabel, gbc);

        // Quantity Selector
        gbc.gridx = 0; gbc.gridy = 1;
        variantPanel.add(new JLabel("Quantity:"), gbc);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        gbc.gridx = 1; gbc.gridy = 1;
        variantPanel.add(quantitySpinner, gbc);

        // Add to Cart Button
        addToCartBtn = new ModernButton("🛒 Add to Order", ACCENT_COLOR, Color.WHITE);
        addToCartBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        addToCartBtn.addActionListener(e -> addItemToCart());
        gbc.gridx = 2; gbc.gridy = 1;
        variantPanel.add(addToCartBtn, gbc);

        panel.add(variantPanel, BorderLayout.SOUTH);

        // Select first product by default
        if (!productListModel.isEmpty()) {
            productJList.setSelectedIndex(0);
        }

        return panel;
    }

    private void filterProducts() {
        String query = searchField.getText();
        List<Product> matches = store.searchProducts(query);
        productListModel.clear();
        for (Product p : matches) {
            productListModel.addElement(p);
        }
        if (!productListModel.isEmpty()) {
            productJList.setSelectedIndex(0);
        } else {
            sizeComboBox.removeAllItems();
            stockStatusLabel.setText("No products matched");
            stockStatusLabel.setForeground(DANGER_COLOR);
        }
    }

    private void updateSelectedProductVariants() {
        Product p = productJList.getSelectedValue();
        if (p == null) return;

        sizeComboBox.removeAllItems();
        Map<String, Integer> sizes = store.getInventory().getStockBySizes(p.getId());
        for (String size : sizes.keySet()) {
            sizeComboBox.addItem(size);
        }
        updateStockBadge();
    }

    private void updateStockBadge() {
        Product p = productJList.getSelectedValue();
        String size = (String) sizeComboBox.getSelectedItem();
        if (p == null || size == null) {
            stockStatusLabel.setText("N/A");
            stockStatusLabel.setForeground(Color.GRAY);
            return;
        }

        int stock = store.getInventory().getStock(p.getId(), size);
        if (stock > 0) {
            stockStatusLabel.setText("✓ " + stock + " in stock");
            stockStatusLabel.setForeground(SUCCESS_COLOR);
        } else {
            stockStatusLabel.setText("⚠ OUT OF STOCK (Backorder OK)");
            stockStatusLabel.setForeground(WARNING_COLOR);
        }
    }

    private void addItemToCart() {
        Product p = productJList.getSelectedValue();
        String size = (String) sizeComboBox.getSelectedItem();
        int qty = (Integer) quantitySpinner.getValue();

        if (p == null || size == null) {
            JOptionPane.showMessageDialog(this, "Please select a product and size.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add to order
        store.addItemToOrder(currentOrder, p.getId(), size, qty);
        refreshCartTable();
        updateFinancialSummary();
    }

    // ==========================================
    // RIGHT PANEL: CART, PROMO & PAYMENT
    // ==========================================
    private JPanel createCartAndCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                "Order Cart & Checkout Counter",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                PRIMARY_COLOR));

        // Customer Details Bar (Top)
        JPanel customerPanel = new JPanel(new GridLayout(1, 4, 8, 4));
        customerPanel.setBorder(new EmptyBorder(6, 8, 6, 8));
        customerPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField("Aarav Sharma");
        customerPanel.add(customerNameField);

        customerPanel.add(new JLabel("Phone No:"));
        customerPhoneField = new JTextField("+91 98765-43210");
        customerPanel.add(customerPhoneField);

        panel.add(customerPanel, BorderLayout.NORTH);

        // Cart Table in Center
        String[] cols = {"Product", "Size", "Qty", "Price", "Total", "Fulfillment"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(24);
        cartTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cartTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Status column color styling
        cartTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && value.toString().contains("BACKORDER")) {
                    l.setForeground(new Color(180, 83, 9)); // Amber dark
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                } else {
                    l.setForeground(new Color(22, 101, 52)); // Green dark
                }
                return l;
            }
        });

        JPanel tableHolder = new JPanel(new BorderLayout(4, 4));
        tableHolder.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JButton clearBtn = new JButton("🗑 Clear Cart");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearBtn.addActionListener(e -> resetOrder());
        tableHolder.add(clearBtn, BorderLayout.SOUTH);

        panel.add(tableHolder, BorderLayout.CENTER);

        // Bottom: Promo, Financials, Payment Method, Checkout Button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Promo Code Row
        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        promoPanel.add(new JLabel("Promo Code:"));
        promoCodeField = new JTextField("STUDENT10", 10);
        promoPanel.add(promoCodeField);
        applyPromoBtn = new ModernButton("Apply", PRIMARY_COLOR, Color.WHITE);
        applyPromoBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        applyPromoBtn.addActionListener(e -> applyPromoCode());
        promoPanel.add(applyPromoBtn);
        promoFeedbackLabel = new JLabel("Try: STUDENT10 (10%), RETAIL20 (20%)");
        promoFeedbackLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        promoFeedbackLabel.setForeground(Color.GRAY);
        promoPanel.add(promoFeedbackLabel);
        bottomPanel.add(promoPanel);

        // Price Summary
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 8, 2));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(4, 4, 4, 4),
                BorderFactory.createLineBorder(new Color(226, 232, 240))
        ));
        summaryPanel.setBackground(CARD_BG);

        summaryPanel.add(new JLabel("  Subtotal:"));
        subtotalLabel = new JLabel("₹0.00  ", SwingConstants.RIGHT);
        summaryPanel.add(subtotalLabel);

        summaryPanel.add(new JLabel("  Discount:"));
        discountLabel = new JLabel("-₹0.00  ", SwingConstants.RIGHT);
        discountLabel.setForeground(SUCCESS_COLOR);
        summaryPanel.add(discountLabel);

        summaryPanel.add(new JLabel("  GST (5%):"));
        taxLabel = new JLabel("₹0.00  ", SwingConstants.RIGHT);
        summaryPanel.add(taxLabel);

        JLabel totalTitle = new JLabel("  TOTAL DUE:");
        totalTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        summaryPanel.add(totalTitle);

        totalLabel = new JLabel("₹0.00  ", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(ACCENT_COLOR);
        summaryPanel.add(totalLabel);

        bottomPanel.add(summaryPanel);

        // Payment Method Selector (Polymorphism)
        JPanel paymentSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        paymentSelectorPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
        paymentSelectorPanel.add(new JLabel("<b>Payment Method:</b>"));
        cashRadio = new JRadioButton("Cash");
        cardRadio = new JRadioButton("Credit/Debit Card", true);
        upiRadio = new JRadioButton("UPI / QR");

        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(cashRadio);
        paymentGroup.add(cardRadio);
        paymentGroup.add(upiRadio);

        paymentSelectorPanel.add(cashRadio);
        paymentSelectorPanel.add(cardRadio);
        paymentSelectorPanel.add(upiRadio);
        bottomPanel.add(paymentSelectorPanel);

        // CardLayout for Payment-specific inputs
        paymentCardLayout = new CardLayout();
        paymentDetailsCardPanel = new JPanel(paymentCardLayout);
        paymentDetailsCardPanel.setBorder(new EmptyBorder(4, 8, 4, 8));

        // 1. Cash Card
        JPanel cashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        cashPanel.add(new JLabel("Cash Tendered (₹):"));
        cashTenderedField = new JTextField("5000.00", 7);
        cashPanel.add(cashTenderedField);
        changeDueLabel = new JLabel("Change Due: ₹0.00");
        changeDueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        cashPanel.add(changeDueLabel);
        paymentDetailsCardPanel.add(cashPanel, "Cash");

        // 2. Card Card
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        cardPanel.add(new JLabel("Card Number:"));
        cardNumberField = new JTextField("4532 8923 1122 7890", 18);
        cardPanel.add(cardNumberField);
        paymentDetailsCardPanel.add(cardPanel, "Card");

        // 3. UPI Card
        JPanel upiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        upiPanel.add(new JLabel("UPI ID / VPA:"));
        upiIdField = new JTextField("customer@oksbi", 16);
        upiPanel.add(upiIdField);
        paymentDetailsCardPanel.add(upiPanel, "UPI");

        bottomPanel.add(paymentDetailsCardPanel);

        // Switch card listener
        cashRadio.addActionListener(e -> paymentCardLayout.show(paymentDetailsCardPanel, "Cash"));
        cardRadio.addActionListener(e -> paymentCardLayout.show(paymentDetailsCardPanel, "Card"));
        upiRadio.addActionListener(e -> paymentCardLayout.show(paymentDetailsCardPanel, "UPI"));

        // Checkout Button
        checkoutBtn = new ModernButton("💳 Complete Checkout & Print Receipt", SUCCESS_COLOR, Color.WHITE);
        checkoutBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        checkoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        checkoutBtn.setPreferredSize(new Dimension(320, 44));
        checkoutBtn.addActionListener(e -> processCheckout());

        bottomPanel.add(Box.createVerticalStrut(6));
        bottomPanel.add(checkoutBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void applyPromoCode() {
        String code = promoCodeField.getText().trim();
        if (currentOrder.applyDiscountCode(code)) {
            promoFeedbackLabel.setText(String.format("✓ Applied %s (%.0f%% OFF)",
                    code.toUpperCase(), currentOrder.getDiscountPercentage() * 100));
            promoFeedbackLabel.setForeground(SUCCESS_COLOR);
        } else {
            promoFeedbackLabel.setText("Invalid promo code.");
            promoFeedbackLabel.setForeground(DANGER_COLOR);
        }
        updateFinancialSummary();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (OrderItem item : currentOrder.getItems()) {
            String fulfillment = item.isBackorder() ? "BACKORDER (Restock)" : "IN-STORE (Immediate)";
            cartTableModel.addRow(new Object[]{
                    item.getProduct().getName(),
                    item.getSize(),
                    item.getQuantity(),
                    String.format("₹%.2f", item.getUnitPrice()),
                    String.format("₹%.2f", item.getSubtotal()),
                    fulfillment
            });
        }
    }

    private void updateFinancialSummary() {
        double subtotal = currentOrder.calculateSubtotal();
        double discount = currentOrder.calculateDiscount();
        double tax = currentOrder.calculateTax();
        double total = currentOrder.calculateTotal();

        subtotalLabel.setText(String.format("₹%.2f  ", subtotal));
        discountLabel.setText(String.format("-₹%.2f  ", discount));
        taxLabel.setText(String.format("₹%.2f  ", tax));
        totalLabel.setText(String.format("₹%.2f  ", total));
    }

    private void resetOrder() {
        currentCustomer = new Customer(customerNameField.getText(), customerPhoneField.getText());
        currentOrder = store.createNewOrder(currentCustomer);
        refreshCartTable();
        updateFinancialSummary();
        promoCodeField.setText("");
        promoFeedbackLabel.setText("Try: STUDENT10 (10%), RETAIL20 (20%)");
        promoFeedbackLabel.setForeground(Color.GRAY);
    }

    private void processCheckout() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Please add items to checkout.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Update customer details from input
        currentOrder.getCustomer().setName(customerNameField.getText().trim());
        currentOrder.getCustomer().setPhoneNumber(customerPhoneField.getText().trim());

        // Polymorphic Payment Selection
        Payment payment;
        if (cashRadio.isSelected()) {
            try {
                double tendered = Double.parseDouble(cashTenderedField.getText().trim());
                payment = new CashPayment(tendered);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid cash amount.", "Invalid Cash", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (cardRadio.isSelected()) {
            payment = new CardPayment(cardNumberField.getText().trim(), currentOrder.getCustomer().getName());
        } else {
            payment = new UpiPayment(upiIdField.getText().trim());
        }

        // Execute checkout through StoreManagement
        Receipt receipt = store.checkout(currentOrder, payment);
        if (receipt != null) {
            // Show formatted receipt popup
            showReceiptDialog(receipt);

            // Update product stock badge and list
            updateSelectedProductVariants();
            refreshInventoryTable();

            // Prepare next order
            resetOrder();
        } else {
            JOptionPane.showMessageDialog(this, "Payment failed! Please check your payment details.", "Transaction Declined", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceiptDialog(Receipt receipt) {
        JDialog dialog = new JDialog(this, "Official Store Receipt - " + receipt.generateReceiptText().split("\n")[4].trim(), true);
        dialog.setSize(620, 680);
        dialog.setLocationRelativeTo(this);

        JTextArea receiptText = new JTextArea(receipt.generateReceiptText());
        receiptText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptText.setEditable(false);
        receiptText.setBackground(new Color(254, 252, 232)); // Warm receipt paper color
        receiptText.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scrollPane = new JScrollPane(receiptText);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyBtn = new JButton("📋 Copy Receipt Text");
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(receiptText.getText()), null);
            JOptionPane.showMessageDialog(dialog, "Receipt copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton closeBtn = new JButton("Done");
        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(copyBtn);
        buttonPanel.add(closeBtn);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==========================================
    // TAB 2: LIVE INVENTORY & RESTOCKING
    // ==========================================
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Filter Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        topBar.add(new JLabel("Low Stock Threshold Alert (<= units):"));
        lowStockThresholdSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 50, 1));
        lowStockThresholdSpinner.addChangeListener(e -> refreshInventoryTable());
        topBar.add(lowStockThresholdSpinner);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshInventoryTable());
        topBar.add(refreshBtn);

        panel.add(topBar, BorderLayout.NORTH);

        // Inventory Table
        String[] columns = {"Product ID", "Product Name", "Size/Variant", "In-Stock Qty", "Status"};
        inventoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setRowHeight(26);
        inventoryTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        inventoryTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    if (value.toString().contains("OUT OF STOCK")) {
                        l.setForeground(DANGER_COLOR);
                        l.setFont(l.getFont().deriveFont(Font.BOLD));
                    } else if (value.toString().contains("LOW STOCK")) {
                        l.setForeground(WARNING_COLOR);
                        l.setFont(l.getFont().deriveFont(Font.BOLD));
                    } else {
                        l.setForeground(SUCCESS_COLOR);
                    }
                }
                return l;
            }
        });

        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        // Bottom: Restocking Action Form
        JPanel restockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        restockPanel.setBorder(BorderFactory.createTitledBorder("Quick Stock Replenishment"));
        restockPanel.setBackground(CARD_BG);

        restockPanel.add(new JLabel("Product:"));
        restockProductCombo = new JComboBox<>();
        for (Product p : store.searchProducts("")) {
            restockProductCombo.addItem(p);
        }
        restockPanel.add(restockProductCombo);

        restockPanel.add(new JLabel("Size:"));
        restockSizeCombo = new JComboBox<>(new String[]{"XS", "S", "M", "L", "XL", "XXL", "28", "30", "32", "34", "36", "Free Size"});
        restockPanel.add(restockSizeCombo);

        restockPanel.add(new JLabel("Add Units:"));
        restockQtySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        restockPanel.add(restockQtySpinner);

        restockBtn = new ModernButton("➕ Replenish Stock", ACCENT_COLOR, Color.WHITE);
        restockBtn.addActionListener(e -> {
            Product selectedP = (Product) restockProductCombo.getSelectedItem();
            String selectedSize = (String) restockSizeCombo.getSelectedItem();
            int addQty = (Integer) restockQtySpinner.getValue();
            if (selectedP != null && selectedSize != null) {
                store.getInventory().addStock(selectedP.getId(), selectedSize, addQty);
                refreshInventoryTable();
                updateSelectedProductVariants();
                JOptionPane.showMessageDialog(this,
                        String.format("Added %d units to %s (%s).", addQty, selectedP.getName(), selectedSize),
                        "Stock Replenished", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        restockPanel.add(restockBtn);

        panel.add(restockPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshInventoryTable() {
        inventoryTableModel.setRowCount(0);
        int threshold = (Integer) lowStockThresholdSpinner.getValue();

        for (Product p : store.searchProducts("")) {
            Map<String, Integer> stockSizes = store.getInventory().getStockBySizes(p.getId());
            for (Map.Entry<String, Integer> entry : stockSizes.entrySet()) {
                String size = entry.getKey();
                int qty = entry.getValue();
                String status;
                if (qty == 0) {
                    status = "OUT OF STOCK (Backorders active)";
                } else if (qty <= threshold) {
                    status = "LOW STOCK WARNING";
                } else {
                    status = "In Stock";
                }

                inventoryTableModel.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        size,
                        qty,
                        status
                });
            }
        }
    }

    // ==========================================
    // TAB 3: ORDER HISTORY
    // ==========================================
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] headers = {"Order ID", "Date & Time", "Customer Name", "Phone", "Items Count", "Total Paid", "Status"};
        historyTableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(26);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshHistBtn = new JButton("🔄 Refresh History");
        refreshHistBtn.addActionListener(e -> refreshHistoryTable());
        bottomBar.add(refreshHistBtn);

        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshHistoryTable() {
        historyTableModel.setRowCount(0);
        for (Order o : store.getOrderHistory()) {
            historyTableModel.addRow(new Object[]{
                    o.getOrderId(),
                    o.getFormattedDate(),
                    o.getCustomer().getName(),
                    o.getCustomer().getPhoneNumber(),
                    o.getItems().size(),
                    String.format("₹%.2f", o.calculateTotal()),
                    o.getStatus()
            });
        }
    }

    /**
     * Modern custom button with anti-aliased rounded background painting.
     * Fixes macOS Aqua look-and-feel where setBackground() is ignored,
     * ensuring white button text is always 100% sharp and visible against vibrant colors.
     */
    public static class ModernButton extends JButton {
        private Color bgColor;
        private Color hoverColor;

        public ModernButton(String text, Color bgColor, Color textColor) {
            super(text);
            this.bgColor = bgColor;
            this.hoverColor = new Color(
                    Math.max(0, bgColor.getRed() - 25),
                    Math.max(0, bgColor.getGreen() - 25),
                    Math.max(0, bgColor.getBlue() - 25)
            );
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(hoverColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(bgColor);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
