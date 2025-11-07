package oop.tanregister.model;

import java.util.Date;

public class Product {
    private String id;
    private String name;
    private String type;
    private int stock;
    private double price;
    private String status;
    private Date date;
    private byte[] imageBytes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public byte[] getImageBytes() { return imageBytes; }
    public void setImageBytes(byte[] imageBytes) { this.imageBytes = imageBytes; }
}
