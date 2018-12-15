package com.jdmc.server;

import com.jdmc.constants.Actions;
import com.jdmc.constants.ResponseTypes;
import com.jdmc.entities.Automobile;
import com.jdmc.entities.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SocketController extends Thread {
    ConnectionController controller;
    Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private DBProcessor dbProcessor;

    public SocketController(ConnectionController controller, Socket socket, DBProcessor dbProcessor) throws IOException {
        this.controller = controller;
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.dbProcessor = dbProcessor;
        start();
    }

    @Override
    public synchronized void run() {
        try{
            while(!this.isInterrupted()){
                Actions action = (Actions) in.readObject();
                switch (action) {
                    case AUTHORIZE: {
                        out.writeObject(ResponseTypes.OK);
                        User user = (User)in.readObject();
                        out.writeObject(dbProcessor.authentificateUser(user));
                        break;
                    }
                    case REGISTER: {
                        out.writeObject(ResponseTypes.OK);
                        User user = (User)in.readObject();
                        out.writeObject(dbProcessor.registerUser(user));
                        break;
                    }
                    case GET_AUTOMOBILES: {
                        out.writeObject(ResponseTypes.OK);
                        ArrayList automobiles = dbProcessor.getAutomobiles(false);
                        out.writeObject(automobiles);
                        break;
                    }
                    case MAKE_BUY_ORDER:{
                        out.writeObject(ResponseTypes.OK);
                        Automobile requestingAutomobile = (Automobile)in.readObject();
                        User requestingUser = (User)in.readObject();
                        out.writeObject(dbProcessor.makeOrder(requestingUser, requestingAutomobile));
                        break;
                    }
                    case ADD_ENTRY: {
                        out.writeObject(ResponseTypes.OK);
                        Automobile requestingAutomobile = (Automobile)in.readObject();
                        out.writeObject(dbProcessor.addNewEntry(requestingAutomobile));
                        break;
                    }
                    case PRIVILEGED_GET_AUTOMOBILES:{
                        out.writeObject(ResponseTypes.OK);
                        ArrayList automobiles = dbProcessor.getAutomobiles(true);
                        out.writeObject(automobiles);
                        break;
                    }
                    case GET_USER_ORDERS:{
                        out.writeObject(ResponseTypes.OK);
                        String login = (String) in.readObject();
                        out.writeObject(dbProcessor.getUserOrders(login));
                        break;
                    }
                    case REMOVE_ORDER:{
                        out.writeObject(ResponseTypes.OK);
                        int orderId = (int)in.readObject();
                        out.writeObject(dbProcessor.declineUserOrder(orderId));
                        break;
                    }
                    case EDIT_CAR:{
                        out.writeObject(ResponseTypes.OK);
                        Automobile oldAuto = (Automobile) in.readObject();
                        out.writeObject(ResponseTypes.OK);
                        Automobile newAuto = (Automobile) in.readObject();
                        out.writeObject(dbProcessor.editCar(oldAuto, newAuto));
                        break;
                    }
                    case DELETE_CAR: {
                        out.writeObject(ResponseTypes.OK);
                        Automobile auto = (Automobile) in.readObject();
                        out.writeObject(dbProcessor.deleteCar(auto));
                        break;
                    }
                    case GET_USERS:{
                        out.writeObject(ResponseTypes.OK);
                        ArrayList<User> users = dbProcessor.getUsers();
                        out.writeObject(users);
                        break;
                    }
                    case ADD_USER: {
                        out.writeObject(ResponseTypes.OK);
                        User user = (User)in.readObject();
                        out.writeObject(dbProcessor.addUser(user));
                        break;
                    }
                    case EDIT_USER:{
                        out.writeObject(ResponseTypes.OK);
                        User oldUser = (User)in.readObject();
                        User newUser = (User)in.readObject();
                        out.writeObject(dbProcessor.editUser(oldUser, newUser));
                        break;
                    }
                    case DELETE_USER: {
                        out.writeObject(ResponseTypes.OK);
                        User userToDelete = (User)in.readObject();
                        out.writeObject(dbProcessor.deleteUser(userToDelete));
                        break;
                    }
                    case GET_ORDERS: {
                        out.writeObject(ResponseTypes.OK);
                        out.writeObject(dbProcessor.getAdminOrders());
                        break;
                    }
                    case APPLY_ORDER: {
                        out.writeObject(ResponseTypes.OK);
                        int orderId = (int)in.readObject();
                        out.writeObject(dbProcessor.acceptOrder(orderId, "accept"));
                        break;
                    }
                    case DECLINE_ORDER: {
                        out.writeObject(ResponseTypes.OK);
                        int orderId = (int)in.readObject();
                        out.writeObject(dbProcessor.acceptOrder(orderId, "decline"));
                        break;
                    }
                    case CLOSE_CONNECTION: {
                        this.out.close();
                        this.in.close();
                        this.socket.close();
                        this.controller.clientSockets.remove(this);
                        this.interrupt();
                        break;
                    }
                    case REGISTER_MORE_INFO:{
                        out.writeObject(ResponseTypes.OK);
                        User user = (User)in.readObject();
                        dbProcessor.addMoreUserInfo(user);
                        break;
                    }
                    default: out.writeObject(ResponseTypes.NOT_FOUND);
                }
            }
        } catch(SocketException err) {
            try{
                this.out.close();
                this.in.close();
                this.socket.close();
            } catch (IOException error) {
                err.printStackTrace();
            }
            this.interrupt();
            this.controller.clientSockets.remove(this);
        } catch(ClassNotFoundException err) {
            err.printStackTrace();
        } catch(SQLException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}
