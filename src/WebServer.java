import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.PortableServer.POAManagerPackage.State;

public class WebServer implements Configurable {
	public static boolean DEBUG = true;  //TODO: change to false for production
	public static ArrayList<Site> sites = new ArrayList<Site>();
	public static int lastRequestID = 1;
	private static ServerStatus status = ServerStatus.Stopped;

	public static double[] requestTimes = new double[50000];
	public static int nRequestTimes = 0;
	public static ArrayList<ThreadedSocket> sockets = new ArrayList<ThreadedSocket>();
	
	public static HashMap<Thread,Runnable> threadRegistry = new HashMap<Thread,Runnable>();
	
	
	/** GETTERS/SETTERS **/
	public static ServerStatus getStatus() {
		return status;
	}
	
	public static void setStatus(ServerStatus newStatus) {
		status = newStatus;
		//TODO: Event >> OnStatusChanged()
	}
	
	/** MAIN **/
	public static void main(String[] rawargs) {
		List<String> args = Arrays.asList(rawargs);
		if (args.contains("debug"))
			DEBUG = true;

		

		
		logInfo("Starting Server");
		start();
		
		logInfo("---END OF MAIN()---");
	}
	
	/** THREAD REGISTRY **/
	public static boolean registerThread(Thread t) {
		return registerThread(t, null);
	}
	
	public static boolean registerThread(Thread t, Runnable target) {
		if (!threadRegistry.containsKey(t) || t.getState() == Thread.State.TERMINATED) {
			threadRegistry.put(t, target);
			try {
				t.start();
			} catch (IllegalThreadStateException e) {
				triggerInternalError("Failed to start thread: " + t.getName() + " - " + t.getId());
				return false;
			}
			return true;
		}
		logDebug("Thread " + t.getName() + " - " + t.getId() + " is already running in the registry");
		return false;
	}

	/** SERVER AND SITE START AND STOP METHODS **/
	public static void restart() {
		stop();
		
		// Do More stuff
		
		start();
	}

	public static boolean start() {
		if (status != ServerStatus.Stopped) {
			triggerInternalError("[start-server] Server Status is currently: " + status + ".  Must be stopped in order to start");
			return false;
		}
		boolean errors = false;
		setStatus(ServerStatus.Starting);
		errors = errors || !Config.load();
		for (Site s : sites) {
			errors = errors || !start(s);
		}
		if (errors) { 
			status = ServerStatus.Error;
			return false;
		}
		setStatus(ServerStatus.Started);
		return true;
	}

	public static boolean stop() {
		if (status != ServerStatus.Started) {
			triggerInternalError("[start-server] Server Status is currently: " + status + ".  Must be started in order to stop");
			return false;
		}
		setStatus(ServerStatus.Stopping);
		boolean errors = false;
		
		for (Site s : sites) {
			errors = errors || !stop(s);
		}
		if (errors) {
			status = ServerStatus.Error;
			return false;
		}
		setStatus(ServerStatus.Stopped);
		return true;
	}

	// restarts the site
	public static void restart(Site site) {
		if (stop(site))
			if (start(site))
				triggerInternalError("[Restart]: Failed to ReStart Site " + site.getName());
		else
			triggerInternalError("[Restart]: Failed to Stop Site " + site.getName());
	}

	public static boolean start(Site site) {
		
		if (site.getStatus() != ServerStatus.Stopped) {
			triggerInternalError("[Start] Didn't start site " + site.getName() + " because it is currently " + site.getStatus());
			return false;
		}
		
		site.setStatus(ServerStatus.Starting);
		
		boolean errors = false;
		for (Binding siteBinding : site.getBindings()) {
			try {
				ThreadedSocket siteSocket = null;

				//Do we need to bind a new socket
				for (ThreadedSocket socket : sockets) {
					if (socket.getLocalPort() == siteBinding.getPort()) {
						siteSocket = socket;
						logInfo("Found Existing Socket for site " + site.getName() + " on port " + siteBinding.getPort());
						logDebug("Site " + site.getName() + " is bound to port " + siteBinding.getPort());

					}
				}

				
				// We need to create a new socket and thread
				if (siteSocket == null) {
					try {
						//Create the server socket
						siteSocket = new ThreadedSocket(siteBinding.getPort());
					} catch (Exception e) {
						triggerInternalError("Could Not Bind siteSocket for " + site.getName() + " to port " + siteBinding.getPort());
						errors = true;
						continue;
					}
					sockets.add(siteSocket);
					
					logDebug("Site " + site.getName() + " is bound to port " + siteBinding.getPort());


					//Create thread for the socket
					Thread socketThread = new Thread(siteSocket);

					logDebug("Starting New Thread For Socket on port " + siteSocket.getLocalPort() );

					//Register and Start thread
					registerThread(socketThread,siteSocket);   
				}
				
			} catch (Exception e) {
				triggerInternalError("Failed to Start " + site.getName());
				errors = true;
			}
		}
		if (!errors) {
			logDebug("Registered All Threads for Site " + site.getName());
			site.setStatus(ServerStatus.Started);
			return true;
		}
		site.setStatus(ServerStatus.Error);
		return false;
		
	}

	public static boolean stop(Site s) {
		if (s.getStatus() != ServerStatus.Started) {
			return false;
		}
		int attemptClosings = 0;
		s.setStatus(ServerStatus.Stopping);
		ArrayList<String> errors = new ArrayList<String>();
		for (Binding b : s.getBindings()) {
			if (getSitesForPort(b.getPort()).size() == 1) {
				attemptClosings++;
				ThreadedSocket ts = getSocketForPort(b.getPort());
				try {
					ts.close();
				} catch (IOException e) {
					errors.add("[Stop]: Failed to close Socket on port " + ts.getLocalPort());
				}
			}
		}
		
		if (errors.size() == 0) {
			s.setStatus(ServerStatus.Stopped);
			return true;
		}
		for (String e : errors)
			triggerInternalError(e);
		
		// If no sockets were closed mark the site as running still, else set state to error
		if (errors.size() == attemptClosings)
			s.setStatus(ServerStatus.Started);
		else
			s.setStatus(ServerStatus.Error);
		
		return false;
		
	}

	/** SITE BINDINGS **/

	public static ThreadedSocket getSocketForPort(int port) {
		for (ThreadedSocket s : sockets) {
			if (s.getLocalPort() == port)
				return s;
		}
		return null; 
	}
	
	public static ArrayList<Site> getSitesForPort(int port) {
		ArrayList<Site> returns = new ArrayList<Site>();
		for (Site s : sites) {
			for (Binding b : s.getBindings()) {
				if (b.getPort() == port) {
					returns.add(s);
				}
			}
		}
		return returns;
	}
	
	public static Thread getThreadForPort(int port) {
		Thread t = null;
		
		Iterator it = threadRegistry.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Thread,Runnable> pair = (Map.Entry<Thread,Runnable>)it.next();
	        
	        for (ThreadedSocket socket : sockets) {
	        	if (pair.getValue() == socket) {
        			t = pair.getKey();
        			break;
	        	}
	        }
	        it.remove(); 
	    }
		return t;
	}
	
	
	public static Site matchSite(String protocol, String host, int port) {
		protocol = protocol.substring(0,protocol.indexOf("/"));
		
		int indx = host.indexOf(':');
		if (indx > 0) {
			port = Integer.valueOf(host.substring(indx + 1));
			host = host.substring(0, indx);
		}
		
		// String to be scanned to find the pattern.

			for (Site s : sites) {
				if (s.getStatus() == ServerStatus.Started) {
					for (Binding b : s.getBindings()) {
						if (b.getProtocol().equals(protocol) && (b.getDomain().equals("*") || b.getDomain().equals(host)) && port == b.getPort()) {
							return s;
						}
					}
				}
			}
		
		return null;
	}

	
	/** ERROR HANDLING AND LOGGING METHODS **/
	
	public static void triggerInternalError(String message) {
		SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		System.err.println("[" + timestamp.format(new Date()) + "] " + message);
	}
	
	public static void logDebug(String message) {
		if (DEBUG)
			System.out.println(message);
	}
	
	public static void logInfo(String message) {
		System.out.println(message);
	}

	@Override
	public boolean configure() {
		// TODO Auto-generated method stub
		return false;
	}
}

