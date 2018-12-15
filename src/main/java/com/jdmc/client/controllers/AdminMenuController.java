package com.jdmc.client.controllers;

import com.jdmc.client.interfaces.Callback;
import com.jdmc.client.windowDrawers.AdminWindowsDrawer;
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

public class AdminMenuController implements Callback {

    private User user;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObservableList<Automobile> automobiles;
    private SafeDeleteBtn safeDeleteController;
    private ArrayList<Automobile> automobilesList;

    public AdminMenuController(User user, ObjectOutputStream out, ObjectInputStream in, SafeDeleteBtn safeDeleteController) {
        this.user = user;
        this.out = out;
        this.in = in;
        this.safeDeleteController = safeDeleteController;
    }

    @FXML
    private Button usersBtn;

    @FXML
    private Button ordersBtn;

    @FXML
    private Button addEntryBtn;

    @FXML
    private Button deleteEntryBtn;

    @FXML
    private Button changeEntryBtn;

    @FXML
    private Button saveChangesBtn;

    @FXML
    private Label exceptionLabel;

    @FXML
    private Button exitBtn;

    @FXML
    private Button statBtn;

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
    private TableColumn<Automobile, String> amountColumn;

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
        automobiles = FXCollections.observableArrayList();
        markColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Mark"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Model"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("ProductionDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Cost"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<Automobile, String>("Amount"));

        try{
            this.automobiles.clear();
            this.automobiles.addAll(getAutomobiles());
            aotoTable.setItems(this.automobiles);
        } catch (IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }

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
                                        String.valueOf(auto.getCost()).contains(searchValue) ||
                                        String.valueOf(auto.getAmount()).contains(searchValue)) filtratedAutos.add(auto);
                            }
                        } else {
                            ArrayList<Automobile> temp = new ArrayList<>();
                            for (Automobile auto : filtratedAutos)
                                if(auto.getMark().contains(searchValue) || auto.getModel().contains(searchValue) ||
                                        String.valueOf(auto.getProductionYear()).contains(searchValue) ||
                                        String.valueOf(auto.getCost()).contains(searchValue) ||
                                        String.valueOf(auto.getAmount()).contains(searchValue)) temp.add(auto);
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

        usersBtn.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminUsers.fxml"));
                SafeDeleteBtn deleteController = new SafeDeleteBtn("Вы уверены, что хотите удалить пользователя?");
                AdminUsersController adminUsersController = new AdminUsersController(this.out, this.in, deleteController, this.user);
                deleteController.registerCallback(adminUsersController);
                loader.setController(adminUsersController);
                Parent tableViewParent = loader.load();
                Scene tableViewScene = new Scene(tableViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(tableViewScene);
                window.show();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        addEntryBtn.setOnAction(event -> {
            try{
                AdminWindowsDrawer adminMenuController = new AdminWindowsDrawer(this.in, this.out, automobiles);
                adminMenuController.getAddingWindow();
            } catch(IOException err) {
                err.printStackTrace();
            }
        });

        changeEntryBtn.setOnAction(event -> {
            try{
                ModalDrawer drawer = new ModalDrawer();
                AdminWindowsDrawer adminMenuController = new AdminWindowsDrawer(this.in, this.out, automobiles);
                if(this.aotoTable.getSelectionModel().getSelectedItem() != null)
                adminMenuController.getEditingWindow(this.aotoTable.getSelectionModel().getSelectedItem());
                else drawer.getErrorModal("Запись для редактирования не выбрана!", "Ошибка");
            } catch(IOException err) {
                err.printStackTrace();
            }
        });

        deleteEntryBtn.setOnAction(event -> {
            try {
                ModalDrawer drawer = new ModalDrawer();
                if(aotoTable.getSelectionModel().getSelectedItem() == null) drawer.getErrorModal("Запись для удаления не выбрана!", "Ошибка");
                else{
                    FXMLLoader loader = null;
                    Stage stage = new Stage();
                    loader = new FXMLLoader(getClass().getResource("/fxml/SafeDeleteModal.fxml"));
                    loader.setController(this.safeDeleteController);
                    stage.setTitle("Подтвердите действие");
                    Parent page = loader.load();
                    stage.setScene(new Scene(page));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.show();
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        ordersBtn.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/AdminOrders.fxml"));
                SafeDeleteBtn safeDeleteController = new SafeDeleteBtn("Вы действительно хотите отклонить заказ?");
                AdminOrdersController ordersController = new AdminOrdersController(this.user, this.out, this.in, safeDeleteController);
                safeDeleteController.registerCallback(ordersController);
                loader.setController(ordersController);
                Parent tableViewParent = loader.load();
                Scene tableViewScene = new Scene(tableViewParent);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(tableViewScene);
                window.show();
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        statBtn.setOnAction(event -> {
            try {
                AdminWindowsDrawer adminMenuController = new AdminWindowsDrawer(this.in, this.out, automobiles);
                adminMenuController.getChartWindow(getAutomobiles());
            } catch (IOException err) {
                err.printStackTrace();
            } catch (ClassNotFoundException err) {
                err.printStackTrace();
            }
        });

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
    }

    private ArrayList<Automobile> getAutomobiles() throws IOException, ClassNotFoundException{
        out.writeObject(Actions.PRIVILEGED_GET_AUTOMOBILES);
        ArrayList<Automobile> automobiles = null;
        if((ResponseTypes)in.readObject() == ResponseTypes.OK)
            automobiles = (ArrayList<Automobile>) in.readObject();
        return automobiles;
    }

    @Override
    public void callingBack() {
        ModalDrawer drawer = new ModalDrawer();
        try{
            out.writeObject(Actions.DELETE_CAR);
            if((ResponseTypes)in.readObject() == ResponseTypes.OK) {

                 out.writeObject(aotoTable.getSelectionModel().getSelectedItem());
                 Object response = in.readObject();
                 if(response != null) {

                     ArrayList<Automobile> automobiles = (ArrayList<Automobile>)response;
                     this.automobiles.clear();
                     this.automobiles.addAll(automobiles);
                     drawer.getSuccessModal("Запись успешно удалена", "Успех");

                 } else drawer.getErrorModal("Возникла ошибка при удалении записи", "Ошибка");
            } else drawer.getErrorModal("Возникла ошибка при удалении записи", "Ошибка");
        } catch(IOException err) {
            err.printStackTrace();
        } catch(ClassNotFoundException err) {
            err.printStackTrace();
        }
    }
}
