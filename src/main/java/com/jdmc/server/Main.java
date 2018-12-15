package com.jdmc.server;

import org.apache.log4j.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try{
            ConnectionController connectionController = new ConnectionController();
            connectionController.startServer();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
