package jServe.Core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;


public class Binding {
    
    public Binding() {}
    
    public Binding(String protocol, String domain, int port) {
        this.protocol = protocol;
        this.domain = domain;
        this.port = port;
    }
    
    public Binding(String protocol, String domain, int port, String localIP, String remoteIP) {
        this.protocol = protocol;
        this.domain = domain;
        this.port = port;
        
        // local and remote IPs not implemented yet
    }
    
    private int port;
    
    private String domain;

    private String protocol;
    
    private String localIP;
    
    private String remoteIP;
    
    
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }
    
    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }
    
    // Checks to make sure that the specified bindings do not conflict.  Includes testing for wildcard (*) domains
    public static boolean isValid(ArrayList<Binding> bindings) {
        WebServer.logDebug("Called isValid with " + bindings.toString());
        HashMap<String, Integer> CompletelyReservedPorts = new HashMap<String, Integer>();
        for (int i = 0; i < bindings.size(); i++) {
            
            if (bindings.get(i).domain.equals("*")) {
                for (int k = 0; k < i; k++) {
                    if (bindings.get(i).protocol.equals(bindings.get(k).protocol) && bindings.get(i).port == bindings.get(k).port) {
                        WebServer.triggerInternalError("Duplicate Bindings found: " + bindings.get(i).protocol + "://"+ bindings.get(i).domain + ":" + bindings.get(i).port + " and " + bindings.get(k).protocol + "://" + bindings.get(k).domain + ":" + bindings.get(k).port);
                        return false;
                    }
                }
                CompletelyReservedPorts.put(bindings.get(i).protocol, bindings.get(i).port);
            }
            for (int j = i + 1; j < bindings.size(); j++) {

                if (bindings.get(i).protocol.equals(bindings.get(j).protocol) && bindings.get(i).domain.equals(bindings.get(j).domain) && bindings.get(i).port == bindings.get(j).port) {
                    WebServer.triggerInternalError("Duplicate Bindings found: " + bindings.get(i).protocol + "://"+ bindings.get(i).domain + ":" + bindings.get(i).port + " and " + bindings.get(j).protocol + "://" + bindings.get(j).domain + ":" + bindings.get(j).port);
                    return false;
                }
            }
        }
            
        return true;
    }


}
