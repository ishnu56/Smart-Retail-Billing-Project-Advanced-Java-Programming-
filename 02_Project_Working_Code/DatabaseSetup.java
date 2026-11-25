
import java.sql.*;

public class DatabaseSetup {

    public static void initialize() {

        String createDatabase =
            "CREATE DATABASE IF NOT EXISTS retail_billing";

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/",
                "root",
                System.getenv("MYSQL_PASSWORD"));
             Statement st = con.createStatement()) {

            st.executeUpdate(createDatabase);

        } catch (SQLException e) {
            System.out.println("Database creation error: " + e.getMessage());
            return;
        }

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS products (" +
                "product_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "product_name VARCHAR(100) NOT NULL, " +
                "category VARCHAR(50) NOT NULL, " +
                "price DOUBLE NOT NULL, " +
                "stock INT NOT NULL)"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS customers (" +
                "customer_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_name VARCHAR(100) NOT NULL)"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS bills (" +
                "bill_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_id INT, " +
                "bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "subtotal DOUBLE, " +
                "discount DOUBLE, " +
                "gst DOUBLE, " +
                "grand_total DOUBLE, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS bill_items (" +
                "item_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "bill_id INT, " +
                "product_id INT, " +
                "quantity INT, " +
                "unit_price DOUBLE, " +
                "discount DOUBLE, " +
                "gst DOUBLE, " +
                "line_total DOUBLE, " +
                "FOREIGN KEY (bill_id) REFERENCES bills(bill_id), " +
                "FOREIGN KEY (product_id) REFERENCES products(product_id))"
            );

            insertSampleProducts(con);

            System.out.println("Database and tables initialized successfully!");

        } catch (SQLException e) {
            System.out.println("Table creation error: " + e.getMessage());
        }
    }

    private static void insertSampleProducts(Connection con)
            throws SQLException {

        String check = "SELECT COUNT(*) FROM products";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(check)) {

            rs.next();

            if (rs.getInt(1) > 0) {
                return;
            }
        }

        String sql =
            "INSERT INTO products(product_name, category, price, stock) " +
            "VALUES (?, ?, ?, ?)";

        Object[][] products = {
            {"Rice 1kg", "Grocery", 60.0, 100},
            {"Wheat Flour 1kg", "Grocery", 45.0, 80},
            {"Sugar 1kg", "Grocery", 48.0, 90},
            {"Milk 1L", "Grocery", 30.0, 100},
            {"Cooking Oil 1L", "Grocery", 140.0, 50},
            {"Salt 1kg", "Grocery", 25.0, 70},
            {"Tea Powder", "Grocery", 120.0, 40},
            {"Coffee Powder", "Grocery", 150.0, 35},
            {"Biscuits", "Grocery", 20.0, 100},
            {"Chocolate", "Grocery", 50.0, 60},
            {"Headphones", "Electronics", 999.0, 25},
            {"Keyboard", "Electronics", 799.0, 30},
            {"Mouse", "Electronics", 499.0, 40},
            {"USB Cable", "Electronics", 199.0, 50},
            {"Power Bank", "Electronics", 1499.0, 20},
            {"Bluetooth Speaker", "Electronics", 1299.0, 15},
            {"Smart Watch", "Electronics", 2499.0, 12},
            {"LED Bulb", "Electronics", 199.0, 60}
        };

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (Object[] p : products) {
                ps.setString(1, (String) p[0]);
                ps.setString(2, (String) p[1]);
                ps.setDouble(3, (Double) p[2]);
                ps.setInt(4, (Integer) p[3]);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
}
