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
import java.util.ArrayList;

public class AdminWindowsDrawer {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObservableList<Automobile> automobiles;

    public AdminWindowsDrawer(ObjectInputStream in, ObjectOutputStream out, ObservableList<Automobile> automobiles) throws IOException {
        this.in = in;
        this.out = out;
        this.automobiles = automobiles;
    }

    public AdminWindowsDrawer getEditingWindow(Automobile pickedAuto) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditEntryWindow.fxml"));
        loader.setController(new EditEntryController(this.out, this.in, this.automobiles, pickedAuto));
        Parent page = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Добавление записи");
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
        return this;
    }

    public AdminWindowsDrawer getAddingWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEntryWindow.fxml"));
        loader.setController(new AddEntryController(this.out, this.in, this.automobiles));
        Parent page = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Добавление записи");
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
        return this;
    }

    public AdminWindowsDrawer getChartWindow(ArrayList<Automobile> automobiles) throws  IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChartView.fxml"));
        loader.setController(new ChartViewController(automobiles));
        stage.setTitle("Статистика");
        Parent page = loader.load();
        stage.setScene(new Scene(page));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
        return this;
    }

}
