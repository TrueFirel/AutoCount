package com.jdmc.entities;

import java.io.Serializable;

public class Order implements Serializable {

    int orderId;
    int userId;
    int carId;
    String mark;
    String model;
    double cost;
    String login;
    Automobile auto;
    String status;

    public void setCarId(int carId) {
        this.carId = carId;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMark(){
        return this.mark;
    }

    public String getModel(){
        return this.model;
    }

    public String getCost(){
        return String.valueOf(this.cost);
    }

    public int getCarId() {
        return carId;
    }

    public Order(int orderId, int userId, int carId) {
        this.orderId = orderId;
        this.userId = userId;
        this.carId = carId;
    }

    public Order(int orderId, int userId, int carId, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.carId = carId;
        this.status = status;
    }

    public String getStatus() {
        if(status.equals("declined")) return "Отклонён";
        if(status.equals("accepted")) return "Принят";
        if(status.equals("in_process")) return "На рассмотрении";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Order(int orderId, int userId, int carId, Automobile auto) {
        this.orderId = orderId;
        this.userId = userId;
        this.auto = auto;
        this.carId = carId;
        this.mark = auto.getMark();
        this.model = auto.getModel();
        this.cost = auto.getCost();
    }

    public Order(int orderId, int userId, int carId, String login, Automobile auto) {
        this.orderId = orderId;
        this.userId = userId;
        this.auto = auto;
        this.carId = carId;
        this.login = login;
    }

    public void setCarData(String mark, String model, double price){
        this.mark = mark;
        this.model = model;
        this.cost = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public Automobile getAuto() {
        return auto;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAuto(Automobile auto) {
        this.auto = auto;
    }
}
