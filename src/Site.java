import java.util.ArrayList;
import java.util.HashMap;

public class Site implements Runnable, Configurable {
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
	/** METHODS **/
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	//
	public void run(Request r) {
		WebServer.triggerInternalError("Base Site Class Run(Request r) Method was called... Should not happen.  Closing Response");
		r.out.close();
	}
	
	
	
	/** ERROR HANDLING API FRAMEWORK **/
	
	/**
	 * @overload
	 * @param code
	 */
	public void triggerError(int code) {
		triggerError((double)code,(String)null);
	}
	/**
	 * @overload
	 * @param code
	 * @param message
	 */
	public void triggerError(int code,String message) {
		triggerError((double)code,message);
	}
	/**
	 * @overload
	 * @param code
	 * @param ex
	 */
	public void triggerError(int code,Exception ex) {
		triggerError((double)code,ex);
	}
	/**
	 * @overload
	 * @param code
	 */
	public void triggerError(double code) {
		triggerError(code,(String)null);
	}
	/**
	 * @overload
	 * @param code
	 * @param message
	 */
	public void triggerError(double code,String message) {
		triggerError(code,message,null);
	}
	/**
	 * @overload
	 * @param code
	 * @param e
	 */
	public void triggerError(double code,Exception e) {
		triggerError(code,null,e);
	}
	/**
	 * @overload master
	 * @param code
	 * @param message
	 * @param e
	 */
	public void triggerError(double code, String message, Exception e) {
		//TODO:
	}
	@Override
	public boolean configure() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	
}