
public abstract class Product {

    protected int productId;
    protected String productName;
    protected String category;
    protected double price;
    protected int stock;

    public Product(int productId, String productName,
                   String category, double price, int stock) {

        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public abstract double getDiscountRate();

    public abstract double getGstRate();

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
