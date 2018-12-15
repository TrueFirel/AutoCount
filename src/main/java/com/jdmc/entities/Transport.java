package com.jdmc.entities;

import java.io.Serializable;
import com.jdmc.constants.Transports;

public abstract class Transport implements Serializable {
    protected int amount;
    protected double cost;
    protected int productionYear;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    abstract Transports getTransportType();
}
