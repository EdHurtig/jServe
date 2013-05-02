import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;


public class Site implements Runnable {
	public Site() {}
	public Site(String name) {
		setName(name);
	}
	
	private HashMap<String, String> settings = new HashMap<String,String>();
	private ArrayList<Binding> bindings = new ArrayList<Binding>();
	
	private String name;
	
	
	public String getName() {
		return name;
	}

	// HotUpdate
	public boolean setName(String name) {
		if (Config.updateSiteName(this.name,this)) {
			this.name = name;
			WebServer.restart();
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		handleRequest();
	}
	
	public void handleRequest() {
		
		
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
	
	public void run(Request r) {
		r.out.println("Not Implemented! Derive a class and Override the run() method of Site");
	}
	
	public HashMap<String, String> getSettings() {
		return settings;
	}
	public void setSettings(HashMap<String, String> settings) {
		this.settings = settings;
	}
	
	public void triggerError(int code) {
		triggerError((double)code,(String)null);
	}
	
	public void triggerError(int code,String message) {
		triggerError((double)code,message);
	}
	
	public void triggerError(int code,Exception ex) {
		triggerError((double)code,ex);
	}
	
	public void triggerError(double code) {
		triggerError(code,(String)null);
	}
	
	public void triggerError(double code,String message) {
		//TODO: Stuff
	}
	
	public void triggerError(double code,Exception e) {
		//TODO: Stuff
	}
	
	
}