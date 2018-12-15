package com.jdmc.client;

import com.jdmc.constants.Actions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.jdmc.client.controllers.SignIn;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        JSONParser parser = new JSONParser();
        JSONObject configurations = (JSONObject)parser.parse(new FileReader("config.json"));
        int port = Integer.valueOf((String)configurations.get("port"));
        String host = (String)configurations.get("host");

        Socket socket = new Socket(host, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/SignIn.fxml"));
        loader.setController(new SignIn(out, in));
        primaryStage.setOnCloseRequest(event -> {
            try{
                out.writeObject(Actions.CLOSE_CONNECTION);
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
