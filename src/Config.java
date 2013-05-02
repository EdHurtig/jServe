import java.util.ArrayList;

import java.util.HashMap;
import net.sf.json.*;

import org.apache.commons.io.IOUtils;

public class Config {
	public static JSONObject currentConfig;
	private static boolean loaded = false;
	
	public static boolean load() { return load(null); }
	public static boolean load(String customJSON) {
		String jsonTxt;
		
		if (customJSON == null)
			jsonTxt = Utils.readFile("config.json");
		else
			jsonTxt = customJSON;
		if (jsonTxt == null)
			return false;

		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		
		for (Object key : json.keySet())
			System.out.println(key);
		
		System.out.println(json);
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
	
	public boolean isLoaded() {
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
	
	public static boolean updateSiteName(String newName, Site s) {
		// TODO: Implement
		return true;
	}
}
