package com.jdmc.client.windowDrawers;

import com.jdmc.client.controllers.*;
import com.jdmc.entities.Automobile;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AdminWindowsDrawer {
    ObjectOutputStream out;
    ObjectInputStream in;
    ObservableList<Automobile> automobiles;

    public AdminWindowsDrawer(ObjectInputStream in, ObjectOutputStream out, ObservableList<Automobile> automobiles) throws IOException {
        this.in = in;
        this.out = out;
        this.automobiles = automobiles;
    }

    public void getEditingWindow(Automobile pickedAuto) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditEntryWindow.fxml"));
        loader.setController(new EditEntryController(this.out, this.in, this.automobiles, pickedAuto));
        Parent page = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Добавление записи");
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }

    public void getAddingWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEntryWindow.fxml"));
        loader.setController(new AddEntryController(this.out, this.in, this.automobiles));
        Parent page = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Добавление записи");
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
