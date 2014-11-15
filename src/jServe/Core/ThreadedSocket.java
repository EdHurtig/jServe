package jServe.Core;

import jServe.Core.Exceptions.JServeException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * An Extension of ServerSocket that will run in a new thread so that it
 * is non blocking when waiting for a connection.
 * <p/>
 * A ThreadedSocket extends Runnable so you can launch it with the Thread class
 * for asynchronous operation.
 * <p/>
 * Every time a new connection is received, a ThreadedSocket will immediately push
 * that connection into a Request object which runs in yet another thread (spawned by Thread).
 * The Request object reads the data from the incoming connection and parses it into a type of
 * Request, currently only HTTP Requests are handled, but theoretically any type of request
 * could be handled.
 * <p/>
 * The Request is then Passed to a Site which processes the Request and using the Request Object's
 * output stream to send back data to the user
 */
public class ThreadedSocket extends ServerSocket implements Runnable {


    /**
     * Constructor for the ThreadedSocket, Takes a port number to listen on
     *
     * @param port The port number to listen on
     * @throws IOException
     */
    public ThreadedSocket(int port) throws IOException {
        super(port);
    }

    /**
     * The main Listener Loop Function. Blocks on the ServerSocket's accept() call
     */
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

            // If the socket was closed (no more sites using it), then stop the thread
            if (isClosed()) {
                return;
            }

            // The Socket Object that represents the raw connection of the next connection
            Socket client;

            // Block until a new connection arrives
            try {
                client = accept();
            } catch (IOException e) {
                WebServer.logError("Accept Failed on Socket for socket on port " + getLocalPort() + ": " + e.getMessage());
                continue;
            }

            // Create a Request object for this connection
            Request req = new Request(client);

            //Create thread for the request
            Thread requestThread = new Thread(req);

            WebServer.logInfo("Starting New Thread For Request " + req.getRequestID());

            //Start thread
            requestThread.start();

        }

        // Exiting the thread
        WebServer.logInfo("Thread Exit for port " + getLocalPort() + " Server Status = " + WebServer.getStatus());

    }

}
