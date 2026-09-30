package com.construction.dao;

import com.construction.config.DBConnection;
import com.construction.model.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    public void addMaterial(Material material) throws SQLException {
        String sql = "INSERT INTO materials "
                + "(material_name, category, unit, quantity, minimum_stock) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, material.getMaterialName());
            ps.setString(2, material.getCategory());
            ps.setString(3, material.getUnit());
            ps.setDouble(4, material.getQuantity());
            ps.setDouble(5, material.getMinimumStock());
            ps.executeUpdate();
        }
    }

    public List<Material> getAllMaterials() throws SQLException {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM materials ORDER BY material_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialName(rs.getString("material_name"));
                m.setCategory(rs.getString("category"));
                m.setUnit(rs.getString("unit"));
                m.setQuantity(rs.getDouble("quantity"));
                m.setMinimumStock(rs.getDouble("minimum_stock"));
                list.add(m);
            }
        }
        return list;
    }
}
