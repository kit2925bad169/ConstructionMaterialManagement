package com.construction.dao;

import com.construction.config.DBConnection;
import com.construction.model.Purchase;

import java.sql.*;

public class PurchaseDAO {

    public void addPurchase(Purchase purchase) throws SQLException {
        String insert = "INSERT INTO purchases "
                + "(material_id, supplier_id, quantity, unit_price, total_amount, purchase_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String updateStock = "UPDATE materials "
                + "SET quantity = quantity + ? WHERE material_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(insert);
                 PreparedStatement stockPs = con.prepareStatement(updateStock)) {

                ps.setInt(1, purchase.getMaterialId());
                ps.setInt(2, purchase.getSupplierId());
                ps.setDouble(3, purchase.getQuantity());
                ps.setDouble(4, purchase.getUnitPrice());
                ps.setDouble(5, purchase.getTotalAmount());
                ps.setDate(6, Date.valueOf(purchase.getPurchaseDate()));
                ps.executeUpdate();

                stockPs.setDouble(1, purchase.getQuantity());
                stockPs.setInt(2, purchase.getMaterialId());

                if (stockPs.executeUpdate() == 0) {
                    throw new SQLException("Material not found.");
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
