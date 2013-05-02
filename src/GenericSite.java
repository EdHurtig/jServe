import java.io.IOException;

import WebServer.Tests.StopWatch;
public class GenericSite extends Site {
	public GenericSite(String name) {
		super(name);
	}
	
	public void run(Request r) {
		WebServer.logDebug("Run Called on RequestID: " + r.getRequestID());
		for (String defaultDocument : ("," + getSettings().get("DefaultDocuments")).split(",")) {

			if (getSettings().get("DOCUMENT_ROOT") != null) {

				String path = getSettings().get("DOCUMENT_ROOT") + r.variables.get("REQUEST_URI") + defaultDocument;
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
				break;
			} else {
				WebServer.triggerInternalError("[DOCUMENT_ROOT] is not defined for site " + getName());
			}
		}
		r.out.close();
			
	}
}