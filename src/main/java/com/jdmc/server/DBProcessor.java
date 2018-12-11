package com.jdmc.server;

import com.jdmc.entities.Automobile;
import com.jdmc.entities.MoreUserInfo;
import com.jdmc.entities.Order;
import com.jdmc.entities.User;

import java.sql.*;
import java.util.ArrayList;

public class DBProcessor {
    public Connection connection;
    private final static String USERNAME = "root";
    private final static String PASSWORD = "1234";
    private final static String URL = "jdbc:mysql://localhost:3306/users?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public DBProcessor() throws SQLException {
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public ArrayList<Automobile> getAutomobiles(boolean isPreviligious) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query;
        if(!isPreviligious) query = "select * from users.car_catalog where amount > 0;" ;
        else query = "select * from users.car_catalog;";
        ArrayList<Automobile> automobiles = null;
        ResultSet result = statement.executeQuery(query);
        automobiles = new ArrayList<>();
        while(result.next()){
            String mark = result.getString("car_mark");
            String model = result.getString("car_model");
            double price = result.getDouble("price");
            int prodYear = result.getInt("prod_year");
            String driveUnit = result.getString("drive_unit");
            String carcassType = result.getString("carcass_type");
            String engineType = result.getString("engine_type");
            String transmissionType = result.getString("transmission_type");
            int amount = result.getInt("amount");
            automobiles.add(new Automobile(mark,model,prodYear, price, driveUnit, carcassType, engineType, transmissionType, amount));
        }
        return automobiles;
    }

    public User authentificateUser(User user) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "select * from users.users_data where login='" + user.getLogin() + "' and password='" + user.getPassword() + "'" ;
        ResultSet result = statement.executeQuery(query);
        int roleId = 0;
        String login = "", password = "", role="";
        if(result.next()) {
            login = result.getString("login");
            password = result.getString("password");
            roleId = result.getInt("role_id");
        }
        query = "select * from users.roles where id=" + roleId;
        result = statement.executeQuery(query);
        if(result.next()) role = result.getString("role");
        User authentificatedUser = new User(login, password, role);
        return authentificatedUser;
    }

    public User registerUser(User user) throws  SQLException {
        Statement statement = this.connection.createStatement();
        String query = "insert into users.users_data (login, password, role_id) values (\"" + user.getLogin() + "\", \"" + user.getPassword() +
                "\", (select id from users.roles where role=\'u\'));";
        try{
            statement.execute(query);
        } catch (SQLException err) {
            return null;
        }
        query = "select * from users.users_data where login=\"" + user.getLogin() + "\" and password=\"" + user.getPassword() + "\"" ;
        ResultSet result = statement.executeQuery(query);
        if(result.next()) {
            String login = result.getString("login");
            String password = result.getString("password");

            User registeredUser = new User(login, password, "u");
            return registeredUser;
        }
        return  null;
    }

    public ArrayList<Automobile> makeOrder(User user, Automobile auto) throws  SQLException {
        Statement statement = this.connection.createStatement();
        String query = "select * from users.users_data where login=\"" + user.getLogin() + "\"";
        ResultSet result = statement.executeQuery(query);
        int userId;
        int carId;
        if(result.next()) userId = result.getInt("user_id");
        else return null;
        query = "select * from users.car_catalog where car_model=\"" + auto.getModel() + "\"";
        result = statement.executeQuery(query);
        if(result.next()) carId = result.getInt("car_id");
        else return null;
        query = "select * from users.orders where car_id=" + carId + " and user_id=" + userId + " and status!=\"declined\"";
        result = statement.executeQuery(query);
        if(result.next()) return null;
        query = "insert into users.orders (user_id, car_id) values(" + userId + ", " + carId + ")";
        statement.execute(query);
        return getAutomobiles(false);
    }


    public ArrayList<Automobile> addNewEntry(Automobile auto) throws SQLException{
        ArrayList<Automobile> automobiles = null;
        Statement statement = this.connection.createStatement();
        String query = "insert into users.car_catalog (car_mark, car_model, prod_year, price, drive_unit, carcass_type, " +
                "engine_type, transmission_type, amount) values (\"" +
                auto.getMark() + "\", \"" + auto.getModel() + "\", + " + auto.getProductionDate() + ", " +
                auto.getCost() + ", \"" + auto.getDriveUnit() + "\", \"" + auto.getCarcassType() + "\", \"" +
                auto.getEngineType() + "\", \"" + auto.getTransmissionType() + "\", " + auto.getAmount() + ");";
        try{
            statement.execute(query);
            return getAutomobiles(true);
        } catch (SQLException err) {
            return null;
        }
    }

    public ArrayList<Order> getUserOrders(String loign) throws SQLException{
        ArrayList<Order> orders = new ArrayList<>();
        Statement statement = this.connection.createStatement();
        String query = "select * from users.orders where user_id=(select user_id from users.users_data where login=\"" + loign + "\");";
        ResultSet ordersSelectResult = statement.executeQuery(query);
        while(ordersSelectResult.next()){
            int userId = ordersSelectResult.getInt("user_id");
            int carId = ordersSelectResult.getInt("car_id");
            int orderId = ordersSelectResult.getInt("order_id");
            String status = ordersSelectResult.getString("status");
            orders.add(new Order(orderId, userId, carId, status));
        }
        for(int i = 0; i < orders.size(); i++) {
            int carId = orders.get(i).getCarId();
            query = "select * from users.car_catalog where car_id=" + carId;
            ResultSet carSelectResult = statement.executeQuery(query);
            if(carSelectResult.next()) {
                String mark = carSelectResult.getString("car_mark");
                String model = carSelectResult.getString("car_model");
                double price = carSelectResult.getDouble("price");
                orders.get(i).setMark(mark);
                orders.get(i).setCost(price);
                orders.get(i).setModel(model);
            }
        }
        return orders;
    }

    public ArrayList<Order> declineUserOrder(int orderId) throws SQLException{
        Statement statement = this.connection.createStatement();
        String query = "select login from users.users_data where user_id=(select user_id from users.orders where order_id=" + orderId + ")";
        ResultSet selectResult = statement.executeQuery(query);
        String login = "";
        if(selectResult.next()) login = selectResult.getString("login");
        query = "update users.orders set status=\"declined\" where order_id=" + orderId;
        statement.execute(query);
        return getUserOrders(login);
    }

    public ArrayList<Automobile> deleteCar(Automobile auto) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "delete from users.car_catalog where car_model=\"" + auto.getModel() + "\" and car_mark=\""
                + auto.getMark() + "\" and prod_year=" + auto.getProductionDate() + " and price=" + auto.getCost();
        statement.execute(query);
        return getAutomobiles(true);
    }

    public ArrayList<Automobile> editCar(Automobile oldAuto, Automobile newAuto) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "update users.car_catalog set car_mark=\"" + newAuto.getMark() + "\", " + "car_model=\"" + newAuto.getModel() +
                "\", prod_year=" + newAuto.getProductionDate() + ", price=" + newAuto.getCost() + ", drive_unit=\"" +
                newAuto.getDriveUnit() + "\", carcass_type=\"" + newAuto.getCarcassType() + "\",engine_type=\"" + newAuto.getEngineType()
                + "\", transmission_type=\"" + newAuto.getTransmissionType() + "\", amount=" + newAuto.getAmount() + " where car_mark=\"" +
                oldAuto.getMark() + "\" and " + "car_model=\"" + oldAuto.getModel() + "\" and prod_year=" +
                oldAuto.getProductionDate() + " and price=" + oldAuto.getCost();
        statement.execute(query);
        return getAutomobiles(true);
    }

    public ArrayList<User> getUsers() throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "select * from users.users_data where role_id=(select role_id from users.roles where role=\'u\')";
        ResultSet result = statement.executeQuery(query);
        ArrayList<User> users = new ArrayList<>();
        while(result.next()){
            String login = result.getString("login");
            String password = result.getString("password");
            users.add(new User(login, password));
        }
        return users;
    }

    public ArrayList<User> addUser(User user) throws SQLException {
        ArrayList<User> oldUsers = getUsers();
        registerUser(user);
        ArrayList<User> newUsers = getUsers();
        if(oldUsers.size() == newUsers.size()) return null;
        else return newUsers;
    }

    public ArrayList<User> editUser(User oldUser, User newUser) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "update users.users_data set login=\"" + newUser.getLogin() + "\", " + "password=\"" + newUser.getPassword() +
                "\"" + " where login=\"" + oldUser.getLogin() + "\" and " + "password=\"" + oldUser.getPassword() + "\"" ;
        try {
            statement.execute(query);
        } catch(SQLException err) {
            err.printStackTrace();
            return null;
        }
        return getUsers();
    }

    public ArrayList<User> deleteUser(User user) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "delete from users.users_data where login=\"" + user.getLogin() + "\" and password=\"" + user.getPassword() + "\"";
        try {
            statement.execute(query);
        } catch(SQLException err) {
            err.printStackTrace();
            return null;
        }
        return getUsers();
    }

    public ArrayList<Order> getAdminOrders() throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "select * from users.orders where status=\"in_process\"";
        ArrayList<Order> orders = new ArrayList<>();
        try {
            ResultSet result = statement.executeQuery(query);
            while(result.next()) {
                int orderId = result.getInt("order_id");
                int carId = result.getInt("car_id");
                int userId = result.getInt("user_id");
                orders.add(new Order(orderId, userId, carId));
            }
            result.close();
            for(Order order : orders) {
                query = "select * from users.car_catalog where car_id=" + order.getCarId();
                result = statement.executeQuery(query);
                while(result.next()) {
                    String mark = result.getString("car_mark");
                    String model = result.getString("car_model");
                    double price = result.getDouble("price");
                    order.setCarData(mark, model, price);
                }
                query = "select * from users.users_data where user_id=" + order.getUserId();
                result = statement.executeQuery(query);
                if(result.next()){
                    order.setLogin(result.getString("login"));
                }
            }
            return orders;
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }

    public void addMoreUserInfo(User user) throws SQLException {
        MoreUserInfo moreUserInfo = user.getMoreUserInfo();
        Statement statement = this.connection.createStatement();
        String query = "insert into users.user_info (user_id, name, surname, lastname, country) values " +
                "((select user_id from users.users_data where login=\"" + user.getLogin() + "\") , \""
                + moreUserInfo.getName() + "\", \"" + moreUserInfo.getSurname() + "\", \"" +
                moreUserInfo.getLastName() + "\", \"" + moreUserInfo.getCountry() + "\");";
        System.out.println(query);
        statement.execute(query);
    }

    public ArrayList<Order> acceptOrder (int orderId, String action) throws SQLException {
        Statement statement = this.connection.createStatement();
        String query = "";
        if(action.equals("accept")) {
            int amount = 0;
            query = "select amount from users.car_catalog where car_id=(select car_id from users.orders where order_id=" + orderId + ")";
            ResultSet result = statement.executeQuery(query);
            if(result.next()) amount = result.getInt("amount");
            int carId;
            query = "select car_id from users.orders where order_id=" + orderId;
            result = statement.executeQuery(query);
            if(result.next()) carId = result.getInt("car_id");
            else return null;
            query = "update users.car_catalog set amount =" + (amount-1) + " where car_id=" + carId +  ";";
            statement.execute(query);
            query = "update users.orders set status=\"accepted\" where order_id=" + orderId;
        }
        else if(action.equals("decline")) query = "update users.orders set status=\"declined\" where order_id=" + orderId;
        statement.execute(query);
        return getAdminOrders();
    }
}
