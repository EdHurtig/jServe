package jServe.Core;

import jServe.Sites.Site;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Main Web Server Class for the entire jServe.
 * 
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Aug 13, 2014
 */
public class WebServer implements Configurable {

    /**
     * Debug Mode constant
     */
    public static boolean DEBUG = true; // TODO: change to false for production

    /**
     * Registry of all the sites that are currently loaded
     */
    public static ArrayList<Site> sites = new ArrayList<Site>();

    /**
     * The ID of the last Request. Counter that keeps track of requests
     */
    public static int lastRequestID = 1;

    /**
     * The Current status of the Server
     */
    private static ServerStatus status = ServerStatus.Stopped;

    /**
     * Default Output stream for the Web Server
     */
    public static PrintStream outputStream = System.out;

    /**
     * Default Input Stream for the Web Server
     */
    public static InputStream inputStream = System.in;

    /**
     * Default Error Stream for the Web Server
     */
    public static PrintStream errorStream = System.err;

    /**
     * Registry of the time taken for each request
     */
    public static HashMap<Integer, Double> requestTimes = new HashMap<Integer, Double>();

    /**
     * Server Variables
     * 
     * The name of the Server
     */
    public static String server_name = null;

    public static String server_hostname = null;

    public static ArrayList<ThreadedSocket> sockets = new ArrayList<ThreadedSocket>();

    public static HashMap<Thread, Runnable> threadRegistry = new HashMap<Thread, Runnable>();

    /**
     * The Command Line Handler Object
     */
    public static CommandLine COMMAND_LINE = new CommandLine();

    /**
     * Crash the server when triggerInternalError is called
     */
    private static boolean crashOnInternalError = true;

    /** GETTERS/SETTERS **/
    public static ServerStatus getStatus() {
        return status;
    }

    public static void setStatus(ServerStatus newStatus) {
        logInfo("Setting Server Status to " + newStatus.toString());
        status = newStatus;

        if (status == ServerStatus.Error) {
            stop();
            triggerInternalError("Server Status was set to Error: Exiting");
        }

    }

    /** MAIN **/
    public static void main(String[] rawargs) {

        List<String> args = Arrays.asList(rawargs);
        if (args.contains("debug")) {
            DEBUG = true;
        }

        if (args.size() > 0) {
            if (args.get(0).startsWith("--config=")) {

                String config_file = args.get(0);
                Config.CONFIG_FILE = config_file.substring(9);
                logInfo("Config file overridden to " + Config.CONFIG_FILE);
            }

            if (args.get(0).startsWith("--listdir=")) {
                logDebug(Utils.join("\n", new File(args.get(0).substring(10)).listFiles()));
            }
        }

        logInfo("Starting Server");

        init();

        start();

        Plugins.do_action("init");

        logInfo("--- SERVER IS UP AND RUNNING ---");

        logInfo("--- COMMAND LINE OPEN AND READY ---");

        COMMAND_LINE.start();

    }

    public static boolean registerThread(Thread t) {
        return registerThread(t, null);
    }

    public static boolean registerThread(Thread t, Runnable target) {
        if ( ! threadRegistry.containsKey(t) || t.getState() == Thread.State.TERMINATED) {
            threadRegistry.put(t, target);
            try {
                t.start();
            }
            catch (IllegalThreadStateException e) {
                triggerInternalError("Failed to start thread: " + t.getName() + " - " + t.getId());
                return false;
            }
            return true;
        }
        logDebug("Thread " + t.getName() + " - " + t.getId() + " is already running in the registry");
        return false;
    }

    public static void init() {
        ArrayList<String> jsmime = new ArrayList<String>();
        jsmime.add("text");
        jsmime.add("javascript");
        MIME.registerMIME("js", jsmime);

        ArrayList<String> cssmime = new ArrayList<String>();
        cssmime.add("text");
        cssmime.add("css");
        MIME.registerMIME("css", cssmime);

        ArrayList<String> htmlmime = new ArrayList<String>();
        htmlmime.add("text");
        htmlmime.add("html");
        MIME.registerMIME("html", htmlmime);

    }

    /** SERVER AND SITE START AND STOP METHODS **/
    /**
    * 
    */
    public static boolean restart() {

        for (Site s : sites) {
            if (s.isStarted()) {
                s.setStatus(ServerStatus.Restarting);
            }
        }

        boolean stopped = stop();

        // Do More stuff
        Config.reload();
        if (stopped) {
            return start();
        }

        triggerInternalError("[restart-server] Server Failed to restart because stopping failed");
        return false;
    }

    /**
     * Starts the entire Server
     * 
     * @return Boolean
     */
    public static boolean start() {
        if (status != ServerStatus.Stopped) {
            triggerInternalError("[start-server] Server Status is currently: " + status
                                 + ".  Must be stopped in order to start");
            return false;
        }
        boolean errors = false;
        setStatus(ServerStatus.Starting);
        errors = errors || ! Config.load();

        for (Site s : sites) {
            if (Config.shouldStartSite(s)) {
                errors = errors || ! start(s);
            }
        }
        if (errors) {
            setStatus(ServerStatus.Error);
            return false;
        }
        setStatus(ServerStatus.Started);
        return true;
    }

    /**
     * Stops the entire server and all sites
     * 
     * @return Whether the stop was successfull
     */
    public static boolean stop() {
        if (status != ServerStatus.Started) {
            triggerInternalError("[start-server] Server Status is currently: " + status
                                 + ".  Must be started in order to stop");
            return false;
        }
        setStatus(ServerStatus.Stopping);
        boolean errors = false;

        Config.saveSiteStates();

        for (Site s : sites) {
            errors = errors || ! stop(s);
        }

        if (errors) {
            status = ServerStatus.Error;
            return false;
        }

        setStatus(ServerStatus.Stopped);
        return true;
    }

    public static void restart(Site site) {
        if (stop(site)) {
            if (start(site)) {
                triggerInternalError("[Restart]: Failed to ReStart Site " + site.getName());
            }
            else {
                triggerInternalError("[Restart]: Failed to Stop Site " + site.getName());
            }
        }
    }

    public static boolean start(Site site) {

        if (site.getStatus() != ServerStatus.Stopped) {
            triggerInternalError("[Start] Didn't start site " + site.getName() + " because it is currently "
                                 + site.getStatus());
            return false;
        }

        site.setStatus(ServerStatus.Starting);

        boolean errors = false;
        for (Binding siteBinding : site.getBindings()) {
            try {
                ThreadedSocket siteSocket = null;

                // Do we need to bind a new socket
                for (ThreadedSocket socket : sockets) {
                    if (socket.getLocalPort() == siteBinding.getPort()) {
                        siteSocket = socket;
                        logInfo("Found Existing Socket for site " + site.getName() + " on port "
                                + siteBinding.getPort());
                        logDebug("Site " + site.getName() + " is bound to port " + siteBinding.getPort());

                    }
                }

                // We need to create a new socket and thread
                if (siteSocket == null) {
                    try {
                        // Create the server socket
                        siteSocket = new ThreadedSocket(siteBinding.getPort());
                    }
                    catch (Exception e) {
                        triggerInternalError("Could Not Bind siteSocket for " + site.getName() + " to port "
                                             + siteBinding.getPort());
                        errors = true;
                        continue;
                    }
                    sockets.add(siteSocket);

                    logDebug("Site " + site.getName() + " is bound to port " + siteBinding.getPort());

                    // Create thread for the socket
                    Thread socketThread = new Thread(siteSocket);

                    logDebug("Starting New Thread For Socket on port " + siteSocket.getLocalPort());

                    // Register and Start thread
                    registerThread(socketThread, siteSocket);
                }

            }
            catch (Exception e) {
                triggerInternalError("Failed to Start " + site.getName());
                errors = true;
            }
        }
        if ( ! errors) {
            logDebug("Registered All Threads for Site " + site.getName());
            site.setStatus(ServerStatus.Started);
            return true;
        }
        site.setStatus(ServerStatus.Error);

        stop(site);

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
                }
                catch (IOException e) {
                    errors.add("[Stop]: Failed to close Socket on port " + ts.getLocalPort());
                }
            }
        }

        if (errors.size() == 0) {
            s.setStatus(ServerStatus.Stopped);
            logInfo("Stopped Site " + s.getName());
            return true;
        }

        for (String e : errors) {
            triggerInternalError(e);
        }

        // If no sockets were closed mark the site as running still, else set
        // state to error
        if (errors.size() == attemptClosings) {
            s.setStatus(ServerStatus.Started);
        }
        else {
            s.setStatus(ServerStatus.Error);
        }

        return false;

    }

    /** SITE BINDINGS **/

    public static ThreadedSocket getSocketForPort(int port) {
        for (ThreadedSocket s : sockets) {
            if (s.getLocalPort() == port) {
                return s;
            }
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

    public static Site getSiteByID(int id) {
        ArrayList<Site> returns = new ArrayList<Site>();
        for (Site s : sites) {
            if (s.getID() == id) {
                return s;
            }
        }
        return null;
    }

    public static Thread getThreadForPort(int port) {
        Thread t = null;

        Iterator it = threadRegistry.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Thread, Runnable> pair = (Map.Entry<Thread, Runnable>) it.next();

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
        protocol = protocol.substring(0, protocol.indexOf("/"));

        int indx = host.indexOf(':');
        if (indx > 0) {
            port = Integer.valueOf(host.substring(indx + 1));
            host = host.substring(0, indx);
        }

        // String to be scanned to find the pattern.

        for (Site s : sites) {
            if (s.getStatus() == ServerStatus.Started) {
                for (Binding b : s.getBindings()) {
                    if (b.getProtocol().equals(protocol) && (b.getDomain().equals("*") || b.getDomain().equals(host))
                        && port == b.getPort()) {
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
        if (errorStream != null) {
            errorStream.println("[" + timestamp.format(new Date()) + "] " + message);

        }
        if (crashOnInternalError) {
            timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            errorStream.println("[" + timestamp.format(new Date()) + "] [Core] FATAL ERROR! Bringing down the server!");
            status = ServerStatus.Error;
            try {
                for (Thread t : threadRegistry.keySet()) {
                    t.interrupt();
                }
                timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                errorStream.println("[" + timestamp.format(new Date()) + "] [Core] ");
            }
            catch (Exception e) {

            }

            new Throwable().printStackTrace();
            System.exit(1);
        }
    }

    public static void triggerPluggableError(String message) {
        if (errorStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            errorStream.println("[pluggable-error] [" + timestamp.format(new Date()) + "] " + message);
        }
    }

    public static void logDebug(String message) {
        if (DEBUG && outputStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            outputStream.println('[' + timestamp.format(new Date()) + "] " + message);
        }
    }

    public static void logInfo(String message) {
        if (outputStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            outputStream.println('[' + timestamp.format(new Date()) + "] " + message);
        }
    }

    /**
     * Returns an array of all the ids of registered sites in the order in which
     * they were registered
     * 
     * @return An ArrayList of the Site IDs
     */
    public static ArrayList<Integer> getSiteIDs() {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for (Site s : sites) {
            ids.add(s.getID());
        }

        return ids;
    }

    @Override
    public boolean configure() {
        // TODO Auto-generated method stub
        return false;
    }

    public static void logError(String message) {
        if (outputStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            outputStream.println('[' + timestamp.format(new Date()) + "] " + message);
        }
    }
}
