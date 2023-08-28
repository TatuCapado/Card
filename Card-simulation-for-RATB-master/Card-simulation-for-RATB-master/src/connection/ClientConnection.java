package connection;

import GUI.Interface;

import java.io.*;
import java.net.Socket;


public class ClientConnection {


    public void connectToServer() throws IOException {

        Socket socket = new Socket("localhost", 7777);
        Interface graphics = new Interface(socket);
        graphics.showContent();

    }


    public static void main(String[] args) throws Exception {
        ClientConnection client = new ClientConnection();
        client.connectToServer();
    }
}