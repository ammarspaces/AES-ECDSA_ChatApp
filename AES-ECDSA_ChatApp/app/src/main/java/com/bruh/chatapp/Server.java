package com.bruh.chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private  Server server;
    private Socket userSocket;
    int port = 8088;

   ServerSocket serverSocket;

    {
        try {
            serverSocket = new ServerSocket(port);
            userSocket = serverSocket.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
