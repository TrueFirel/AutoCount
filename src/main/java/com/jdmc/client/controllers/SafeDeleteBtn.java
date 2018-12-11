package com.jdmc.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.jdmc.client.interfaces.*;


public class SafeDeleteBtn {

    private Callback callback;
    private String labelText;
    @FXML
    private Label modalLabel;

    @FXML
    private Button yesBtn;

    @FXML
    private Button noBtn;

    public SafeDeleteBtn(String labelText) {
        this.labelText = labelText;
    }

    public void registerCallback(Callback callback){
        this.callback = callback;
    }

    @FXML
    void initialize() {
        modalLabel.setText(this.labelText);
        yesBtn.setOnAction(event -> {
            callback.callingBack();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });
        noBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });
    }
}
