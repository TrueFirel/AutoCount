package com.jdmc.client.controllers;

import com.jdmc.client.windowDrawers.ModalDrawer;
import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.Automobile;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddEntryController {

    ObjectOutputStream out;
    ObjectInputStream in;
    private ObservableList<Automobile> automobiles;

    @FXML
    private TextField markField;

    @FXML
    private TextField productYearField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField driveForm;

    @FXML
    private Button addEntryBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextField transmissionField;

    @FXML
    private TextField carcassField;

    @FXML
    private TextField engineField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField amountField;

    public AddEntryController(ObjectOutputStream out, ObjectInputStream input, ObservableList<Automobile> automobiles) {
        this.out = out;
        this.in = input;
        this.automobiles = automobiles;
    }

    @FXML
    void initialize() {
        ModalDrawer drawer = new ModalDrawer();
        addEntryBtn.setOnAction(event -> {
            try{
                if(markField.getText().length() != 0 && modelField.getText().length() != 0 &&
                        priceField.getText().length() != 0 && productYearField.getText().length() != 0 &&
                            transmissionField.getText().length() != 0 && carcassField.getText().length() != 0 &&
                                engineField.getText().length() != 0 && driveForm.getText().length() != 0 &&
                                    amountField.getText().length() != 0) {
                    Automobile auto = new Automobile(markField.getText(), modelField.getText(),
                            Integer.valueOf(productYearField.getText()), Double.valueOf(priceField.getText()),
                            driveForm.getText(), carcassField.getText(), engineField.getText(), transmissionField.getText(),
                            Integer.valueOf(amountField.getText()));
                    out.writeObject(Actions.ADD_ENTRY);
                    if((ResponseTypes) in.readObject() != ResponseTypes.OK) {
                        drawer.getErrorModal("Ошибка сервера", "Ошибка");
                        return;
                    } else  {
                        out.writeObject(auto);
                        Object entity = in.readObject();
                        if(entity == null) return;
                        else {
                            automobiles.clear();
                            ArrayList<Automobile> automobiles = (ArrayList<Automobile>)entity;
                            drawer.getSuccessModal("Запись успешно добавлена", "Успех");
                            this.automobiles.addAll(automobiles);
                        }
                        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                        stage.close();
                    }
                } else drawer.getErrorModal("Поля не могут быть пустыми", "Ошибка");
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