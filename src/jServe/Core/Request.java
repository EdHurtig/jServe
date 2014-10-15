package jServe.Core;

import jServe.Sites.Site;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class Request implements Runnable {
    public HashMap<String, String> variables = new HashMap<String, String>();
    private final Socket client;
    private final int requestID = WebServer.lastRequestID++;
    public BufferedReader in = null;
    public PrintWriter out = null;
    public String headers = "";

    public Request(Socket client) {
        this.client = client;
    }

    public Socket getClient() {
        return client;
    }

    public void parseHeaders(String headersRaw) {
        String[] headers = headersRaw.split("\n");

        if (headers.length > 0) {
            String[] first = headers[0].split(" ");
            if (first.length >= 3) {
                variables.put("HTTP_METHOD", first[0]);
                variables.put("REQUEST_URI", first[1]);
                variables.put("SERVER_PROTOCOL", first[2]);
            }
        }
        for (int i = 1; i < headers.length; i++) {
            String[] HTTPvar = headers[i].split(":");
            variables.put("HTTP_" + HTTPvar[0].toUpperCase().replace('-', '_').trim(), HTTPvar[1].trim());
        }
    }

    @Override
    public void run() {
        Long start = System.nanoTime();
        try {
            client.setSoTimeout(10000);
        }
        catch (SocketException e) {
            WebServer.triggerInternalError("Socket Timeout Set failed for socket on port " + client.getLocalPort()
                                           + ": " + e.getMessage());
            return;
        }

        WebServer.logDebug("Connection Established for with id " + requestID);

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            WebServer.logDebug("Established in and out streams for request " + requestID);
        }
        catch (IOException e) {
            WebServer.triggerInternalError("Read Failed for streams from request " + requestID);
            return;
        }

        String line;
        int lineNum = 1;

        while (client.isConnected()) {
            // reading all lines from request
            try {
                while ((line = in.readLine()).length() != 0) {
                    headers += line + "\n";
                    WebServer.logInfo("Reading Line " + lineNum++ + " from request: " + requestID + ": " + line);
                }
            }
            catch (IOException e) {
                break;
            }

            parseHeaders(headers);

            Site site = WebServer.matchSite(variables.get("SERVER_PROTOCOL"), variables.get("HTTP_HOST"),
                    client.getLocalPort());

            if (site != null) {
                WebServer.logDebug("Transfering Request to site: " + site.getName());

                site.run(this);
            }
            else {
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

    public int getRequestID() {
        return requestID;
    }

    public String getPath() {
        if (variables.containsKey("REQUEST_URI")) {
            String path = variables.get("REQUEST_URI");
            return path.substring(Utils.nthIndexOf(path, '/', 3));
        }
        return null;
    }

    public void close() {
        Plugins.do_action("request_close", this);
        out.close();
    }
}
