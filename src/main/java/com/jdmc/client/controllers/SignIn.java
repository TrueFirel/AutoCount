package com.jdmc.client.controllers;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
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
import com.jdmc.entities.User;

public class SignIn {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginSignInButton;

    @FXML
    private Button loginSignUpButton;

    @FXML
    private Label exceptionLabel;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SignIn(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public User sendRequest(String login, String password) throws IOException, ClassNotFoundException {
        User user = new User(login, password);
        out.writeObject(Actions.Authorizate);
        if((ResponseTypes)in.readObject() == ResponseTypes.OK) {
            out.writeObject(user);
            Object account = in.readObject();
            if(account != null) return (User) account;
        }
        return null;
    }

    @FXML
    void initialize() {

        loginSignUpButton.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/SignUp.fxml"));
                loader.setController(new SignUp(this.out, this.in));
                Parent signUpViewParent = loader.load();
                Scene signUpViewScene = new Scene(signUpViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(signUpViewScene);
                window.show();
            } catch(IOException err) {
                err.printStackTrace();
            }
        });
        loginSignInButton.setOnAction(event -> {
            if(loginField.getText().length() == 0 || passwordField.getText().length() == 0)
                exceptionLabel.setText("Поля не могут быть пустыми!");
            else {
                User account = null;
                try{
                    account = sendRequest(loginField.getText(), passwordField.getText());
                } catch(IOException err) {
                    err.printStackTrace();
                } catch (ClassNotFoundException err){
                    err.printStackTrace();
                }
                if(account == null) {
                    exceptionLabel.setText("Не верный логин или пароль");
                }
                else {
                    try{
                        FXMLLoader loader = new FXMLLoader();
                        if(account.getRole().equals("u")){
                            loader.setLocation(getClass().getResource("/fxml/UserMenu.fxml"));
                            loader.setController(new UserMenuController(account, this.out, this.in));
                        } else if(account.getRole().equals("a")) {
                            loader.setLocation(getClass().getResource("/fxml/AdminMenu.fxml"));
                            SafeDeleteBtn safeDeleteController = new SafeDeleteBtn("Вы уверены, что хотите удалить запись?");
                            AdminMenuController adminMenuController = new AdminMenuController(account, this.out, this.in, safeDeleteController);
                            safeDeleteController.registerCallback(adminMenuController);
                            loader.setController(adminMenuController);
                        }
                        Parent tableViewParent = loader.load();
                        Scene tableViewScene = new Scene(tableViewParent);
                        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                        window.setScene(tableViewScene);
                        window.show();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }
            }
        });
    }
}

