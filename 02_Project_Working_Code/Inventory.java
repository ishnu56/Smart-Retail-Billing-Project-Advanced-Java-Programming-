
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    public List<Product> getAllProducts() throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products ORDER BY product_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String category = rs.getString("category");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");

                if (category.equalsIgnoreCase("Grocery")) {
                    products.add(new Grocery(id, name, price, stock));
                } else {
                    products.add(new Electronics(id, name, price, stock));
                }
            }
        }

        return products;
    }

    public void addProduct(String name, String category,
                           double price, int stock) throws SQLException {

        String sql =
            "INSERT INTO products(product_name, category, price, stock) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, stock);

            ps.executeUpdate();
        }
    }

    public void updateStock(int id, int stock) throws SQLException {

        String sql = "UPDATE products SET stock=? WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, stock);
            ps.setInt(2, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Product not found.");
            }
        }
    }
}
