package com.jdmc.client.controllers;

import java.io.*;

import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
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

public class SignUp {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordVerificationField;

    @FXML
    private Button loginSignInButton;

    @FXML
    private Button loginSignUpButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label exceptionLabel;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public User sendRequest (User user) throws IOException, ClassNotFoundException {
        this.out.writeObject(Actions.Register);
        if((ResponseTypes)this.in.readObject() == ResponseTypes.OK) {
            this.out.writeObject(user);
            Object res_user = in.readObject();
            if(res_user != null) return (User)res_user;
        }
        return null;
    }

    public SignUp(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    @FXML
    void initialize() {
        loginSignInButton.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/SignIn.fxml"));
                loader.setController(new SignIn(this.out, this.in));
                Parent tableViewParent = loader.load();
                Scene tableViewScene = new Scene(tableViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(tableViewScene);
                window.show();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        loginSignUpButton.setOnAction(event -> {
            if(loginField.getText().length() == 0 || passwordField.getText().length() == 0
                    || passwordVerificationField.getText().length() == 0)
                exceptionLabel.setText("Поля не могут быть пустыми!");
            else if (!passwordVerificationField.getText().equals(passwordField.getText()))
                exceptionLabel.setText("Пароли должны совпадать!");
            else {
                User user = new User(loginField.getText(), passwordField.getText());
                try{
                    user = sendRequest(user);
                    if(user == null) exceptionLabel.setText("Такой пользователь уже зарегистрирован");
                    else {
                        try{
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("/fxml/SignUpMoreData.fxml"));
                            loader.setController(new SignUpMoreInfo(this.out, this.in, user));
                            Parent tableViewParent = loader.load();
                            Scene tableViewScene = new Scene(tableViewParent);
                            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                            window.setScene(tableViewScene);
                            window.show();
                        } catch (IOException err) {
                            err.printStackTrace();
                        }
                    }
                } catch(IOException err){
                    err.printStackTrace();
                } catch (ClassNotFoundException err) {
                    err.printStackTrace();
                }
            }
        });
    }
}
