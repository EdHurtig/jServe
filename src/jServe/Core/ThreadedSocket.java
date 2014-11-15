package jServe.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class ThreadedSocket extends ServerSocket implements Runnable {


    public ThreadedSocket(int port) throws IOException {
        super(port);
    }

    @Override
    public void run() {
        int n = 0;
        while (!isBound() && n < 10) {
            WebServer.logInfo("Waiting for Socket on port " + this.getLocalPort() + " to bind");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            n++;
        }
        if (!isBound()) {

            throw new JServeException(new SocketException("Socket failed to bind after 10 seconds"));

        }
        WebServer.logInfo("Thread Registered for port " + getLocalPort());
        while (WebServer.getStatus() == ServerStatus.Started || WebServer.getStatus() == ServerStatus.Starting) {
            Socket client = null;

            // If the socket was closed (no more sites using it), then stop the thread
            if (isClosed()) {
                return;
            }
            try {
                client = accept();
            } catch (IOException e) {
                WebServer.logError("Accept Failed on Socket for socket on port " + getLocalPort() + ": " + e.getMessage());
                continue;
            }

            long start = System.nanoTime();
            Request req = new Request(client);
            //Create thread for the socket
            Thread requestThread = new Thread(req);

            WebServer.logInfo("Starting New Thread For Request " + req.getRequestID());

            //Start thread
            requestThread.start();

        }
        WebServer.logInfo("Thread Exit for port " + getLocalPort() + " Server Status = " + WebServer.getStatus());

    }

}
