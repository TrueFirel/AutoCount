package com.jdmc.client.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.Automobile;
import com.jdmc.entities.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserController {

    ObjectOutputStream out;
    ObjectInputStream in;
    ObservableList<User> users;

    public AddUserController(ObjectOutputStream out, ObjectInputStream in, ObservableList<User> users) {
        this.out = out;
        this.in = in;
        this.users = users;
    }

    @FXML
    private TextField loginField;

    @FXML
    private Button addUserBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;


    @FXML
    void initialize() {

        ModalDrawer drawer = new ModalDrawer();

        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });

        addUserBtn.setOnAction(event -> {
            try{
                String login = loginField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if(login.length() == 0 || password.length() == 0 || confirmPassword.length() == 0) drawer.getErrorModal("Поля не могут быть пустыми!", "Ошибка");
                else if(!password.equals(confirmPassword)) drawer.getErrorModal("Пароли должны совпадать!", "Ошибка");
                else {

                    out.writeObject(Actions.AddUser);
                    if((ResponseTypes)in.readObject() == ResponseTypes.OK) {

                        out.writeObject(new User(login, password));
                        Object response = in.readObject();
                        if(response != null) {

                            ArrayList<User> users = (ArrayList<User>) response;
                            this.users.clear();
                            this.users.addAll(users);
                            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            stage.close();
                            drawer.getSuccessModal("Новый пользователь успешно добавлен!", "Успех");

                        } else drawer.getErrorModal("Таой пользователь уже существует!", "Ошибка");
                    } else drawer.getErrorModal("Ошибка сервера!", "Ошибка");
                }
            } catch (IOException err) {
                err.printStackTrace();
            } catch (ClassNotFoundException err) {
                err.printStackTrace();
            }

        });
    }
}