
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class SmartRetailBillingApp extends JFrame {

    private Inventory inventory = new Inventory();

    private JTabbedPane tabs;
    private JTable productTable;
    private JTable cartTable;
    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    private JTextField customerField;
    private JLabel totalLabel;

    private List<Bill.CartItem> cart = new ArrayList<>();

    public SmartRetailBillingApp() {

        setTitle("Smart Retail Billing Counter");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        tabs.addTab("Dashboard", createDashboard());
        tabs.addTab("Products", createProductsPanel());
        tabs.addTab("Billing", createBillingPanel());
        tabs.addTab("Bill History", createHistoryPanel());

        add(tabs);

        refreshProducts();
    }

    private JPanel createDashboard() {

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel(
            "SMART RETAIL BILLING COUNTER",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel(
            "Java Swing + JDBC + MySQL",
            SwingConstants.CENTER
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton openBilling = new JButton("Start New Billing");

        openBilling.addActionListener(e -> tabs.setSelectedIndex(2));

        panel.add(title);
        panel.add(subtitle);
        panel.add(openBilling);

        return panel;
    }

    private JPanel createProductsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {
            "ID", "Product Name", "Category", "Price", "Stock"
        };

        productModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        productTable = new JTable(productModel);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add Product");
        JButton update = new JButton("Update Stock");

        refresh.addActionListener(e -> refreshProducts());
        add.addActionListener(e -> addProduct());
        update.addActionListener(e -> updateStock());

        buttons.add(refresh);
        buttons.add(add);
        buttons.add(update);

        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBillingPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel();

        customerField = new JTextField(18);

        top.add(new JLabel("Customer Name:"));
        top.add(customerField);

        panel.add(top, BorderLayout.NORTH);

        String[] columns = {
            "Product ID", "Product", "Price", "Quantity", "Total"
        };

        cartModel = new DefaultTableModel(columns, 0);

        cartTable = new JTable(cartModel);

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton add = new JButton("Add Product");
        JButton remove = new JButton("Remove Selected");
        JButton checkout = new JButton("Checkout");

        totalLabel = new JLabel("Total: Rs. 0.00");

        add.addActionListener(e -> addToCart());
        remove.addActionListener(e -> removeFromCart());
        checkout.addActionListener(e -> checkout());

        bottom.add(add);
        bottom.add(remove);
        bottom.add(totalLabel);
        bottom.add(checkout);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHistoryPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
            "Bill ID", "Customer", "Date", "Subtotal",
            "Discount", "GST", "Grand Total"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        JButton refresh = new JButton("Refresh History");

        refresh.addActionListener(e -> {

            model.setRowCount(0);

            String sql =
                "SELECT b.bill_id, c.customer_name, b.bill_date, " +
                "b.subtotal, b.discount, b.gst, b.grand_total " +
                "FROM bills b JOIN customers c " +
                "ON b.customer_id=c.customer_id " +
                "ORDER BY b.bill_id DESC";

            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {

                    model.addRow(new Object[] {
                        rs.getInt("bill_id"),
                        rs.getString("customer_name"),
                        rs.getTimestamp("bill_date"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("discount"),
                        rs.getDouble("gst"),
                        rs.getDouble("grand_total")
                    });
                }

            } catch (SQLException ex) {
                showError(ex);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshProducts() {

        if (productModel == null) return;

        productModel.setRowCount(0);

        try {

            for (Product p : inventory.getAllProducts()) {

                productModel.addRow(new Object[] {
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getStock()
                });
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void addProduct() {

        JTextField name = new JTextField();
        String[] categories = {"Grocery", "Electronics"};
        JComboBox<String> category = new JComboBox<>(categories);
        JTextField price = new JTextField();
        JTextField stock = new JTextField();

        Object[] fields = {
            "Product Name:", name,
            "Category:", category,
            "Price:", price,
            "Stock:", stock
        };

        int result = JOptionPane.showConfirmDialog(
            this, fields, "Add Product",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String productName = name.getText().trim();
            double productPrice = Double.parseDouble(price.getText());
            int productStock = Integer.parseInt(stock.getText());

            if (productName.isEmpty() ||
                productPrice <= 0 || productStock < 0) {

                throw new IllegalArgumentException("Enter valid product details.");
            }

            inventory.addProduct(
                productName,
                (String) category.getSelectedItem(),
                productPrice,
                productStock
            );

            refreshProducts();

            JOptionPane.showMessageDialog(this, "Product added successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateStock() {

        int row = productTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }

        int id = (int) productModel.getValueAt(row, 0);

        String input = JOptionPane.showInputDialog(
            this, "Enter new stock quantity:"
        );

        if (input == null) return;

        try {

            int stock = Integer.parseInt(input);

            if (stock < 0) {
                throw new IllegalArgumentException("Stock cannot be negative.");
            }

            inventory.updateStock(id, stock);

            refreshProducts();

            JOptionPane.showMessageDialog(this, "Stock updated!");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void addToCart() {

        try {

            List<Product> products = inventory.getAllProducts();

            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products available.");
                return;
            }

            String[] choices = products.stream()
                .map(p -> p.getProductId() + " - " +
                    p.getProductName() + " (Rs. " +
                    p.getPrice() + ", Stock: " + p.getStock() + ")")
                .toArray(String[]::new);

            String selected = (String) JOptionPane.showInputDialog(
                this,
                "Choose a product:",
                "Add to Cart",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]
            );

            if (selected == null) return;

            int id = Integer.parseInt(selected.split(" - ")[0]);

            Product chosen = products.stream()
                .filter(p -> p.getProductId() == id)
                .findFirst()
                .orElseThrow();

            String input = JOptionPane.showInputDialog(
                this, "Enter quantity:"
            );

            if (input == null) return;

            int quantity = Integer.parseInt(input);

            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }

            int existingQuantity = 0;

            for (Bill.CartItem item : cart) {
                if (item.product.getProductId() == id) {
                    existingQuantity += item.quantity;
                }
            }

            if (existingQuantity + quantity > chosen.getStock()) {
                throw new IllegalArgumentException("Insufficient stock.");
            }

            Bill.CartItem existing = null;

            for (Bill.CartItem item : cart) {
                if (item.product.getProductId() == id) {
                    existing = item;
                    break;
                }
            }

            if (existing == null) {
                cart.add(new Bill.CartItem(chosen, quantity));
            } else {
                existing.quantity += quantity;
            }

            refreshCart();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void refreshCart() {

        cartModel.setRowCount(0);

        double total = 0;

        for (Bill.CartItem item : cart) {

            double amount = item.product.getPrice() * item.quantity;

            double discountRate =
                item.product.getCategory().equalsIgnoreCase("Grocery")
                ? 0.05 : 0.10;

            double gstRate =
                item.product.getCategory().equalsIgnoreCase("Grocery")
                ? 0.05 : 0.18;

            double discount = amount * discountRate;
            double lineTotal = amount - discount +
                (amount - discount) * gstRate;

            total += lineTotal;

            cartModel.addRow(new Object[] {
                item.product.getProductId(),
                item.product.getProductName(),
                item.product.getPrice(),
                item.quantity,
                String.format("%.2f", lineTotal)
            });
        }

        totalLabel.setText(String.format("Total: Rs. %.2f", total));
    }

    private void removeFromCart() {

        int row = cartTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.");
            return;
        }

        int id = (int) cartModel.getValueAt(row, 0);

        cart.removeIf(item -> item.product.getProductId() == id);

        refreshCart();
    }

    private void checkout() {

        try {

            int billId = Bill.checkout(customerField.getText(), cart);

            StringBuilder receipt = new StringBuilder();

            receipt.append("SMART RETAIL BILLING COUNTER\n");
            receipt.append("--------------------------------\n");
            receipt.append("Bill ID: ").append(billId).append("\n");
            receipt.append("Customer: ").append(customerField.getText()).append("\n");
            receipt.append("--------------------------------\n");

            double total = 0;

            for (Bill.CartItem item : cart) {

                double amount = item.product.getPrice() * item.quantity;

                double discountRate =
                    item.product.getCategory().equalsIgnoreCase("Grocery")
                    ? 0.05 : 0.10;

                double gstRate =
                    item.product.getCategory().equalsIgnoreCase("Grocery")
                    ? 0.05 : 0.18;

                double lineTotal = amount - amount * discountRate +
                    (amount - amount * discountRate) * gstRate;

                total += lineTotal;

                receipt.append(item.product.getProductName())
                    .append(" x ").append(item.quantity)
                    .append(" = Rs. ")
                    .append(String.format("%.2f", lineTotal))
                    .append("\n");
            }

            receipt.append("--------------------------------\n");
            receipt.append(String.format("Grand Total: Rs. %.2f", total));

            JOptionPane.showMessageDialog(
                this,
                receipt.toString(),
                "Bill Receipt",
                JOptionPane.INFORMATION_MESSAGE
            );

            cart.clear();
            cartModel.setRowCount(0);
            customerField.setText("");
            refreshCart();
            refreshProducts();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );

            DatabaseSetup.initialize();

            SwingUtilities.invokeLater(() -> {
                new SmartRetailBillingApp().setVisible(true);
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Application startup failed: " + e.getMessage()
            );
        }
    }
}
