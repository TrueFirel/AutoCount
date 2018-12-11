package com.jdmc.entities;

import java.io.Serializable;

public class User implements Serializable {
    private String login;
    private String password;
    private String role;
    private MoreUserInfo moreUserInfo;

    public MoreUserInfo getMoreUserInfo() {
        return moreUserInfo;
    }

    public void setMoreUserInfo(MoreUserInfo moreUserInfo) {
        this.moreUserInfo = moreUserInfo;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return this.login;
    }
}
