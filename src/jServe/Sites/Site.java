package jServe.Sites;

import jServe.Core.Binding;
import jServe.Core.Configuration.Configuration;
import jServe.Core.Configuration.ConfigurationManager;
import jServe.Core.Configuration.Configurable;
import jServe.Core.Plugins;
import jServe.Core.Request;
import jServe.Core.ServerStatus;
import jServe.Core.WebServer;
import jServe.Plugins.HTTPErrors.HTTPError;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a Site that the Server can route Requests to for
 * processing.  It provides a standard framework for every site
 * to interface with for things like:
 * - Unique Site Identification using getID() and human
 * identification with getName().
 * - Centralized Settings Hash
 * - HTTPError Handling and Responses
 * - Binding Management
 */
public abstract class Site implements Runnable, Configurable {

    /**
     * The ID of the site
     */
    private int ID;

    /**
     * The Human Recognizable name of the site. Has nothing to do with functionality
     */
    private String name;

    /**
     * The current status of the site
     */
    private ServerStatus status = ServerStatus.Stopped;

    /**
     * The Site's Centralized Settings Hash
     */
    private HashMap<String, String> settings = new HashMap<String, String>();

    /**
     * The Set of Bindings that this Site requires
     */
    private ArrayList<Binding> bindings = new ArrayList<Binding>();

    /**
     * @deprecated
     */
    private ArrayList<String> defaultDocuments = new ArrayList<String>();

    /**
     * Empty Constructor
     */
    public Site() {
    }

    /**
     * Initializes a site with an ID and a name
     *
     * @param id   The site's ID
     * @param name The site's Name
     */
    public Site(int id, String name) {
        setName(name);
        ID = id;
    }

    /**
     * Gets the ID of the site.  IDs are unique integers for each site
     *
     * @return The ID of the site
     */
    public int getID() {
        return ID;
    }

    /**
     * Gets the name of the site.  The name is for human recognition, it has no functional
     * purpose at this time.
     *
     * @return the name of the site
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the site
     *
     * @param name Thw new name of the site
     * @return Success if it was changed, false if it failed to write the config
     */
    public boolean setName(String name) {
        //TODO: This ConfigurationManager call needs some reworking
        if (ConfigurationManager.add("sites." + getID() + ".name", this.getName())) {
            this.name = name;
            return true;
        }
        return false;
    }

    /**
     * Gets the bindings for the site
     *
     * @return The current bindings for this site
     */
    public ArrayList<Binding> getBindings() {
        return bindings;
    }

    /**
     * Sets the Bindings for the site
     *
     * @param bindings The New Bindings
     * @return Success if it was changed, false if it failed to write the config
     */
    public boolean setBindings(ArrayList<Binding> bindings) {
        //TODO: ConfigurationManager
        if (ConfigurationManager.addBindings(bindings, this)) {
            this.bindings = bindings;
            return true;
        }
        return false;
    }

    /**
     * Returns the centralized Settings Hash of key-value pairs for this site
     *
     * @return the Settings Hash
     */
    public HashMap<String, String> getSettings() {
        return settings;
    }

    /**
     * Updates the Settings Hash
     *
     * @param settings The new Hash
     */
    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }

    /**
     * Gets the Status of this site (starting, started, stopping, stopped, error)
     *
     * @return The ServerStatus of this site
     */
    public ServerStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the site
     *
     * @param status The Status of the site
     */
    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    /**
     * @deprecated
     */
    public ArrayList<String> getDefaultDocuments() {
        return defaultDocuments;
    }

    public void setDefaultDocuments(ArrayList<String> defaultDocuments) {
        if (!defaultDocuments.contains("")) {
            defaultDocuments.add(0, "");
        }

        this.defaultDocuments = defaultDocuments;
    }

    /**
     * Runs on site startup and thread creation
     */
    @Override
    public void run() {
        onStart();
    }

    ;

    /**
     * Method that runs when the site starts up called directly by run() from
     * Runnable
     */
    public abstract void onStart();

    /**
     * Runs the site with the given Request
     *
     * @param r The Request
     */
    public abstract void run(Request r);

    public void destroy() {
        Plugins.do_action("site_destroy", this);
    }

    /** ERROR HANDLING API FRAMEWORK **/

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r    The Request to respond to
     * @param code The HTTP Error Code
     */
    public void triggerHTTPError(Request r, int code) {
        triggerHTTPError(r, (double) code, (String) null);
    }

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r       The Request to respond to
     * @param code    The HTTP Error Code
     * @param message The HTTP Error Message
     */
    public void triggerHTTPError(Request r, int code, String message) {
        triggerHTTPError(r, (double) code, message);
    }

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r    The Request to respond to
     * @param code The HTTP Error Code
     * @param e    The HTTPError Object
     */
    public void triggerHTTPError(Request r, int code, HTTPError e) {
        triggerHTTPError(r, (double) code, e);
    }

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r    The Request to respond to
     * @param code The HTTP Error Code
     */
    public void triggerHTTPError(Request r, double code) {
        triggerHTTPError(r, code, (String) null);
    }

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r       The Request to respond to
     * @param code    The HTTP Error Code
     * @param message The HTTP Error Message
     */
    public void triggerHTTPError(Request r, double code, String message) {
        triggerHTTPError(r, code, message, null);
    }

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r    The Request to respond to
     * @param code The HTTP Error Code
     * @param e    The HTTPError Object
     */
    public void triggerHTTPError(Request r, double code, Exception e) {
        triggerHTTPError(r, code, null, e);
    }

    /**
     * Responds to the given Request with the specified HTTP Error Response using the method
     * proffered by the user via configuration (Static File Response, Plugin Response, Redirection,
     * execute url, ect.)
     *
     * @param r       The Request to respond to
     * @param code    The HTTP Error Code
     * @param message The HTTP Error Message
     * @param e       The HTTPError Object
     */
    public void triggerHTTPError(Request r, double code, String message, Exception e) {
        Plugins.do_action("http_error_" + code, new Object[]{this, message, e});
        WebServer.logInfo("[HTTP_ERROR] Site: " + getID() + " HTTP/" + code + ": " + message + " Exception: "
                + (e instanceof Exception ? e.getMessage() : "null"));

    }

    /**
     * Configures the site
     *
     * @param c The Configuration
     * @return Whether the configuration was successfully applied
     */
    @Override
    public boolean configure(Configuration c) {
        return true;
    }


    /**
     * Utility Method: Determines if the Site is stopped
     *
     * @return True if the server is stopped, otherwise False
     */
    public boolean isStopped() {
        return (status == ServerStatus.Stopped);
    }

    /**
     * Utility Method: Determines if the Site is started
     *
     * @return True if the server is started, otherwise False
     */
    public boolean isStarted() {
        return (status == ServerStatus.Started);
    }

    /**
     * Utility Method: Determines if the Site has encountered a fatal error and
     * stopped
     *
     * @return True if the server is has encountered a fatal error and stopped,
     * otherwise False
     */
    public boolean isError() {
        return (status == ServerStatus.Error);
    }

    /**
     * Utility Method: Determines if the Site is in the process of stopping
     *
     * @return True if the server is stopping, otherwise False
     */
    public boolean isStopping() {
        return (status == ServerStatus.Stopping);
    }

    /**
     * Utility Method: Determines if the Site is in the process of starting
     *
     * @return True if the server is starting, otherwise False
     */
    public boolean isStarting() {
        return (status == ServerStatus.Starting);
    }

    /**
     * Utility Method: Determines if the Site is in the process of restarting
     *
     * @return True if the server is restarting, otherwise False
     */
    public boolean isRestarting() {
        return (status == ServerStatus.Restarting);
    }

}