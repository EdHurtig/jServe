package jServe.Sites;

import jServe.Core.MIME;
import jServe.Core.Request;
import jServe.Core.Utils;
import jServe.Core.WebServer;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class GenericSite extends Site {
    public GenericSite(int id, String name) {
        super(id, name);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void run(Request r) {
        if (!isStarted()) {
            return;
        }

        WebServer.logDebug("Site's Run Called on RequestID: " + r.getRequestID() + " For site " + getName());
        boolean found = false;
        for (String defaultDocument : getDefaultDocuments()) {

            if (getSettings().get("DOCUMENT_ROOT") != null) {
                String uri = Utils.stripQueryString(r.getVariables().get("REQUEST_URI"));
                String path = getSettings().get("DOCUMENT_ROOT") + Utils.cPath(uri + defaultDocument);
                WebServer.logDebug("Testing for Document " + path);

                System.out.println("Reading: " + path);
                // String file = Utils.readFile(path);
                byte[] file = Utils.readBytes(path);
                System.out.println("Read: " + path);
                if (file == null) {
                    continue;
                }

                MIME mime = MIME.getMIME(path);
                if (mime.getTypes() != null) {
                    try {
                        r.getClient().getOutputStream()
                                .write(("Content-Type: " + StringUtils.join(mime.getTypes(), '/')).getBytes());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                WebServer.logDebug("Found a File (" + path + ") for RequestID: " + r.getRequestID());
                // r.out.println("Content-Type: text/html");
                try {
                    r.getClient().getOutputStream().write(file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // r.out.write(file);

                WebServer.logDebug("Returned " + r.getPath());
                found = true;
                break;
            } else {
                WebServer.triggerInternalError("[DOCUMENT_ROOT] is not defined for site " + getName());
            }
        }
        if (!found) {
            triggerHTTPError(r, 404);
        }
        r.close();
    }
}
