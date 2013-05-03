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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebServer {
	public static boolean DEBUG = true;  //TODO: change to false for production
	public static ArrayList<Site> sites = new ArrayList<Site>();

	public static void main(String[] rawargs) {
		List<String> args = Arrays.asList(rawargs);
		if (args.contains("debug"))
			DEBUG = true;

		

		GenericSite example = new GenericSite(1,"Example Site");
		ArrayList<Binding> exampleBindings = new ArrayList<Binding>();
		exampleBindings.add(new Binding("HTTP","*",8888));
		exampleBindings.add(new Binding("HTTP","localhost",8889));
		exampleBindings.add(new Binding("HTTP","localhost2",8889));

		example.setBindings(exampleBindings);

		example.getSettings().put("DOCUMENT_ROOT", "/Users/ehurtig/desktop/bootstrap-master-2/docs/");
		example.getSettings().put("DefaultDocuments", "index.php,index.html");
		
		sites.add(example);
		logInfo("Starting Server");
		start();
		logInfo("Starting All Sites on Server");

		for (Site site : sites)
			start(site);
		logInfo("---END OF MAIN()---");
	}
	
	public static int lastRequestID = 1;
	public static String status = "started";

	public static double[] requestTimes = new double[50000];
	public static int nRequestTimes = 0;
	public static ArrayList<ThreadedSocket> sockets = new ArrayList<ThreadedSocket>();
	// Hashmap of <SiteName, Bindings for site>



	// restarts the entire server
	public static void restart() {

	}

	public static boolean start() {
		Config.load();
		status = "started";
		return true;
	}

	public static void stop() {

	}

	// restarts the site
	public static void restart(Site site) {

	}

	public static void start(Site site) {
		for (Binding siteBinding : site.getBindings()) {
			try {
				ThreadedSocket siteSocket = null;

				//Do we need to bind a new socket
				for (ThreadedSocket socket : sockets) {
					if (socket.getLocalPort() == siteBinding.getPort()) {
						siteSocket = socket;
						if (DEBUG) logInfo("Found Existing Socket for site " + site.getName() + " on port " + siteBinding.getPort());
					}
				}

				// We need to create a new socket
				if (siteSocket == null) {
					try {
						//Create the server socket
						siteSocket = new ThreadedSocket(siteBinding.getPort());
					} catch (Exception e) {
						triggerInternalError("Could Not Bind siteSocket for " + site.getName() + " to port " + siteBinding.getPort());
						continue;
					}
					sockets.add(siteSocket);
				}

				logDebug("Site " + site.getName() + " is bound to port " + siteBinding.getPort());


				//Create thread for the socket
				Thread socketThread = new Thread(siteSocket);

				logDebug("Starting New Thread For Socket on port " + siteSocket.getLocalPort() );

				//Start thread
				socketThread.start();    
			} catch (Exception e) {
				triggerInternalError("Failed to Start " + site.getName());
			}
		}
	}

	public static void stop(Site s) {

	}

	public static void triggerInternalError(String message) {
		SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		System.err.println("[" + timestamp.format(new Date()) + "] " + message);
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
				for (Binding b : s.getBindings()) {
					if (b.getProtocol().equals(protocol) && (b.getDomain().equals("*") || b.getDomain().equals(host)) && port == b.getPort()) {
						return s;
					}
				}
			}
		
		return null;
	}

	public static void logDebug(String message) {
		if (DEBUG)
			System.out.println(message);
	}
	
	public static void logInfo(String message) {
		System.out.println(message);
	}
}

