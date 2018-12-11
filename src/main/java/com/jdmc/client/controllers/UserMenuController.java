package com.jdmc.client.controllers;

import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.Automobile;
import com.jdmc.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class UserMenuController {

    private User user;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObservableList<Automobile> automobiles;

    public UserMenuController(User user, ObjectOutputStream out, ObjectInputStream in) {
        this.user = user;
        this.out = out;
        this.in = in;
    }

    @FXML
    private Button makeOrderBtn;

    @FXML
    private Label exceptionLabel;

    @FXML
    private Button exitBtn;

    @FXML
    private Button ordersBtn;

    @FXML
    private TableView<Automobile> aotoTable;

    @FXML
    private TableColumn<Automobile, String> markColumn;

    @FXML
    private TableColumn<Automobile,String> modelColumn;

    @FXML
    private TableColumn<Automobile, String> dateColumn;

    @FXML
    private TableColumn<Automobile, String> priceColumn;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField fromField;

    @FXML
    private TextField toField;

    @FXML
    private TextField searchField;

    @FXML
    void initialize(){
        this.automobiles = FXCollections.observableArrayList();
        markColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Mark"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Model"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("ProductionDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Cost"));

        aotoTable.setRowFactory( tv -> {
            TableRow<Automobile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try {

                        Automobile automobile = row.getItem();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShowMoreEntryInfo.fxml"));
                        loader.setController(new ShowMoreEntryInfoController(automobile));
                        Parent page = loader.load();
                        Stage stage = new Stage();
                        stage.setTitle("Дополнительная информация");
                        stage.setScene(new Scene(page));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setResizable(false);
                        stage.show();

                    } catch(IOException err) {
                        err.printStackTrace();
                    }
                }
            });
            return row ;
        });

        try{
            this.automobiles.addAll(getAutomobiles());
            aotoTable.setItems(this.automobiles);
        } catch (IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }
        exitBtn.setOnAction(event -> {
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

        searchBtn.setOnAction(event -> {
            try{
                ArrayList<Automobile> autos = getAutomobiles();
                ArrayList<Automobile> filtratedAutos = new ArrayList<>();
                this.automobiles.clear();
                if(fromField.getText().length() == 0 && toField.getText().length() == 0 && searchField.getText().length() == 0){
                    this.automobiles.addAll(autos);
                } else {
                    if (fromField.getText().length() != 0) {
                        double fromValue = Double.valueOf(fromField.getText());
                        for(Automobile auto : autos) if(auto.getCost() >= fromValue) filtratedAutos.add(auto);
                    }
                    if (toField.getText().length() != 0) {
                        double toValue = Double.valueOf(toField.getText());
                        if(filtratedAutos.size() == 0){
                            for(Automobile auto : autos)
                                if(auto.getCost() <= toValue) filtratedAutos.add(auto);
                        } else {
                            ArrayList<Automobile> temp = new ArrayList<>();
                            for(Automobile auto : filtratedAutos)
                                if(auto.getCost() <= toValue) temp.add(auto);
                                filtratedAutos = temp;
                        }
                    }
                    if (searchField.getText().length() != 0) {
                        String searchValue = searchField.getText();
                        if(filtratedAutos.size() == 0){
                            for(Automobile auto : autos) {
                                if(auto.getMark().contains(searchValue) || auto.getModel().contains(searchValue) ||
                                    String.valueOf(auto.getProductionYear()).contains(searchValue) ||
                                    String.valueOf(auto.getCost()).contains(searchValue)) filtratedAutos.add(auto);
                            }
                        } else {
                            ArrayList<Automobile> temp = new ArrayList<>();
                            for (Automobile auto : filtratedAutos)
                                if(auto.getMark().contains(searchValue) || auto.getModel().contains(searchValue) ||
                                        String.valueOf(auto.getProductionYear()).contains(searchValue) ||
                                        String.valueOf(auto.getCost()).contains(searchValue)) temp.add(auto);
                                filtratedAutos = temp;
                        }

                    }
                    this.automobiles.addAll(filtratedAutos);
                }
            } catch (IOException err) {
                err.printStackTrace();
            } catch (ClassNotFoundException err) {
                err.printStackTrace();
            }
        });

        ordersBtn.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/UserOrders.fxml"));
                UserOrdersController controller = new UserOrdersController(this.user, this.out, this.in);
                SafeDeleteBtn safeDeleteBtn = new SafeDeleteBtn("Вы уверены, что хотите отменить заказ?");
                safeDeleteBtn.registerCallback(controller);
                controller.setSafeDeleteController(safeDeleteBtn);
                loader.setController(controller);
                Parent tableViewParent = loader.load();
                Scene tableViewScene = new Scene(tableViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(tableViewScene);
                window.show();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        makeOrderBtn.setOnAction(event -> {
            if(aotoTable.getSelectionModel().getSelectedItem() != null) {
                try{
                    Automobile auto = aotoTable.getSelectionModel().getSelectedItem();
                    out.writeObject(Actions.MakeBuyOrder);
                    if((ResponseTypes)in.readObject() != ResponseTypes.OK) return;
                    else {
                        out.writeObject(auto);
                        out.writeObject(user);
                        Object entity = in.readObject();
                        ModalDrawer modal = new ModalDrawer();
                        if(entity != null) {
                            this.automobiles.clear();
                            ArrayList<Automobile> responseAutomobiles = (ArrayList<Automobile>)entity;
                            for(int i = 0; i< responseAutomobiles.size(); i++){
                                this.automobiles.add(responseAutomobiles.get(i));
                            }
                            modal.getSuccessModal("Ваш запрос успешно принят", "Успех");
                        } else {
                            modal.getErrorModal("Возникла ошибка при отправке запроса", "Ошибка");
                        }
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                } catch (ClassNotFoundException err) {
                    err.printStackTrace();
                }
            }
        });
    }

    private ArrayList<Automobile> getAutomobiles() throws IOException, ClassNotFoundException{
        out.writeObject(Actions.GetAutomobiles);
        ArrayList<Automobile> automobileList = null;
        if((ResponseTypes)in.readObject() == ResponseTypes.OK) automobileList = (ArrayList<Automobile>)in.readObject();
        return  automobileList;
    }

}
