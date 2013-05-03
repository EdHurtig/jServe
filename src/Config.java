import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.*;
import java.util.HashMap;
import net.sf.json.*;

import org.apache.commons.io.IOUtils;

public class Config {
	public static final String CONFIG_FILE = "config/config.sample.json";
	private static Document currentConfig;
	private static boolean loaded = false;
	
	public static boolean load() { return load(null); }
	public static boolean load(String customHTML) {
		
		String html = null;
		
		if (customHTML == null)
			html = Utils.readTextFile(CONFIG_FILE);
		else
			html = customHTML;
		if (html == null)
			return false;

		
		currentConfig = Jsoup.parse(html);
	
		System.out.println(currentConfig);
		
		
		/** EXAMPLE CONTENT **/
		
		
		GenericSite example = new GenericSite(1,"Example Site");
		ArrayList<Binding> exampleBindings = new ArrayList<Binding>();
		exampleBindings.add(new Binding("HTTP","*",8888));
		exampleBindings.add(new Binding("HTTP","localhost",8889));
		exampleBindings.add(new Binding("HTTP","localhost2",8889));

		example.setBindings(exampleBindings);

		example.getSettings().put("DOCUMENT_ROOT", "/Users/ehurtig/desktop/bootstrap-master-2/docs/");
		example.getSettings().put("DefaultDocuments", "index.php,index.html");
		
		WebServer.sites.add(example);
		
		
		
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
	
	public static boolean add(String selector, String value) {
		
		String newFile = currentConfig.toString();
		WebServer.logInfo("Writing new Configuration as: " + newFile);
		//return Utils.writeTextFile(CONFIG_FILE,newFile);
		return true;
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
	
	
}
