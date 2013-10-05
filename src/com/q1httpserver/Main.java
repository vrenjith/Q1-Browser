package com.q1httpserver;

import com.q1httpserver.browsers.DefaultBrowserFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {

        //This in-fact is not needed if we are looking at deploying our code in one of the the standard
        //servers/containers
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(6868), 0);
            server.createContext("/command", new PathHandler(new DefaultBrowserFactory()));
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            l("Error starting server - " + e.getMessage());
        }
    }
    //Simple logging, can be log4j
    public static void l(String message){
        System.out.println(message);
    }
}
