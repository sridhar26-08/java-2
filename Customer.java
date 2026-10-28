/**
 * Customer.java
 * Holds customer details for in-store checkout and receipt delivery.
 */
public class Customer {
    private String name;
    private String phoneNumber;

    public Customer(String name, String phoneNumber) {
        this.name = (name == null || name.trim().isEmpty()) ? "Walk-in Customer" : name.trim();
        this.phoneNumber = (phoneNumber == null || phoneNumber.trim().isEmpty()) ? "N/A" : phoneNumber.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return name + " (" + phoneNumber + ")";
    }
}
