package jServe.Sites;
import jServe.Core.Request;
import jServe.Core.Utils;
import jServe.Core.WebServer;

import java.io.IOException;
import java.util.HashMap;

import javax.naming.OperationNotSupportedException;
public class GenericSite extends Site {
    public GenericSite(int id, String name) {
        super(id,name);    
    }

    public void onStart() {
        
    }
    
    @Override
    public void run(Request r) {
        if (!isStarted()) 
            return;
        
        WebServer.logDebug("Site's Run Called on RequestID: " + r.getRequestID() + " For site " + getName());
        boolean found = false;
        for (String defaultDocument : getDefaultDocuments()) {

            if (getSettings().get("DOCUMENT_ROOT") != null) {

                String path = getSettings().get("DOCUMENT_ROOT") + Utils.cPath(r.variables.get("REQUEST_URI") + defaultDocument);
                WebServer.logDebug("Testing for Document " + path);
                
                //String file = Utils.readFile(path);
                byte[] file = Utils.readBytes(path);
                
                if (file == null) continue;
                
                WebServer.logDebug("Found a File (" + path + ") for RequestID: " + r.getRequestID());
                //r.out.println("Content-Type: text/html");
                try {
                    r.getClient().getOutputStream().write(file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //r.out.write(file);
                
                WebServer.logDebug("Returned " + r.getPath());
                found = true;
                break;
            } else {
                WebServer.triggerInternalError("[DOCUMENT_ROOT] is not defined for site " + getName());
            }
        }
        if (!found) 
            triggerHTTPError(404);
        r.close();
    }
}
