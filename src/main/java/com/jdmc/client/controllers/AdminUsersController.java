package com.jdmc.client.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.jdmc.client.interfaces.Callback;
import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminUsersController implements Callback {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObservableList<User> users;
    private SafeDeleteBtn safeDeleteController;
    private User user;

    public AdminUsersController(ObjectOutputStream out, ObjectInputStream in, SafeDeleteBtn safeDeleteController, User admin) {
        this.out = out;
        this.in = in;
        this.user = admin;
        this.safeDeleteController = safeDeleteController;
    }

    @FXML
    private Label exceptionLabel;

    @FXML
    private Button backBtn;

    @FXML
    private Button addUser;

    @FXML
    private Button deleteUser;

    @FXML
    private Button changeUser;

    @FXML
    private ListView<User> userList;

    @FXML
    void initialize() {
        this.users = FXCollections.observableArrayList();
        ModalDrawer drawer = new ModalDrawer();
        try {
            getUsers();
        } catch(IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }
        userList.setItems(this.users);

        backBtn.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminMenu.fxml"));
                SafeDeleteBtn safeDeleteController = new SafeDeleteBtn("Вы уверены, что хотите удалить запись?");
                AdminMenuController adminMenuController = new AdminMenuController(this.user, this.out, this.in, safeDeleteController);
                safeDeleteController.registerCallback(adminMenuController);
                loader.setController(adminMenuController);
                Parent tableViewParent = loader.load();
                Scene tableViewScene = new Scene(tableViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(tableViewScene);
                window.show();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        changeUser.setOnAction(event -> {

            User oldUser = userList.getSelectionModel().getSelectedItem();
            try{
                if(oldUser != null) {
                    FXMLLoader loader = null;
                    Stage stage = new Stage();
                    loader = new FXMLLoader(getClass().getResource("/fxml/EditUser.fxml"));
                    loader.setController(new EditUserController(this.out, this.in, this.users, oldUser));
                    stage.setTitle("Измение пользователя");
                    Parent page = loader.load();
                    stage.setScene(new Scene(page));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.show();
                } else drawer.getErrorModal("Пользователь не выбран", "Ошибка");
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        addUser.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddUser.fxml"));
                loader.setController(new AddUserController(this.out, this.in, this.users));
                Parent page = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Добавление пользователя");
                stage.setScene(new Scene(page));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.show();
            } catch(IOException err) {
                err.printStackTrace();
            }
        });

        deleteUser.setOnAction(event -> {
            User selectedUser = userList.getSelectionModel().getSelectedItem();
            try {
                if(selectedUser != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SafeDeleteModal.fxml"));
                    loader.setController(this.safeDeleteController);
                    Parent page = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Удаление пользователя");
                    stage.setScene(new Scene(page));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.show();
                } else drawer.getErrorModal("Пользователь не выбран", "Ошибка");
            } catch(IOException err) {
                err.printStackTrace();
            }
        });
    }


    void getUsers() throws IOException, ClassNotFoundException {
        out.writeObject(Actions.GET_USERS);
        if((ResponseTypes)in.readObject() == ResponseTypes.OK){
            Object response = in.readObject();
            if(response != null) {
                this.users.clear();
                ArrayList<User> users = (ArrayList<User>) response;
                this.users.addAll(users);
            }
        }
    }

    @Override
    public void callingBack() {
        ModalDrawer drawer = new ModalDrawer();
        try{
            User selectedUser = userList.getSelectionModel().getSelectedItem();
            out.writeObject(Actions.DELETE_USER);
            if((ResponseTypes)in.readObject() == ResponseTypes.OK) {
                out.writeObject(selectedUser);
                Object response = in.readObject();
                if(response != null) {
                    this.users.clear();
                    this.users.addAll((ArrayList<User>)response);
                    drawer.getSuccessModal("Пользователь успешно удалён", "Успех");
                } else drawer.getErrorModal("Ошибка сервера", "Ошибка");
            } else drawer.getErrorModal("Ошибка сервера", "Ошибка");

        } catch (IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }
    }
}