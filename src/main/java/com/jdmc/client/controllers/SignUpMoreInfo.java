package com.jdmc.client.controllers;

import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.MoreUserInfo;
import com.jdmc.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SignUpMoreInfo {

    @FXML
    private TextField nameField;

    @FXML
    private Button loginSignUpButton;

    @FXML
    private Label exceptionLabel;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField countryField;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User user;

    public SignUpMoreInfo(ObjectOutputStream out, ObjectInputStream in, User user) {
        this.out = out;
        this.in = in;
        this.user = user;
    }

    @FXML
    void initialize() {

        loginSignUpButton.setOnAction(event -> {
            MoreUserInfo moreUserInfo = new MoreUserInfo(nameField.getText(), surnameField.getText(),
                    lastNameField.getText(), countryField.getText());
            try {
                out.writeObject(Actions.registerMoreInfo);
                    if((ResponseTypes)this.in.readObject() == ResponseTypes.OK) {
                        this.user.setMoreUserInfo(moreUserInfo);
                        out.writeObject(this.user);
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/fxml/UserMenu.fxml"));
                        loader.setController(new UserMenuController(this.user, this.out, this.in));
                        Parent tableViewParent = loader.load();
                        Scene tableViewScene = new Scene(tableViewParent);
                        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                        window.setScene(tableViewScene);
                        window.show();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
