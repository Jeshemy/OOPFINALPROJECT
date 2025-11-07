package oop.tanregister.register;

public class ProductTableData {
    private String name;
    private double price;
    private int stock;
    private byte[] imageBytes;

    public ProductTableData(String name, double price, int stock, byte[] imageBytes) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageBytes = imageBytes;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public byte[] getImageBytes() { return imageBytes; }

    public void setStock(int stock) { this.stock = stock; }
    public void setPrice(double price) { this.price = price; }
}
