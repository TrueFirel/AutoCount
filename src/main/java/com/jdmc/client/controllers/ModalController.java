package com.jdmc.client.controllers;

import com.jdmc.client.interfaces.Callback;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ModalController {

    private Callback callback;
    @FXML
    private Label modalLabel;
    @FXML
    private Button OkBtn;
    private String labelText;

    public ModalController(String labelText) {
        this.labelText = labelText;
        this.callback = null;
    }

    public ModalController(String labelText, Callback callback) {
        this.callback = callback;
    }


    @FXML
    void initialize() {
        modalLabel.setText(labelText);
        OkBtn.setOnAction(event -> {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
            if(this.callback != null) this.callback.callingBack();
        });
    }
}
