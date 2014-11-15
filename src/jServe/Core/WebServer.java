package jServe.Core;

import jServe.Core.Configuration.Configuration;
import jServe.Core.Configuration.ConfigurationManager;
import jServe.Core.Configuration.Configurable;
import jServe.Core.Exceptions.JServeException;
import jServe.Sites.Site;
import org.apache.commons.lang.StringUtils;

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
 * <p/>
 * Contains the main() function for the server
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
     * <p/>
     * The name of the Server
     */
    public static String server_name = null;

    /**
     * The hostname of the server... a unique fqdn identifier for the server
     */
    public static String server_hostname = null;

    /**
     * List of Sockets that this server is using
     */
    public static ArrayList<ThreadedSocket> sockets = new ArrayList<ThreadedSocket>();

    /**
     * Threads that are currently running
     */
    public static HashMap<Thread, Runnable> threadRegistry = new HashMap<Thread, Runnable>();

    /**
     * The Command Line Handler Object
     */
    public static CommandLine COMMAND_LINE = new CommandLine();

    /**
     * Crash the server when triggerInternalError is called
     */
    private static boolean crashOnInternalError = true;

    /**
     * MAIN *
     */
    public static void main(String[] rawargs) {

        List<String> args = Arrays.asList(rawargs);
        if (args.contains("debug")) {
            DEBUG = true;
        }

        if (args.size() > 0) {
            if (args.get(0).startsWith("--config=")) {

                String config_file = args.get(0);
                ConfigurationManager.CONFIG_FILE = config_file.substring(9);
                logInfo("Config file overridden to " + ConfigurationManager.CONFIG_FILE);
            }

            if (args.get(0).startsWith("--listdir=")) {
                logDebug(StringUtils.join(new File(args.get(0).substring(10)).listFiles(), '\n'));
            }
        }

        logInfo("Starting Server");

        init();

        start();

        Plugins.do_action("init");

        logInfo("--- SERVER IS UP AND RUNNING ---");

        COMMAND_LINE.start();

    }

    /**
     * Registers a Thread without a Runnable
     *
     * @param t The Thread
     * @return Whether the thread was registered successfully
     */
    public static boolean registerThread(Thread t) {
        return registerThread(t, null);
    }

    /**
     * Registers the given thread with a runnable
     *
     * @param t      The Thread
     * @param target The Runnable
     * @return Whether the thread was registered successfully
     */
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

    /**
     * Initializes the Server
     */
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
     * Restarts the entire server. Stops all sites, reloads config, starts sites again
     *
     * @throws jServe.Core.Exceptions.JServeException
     * @throws jServe.Core.JServeError
     */
    public static void restart() {

        for (Site s : sites) {
            if (s.isStarted()) {
                s.setStatus(ServerStatus.Restarting);
            }
        }

        stop();

        ConfigurationManager.reload();

        start();
    }

    /**
     * Starts the entire Server
     *
     * @throws jServe.Core.Exceptions.JServeException
     * @throws jServe.Core.JServeError
     */
    public static void start() {
        if (status != ServerStatus.Stopped) {
            triggerInternalError("[start-server] Server Status is currently: " + status
                    + ".  Must be stopped in order to start");
            throw new JServeException("Server already running");
        }

        JServeError errors = new JServeError();
        WebServer.setStatus(ServerStatus.Starting);

        try {
            ConfigurationManager.load();
        } catch (Exception e) {
            WebServer.setStatus(ServerStatus.Error);
            errors.add(e);
            throw errors;
        }

        for (Site s : sites) {
            try {
                if (ConfigurationManager.shouldStartSite(s)) {
                    WebServer.start(s);
                }
            } catch (Exception e) {
                errors.add(e);
            }
        }

        if (errors.ok()) {
            setStatus(ServerStatus.Started);
        } else {
            setStatus(ServerStatus.Error);
            throw errors;
        }
    }

    /**
     * Stops the entire server and all sites
     *
     * @throws jServe.Core.Exceptions.JServeException
     * @throws jServe.Core.JServeError
     */
    public static void stop() {
        if (status != ServerStatus.Started) {
            triggerInternalError("[start-server] Server Status is currently: " + status
                    + ".  Must be started in order to stop");
            throw new JServeException("Server not running");
        }
        setStatus(ServerStatus.Stopping);
        JServeError errors = new JServeError();

        ConfigurationManager.saveSiteStates();

        for (Site s : sites) {
            try {
                stop(s);
            } catch (Exception e) {
                errors.add(e);
            }
        }

        // Check if there was an error
        if (errors.ok()) {
            setStatus(ServerStatus.Stopped);
        } else {
            setStatus(ServerStatus.Error);
            throw errors;
        }
    }

    /**
     * Restarts the specified Site
     *
     * @param site the Site to restart
     * @throws jServe.Core.Exceptions.JServeException
     * @throws jServe.Core.JServeError
     */
    public static void restart(Site site) {
        try {
            stop(site);
            start(site);
        } catch (JServeException e) {
            triggerInternalError("[Restart]: Failed to Restart Site " + site.getName());

            throw e;
        }
    }

    /**
     * Starts the specified site
     *
     * @param site The site to start
     * @throws jServe.Core.Exceptions.JServeException
     * @throws jServe.Core.JServeError
     */
    public static void start(Site site) {

        if (site.getStatus() != ServerStatus.Stopped) {
            triggerInternalError("[Start] Didn't start site " + site.getName() + " because it is currently "
                    + site.getStatus());
            throw new JServeException("Site is not stopped");
        }

        site.setStatus(ServerStatus.Starting);

        JServeError errors = new JServeError();
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
                    } catch (Exception e) {
                        triggerInternalError("Could Not Bind siteSocket for " + site.getName() + " to port "
                                + siteBinding.getPort());
                        errors.add(e);
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

            } catch (Exception e) {
                triggerInternalError("Failed to Start " + site.getName());
                errors.add(e);
            }
        }
        if (errors.ok()) {
            logDebug("Registered All Threads for Site " + site.getName());
            site.setStatus(ServerStatus.Started);
        } else {
            site.setStatus(ServerStatus.Error);

            try {
                stop(site);
            } catch (Exception e) {
                errors.add(e);
                throw errors;
            }
        }
    }

    /**
     * Stops the specified Site
     *
     * @param s The site to stop
     * @throws jServe.Core.Exceptions.JServeException
     * @throws jServe.Core.JServeError
     */
    public static void stop(Site s) {
        if (s.getStatus() != ServerStatus.Started) {
            // Don't Try to Stop a site that is not running
            throw new JServeException("Site is not running");
        }

        // Set status to Stopping
        s.setStatus(ServerStatus.Stopping);

        JServeError errors = new JServeError();

        for (Binding b : s.getBindings()) {

            if (getSitesForPort(b.getPort()).size() == 0) {

                ThreadedSocket ts = getSocketForPort(b.getPort());

                try {
                    ts.close();
                } catch (IOException e) {
                    errors.add(e);
                }
            }
        }

        if (errors.ok()) {

            // Set the Site status to Stopped
            s.setStatus(ServerStatus.Stopped);

            logInfo("Stopped Site " + s.getName());

        } else {

            // Print out the error messages
            for (String m : errors.getMessages()) {
                logError(m);
            }

            // Set the Status of the site to Error
            s.setStatus(ServerStatus.Error);
            throw errors;
        }


    }

    /**
     * SITE BINDINGS *
     */

    /**
     * Gets the ThreadedSocket that is listening on the specified port
     *
     * @param port The Port Number
     * @return The ThreadedSocket that is listening on the given port
     */
    public static ThreadedSocket getSocketForPort(int port) {
        for (ThreadedSocket s : sockets) {
            if (s.getLocalPort() == port) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns a list of all the sites that are started and that are listening on the given port
     *
     * @param port The Port Number
     * @return A List of sites that are actively registered on a given port
     */
    public static ArrayList<Site> getSitesForPort(int port) {
        ArrayList<Site> returns = new ArrayList<Site>();
        for (Site s : sites) {
            if (s.getStatus().equals(ServerStatus.Started)) {
                for (Binding b : s.getBindings()) {
                    if (b.getPort() == port) {
                        returns.add(s);
                    }
                }
            }
        }
        return returns;
    }

    /**
     * Gets the site registered that has the given ID
     *
     * @param id The ID of the site you want
     * @return The Site object with the given ID
     */
    public static Site getSiteByID(int id) {
        ArrayList<Site> returns = new ArrayList<Site>();
        for (Site s : sites) {
            if (s.getID() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Gets the Thread object that was used to launch a ThreadedSocket object
     * which is listening on the given port number
     *
     * @param port The port number
     * @return The Thread object that is running the ThreadedSocket for the given port
     */
    public static Thread getThreadForPort(int port) {
        Thread t = null;

        Iterator it = threadRegistry.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Thread, Runnable> pair = (Map.Entry<Thread, Runnable>) it.next();

            for (ThreadedSocket socket : WebServer.sockets) {
                if (pair.getValue().equals(socket)) {
                    t = pair.getKey();
                    break;
                }
            }
            it.remove();
        }
        return t;
    }

    /**
     * Given the Protocol, Host, and port number, determine the site that is bound with that information
     *
     * @param protocol The protocol (HTTP, HTTPS)
     * @param host     The Host header.  An IP Address, fqdn, or hostname i.e. localhost, 192.168.1.100, or example.com
     * @param port     The Port number the request came in on
     * @return The Site that matches the criteria
     */
    public static Site matchSite(String protocol, String host, int port) {
        // This shouldn't happen
        protocol = protocol.substring(0, protocol.indexOf("/"));

        // If the host argument has a port in it
        int indx = host.indexOf(':');
        if (indx > 0) {
            port = Integer.valueOf(host.substring(indx + 1));
            host = host.substring(0, indx);
        }


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

    /**
     * ERROR HANDLING AND LOGGING METHODS *
     */

    /**
     * ------ In Case of Emergency ------
     * --- Break Server and Log Error ---
     * ----------------------------------
     * Logs an error message to the console and halts the server if WebServer.crashOnInternalError
     * is set to true
     *
     * @param message The message to print to the console
     */
    public static void triggerInternalError(String message) {
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        if (errorStream != null) {
            // Print message
            errorStream.println("[" + timestamp.format(new Date()) + "] " + message);
        }

        // Only crash if this setting is true
        if (crashOnInternalError) {

            if (errorStream != null) {
                // Generate new Timestamp
                timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                // Print the shutdown message
                errorStream.println("[" + timestamp.format(new Date()) + "] [Core] FATAL ERROR! Bringing down the server!");
            }

            // Erring out
            WebServer.status = ServerStatus.Error;

            // Kill all the threads so that we don't have zombies
            try {
                for (Thread t : threadRegistry.keySet()) {
                    t.interrupt();
                }
                if (errorStream != null) {
                    timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    errorStream.println("[" + timestamp.format(new Date()) + "] [Core] ");
                }
            } catch (Exception e) {

            }
            // Print the stack trace that led to this predicament
            new Throwable().printStackTrace();
            // Shutdown
            System.exit(1);
        }
    }

    /**
     * For Less serious errors.  Prints an error message to the console
     *
     * @param message THe message to print
     */
    public static void triggerPluggableError(String message) {
        if (errorStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            errorStream.println("[pluggable-error] [" + timestamp.format(new Date()) + "] " + message);
        }
    }

    /**
     * Logs debugging information to the console
     *
     * @param message the message to print
     */
    public static void logDebug(String message) {
        if (DEBUG && outputStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            outputStream.println('[' + timestamp.format(new Date()) + "] " + message);
        }
    }

    /**
     * Logs some informational information to the console
     *
     * @param message the message to print
     */
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

    /**
     * Configures this class
     *
     * @param c the full Configuration
     * @return whether configuration was successful
     */
    @Override
    public boolean configure(Configuration c) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Logs a generic Error to the console
     *
     * @param message The Error message to print
     */
    public static void logError(String message) {
        if (outputStream != null) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            outputStream.println('[' + timestamp.format(new Date()) + "] " + message);
        }
    }

    /**
     * GETTERS/SETTERS *
     */
    public static ServerStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the server
     *
     * @param newStatus THe new status
     */
    public static void setStatus(ServerStatus newStatus) {
        logInfo("Setting Server Status to " + newStatus.toString());
        status = newStatus;

        if (status == ServerStatus.Error) {
            stop();
            triggerInternalError("Server Status was set to Error: Exiting");
        }

    }
}
