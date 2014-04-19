package jServe.Sites;
import jServe.Core.Binding;
import jServe.Core.Config;
import jServe.Core.Configurable;
import jServe.Core.Plugins;
import jServe.Core.Request;
import jServe.Core.ServerStatus;
import jServe.Core.WebServer;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Site implements Runnable, Configurable {
    public Site() {}
    public Site(int id, String name) {
        setName(name);
        ID = id;
    }
    
    /** PROPERTIES **/
    private int ID;  
    private String name;
    private ServerStatus status = ServerStatus.Stopped;
    private HashMap<String, String> settings = new HashMap<String,String>();
    private ArrayList<Binding> bindings = new ArrayList<Binding>();
    
    private ArrayList<String> defaultDocuments = new ArrayList<String>();
    
    
    /** GETTERS/SETTERS **/
    
    public int getID() {
        return ID;
    }
    
    public String getName() {
        return name;
    }

    public boolean setName(String name) {
        if (Config.add("sites." + getID() + ".name", this.getName()))
        {
            this.name = name;
            return true;
        }
        return false;
    }
    
    public ArrayList<Binding> getBindings() {
        return bindings;
    }
    
    public boolean setBindings(ArrayList<Binding> bindings) {
        if (Config.addBindings(bindings,this)) {
            this.bindings = bindings;
            return true;
        }
        return false;
    }
    
    public HashMap<String, String> getSettings() {
        return settings;
    }
    
    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }
    
    public ServerStatus getStatus() {
        return status;
    }
    
    public void setStatus(ServerStatus status) {
        this.status = status;
    }
    
    public ArrayList<String> getDefaultDocuments() {
        return defaultDocuments;
    }
    
    public void setDefaultDocuments(ArrayList<String> defaultDocuments) {
        if (!defaultDocuments.contains(""))
            defaultDocuments.add(0, "");
        
        this.defaultDocuments = defaultDocuments;
    }
    
    
    /** METHODS **/
    
    /**
     * Runs on site startup and thread creation
     */
    public void run() {
        onStart();
    };
    
    
    /**
     * Method that runs when the site starts up
     *         called directly by run() from Runnable
     */
    public abstract void onStart();
    
    
    /**
     * Runs the site with the given Request
     * @param r
     */
    public abstract void run(Request r);
    
    
    public void destroy() {
        Plugins.do_action("site_destroy", this);
    }
    
    /** ERROR HANDLING API FRAMEWORK **/
    
    /**
     * @overload
     * @param code
     */
    public void triggerHTTPError(int code) {
        triggerHTTPError((double)code,(String)null);
    }
    /**
     * @overload
     * @param code
     * @param message
     */
    public void triggerHTTPError(int code,String message) {
        triggerHTTPError((double)code,message);
    }
    /**
     * @overload
     * @param code
     * @param ex
     */
    public void triggerHTTPError(int code,Exception ex) {
        triggerHTTPError((double)code,ex);
    }
    /**
     * @overload
     * @param code
     */
    public void triggerHTTPError(double code) {
        triggerHTTPError(code,(String)null);
    }
    /**
     * @overload
     * @param code
     * @param message
     */
    public void triggerHTTPError(double code,String message) {
        triggerHTTPError(code,message,null);
    }
    /**
     * API for HTTP Errors.
     * @overload
     * @param code
     * @param e
     */
    public void triggerHTTPError(double code,Exception e) {
        triggerHTTPError(code,null,e);
    }
    /**
     * API for HTTP Errors. [Overload Master]
     * @overload master
     * @param code
     * @param message
     * @param e
     */
    public void triggerHTTPError(double code, String message, Exception e) {
        Plugins.do_action("http_error_" + code, new Object[] { this, message, e });
        WebServer.logInfo("[HTTP_ERROR] Site: " + getID() + " HTTP/" + code + ": " + message + " Exception: " + (e instanceof Exception ? e.getMessage() : "null"));
    }
    
    @Override
    public boolean configure() {
        return true;
    }
    
    /**
     * Utility Method: Determines if the Site is stopped
     * @return True if the server is stopped, otherwise False
     */
    public boolean isStopped() {
        return (status == ServerStatus.Stopped);
    }
    
    /**
     * Utility Method: Determines if the Site is started
     * @return True if the server is started, otherwise False
     */
    public boolean isStarted() {
        return (status == ServerStatus.Started);
    }
    
    /**
     * Utility Method: Determines if the Site has encountered a fatal error and stopped
     * @return True if the server is  has encountered a fatal error and stopped, otherwise False
     */
    public boolean isError() {
        return (status == ServerStatus.Error);
    }
    
    /**
     * Utility Method: Determines if the Site is in the process of stopping
     * @return True if the server is stopping, otherwise False
     */
    public boolean isStopping() {
        return (status == ServerStatus.Stopping);
    }
    
    /**
     * Utility Method: Determines if the Site is in the process of starting
     * @return True if the server is starting, otherwise False
     */
    public boolean isStarting() {
        return (status == ServerStatus.Starting);
    }
    
    /**
     * Utility Method: Determines if the Site is in the process of restarting
     * @return True if the server is restarting, otherwise False
     */
    public boolean isRestarting() {
        return (status == ServerStatus.Restarting);
    }
    
}