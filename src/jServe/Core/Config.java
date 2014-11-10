package jServe.Core;

import jServe.Core.Exceptions.JServeException;
import jServe.Sites.GenericSite;
import jServe.Sites.Site;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


/**
 * Contains the functionality for interfacing and parsing the Configuration file
 */
public class Config {

    /**
     * The configuration
     */
    public static String CONFIG_FILE = "config/config.sample.html";

    /**
     * The current configuration HTML document
     */
    private static Document currentConfig;

    /**
     * The path separator for the current OS
     */
    public static String PATH_SEPARATOR = File.separator;

    /**
     * The status of all the sites
     */
    private static HashMap<Integer, ServerStatus> savedSiteStati = new HashMap<Integer, ServerStatus>();

    /**
     * Saves the last state of the sites to the configuration file
     */
    public static void saveSiteStates() {
        for (Site s : WebServer.sites) {
            savedSiteStati.put(s.getID(), s.getStatus());
        }
    }

    /**
     * Determines if a site should be started automatically
     *
     * @param site The site
     * @return Whether the site should be started
     */
    public static boolean shouldStartSite(Site site) {
        return ((site.getSettings().containsKey("autostart") && site.getSettings().get("autostart").equals("true")) ||
                site.getStatus() == ServerStatus.Restarting);
    }

    /**
     * Unloads the Server of all it's configuration.  Does not write to configuration file
     */
    public static void unload() {
        unload(false);
    }

    /**
     * Unloads the Server of all it's configuration
     *
     * @param fileWrite Whether to remove them from the configuration file
     */
    public static void unload(boolean fileWrite) {
        for (Site s : WebServer.sites) {
            if (!s.isStopped())
                WebServer.stop(s);
            s.destroy();
        }
    }

    /**
     * Unloads the server and then reloads them from the configuration file
     */
    public static void reload() {
        unload();
        load();
    }

    /**
     * Loads the server with the configuration file
     */
    public static void load() {
        load(null);
    }

    /**
     * Loads the server with the given configuration HTML
     *
     * @param customHTML The Custom Configuration
     */
    public static void load(String customHTML) {

        String html = null;

        if (customHTML == null)
            html = Utils.readTextFile(CONFIG_FILE);
        else
            html = customHTML;

        if (html == null)
            throw new JServeException("Loading the configuration file failed");

        currentConfig = Jsoup.parse(html);

        WebServer.logDebug("[CONFIG]" + html);

        /* Starting to configure application */

        WebServer.server_name = currentConfig.select("#server name").first().text();
        WebServer.server_hostname = currentConfig.select("#server hostname").first().text();

        Elements sites = currentConfig.select("#sites site");
        int counter = 0;
        for (Element s : sites) {
            counter++;
            String name = s.select("name").first().text().trim();
            int id;
            String str_id = s.attr("id").trim();

            try {
                id = Integer.parseInt(str_id);
            } catch (Exception e) {
                id = -1;
            }

            if (id == -1 || !Utils.isUniqueID(id, WebServer.getSiteIDs())) {

                id = Utils.getUniqueID(WebServer.getSiteIDs());

                s.attr("id", String.valueOf(id));
                writeConfig();
            }

            Site site = new GenericSite(id, str_id);
            Element type = s.select("type").first();
            if (type != null) {

                if (!type.text().contains("."))
                    type.text("jServe.Sites." + type.text());

                try {
                    Class cls = Class.forName(type.text());
                    if (cls.isInstance(site)) {
                        site = (Site) cls.newInstance();
                    }
                } catch (ClassNotFoundException e) {
                    WebServer.logError("Could Not Find Class: " + type.text());
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    WebServer.logError("Could Not Initiate Class: " + type.text());
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    WebServer.logError("Illegal Access to Class: " + type.text());
                    e.printStackTrace();
                }
            }

            String document_root = "<NOT SET>";

            if (s.select("documentroot").first() != null) {
                document_root = s.select("documentroot").first().text().trim();
                if (!document_root.startsWith("/") && !document_root.startsWith("\\") && document_root.charAt(1) != ':')
                    document_root = getAppPath().getParentFile().getPath() + Utils.cPath(document_root);
                else
                    document_root = Utils.cPath(document_root);
            }

            site.getSettings().put("DOCUMENT_ROOT", document_root); //getAppPath().getParentFile().getPath() + Utils.cPath("/www/Example"));


            Elements docs = s.select("defaultdocuments add");
            ArrayList<String> default_docs = new ArrayList<String>();

            for (Element doc : docs) {
                default_docs.add(doc.text().trim());
            }

            site.setDefaultDocuments(default_docs);

            if (savedSiteStati.containsKey(site.getID()))
                site.setStatus(savedSiteStati.get(site.getID()));

            Elements settings = s.select("settings setting");

            for (Element setting : settings) {
                if (setting.hasAttr("key") && setting.hasAttr("value"))
                    site.getSettings().put(setting.attr("key"), setting.attr("value"));
            }


            Plugins.do_action("load_config_site", site, s);

            WebServer.sites.add(site);
            
            /* Bindings */

            ArrayList<Binding> siteBindings = new ArrayList<Binding>();
            Elements bindings = s.select("bindings binding");
            for (Element b : bindings) {
                String host = b.attr("host").trim();
                int port = Integer.parseInt(b.attr("port").trim());
                String protocol = b.attr("protocol").trim();
                String local_ip = b.attr("local_ip").trim();
                String remote_ip = b.attr("remote_ip").trim();

                siteBindings.add(new Binding(protocol, host, port, local_ip, remote_ip));

            }

            site.setBindings(siteBindings);
        }
    }

    /**
     * Does nothing at all
     *
     * @param selector Unused
     * @param value    Unused
     * @return True
     * @deprecated I don't remember what this function was supposed to do
     */
    public static boolean add(String selector, String value) {

        String newFile = currentConfig.toString();
        //WebServer.logInfo("Writing new Configuration as: " + newFile);
        //return Utils.writeTextFile(CONFIG_FILE,newFile);
        return true;
    }

    /**
     * Adds bindings for the given site
     *
     * @param newBindings The Bindings desired
     * @param site        The site the bindings are fore
     * @return Whether the bindings were conflict free and registered, otherwise false
     */
    public static boolean addBindings(ArrayList<Binding> newBindings, Site site) {
        ArrayList<Binding> allBindings = new ArrayList<Binding>();
        for (Site s : WebServer.sites) {
            if (s != site) {
                allBindings.addAll(s.getBindings());
            }
        }
        allBindings.addAll(newBindings);

        if (Binding.isValid(allBindings)) {
            return true;
        }
        return false;
    }

    /**
     * Gets the path of the application
     *
     * @return The path of the application
     */
    public static File getAppPath() {
        return new File(WebServer.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    /**
     * Writes the configuration HTML to the config file
     *
     * @return Whether the write was successful
     */
    public static boolean writeConfig() {
        return Utils.writeTextFile(CONFIG_FILE, currentConfig.html());
    }
}
