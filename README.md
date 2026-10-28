# Check&Buy - In-Store Inventory & Ordering System

An Object-Oriented Java application built for retail shops (e.g., clothing/apparel stores) to check real-time stock availability across sizes/variants, process orders (including backorders for out-of-stock items), deduct inventory upon purchase, handle payments polymorphically, and print formatted receipts.

---

## 📁 Project Architecture & OOP Concepts

| File | Class | OOP Concepts Demonstrated | Responsibility |
|---|---|---|---|
| [Product.java](file:///Users/ysridhar/Desktop/java%202/Product.java) | `Product` | **Encapsulation** | Stores ID, name, price, description with validation. |
| [Inventory.java](file:///Users/ysridhar/Desktop/java%202/Inventory.java) | `Inventory` | **Data Structures (`Map`)** | Real-time stock counts organized by `ProductId -> Size/Variant`. |
| [InsufficientStockException.java](file:///Users/ysridhar/Desktop/java%202/InsufficientStockException.java) | `InsufficientStockException` | **Exception Handling** | Custom checked exception for stock deduction protection. |
| [Customer.java](file:///Users/ysridhar/Desktop/java%202/Customer.java) | `Customer` | **Encapsulation** | Tracks customer details (Name, Phone number). |
| [OrderItem.java](file:///Users/ysridhar/Desktop/java%202/OrderItem.java) | `OrderItem` | **Composition** | Line item with product reference, size, qty, and backorder flag. |
| [Payment.java](file:///Users/ysridhar/Desktop/java%202/Payment.java) | `Payment` (abstract) | **Abstraction & Polymorphism** | Abstract base class defining `processPayment()`. |
| [CashPayment.java](file:///Users/ysridhar/Desktop/java%202/CashPayment.java) | `CashPayment` | **Inheritance & Polymorphism** | Cash processing with change calculation. |
| [CardPayment.java](file:///Users/ysridhar/Desktop/java%202/CardPayment.java) | `CardPayment` | **Inheritance & Polymorphism** | Credit/Debit processing with card number masking. |
| [UpiPayment.java](file:///Users/ysridhar/Desktop/java%202/UpiPayment.java) | `UpiPayment` | **Inheritance & Polymorphism** | UPI / QR processing with reference IDs. |
| [Order.java](file:///Users/ysridhar/Desktop/java%202/Order.java) | `Order` | **Business Logic** | Total, taxes, promo codes (`STUDENT10`), order state. |
| [Receipt.java](file:///Users/ysridhar/Desktop/java%202/Receipt.java) | `Receipt` | **Formatting & Presentation** | Generates ASCII printable receipts with backorder notices. |
| [StoreAppUI.java](file:///Users/ysridhar/Desktop/java%202/StoreAppUI.java) | `StoreAppUI` | **GUI (Java Swing)** | Interactive POS Counter, Cart, Variant Stock Badges, Payment, and Receipt Popup. |
| [StoreManagement.java](file:///Users/ysridhar/Desktop/java%202/StoreManagement.java) | `StoreManagement` | **Facade / Controller** | Central coordinator for catalog, search, stock, checkout, and history. |
| [Main.java](file:///Users/ysridhar/Desktop/java%202/Main.java) | `Main` | **Driver Program** | Starts GUI by default; supports automated demo and `--cli` mode. |

---

## 🚀 How to Compile and Run

Open terminal in the project directory:

```bash
cd "/Users/ysridhar/Desktop/java 2"

# 1. Compile all Java files
javac *.java

# 2. Run the application (Opens the Graphical Desktop POS UI)
java Main

# 3. Or run in terminal CLI mode
java Main --cli
```

---

## 💡 Key Features Implemented

1. **Product Search**: Search catalog by ID or keyword in product name.
2. **Real-time Variant Tracking**: Stock tracked per size (S, M, L, XL, etc.).
3. **Out-of-Stock Handling (Backorder)**: Customers can place orders for items currently out-of-stock. These items are tagged as `[BACKORDER]` and not deducted from current stock.
4. **Automatic Stock Deduction**: Successfully purchased in-stock items are deducted from the inventory.
5. **Promotional Discounts**: Supports discount codes like `STUDENT10` (10% off), `RETAIL20` (20% off), `FIRSTBUY` (15% off).
6. **Multiple Payment Methods**: Demonstrates OOP Polymorphism via `CashPayment`, `CardPayment`, and `UpiPayment`.
7. **Clean Receipt Printing**: Formatted ASCII receipt showing itemized breakdown, tax, discount, total, payment details, and backorder notifications.
8. **Low Stock Alerts**: Identifies variants at or below a configurable threshold.
9. **Order History**: Stores and displays past transaction records.
