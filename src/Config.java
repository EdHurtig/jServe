import java.io.File;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;


public class Config {
	public static final String CONFIG_FILE = "config/config.sample.json";
	private static Document currentConfig;
	private static boolean loaded = false;
	private static String PATH_SEPARATOR = File.separator;
	public static boolean load() { return load(null); }
	public static boolean load(String customHTML) {
		
		String html = null;
		
		if (customHTML == null)
			html = "  "; //Utils.readTextFile(CONFIG_FILE);
		else
			html = customHTML;
		if (html == null)
			return false;

		
		currentConfig = new Document(html); //Jsoup.parse(html);
	
		WebServer.logDebug("[CONFIG]" + currentConfig);
		
		
		/** EXAMPLE CONTENT **/
		
		
		GenericSite example = new GenericSite(1,"Example Site");
		ArrayList<Binding> exampleBindings = new ArrayList<Binding>();
		exampleBindings.add(new Binding("HTTP","*",8888));
		exampleBindings.add(new Binding("HTTP","localhost",8889));
		exampleBindings.add(new Binding("HTTP","localhost2",8889));

		example.setBindings(exampleBindings);

		example.getSettings().put("DOCUMENT_ROOT", getAppPath().getParentFile().getPath() + Utils.cPath("/www/Example"));
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
		//WebServer.logInfo("Writing new Configuration as: " + newFile);
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
	
	public static File getAppPath() {
		return new File(WebServer.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}
	
}
