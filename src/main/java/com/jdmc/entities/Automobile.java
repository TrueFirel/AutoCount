package com.jdmc.entities;

import java.io.Serializable;

import com.jdmc.constants.Transports;

public class Automobile extends Transport implements Serializable  {
    private String mark;
    private String model;
    private String driveUnit;
    private String carcassType;
    private String engineType;
    private String transmissionType;

    public Automobile(String mark, int amount) {
        this.mark = mark;
        this.amount = amount;
    }

    @Override
    Transports getTransportType() {
        return Transports.AUTOMOBILE;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setModel(String model) {
        this.model = model;
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

}
