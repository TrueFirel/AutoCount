package com.jdmc.client.controllers;

import com.jdmc.entities.Automobile;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ShowMoreEntryInfoController {

    private Automobile auto;

    @FXML
    private Button confirmBtn;

    @FXML
    private Label markLabel;

    @FXML
    private Label modelLabel;

    @FXML
    private Label prodYearLabel;

    @FXML
    private Label carcassLabel;

    @FXML
    private Label engineLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label amountLabel;

    @FXML
    private Label driveLabel;

    @FXML
    private Label transmissionLabel;

    public ShowMoreEntryInfoController(Automobile auto) {
        this.auto = auto;
    }

    @FXML
    void initialize() {
        markLabel.setText(this.auto.getMark());
        modelLabel.setText(this.auto.getModel());
        prodYearLabel.setText(String.valueOf(this.auto.getProductionYear()));
        carcassLabel.setText(this.auto.getCarcassType());
        engineLabel.setText(this.auto.getEngineType());
        priceLabel.setText(String.valueOf(this.auto.getCost()));
        amountLabel.setText(String.valueOf(this.auto.getAmount()));
        driveLabel.setText(this.auto.getDriveUnit());
        transmissionLabel.setText(this.auto.getTransmissionType());

        confirmBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });
    }
}