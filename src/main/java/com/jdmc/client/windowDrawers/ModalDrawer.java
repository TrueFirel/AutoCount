package com.jdmc.client.windowDrawers;

import com.jdmc.client.controllers.ModalController;
import com.jdmc.client.interfaces.Callback;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ModalDrawer {

    public void getSuccessModal(String label, String windowLabel) throws IOException{
        FXMLLoader loader = null;
        Stage stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/fxml/SuccessModal.fxml"));
        loader.setController(new ModalController(label));
        stage.setTitle(windowLabel);
        Parent page = loader.load();
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }

    public void getErrorModal(String label, String windowLabel) throws IOException{
        FXMLLoader loader = null;
        Stage stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/fxml/ErrorModal.fxml"));
        loader.setController(new ModalController(label));
        stage.setTitle(windowLabel);
        Parent page = loader.load();
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }

}
