package com.construction.dao;

import com.construction.config.DBConnection;
import com.construction.model.MaterialUsage;

import java.sql.*;

public class UsageDAO {

    public void addUsage(MaterialUsage usage) throws SQLException {
        String checkStock = "SELECT quantity FROM materials WHERE material_id = ? FOR UPDATE";

        String insert = "INSERT INTO material_usage "
                + "(material_id, quantity_used, project_name, usage_date) "
                + "VALUES (?, ?, ?, ?)";

        String updateStock = "UPDATE materials "
                + "SET quantity = quantity - ? WHERE material_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(checkStock);
                 PreparedStatement ps = con.prepareStatement(insert);
                 PreparedStatement stockPs = con.prepareStatement(updateStock)) {

                check.setInt(1, usage.getMaterialId());

                double availableStock;

                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Material not found.");
                    }
                    availableStock = rs.getDouble("quantity");
                }

                if (usage.getQuantityUsed() <= 0) {
                    throw new SQLException("Usage quantity must be greater than zero.");
                }

                if (usage.getQuantityUsed() > availableStock) {
                    throw new SQLException(
                            "Insufficient stock. Available: " + availableStock);
                }

                ps.setInt(1, usage.getMaterialId());
                ps.setDouble(2, usage.getQuantityUsed());
                ps.setString(3, usage.getProjectName());
                ps.setDate(4, Date.valueOf(usage.getUsageDate()));
                ps.executeUpdate();

                stockPs.setDouble(1, usage.getQuantityUsed());
                stockPs.setInt(2, usage.getMaterialId());
                stockPs.executeUpdate();

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
