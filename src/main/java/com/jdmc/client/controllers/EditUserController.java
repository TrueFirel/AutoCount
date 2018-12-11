package com.jdmc.client.controllers;

import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class EditUserController {

    ObjectOutputStream out;
    ObjectInputStream in;
    ObservableList<User> users;
    User oldUser;

    public EditUserController(ObjectOutputStream out, ObjectInputStream in, ObservableList<User> users, User oldUser) {
        this.out = out;
        this.in = in;
        this.users = users;
        this.oldUser = oldUser;
    }

    @FXML
    private TextField loginField;

    @FXML
    private Button editUserBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextField passwordField;

    @FXML
    void initialize() {

        ModalDrawer drawer = new ModalDrawer();
        this.loginField.setText(this.oldUser.getLogin());
        this.passwordField.setText(this.oldUser.getPassword());

        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });

        editUserBtn.setOnAction(event -> {
            String login, password;
            try {
                if(this.loginField.getText().length() == 0 && this.loginField.getText().length() == 0)
                    drawer.getErrorModal("Поля не могут быть пустыми!", "Ошибка");
                else if(oldUser.getLogin().equals(loginField.getText()) && oldUser.getPassword().equals(passwordField.getText()))
                    drawer.getErrorModal("Данные не были изменены!", "Ошибка");
                else {
                    if(this.loginField.getText().length() != 0) login = this.loginField.getText();
                    else login = this.oldUser.getLogin();
                    if(this.passwordField.getText().length() != 0) password = this.passwordField.getText();
                    else password = this.oldUser.getPassword();

                    User newUser = new User(login, password);
                    out.writeObject(Actions.EditUser);
                    if((ResponseTypes)in.readObject() == ResponseTypes.OK){
                        out.writeObject(oldUser);
                        out.writeObject(newUser);
                        Object response = in.readObject();
                        if(response != null) {
                            ArrayList<User> users = (ArrayList<User>) response;
                            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            stage.close();
                            drawer.getSuccessModal("Пользователь успешно иззменён", "Успех");
                            this.users.clear();
                            this.users.addAll(users);
                        } else drawer.getErrorModal("Пользователь с таким именем уже существует!", "Ошибка");
                    }
                }
            } catch (IOException err) {
                err.printStackTrace();
            } catch (ClassNotFoundException err) {
                err.printStackTrace();
            }
        });
    }
}