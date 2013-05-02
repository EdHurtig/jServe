import java.util.ArrayList;
import java.util.HashMap;


public class Binding {
	
	public Binding() {}
	
	public Binding(String protocol, String domain, int port) {
		this.protocol = protocol;
		this.domain = domain;
		this.port = port;
	}
	
	private int port;
	
	private String domain;

	private String protocol;
	
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
	
	// Checks to make sure that the specified bindings do not conflict.  Includes testing for wildcard (*) domains
	public static boolean isValid(ArrayList<Binding> bindings) {
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
