package jServe.Core;

import jServe.Sites.Site;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Handles a single Request from a remote client.
 * <p/>
 * A Request should be given a Socket at initialization and then the run()
 * method should be called by in a new thread by using the Thread class.
 * This is because the Request object will read the input stream of the
 * Socket provided at initialization which can be a blocking operation.
 * <p/>
 * When the run() function is called the Socket is read until it encounters
 * a completely blank line signifying the end of the request rawRequest.  It
 * then parses the rawRequest into a HashMap and determines the appropriate
 * Site that should handle this request using the rawRequest and the site Binding
 * Information in WebServer. Once the Site has been determined, the Request Object
 * is handed off to the Site for processing.
 */
public class Request implements Runnable {
    /**
     * The request variables like REQUEST_URI and HTTP_HOST as well as all the HTTP Headers
     */
    private HashMap<String, String> variables = new HashMap<String, String>();

    /**
     * The Socket associated with this Request
     */
    private final Socket client;

    /**
     * The ID of this Request
     */
    private final int requestID = WebServer.lastRequestID++;

    /**
     * The Input Reader for the Socket of this Request
     */
    public BufferedReader in = null;

    /**
     * The output writer for the Socket of this Request
     */
    public PrintWriter out = null;

    /**
     * The raw client request string read by this Request so far
     */
    private String rawRequest = "";

    /**
     * Constructor that creates a Request with the Socket
     *
     * @param client The socket that connects tot the remote client
     */
    public Request(Socket client) {
        this.client = client;
    }

    /**
     * Gets the Socket that is connected to the remote client
     *
     * @return The Socket that is connected to the remote client
     */
    public Socket getClient() {
        return client;
    }

    /**
     * Takes the information read from the Socket and parses it into HTTP Headers
     *
     * @param headersRaw The Raw Header String
     */
    public void parseHeaders(String headersRaw) {
        String[] headers = headersRaw.split("\n");

        if (headers.length > 0) {
            String[] first = headers[0].split(" ");
            if (first.length >= 3) {
                this.variables.put("HTTP_METHOD", first[0]);
                this.variables.put("REQUEST_URI", first[1]);
                this.variables.put("SERVER_PROTOCOL", first[2]);
            }
        }
        for (int i = 1; i < headers.length; i++) {
            String[] HTTPvar = headers[i].split(":");
            this.variables.put("HTTP_" + HTTPvar[0].toUpperCase().replace('-', '_').trim(), HTTPvar[1].trim());
        }
    }

    /**
     * Reads the Request and determines the correct Site to handle this Request.  It then has the Site process this
     * Request
     */
    @Override
    public void run() {
        Long start = System.nanoTime();
        try {
            client.setSoTimeout(10000);
        } catch (SocketException e) {
            WebServer.triggerInternalError("Socket Timeout Set failed for socket on port " + client.getLocalPort()
                    + ": " + e.getMessage());
            return;
        }

        WebServer.logDebug("Connection Established for with id " + requestID);

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            WebServer.logDebug("Established in and out streams for request " + requestID);
        } catch (IOException e) {
            WebServer.triggerInternalError("Read Failed for streams from request " + requestID);
            return;
        }

        String line;
        int lineNum = 1;

        while (client.isConnected()) {
            // reading all lines from request
            try {
                while ((line = in.readLine()).length() != 0) {
                    rawRequest += line + "\n";
                    WebServer.logInfo("Reading Line " + lineNum++ + " from request: " + requestID + ": " + line);
                }
            } catch (IOException e) {
                break;
            }

            parseHeaders(rawRequest);

            Site site = WebServer.matchSite(variables.get("SERVER_PROTOCOL"), variables.get("HTTP_HOST"),
                    client.getLocalPort());

            if (site != null) {
                WebServer.logDebug("Transferring Request to site: " + site.getName());

                site.run(this);
            } else {
                out.close();
            }
            WebServer.logInfo("Closed Request " + requestID);

            Double estimated = (System.nanoTime() - start) / 1000000000.0;
            WebServer.requestTimes.put(getRequestID(), estimated);

            WebServer.logInfo("Request " + requestID + " was completed in " + estimated + " seconds.  Average of "
                    + Utils.sum(WebServer.requestTimes.values().toArray(new Double[0])) + " Seconds / "
                    + WebServer.requestTimes.size() + " Requests is "
                    + Utils.sum(WebServer.requestTimes.values().toArray(new Double[0]))
                    / (WebServer.requestTimes.size()));
        }
    }

    /**
     * Returns the unique request ID of this request
     *
     * @return integer request ID
     */
    public int getRequestID() {
        return requestID;
    }

    /**
     * Gets the path of the Request
     *
     * @return The Path of the Request
     * @deprecated should just user getVariables() and work with that directly
     */
    public String getPath() {
        if (variables.containsKey("REQUEST_URI")) {
            String path = variables.get("REQUEST_URI");
            return path.substring(Utils.nthIndexOf(path, '/', 3));
        }
        return null;
    }

    /**
     * Closes the Request
     */
    public void close() {
        Plugins.do_action("request_close", this);
        out.close();
    }

    /**
     * Gets the HTTP Request Headers that have been parsed so far for this Request
     *
     * @return A HashMap of the HTTP Request Headers
     */
    public HashMap<String, String> getVariables() {
        return variables;
    }

    /**
     * Gets the Raw Request Data that has been read so far
     *
     * @return The Request Data
     */
    public String getRawRequest() {
        return rawRequest;
    }
}
