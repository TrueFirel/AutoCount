package com.jdmc.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringPropertyBase;

import java.io.Serializable;
import java.time.LocalDate;

public class Automobile implements Serializable {
    private String mark;
    private String model;
    private double cost;
    private int productionYear;
    private String driveUnit;
    private String carcassType;
    private String engineType;
    private String transmissionType;
    private int amount;

    public Automobile(String mark, int amount) {
        this.mark = mark;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public void setDriveUnit(String driveUnit) {
        this.driveUnit = driveUnit;
    }

    public void setCarcassType(String carcassType) {
        this.carcassType = carcassType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public Automobile(String mark, String model, int productionYear, double cost, String driveUnit, String carcassType, String engineType, String transmissionType, int amount) {
        this.mark = mark;
        this.model = model;
        this.cost = cost;
        this.productionYear = productionYear;
        this.driveUnit = driveUnit;
        this.carcassType = carcassType;
        this.engineType = engineType;
        this.transmissionType = transmissionType;
        this.amount = amount;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public String getDriveUnit() {
        return driveUnit;
    }

    public String getCarcassType() {
        return carcassType;
    }

    public String getEngineType() {
        return engineType;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public Automobile(String mark, String model, int productionDate, double cost) {
        this.mark = mark;
        this.model = model;
        this.productionYear = productionDate;
        this.cost = cost;
    }


    public String getMark() {
        return mark;
    }

    public String getModel() {
        return model;
    }

    public int getProductionDate() {
        return productionYear;
    }

    public double getCost() {
        return cost;
    }
}
