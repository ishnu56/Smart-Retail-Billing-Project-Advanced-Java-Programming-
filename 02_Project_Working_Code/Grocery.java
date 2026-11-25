
public class Grocery extends Product {

    public Grocery(int id, String name, double price, int stock) {
        super(id, name, "Grocery", price, stock);
    }

    @Override
    public double getDiscountRate() {
        return 0.05;
    }

    @Override
    public double getGstRate() {
        return 0.05;
    }
}
