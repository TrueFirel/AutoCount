package com.jdmc.entities;

import java.io.Serializable;

public class MoreUserInfo implements Serializable {
    private String name;
    private String surname;
    private String lastName;
    private String country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public MoreUserInfo(String name, String surname, String lastName, String country) {
        this.name = name;
        this.surname = surname;
        this.lastName = lastName;
        this.country = country;
    }
}
