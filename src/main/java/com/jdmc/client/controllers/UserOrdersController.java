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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class UserOrdersController implements Callback {

    private ObservableList<Order> orders;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private SafeDeleteBtn safeDeleteController;
    private User user;

    @FXML
    private Button deleteOrderBtn;

    @FXML
    private Label exceptionLabel;

    @FXML
    private Button exitBtn;

    public void setSafeDeleteController(SafeDeleteBtn safeDeleteController) {
        this.safeDeleteController = safeDeleteController;
    }

    @FXML
    private TableView<Order> aotoTable;

    @FXML
    private TableColumn<Order, String> markColumn;

    @FXML
    private TableColumn<Order, String> modelColumn;

    @FXML
    private TableColumn<Order, String> priceColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    public UserOrdersController(User user, ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
        this.user = user;
        this.orders = null;
    }

    @FXML
    void initialize() {

        this.orders = FXCollections.observableArrayList();
        markColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("Mark"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("Model"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("Cost"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("Status"));

        try{
            getUserOrders(this.user.getLogin());
            aotoTable.setItems(this.orders);
        } catch (IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }

        deleteOrderBtn.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SafeDeleteModal.fxml"));
                loader.setController(safeDeleteController);
                Parent page = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Удаление заказа");
                stage.setScene(new Scene(page));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.show();
            } catch(IOException err) {
                err.printStackTrace();
            }
        });

        exitBtn.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserMenu.fxml"));
                loader.setController(new UserMenuController(this.user, this.out, this.in));
                Scene userMenuScene = new Scene(loader.load());
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(userMenuScene);
                window.show();
            } catch(IOException err) {
                err.printStackTrace();
            }
        });
    }

    public void getUserOrders(String login) throws IOException, ClassNotFoundException{
        out.writeObject(Actions.GET_USER_ORDERS);
        if((ResponseTypes)in.readObject() == ResponseTypes.OK) {
            out.writeObject(login);
            Object response = in.readObject();
            if(response == null) return;
            this.orders.clear();
            ArrayList<Order> orders = (ArrayList<Order>)response;
            this.orders.addAll(orders);
        }
    }

    @Override
    public void callingBack() {
        try{
            if(aotoTable.getSelectionModel().getSelectedItem() != null) {
                Order order = aotoTable.getSelectionModel().getSelectedItem();
                int orderId = order.getOrderId();
                out.writeObject(Actions.REMOVE_ORDER);
                ModalDrawer drawer = new ModalDrawer();
                if((ResponseTypes)in.readObject() == ResponseTypes.OK) {
                    out.writeObject(orderId);
                    Object response = in.readObject();
                    if(response == null) return;
                    this.orders.clear();
                    this.orders.addAll((ArrayList<Order>)response) ;
                    drawer.getSuccessModal("Ваш заказ успешно удалён", "Успех");
                } else drawer.getErrorModal("Произошла ошибка при удалении заказа", "Ошибка");
            }
        } catch(IOException err) {
            err.printStackTrace();
        } catch(ClassNotFoundException err) {
            err.printStackTrace();
        }
    }
}
