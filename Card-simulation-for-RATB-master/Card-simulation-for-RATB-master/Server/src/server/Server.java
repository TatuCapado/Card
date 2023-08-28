package server;

import models.Card;
import models.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Server {

    public static void main(String[] args) throws Exception {
        System.out.println("Server running.");
        try (ServerSocket listener = new ServerSocket(7777)) {
            while (true) {
                new ServerConnection(listener.accept()).start();
            }
        }
    }

    private static class ServerConnection extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private Map welcome;

        public ServerConnection(Socket socket) throws IOException {
            System.out.println("A client entered");
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
        }

        public void run() {
            DataBase db = DataBase.getInstance();
            try {
                while (true) {
                    welcome = new HashMap();
                    try {
                        welcome = (HashMap) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (welcome.containsKey("afisare")) {
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(db.printClients());
                    }

                    if (welcome.containsKey("addClient")) {
                        Client newClient = (Client) welcome.get("addClient");
                        db.createClient(newClient.getFirstName(), newClient.getLastName());
                        System.out.println("New Client");
                    }

                    if (welcome.containsKey("chargePass")) {
                        Card card = (Card) welcome.get("chargePass");
                        db.chargePass(card);
                        System.out.println("Pass created");
                    }

                    if (welcome.containsKey("validateCard")) {
                        Card card = (Card) welcome.get("validateCard");
                        db.validateCard(card);
                        System.out.println("Validation made");
                    }

                    if (welcome.containsKey("verifyCard")) {
                        Card card = (Card) welcome.get("verifyCard");
                        Boolean check = db.verifyCard(card);
                        String validate = check.toString();
                        try {
                            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                            dout.writeUTF(validate);
                        } catch (Exception ignored) {
                        }
                        System.out.println("Card verified");
                    }
                    welcome.clear();
                }
            } catch (IOException e) {
                System.out.println("A client left.");
            } finally {
                try {
                    db.closeConnection();
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket.");
                }
            }
        }

    }
}
