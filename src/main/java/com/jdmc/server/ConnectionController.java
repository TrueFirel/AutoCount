package com.jdmc.server;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConnectionController {
    public LinkedList<SocketController> clientSockets;
    private ServerSocket socket;

    public ConnectionController() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject configurations = (JSONObject)parser.parse(new FileReader("config.json"));
        this.socket = new ServerSocket(Integer.valueOf((String)configurations.get("port")));
        this.clientSockets = new LinkedList<>();
    }

    public ConnectionController(ServerSocket socket) {
        this.socket = socket;
    }

    public ConnectionController startServer() {
        try{
            DBProcessor dbProcessor = new DBProcessor();
            ServerController serverController = new ServerController(this, this.socket, dbProcessor);
            while(true){
                Socket cientSocket = this.socket.accept();
                SocketController controller = new SocketController(this, cientSocket, dbProcessor);
                this.clientSockets.add(controller);
            }
        } catch(IOException err) {
            err.printStackTrace();
        } catch (SQLException err) {
            err.printStackTrace();
        } finally {
            return this;
        }
    }
}

