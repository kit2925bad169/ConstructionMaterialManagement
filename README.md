# Construction Material Management System

College project using:
- Java OOP
- JDBC
- MySQL
- HTML/CSS/JavaScript
- Maven
- Java's built-in HTTP server

## Requirements
- JDK 17+
- MySQL Server
- VS Code
- Extension Pack for Java
- Maven (or Maven wrapper if you add one)

## Before running
1. Create the database by running `database.sql` in MySQL.
2. Open `src/main/java/com/construction/config/DBConnection.java`.
3. Change DB_USER and DB_PASSWORD to your MySQL username/password.
4. Open a terminal in the project folder.
5. Run:
   mvn clean compile
   mvn exec:java
6. Open:
   http://localhost:8080

## Main features
- Material registration
- Supplier registration
- Purchase records
- Material usage records
- Automatic stock increase after purchase
- Automatic stock decrease after usage
- Low-stock display
- Dashboard counts
