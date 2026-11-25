
public class Electronics extends Product {

    public Electronics(int id, String name, double price, int stock) {
        super(id, name, "Electronics", price, stock);
    }

    @Override
    public double getDiscountRate() {
        return 0.10;
    }

    @Override
    public double getGstRate() {
        return 0.18;
    }
}
