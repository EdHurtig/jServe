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
		WebServer.logInfo("Thread Registered for port " + getLocalPort());
		while (WebServer.getStatus() == ServerStatus.Started || WebServer.getStatus() == ServerStatus.Starting ) {
			Socket client = null;
			
			try {
				client = accept();
			} catch (IOException e) {
				WebServer.triggerInternalError("Accept Failed on Socket for socket on port " + getLocalPort() + ": " + e.getMessage());
				continue;
			}
			
			long start = System.nanoTime();
			Request req = new Request(client);
			 //Create thread for the socket
            Thread requestThread = new Thread(req);

           	if (WebServer.DEBUG) WebServer.logInfo("Starting New Thread For Request " + req.getRequestID() );

            //Start thread
            requestThread.start();  
	       
		}
		WebServer.logInfo("Thread Exit for port " + getLocalPort() + " Server Status = " + WebServer.getStatus() );

	}
	
}
