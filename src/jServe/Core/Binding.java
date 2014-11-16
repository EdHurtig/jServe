package jServe.Core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the criteria that will determine which Site a Request is assigned to.
 */
public class Binding {

    /**
     * The Port number that the Request must have
     */
    private int port;

    /**
     * The Domain (Host: header) that the Request must have.  Allows the * wildcard
     */
    // TODO: Document the wildcard better (does *.example.com match example.com? www.example.com? www.beta.example.com? How about *example.com?)
    private String domain;

    /**
     * The Protocol
     */
    private String protocol;

    /**
     * The Local IP that the Request Must have come in on
     */
    private String localIP;

    /**
     * The Remote ID that the Request must have come from
     */
    private String remoteIP;

    /**
     * Empty Constructor
     */
    public Binding() {
    }

    /**
     * Basic Constructor with Protocol, domain, and port
     *
     * @param protocol The Protocol (HTTP, HTTPS)
     * @param domain   The Domain name to match (wildcard * is allowed)
     * @param port     The Port number
     */
    public Binding(String protocol, String domain, int port) {
        this.protocol = protocol;
        this.domain = domain;
        this.port = port;
    }

    /**
     * Full Constructor with Protocol, domain, and port
     *
     * @param protocol The Protocol (HTTP, HTTPS)
     * @param domain   The Domain name to match (wildcard * is allowed)
     * @param port     The Port number
     * @param localIP  The Local IP that is required
     * @param remoteIP The Remote IP that is required
     */
    public Binding(String protocol, String domain, int port, String localIP, String remoteIP) {
        this.protocol = protocol;
        this.domain = domain;
        this.port = port;

        //TODO: local and remote IPs not implemented yet
    }


    /**
     * Gets the port number that the Request must have come in on
     *
     * @return The Port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port number that the request must have come in on
     *
     * @param port The new poer numver
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * The Domain name (Host: header) that the Request must have.  Allows Wildcards
     *
     * @return The Domain name
     */
    // TODO: Doc Wildcards again
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the required domain name of a Request
     *
     * @param domain The new Domain name
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets the Protocol (HTTP, HTTPS) that a Request must be using
     *
     * @return The protocol that a Request must be using
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol that a Request must be using
     *
     * @param protocol The new Protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the remote IP that a Request must have come from
     *
     * @return The remote IP that the Request must have come from
     */
    public String getRemoteIP() {
        return remoteIP;
    }

    /**
     * Sets the Remote IP that a Request must have come from
     *
     * @param remoteIP The new remote IP
     */
    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    /**
     * Gets the Local IP that a Request myst have come in on
     *
     * @return The local IP
     */
    public String getLocalIP() {
        return localIP;
    }

    /**
     * Sets the Local IP that a Request must come in on
     *
     * @param localIP The new Local IP
     */
    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    /**
     * Checks to make sure that the specified bindings do not conflict.  Includes testing for wildcard (*) domains
     *
     * @param bindings All the Bindings that might conflict
     * @return false if any of the bindings conflict, otherwise true (valid)
     */
    public static boolean isValid(ArrayList<Binding> bindings) {
        WebServer.logDebug("Called isValid with " + bindings.toString());
        HashMap<String, Integer> CompletelyReservedPorts = new HashMap<String, Integer>();
        for (int i = 0; i < bindings.size(); i++) {

            if (bindings.get(i).domain.equals("*")) {
                for (int k = 0; k < i; k++) {
                    if (bindings.get(i).protocol.equals(bindings.get(k).protocol) && bindings.get(i).port == bindings.get(k).port) {
                        WebServer.triggerInternalError("Duplicate Bindings found: " + bindings.get(i).protocol + "://" + bindings.get(i).domain + ":" + bindings.get(i).port + " and " + bindings.get(k).protocol + "://" + bindings.get(k).domain + ":" + bindings.get(k).port);
                        return false;
                    }
                }
                CompletelyReservedPorts.put(bindings.get(i).protocol, bindings.get(i).port);
            }
            for (int j = i + 1; j < bindings.size(); j++) {

                if (bindings.get(i).protocol.equals(bindings.get(j).protocol) && bindings.get(i).domain.equals(bindings.get(j).domain) && bindings.get(i).port == bindings.get(j).port) {
                    WebServer.triggerInternalError("Duplicate Bindings found: " + bindings.get(i).protocol + "://" + bindings.get(i).domain + ":" + bindings.get(i).port + " and " + bindings.get(j).protocol + "://" + bindings.get(j).domain + ":" + bindings.get(j).port);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Converts this Binding to a Human Readable string
     *
     * @return A Human readable representation of the binding
     */
    @Override
    public String toString() {
        return this.getProtocol() + "://" + this.getDomain() + ":" + this.getPort() + " [Local " + this.getLocalIP() + "] [Remote " + this.getRemoteIP() + "]";
    }


}
