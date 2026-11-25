
import java.sql.*;
import java.util.*;

public class Bill {

    public static class CartItem {
        public Product product;
        public int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    public static int checkout(String customerName,
                               List<CartItem> cart) throws SQLException {

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new SQLException("Enter customer name.");
        }

        if (cart == null || cart.isEmpty()) {
            throw new SQLException("Cart is empty.");
        }

        double subtotal = 0;
        double totalDiscount = 0;
        double totalGst = 0;
        double grandTotal = 0;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                // Calculate bill
                for (CartItem item : cart) {

                    if (item.quantity <= 0) {
                        throw new SQLException("Invalid quantity.");
                    }

                    String stockSQL =
                        "SELECT price, stock, category FROM products " +
                        "WHERE product_id=? FOR UPDATE";

                    try (PreparedStatement ps =
                             con.prepareStatement(stockSQL)) {

                        ps.setInt(1, item.product.getProductId());

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException("Product not found.");
                            }

                            if (rs.getInt("stock") < item.quantity) {
                                throw new SQLException(
                                    "Insufficient stock for " +
                                    item.product.getProductName()
                                );
                            }

                            double price = rs.getDouble("price");
                            String category = rs.getString("category");

                            double discountRate =
                                category.equalsIgnoreCase("Grocery")
                                ? 0.05 : 0.10;

                            double gstRate =
                                category.equalsIgnoreCase("Grocery")
                                ? 0.05 : 0.18;

                            double amount = price * item.quantity;
                            double discount = amount * discountRate;
                            double taxable = amount - discount;
                            double gst = taxable * gstRate;
                            double lineTotal = taxable + gst;

                            subtotal += amount;
                            totalDiscount += discount;
                            totalGst += gst;
                            grandTotal += lineTotal;
                        }
                    }
                }

                // Save customer
                int customerId;

                String customerSQL =
                    "INSERT INTO customers(customer_name) VALUES(?)";

                try (PreparedStatement ps = con.prepareStatement(
                        customerSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, customerName.trim());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        customerId = rs.getInt(1);
                    }
                }

                // Save bill
                int billId;

                String billSQL =
                    "INSERT INTO bills(customer_id, subtotal, discount, " +
                    "gst, grand_total) VALUES(?,?,?,?,?)";

                try (PreparedStatement ps = con.prepareStatement(
                        billSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, customerId);
                    ps.setDouble(2, subtotal);
                    ps.setDouble(3, totalDiscount);
                    ps.setDouble(4, totalGst);
                    ps.setDouble(5, grandTotal);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        billId = rs.getInt(1);
                    }
                }

                // Save bill items and update stock
                String itemSQL =
                    "INSERT INTO bill_items(bill_id, product_id, quantity, " +
                    "unit_price, discount, gst, line_total) " +
                    "VALUES(?,?,?,?,?,?,?)";

                String stockUpdate =
                    "UPDATE products SET stock=stock-? WHERE product_id=?";

                try (PreparedStatement itemPS =
                         con.prepareStatement(itemSQL);
                     PreparedStatement stockPS =
                         con.prepareStatement(stockUpdate)) {

                    for (CartItem item : cart) {

                        String sql =
                            "SELECT price, category FROM products " +
                            "WHERE product_id=?";

                        double price;
                        String category;

                        try (PreparedStatement ps =
                                 con.prepareStatement(sql)) {

                            ps.setInt(1, item.product.getProductId());

                            try (ResultSet rs = ps.executeQuery()) {
                                rs.next();
                                price = rs.getDouble("price");
                                category = rs.getString("category");
                            }
                        }

                        double amount = price * item.quantity;

                        double discountRate =
                            category.equalsIgnoreCase("Grocery")
                            ? 0.05 : 0.10;

                        double gstRate =
                            category.equalsIgnoreCase("Grocery")
                            ? 0.05 : 0.18;

                        double discount = amount * discountRate;
                        double gst = (amount - discount) * gstRate;
                        double lineTotal = amount - discount + gst;

                        itemPS.setInt(1, billId);
                        itemPS.setInt(2, item.product.getProductId());
                        itemPS.setInt(3, item.quantity);
                        itemPS.setDouble(4, price);
                        itemPS.setDouble(5, discount);
                        itemPS.setDouble(6, gst);
                        itemPS.setDouble(7, lineTotal);
                        itemPS.addBatch();

                        stockPS.setInt(1, item.quantity);
                        stockPS.setInt(2, item.product.getProductId());
                        stockPS.addBatch();
                    }

                    itemPS.executeBatch();
                    stockPS.executeBatch();
                }

                con.commit();
                return billId;

            } catch (Exception e) {
                con.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(e);
            }
        }
    }
}
