package com.construction.model;

public class Material {
    private int materialId;
    private String materialName;
    private String category;
    private String unit;
    private double quantity;
    private double minimumStock;

    public Material() {
    }

    public Material(String materialName, String category, String unit,
                     double quantity, double minimumStock) {
        this.materialName = materialName;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getMinimumStock() { return minimumStock; }
    public void setMinimumStock(double minimumStock) { this.minimumStock = minimumStock; }
}
