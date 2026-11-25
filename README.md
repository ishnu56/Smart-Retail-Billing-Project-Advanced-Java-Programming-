# Smart-Retail-Billing-Project-Advanced-Java-Programming-
Smart Retail Billing System – Project Report, Working Code and Video Presentation.


🛒 Smart Retail Billing Counter
A desktop-based retail billing management system developed using Java Swing, JDBC, and MySQL. The application simplifies retail store operations by providing product management, inventory tracking, automated billing, GST calculation, and bill history in a user-friendly graphical interface.

📌 Project Overview
The Smart Retail Billing Counter is designed to make retail shop billing faster, easier, and more accurate. It allows shopkeepers to manage grocery and electronics products, maintain stock quantities, generate customer bills, and store transaction details in a MySQL database.

✨ Features
Dashboard: Simple graphical interface to access application modules.
Product Management: View and add grocery and electronics products.
Inventory Management: Update product stock quantities.
Shopping Cart: Add products, change quantities, and remove selected items.
Automated Billing: Calculate the total bill automatically.
Category-Based Discounts:
Grocery: 5% discount.
Electronics: 10% discount.
GST Calculation:
Grocery: 5% GST.
Electronics: 18% GST.
Bill Generation: Generate customer receipts with bill ID and total amount.
Bill History: View previously generated bills and customer details.
Database Integration: Store products, customers, bills, and bill items in MySQL.
Stock Management: Automatically reduce product stock after successful checkout.
🛠️ Technologies Used
Technology	Purpose
Java	Core programming language
Java Swing	Graphical User Interface
JDBC	Database connectivity
MySQL	Database management
MySQL Connector/J	Connect Java with MySQL
OOP	Object-oriented programming concepts
📂 Project Structure
SmartRetailBilling/
│
├── Bill.java
├── DatabaseSetup.java
├── DBConnection.java
├── Electronics.java
├── Grocery.java
├── Inventory.java
├── Product.java
├── SmartRetailBillingApp.java
│
├── mysql-connector-j.jar
│
└── README.md
🗄️ Database Structure
Database Name: retail_billing

The application automatically creates the database and the following tables:

Table	Description
products	Stores product information and stock
customers	Stores customer details
bills	Stores billing and payment totals
bill_items	Stores individual products in each bill
The application also inserts sample grocery and electronics products when the products table is empty.

⚙️ Requirements
Before running the project, install:

Java JDK (Java 8 or later).
MySQL Server.
MySQL Connector/J.
VS Code, IntelliJ IDEA, or any Java IDE.
🚀 How to Run the Project
Step 1: Clone the Repository
git clone https://github.com/YOUR-USERNAME/SmartRetailBilling.git
Step 2: Open the Project Folder
cd SmartRetailBilling
Step 3: Configure MySQL Password
The application uses the MySQL root account.

Set your MySQL password as an environment variable.

Windows PowerShell:

$env:MYSQL_PASSWORD="your_mysql_password"
Replace your_mysql_password with your actual MySQL password.

Make sure your MySQL server is running.

Step 4: Compile the Java Files
javac -cp ".;mysql-connector-j.jar" *.java
Step 5: Run the Application
java -cp ".;mysql-connector-j.jar" SmartRetailBillingApp
The application will automatically initialize the database and tables before opening the graphical interface.

🖥️ Application Modules
1. Dashboard
Provides access to the main billing application.

2. Products
Displays available products with their IDs, names, categories, prices, and stock quantities. Users can add products and update stock.

3. Billing
Allows users to select products, enter quantities, manage the shopping cart, and generate customer bills.

4. Bill History
Displays previously generated bills, including customer names, dates, discounts, GST, and grand totals.

🧠 OOP Concepts Used
Abstraction: Product is an abstract class.
Inheritance: Grocery and Electronics extend Product.
Method Overriding: Category-specific discount and GST methods are implemented in subclasses.
Encapsulation: Product attributes are protected and accessed through getter methods.
Polymorphism: Different product types are handled through the common Product reference.
🔒 Database Operations
The application uses JDBC and PreparedStatement for database operations.

It also uses database transactions during checkout to ensure that billing records and stock updates are committed together or rolled back if an error occurs.

🎯 Project Objectives
Automate retail billing operations.
Reduce manual billing errors.
Maintain accurate inventory records.
Calculate discounts and GST automatically.
Store customer and billing information.
Demonstrate Java OOP and JDBC concepts in a practical application.
👨‍💻 Developed By
Team Project – Smart Retail Billing Counter

Developed as an academic Java project using Java Swing, JDBC, and MySQL.

⭐ If you find this project useful, consider giving this repository a star!
