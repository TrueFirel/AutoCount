package com.jdmc.client;

import com.jdmc.constants.Actions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.jdmc.client.controllers.SignIn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Socket socket = new Socket("localhost", 8081);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/SignIn.fxml"));
        loader.setController(new SignIn(out, in));
        primaryStage.setOnCloseRequest(event -> {
            try{
                out.writeObject(Actions.CloseConnection);
            } catch (IOException err) {
                err.printStackTrace();
            }
        });
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 800, 600) );
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
