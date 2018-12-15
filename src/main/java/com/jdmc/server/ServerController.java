package com.jdmc.server;

import java.io.*;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ServerController extends Thread {
    private ConnectionController controller;
    private DBProcessor dbProcessor;
    private ServerSocket socket;

    public ServerController(ConnectionController controller, ServerSocket socket, DBProcessor dbProcessor) throws IOException {
        this.socket = socket;
        this.controller = controller;
        this.dbProcessor = dbProcessor;
        start();
    }

    private boolean writeDataFromDatabase(){
        try(FileWriter writer = new FileWriter("database.txt", false)){
            Statement statement = dbProcessor.connection.createStatement();
            String query = "select * from users.users_data";
            ResultSet set = statement.executeQuery(query);
            writer.write("--------------------------\n");
            writer.write("Table users.data\n");
            writer.write("--------------------------\n");
            writer.write("|id|login|password|role_id|\n");
            while(set.next()){
                int id = set.getInt("user_id");
                String login = set.getString("login");
                String password = set.getString("password");
                int roleId = set.getInt("role_id");
                writer.write("|" + id + "|" + login + "|" + password + "|" + roleId + "|\n");
            }
            writer.write("--------------------------\n\n");
            query = "select * from users.car_catalog";
            set = statement.executeQuery(query);
            writer.write("----------------------------------------------------------------------------------------------------\n");
            writer.write("Table users.car_catalog\n");
            writer.write("----------------------------------------------------------------------------------------------------\n");
            writer.write("|id|car_mark|car_model|prod_year|price|drive_unit|carcass_type|engine_type|transmission_type|amount|\n");
            while(set.next()){
                int id = set.getInt("car_id");
                String mark = set.getString("car_mark");
                String model = set.getString("car_model");
                double price = set.getDouble("price");
                int prodYear = set.getInt("prod_year");
                String driveUnit = set.getString("drive_unit");
                String carcassType = set.getString("carcass_type");
                String engineType = set.getString("engine_type");
                String transmissionType = set.getString("transmission_type");
                int amount = set.getInt("amount");
                writer.write("|" + id + "|" + mark + "|" + model + "|" + prodYear + "|" + price + "|" + driveUnit +
                        "|" + carcassType + "|" + engineType + "|" + transmissionType + "|" + amount + "|\n");
            }
            writer.write("----------------------------------------------------------------------------------------------------\n\n");
            query = "select * from users.orders";
            set = statement.executeQuery(query);
            writer.write("--------------------------------\n");
            writer.write("Table users.orders\n");
            writer.write("--------------------------------\n");
            writer.write("|order_id|user_id|car_id|status|\n");
            while(set.next()) {
                int orderId = set.getInt("order_id");
                int userId = set.getInt("user_id");
                int carId = set.getInt("car_id");
                String status = set.getString("status");
                writer.write("|" + orderId + "|" + userId + "|" + carId + "|" + status + "|\n");
            }
            writer.write("--------------------------------\n\n");
            query = "select * from users.user_info";
            set = statement.executeQuery(query);
            writer.write("---------------------------------------\n");
            writer.write("Table users.user_info\n");
            writer.write("---------------------------------------\n");
            writer.write("|user_id|name|surname|lastname|country|\n");
            while(set.next()) {
                int userId = set.getInt("user_id");
                String name = set.getString("name");
                String surname = set.getString("surname");
                String lastname = set.getString("lastname");
                String country= set.getString("country");
                writer.write("|" + userId + "|" + name+ "|" + surname + "|" + lastname + "|" + country + "|\n");
            }
            writer.write("---------------------------------------\n");
            query = "select * from users.roles";
            set = statement.executeQuery(query);
            writer.write("-----------------\n");
            writer.write("Table users.roles\n");
            writer.write("-----------------\n");
            writer.write("|id|role|\n");
            while(set.next()) {
                int id = set.getInt("id");
                String role = set.getString("role");
                writer.write("|" + id + "|" + role + "|\n");
            }
            writer.write("-----------------\n");
        } catch (IOException err){
            return false;
        } catch (SQLException err){
            return false;
        }
        return true;
    }

    private ServerController readDataFromFile(){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("database.txt")))){
            String line;
            while((line = reader.readLine()) != null) System.out.println(line);
        } catch (IOException err) {
            System.out.println("Произошла ошибка при чтении файла!\n");
        } finally {
            return this;
        }
    }

    private ServerController pressEnter(){
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return this;
        }
    }

    @Override
    public synchronized void run() {
        try{
            while(true) {
                Scanner in = new Scanner(System.in);
                System.out.println("+----------------------------------------------------------+");
                System.out.println("| show-server-state - показывает текущее состояние сервера |");
                System.out.println("| get-connections - просмотр статистики подключений        |");
                System.out.println("| get-data-from-database - выгрузка данных из базы в файл  |");
                System.out.println("| show-data - просмотр файла с данными                     |");
                System.out.println("+----------------------------------------------------------+\n");
                System.out.print("Введите команду: ");
                String command = in.nextLine();
                switch (command) {
                    case "show-server-state": {
                        System.out.println("Адрес: " + this.socket.getInetAddress());
                        System.out.println("Порт: " + this.socket.getLocalPort() + "\n");
                        System.out.println("Нажмите кнопку Enter для продолжения: ");
                        pressEnter();
                        break;
                    }
                    case "get-data-from-database": {
                        if(writeDataFromDatabase()) System.out.println("Данные успешно записаны в файл!\n");
                        else System.out.println("Произошла ошибка при записи в файл!\n");
                        System.out.println("Нажмите кнопку Enter для продолжения: ");
                        pressEnter();
                        break;
                    }
                    case "show-data": {
                        readDataFromFile();
                        System.out.println("Нажмите кнопку Enter для продолжения: ");
                        pressEnter();
                        break;
                    }
                    case "get-connections": {
                        System.out.println("Количество подключённых пользователей: " + this.controller.clientSockets.size());
                        for(int i = 0; i < this.controller.clientSockets.size(); i++){
                            System.out.println("---------------------------------");
                            System.out.println("Клиент: " + (i+1) );
                            System.out.println("Адрес: " + controller.clientSockets.get(i).socket.getInetAddress());
                            System.out.println("Порт: " + controller.clientSockets.get(i).socket.getLocalPort());
                            System.out.println("---------------------------------\n");
                        }
                        System.out.println("Нажмите кнопку Enter для продолжения: ");
                        pressEnter();
                        break;
                    }
                    default: {
                        System.out.println("Команда не распознана\n");
                        System.out.println("Нажмите кнопку Enter для продолжения: ");
                        pressEnter();
                    }
                }
            }
        } catch(Exception err) {
            err.printStackTrace();
        }
    }
}
