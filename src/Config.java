import java.util.ArrayList;

import java.util.HashMap;
import net.sf.json.*;

import org.apache.commons.io.IOUtils;

public class Config {
	public static final String CONFIG_FILE = "config/config.sample.json";
	private static JSONObject currentConfig = new JSONObject();
	private static boolean loaded = false;
	
	public static boolean load() { return load(null); }
	public static boolean load(String customJSON) {
		String jsonTxt;
		
		if (customJSON == null)
			jsonTxt = Utils.readTextFile(CONFIG_FILE);
		else
			jsonTxt = customJSON;
		if (jsonTxt == null)
			return false;

		currentConfig = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		
		printAll();
		
		
		System.out.println(currentConfig);
		return true;
//		double coolness = json.getDouble( "coolness" );
//	    int altitude = json.getInt( "altitude" );
//	    JSONObject pilot = json.getJSONObject("pilot");
//	    String firstName = pilot.getString("firstName");
//	    String lastName = pilot.getString("lastName");
//	    
//	    System.out.println( "Coolness: " + coolness );
//	    System.out.println( "Altitude: " + altitude );
//	    System.out.println( "Pilot: " + lastName );
	}
	
	public static boolean put(Object key, Object value) {
		currentConfig.put(key, value);
		String newFile = currentConfig.toString();
		WebServer.logInfo("Writing new Configuration as: " + newFile);
		//return Utils.writeTextFile(CONFIG_FILE,newFile);
		return true;
	}
	
	public static Object get(String key) {
		return currentConfig.get(key);
	}
	
	public static String getString(String key) {
		return currentConfig.getString(key);
	}
	
	public static Boolean getBoolean(String key) {
		return currentConfig.getBoolean(key);
	}
	
	public static Double getDouble(String key) {
		return currentConfig.getDouble(key);
	}
	
	public static Integer getInt(String key) {
		return currentConfig.getInt(key);
	}
	public static JSONObject getJSONObject(String key) {
		return currentConfig.getJSONObject(key);
	}
	
	
	public static boolean isLoaded() {
		return loaded;
	}
	
	public static boolean addBindings(ArrayList<Binding> newBindings, Site site) {
		ArrayList<Binding> allBindings = new ArrayList<Binding>();
		for (Site s : WebServer.sites) {
			if (s != site) {
				allBindings.addAll(s.getBindings());
			}	
		}
		allBindings.addAll(newBindings);
		
		if (Binding.isValid(allBindings)) {
			
			// TODO: Set JSON
			return true;
		} 
		return false;
	}
	
	public static void printAll() {
		System.out.println("Printing Config");
		for (Object s : currentConfig.keySet())
		{
			print_r((String) s, currentConfig.getJSONObject((String) s));
		}
	}
	
	private static void print_r(String key, JSONObject element) {
		System.out.print(key + " = { ");
		for (Object s : element.keySet())
		{
			print_r((String) s,element.getJSONObject((String) s));
		}
		System.out.println("}");
	}
}
