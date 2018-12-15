package com.jdmc.client.controllers;

import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.Automobile;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class EditEntryController {

    ObjectOutputStream out;
    ObjectInputStream in;
    private ObservableList<Automobile> automobiles;
    private Automobile pickedAuto;

    public EditEntryController(ObjectOutputStream out, ObjectInputStream in, ObservableList<Automobile> automobiles, Automobile auto) {
        this.out = out;
        this.in = in;
        this.automobiles = automobiles;
        this.pickedAuto = auto;
    }

    @FXML
    private TextField markField;

    @FXML
    private TextField productYearField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField priceField;

    @FXML
    private Button editEntryBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField driveForm;

    @FXML
    private TextField transmissionField;

    @FXML
    private TextField carcassField;

    @FXML
    private TextField engineField;

    @FXML
    private TextField amountField;


    @FXML
    void initialize() {

        markField.setText(this.pickedAuto.getMark());
        productYearField.setText(String.valueOf(this.pickedAuto.getProductionDate()));
        modelField.setText(this.pickedAuto.getModel());
        priceField.setText(String.valueOf(this.pickedAuto.getCost()));
        transmissionField.setText(this.pickedAuto.getTransmissionType());
        carcassField.setText(this.pickedAuto.getCarcassType());
        engineField.setText(this.pickedAuto.getEngineType());
        driveForm.setText(this.pickedAuto.getDriveUnit());
        amountField.setText(String.valueOf(this.pickedAuto.getAmount()));

        editEntryBtn.setOnAction(event -> {
            try {
                ModalDrawer drawer = new ModalDrawer();
                if(this.markField.getText().length() == 0 && this.modelField.getText().length() == 0 &&
                        this.productYearField.getText().length() == 0 && this.priceField.getText().length() == 0  &&
                            transmissionField.getText().length() == 0 && carcassField.getText().length() == 0 &&
                                engineField.getText().length() == 0 && driveForm.getText().length() == 0 &&
                                    amountField.getText().length() == 0)
                    drawer.getErrorModal("Поля не могут быть пустыми", "Ошибка");
                else {
                    out.writeObject(Actions.EDIT_CAR);
                    if((ResponseTypes)in.readObject() == ResponseTypes.OK) {

                        String markField, modelField, transmissionField, carcassField, engineField, driveForm;
                        int productYearField, amountField;
                        double priceField;

                        if(this.markField.getText().length() != 0) markField = this.markField.getText();
                        else markField = pickedAuto.getMark();
                        if(this.modelField.getText().length() != 0) modelField = this.modelField.getText();
                        else modelField = pickedAuto.getModel();
                        if(this.productYearField.getText().length() != 0) productYearField = Integer.valueOf(this.productYearField.getText());
                        else productYearField = this.pickedAuto.getProductionDate();
                        if(this.priceField.getText().length() != 0) priceField = Double.valueOf(this.priceField.getText());
                        else priceField = pickedAuto.getCost();
                        if(this.transmissionField.getText().length() != 0) transmissionField = this.transmissionField.getText();
                        else transmissionField = pickedAuto.getTransmissionType();
                        if(this.carcassField.getText().length() != 0) carcassField = this.carcassField.getText();
                        else carcassField = pickedAuto.getCarcassType();
                        if(this.engineField.getText().length() != 0) engineField = this.engineField.getText();
                        else engineField = pickedAuto.getEngineType();
                        if(this.driveForm.getText().length() != 0) driveForm = this.driveForm.getText();
                        else driveForm = pickedAuto.getDriveUnit();
                        if(this.amountField.getText().length() != 0) amountField = Integer.valueOf(this.amountField.getText());
                        else amountField = pickedAuto.getAmount();


                        Automobile formAuto = new Automobile(markField, modelField, productYearField, priceField,
                                transmissionField, carcassField, engineField, driveForm, amountField);
                        out.writeObject(this.pickedAuto);
                        if((ResponseTypes)in.readObject() == ResponseTypes.OK) {
                            out.writeObject(formAuto);
                            Object response = in.readObject();
                            if(response != null) {

                                this.automobiles.clear();
                                drawer.getSuccessModal("Запись успешно изменена", "Успех");
                                ArrayList<Automobile> responseAutomobiles = (ArrayList<Automobile>) response;
                                this.automobiles.addAll(responseAutomobiles);

                            } else drawer.getErrorModal("Произошла ошибка при изменении данных", "Ошибка");
                        } else drawer.getErrorModal("Произошла ошибка при изменении данных", "Ошибка");

                        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                        stage.close();

                    } else drawer.getErrorModal("Произошла ошибка при изменении данных", "Ошибка");
                }
            } catch(IOException err){
                err.printStackTrace();
            } catch(ClassNotFoundException err){
                err.printStackTrace();
            }
        });

        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });
    }
}