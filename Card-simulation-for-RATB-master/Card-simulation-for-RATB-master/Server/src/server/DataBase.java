package server;

import models.Card;
import models.Client;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


public class DataBase {
    private Connection myConn;
    private static DataBase db_instance = null;

    private DataBase() {
        final String DB_URL = "jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2237743";
        final String DB_USER = "-";
        final String DB_PASS = "-";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            myConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GRIGORE DAN-ANDREI 6
    
    public void closeConnection() {
        try {
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataBase getInstance() {
        if (db_instance == null)
            db_instance = new DataBase();
        return db_instance;
    }

    public Client printClients() {

        Client result = new Client();

        try (PreparedStatement ps = myConn.prepareStatement("SELECT * FROM CLIENT");
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            result.setId(rs.getInt("client_id"));
            result.setFirstName(rs.getString("first_name"));
            result.setLastName(rs.getString("last_name"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void createClient(String firstName, String lastName) {

        try (PreparedStatement ps = myConn.prepareStatement("INSERT INTO CLIENT(first_name,last_name) VALUES(?,?);");
             PreparedStatement ps2 = myConn.prepareStatement("SELECT client_id FROM CLIENT WHERE first_name = ? AND last_name = ?");

             PreparedStatement ps3 = myConn.prepareStatement("INSERT INTO CARD(card_money,expire_on,client_id) VALUES(0,null,?);");
             PreparedStatement ps4 = myConn.prepareStatement("SELECT card_id FROM CARD WHERE client_id = ?");
             PreparedStatement ps5 = myConn.prepareStatement("INSERT INTO CARD_TYPE(pass_type,price,card_id) VALUES('recharge',null,?);")) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.executeUpdate();

            ps2.setString(1, firstName);
            ps2.setString(2, lastName);

            ResultSet rs = ps2.executeQuery();
            rs.next();
            int client_id = rs.getInt("client_id");

            ps3.setInt(1, client_id);
            ps3.executeUpdate();

            ps4.setInt(1, client_id);
            ResultSet rs2 = ps4.executeQuery();
            rs2.next();
            int card_id = rs2.getInt("card_id");

            ps5.setInt(1, card_id);
            ps5.executeUpdate();

            System.out.print("DB UPDATED - ");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void chargePass(Card card) {
        Client person;
        person = card.getPerson();

        try (PreparedStatement ps = myConn.prepareStatement("SELECT cr.card_id FROM CLIENT cl, CARD cr WHERE cl.client_id = cr.client_id AND cl.first_name = ? AND cl.last_name = ?;");
             PreparedStatement ps2 = myConn.prepareStatement("UPDATE CARD_TYPE SET pass_type = ?, price = ? WHERE card_id = ?;");
             PreparedStatement ps3 = myConn.prepareStatement("UPDATE CARD SET card_money = card_money + ?, expire_on = null WHERE card_id = ?");
             PreparedStatement ps4 = myConn.prepareStatement("UPDATE CARD SET card_money = 0, expire_on = CURDATE() + INTERVAL ? DAY  WHERE card_id = ?")) {

            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ResultSet rs = ps.executeQuery();
            rs.next();
            card.setCard_id(rs.getInt("card_id"));

            if (!card.getPass_type().equals("Rechargeable")) {
                ps2.setString(1, card.getPass_type());
                ps2.setFloat(2, card.getPass_price());
                ps2.setInt(3, card.getCard_id());
                ps2.executeUpdate();

                if (card.getPass_type().equals("Monthly Pass"))
                    ps4.setInt(1, 30);
                else
                    ps4.setInt(1, 1);
                ps4.setInt(2, card.getCard_id());
                ps4.executeUpdate();
            } else {
                ps2.setString(1, card.getPass_type());
                ps2.setFloat(2, 0);
                ps2.setInt(3, card.getCard_id());
                ps2.executeUpdate();

                ps3.setFloat(1, card.getPass_price());
                ps3.setInt(2, card.getCard_id());
                ps3.executeUpdate();
            }
            System.out.print("DB UPDATED - ");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void validateCard(Card card) {
        Client person;
        person = card.getPerson();

        try (PreparedStatement ps = myConn.prepareStatement("SELECT cr.card_id, cr.card_money, cr.expire_on, ct.pass_type FROM CLIENT cl, CARD cr, CARD_TYPE ct WHERE cl.client_id = cr.client_id AND cl.first_name = ? AND cl.last_name = ? AND ct.card_id = cr.card_id;");
             PreparedStatement ps2 = myConn.prepareStatement("SELECT transport_id, charge_per_trip FROM TRANSPORT WHERE line = ?");
             PreparedStatement ps3 = myConn.prepareStatement("INSERT INTO VALIDATION(card_id,transport_id,`date&time`) VALUES(?,?,NOW() + INTERVAL 3 HOUR);");
             PreparedStatement ps4 = myConn.prepareStatement("UPDATE CARD SET card_money = card_money - ? WHERE card_id = ?")) {

            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ResultSet rs = ps.executeQuery();
            rs.next();
            card.setCard_id(rs.getInt("cr.card_id"));
            card.setCard_money(rs.getInt("cr.card_money"));
            card.setPass_type(rs.getString("ct.pass_type"));
            card.setExpireDate(rs.getString("cr.expire_on"));


            ps2.setInt(1, card.getLine_validation());
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            int transportLine = rs2.getInt("transport_id");
            int chargePerTrip = rs2.getInt("charge_per_trip");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");

            if (!card.getPass_type().equals("Rechargeable"))
                try {
                    System.out.println(card.getExpireDate());
                    Date expireDate = format.parse(card.getExpireDate());
                    System.out.println(card.getExpireDate());
                    Date todayDate = format.parse(LocalDate.now().toString());
                    if (expireDate.compareTo(todayDate) <= 0) {
                        ps3.setInt(1, card.getCard_id());
                        ps3.setInt(2, transportLine);
                        ps3.executeUpdate();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            else {
                if (card.getCard_money() >= chargePerTrip) {
                    ps3.setInt(1, card.getCard_id());
                    ps3.setInt(2, transportLine);
                    ps3.executeUpdate();

                    ps4.setInt(1, chargePerTrip);
                    ps4.setInt(2, card.getCard_id());
                    ps4.executeUpdate();
                }
            }
            System.out.print("DB UPDATED - ");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Boolean verifyCard(Card card) {
        Client person;
        person = card.getPerson();

        try (PreparedStatement ps = myConn.prepareStatement("SELECT cr.card_id FROM CLIENT cl, CARD cr WHERE cl.client_id = cr.client_id AND cl.first_name = ? AND cl.last_name = ?;");
             PreparedStatement ps2 = myConn.prepareStatement("SELECT v.`date&time`, t.line FROM VALIDATION v, TRANSPORT t WHERE card_id = ? AND v.transport_id = t.transport_id")) {

            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ResultSet rs = ps.executeQuery();
            rs.next();
            card.setCard_id(rs.getInt("cr.card_id"));

            ps2.setInt(1, card.getCard_id());
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String validationDate = sdf.format(rs2.getTimestamp("v.date&time"));
                String now = LocalDate.now().toString();

                if (now.equals(validationDate))
                    if (rs2.getInt("t.line") == card.getLine_validation())
                        return true;
            }
            System.out.print("DB UPDATED - ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

