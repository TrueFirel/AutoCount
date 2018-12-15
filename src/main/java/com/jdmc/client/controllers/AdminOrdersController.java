package com.jdmc.client.controllers;

import com.jdmc.client.interfaces.Callback;
import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.Order;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AdminOrdersController implements Callback {

    private User user;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private SafeDeleteBtn safeDeleteController;
    private ObservableList<Order> orders;

    public AdminOrdersController(User user, ObjectOutputStream out, ObjectInputStream in, SafeDeleteBtn safeDeleteController) {
        this.user = user;
        this.out = out;
        this.in = in;
        this.safeDeleteController = safeDeleteController;
    }

    @FXML
    private Label exceptionLabel;

    @FXML
    private Button backBtn;

    @FXML
    private TableView<Order> aotoTable;

    @FXML
    private TableColumn<Order, String> userColumn;

    @FXML
    private TableColumn<Order, String> markColumn;

    @FXML
    private TableColumn<Order, String> modelColumn;

    @FXML
    private TableColumn<Order, String> priceColumn;

    @FXML
    private Button declineOrder;

    @FXML
    private Button applyOrder;

    @FXML
    void initialize() {
        this.orders = FXCollections.observableArrayList();
        userColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("Login"));
        markColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("mark"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("model"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("cost"));

        try {
            getOrdersData();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }

        this.aotoTable.setItems(this.orders);

        applyOrder.setOnAction(event -> {
            responseToOrder(true);
        });

        declineOrder.setOnAction(event -> {
            responseToOrder(false);
        });

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
    }

    public void getOrdersData() throws IOException, ClassNotFoundException {
        out.writeObject(Actions.GET_ORDERS);
        if((ResponseTypes)in.readObject() == ResponseTypes.OK) {
            Object response = in.readObject();
            if(response != null) {
                ArrayList<Order> orders = (ArrayList<Order>) response;
                this.orders.clear();
                this.orders.addAll(orders);
            }
        }
    }

    public void responseToOrder(boolean isAccepted) {
        try {
            ModalDrawer drawer = new ModalDrawer();
            Order selectedOrder = this.aotoTable.getSelectionModel().getSelectedItem();
            if(selectedOrder != null) {
                if(isAccepted == true) out.writeObject(Actions.APPLY_ORDER);
                else out.writeObject(Actions.DECLINE_ORDER);
                if((ResponseTypes)in.readObject() != null) {
                    out.writeObject(selectedOrder.getOrderId());
                    Object response = in.readObject();
                    if(response != null) {
                        ArrayList<Order> orders = (ArrayList<Order>)response;
                        this.orders.clear();
                        this.orders.addAll(orders);
                    } else drawer.getErrorModal("Ошибка сервера", "Ошибка");
                } else drawer.getErrorModal("Ошибка сервера", "Ошибка");
            } else drawer.getErrorModal("Заказ не выбран", "Ошибка");
        } catch (IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }
    }

    @Override
    public void callingBack() {

    }
}
