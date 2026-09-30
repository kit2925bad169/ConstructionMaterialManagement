package com.construction.model;

public class MaterialUsage {
    private int usageId;
    private int materialId;
    private double quantityUsed;
    private String projectName;
    private String usageDate;

    public MaterialUsage() {
    }

    public MaterialUsage(int materialId, double quantityUsed,
                         String projectName, String usageDate) {
        this.materialId = materialId;
        this.quantityUsed = quantityUsed;
        this.projectName = projectName;
        this.usageDate = usageDate;
    }

    public int getUsageId() { return usageId; }
    public void setUsageId(int usageId) { this.usageId = usageId; }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public double getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(double quantityUsed) { this.quantityUsed = quantityUsed; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getUsageDate() { return usageDate; }
    public void setUsageDate(String usageDate) { this.usageDate = usageDate; }
}
