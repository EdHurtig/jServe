package jServe.Core;

import jServe.ConsoleCommands.CLICommand;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PackageLoader {

    /**
     * Loads the classes from the given package
     *
     * @param packageName
     */
    public static ArrayList<String> load(String packageName) {

        URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));

        File[] files = new File(root.getFile()).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });
        ArrayList<String> loaded = new ArrayList<String>(files.length);

        for (File file : files) {
            String className = file.getName().replaceAll(".class$", "");

            Class<?> cls = null;
            try {
                cls = Class.forName(packageName + "." + className);
                loaded.add(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                WebServer.logError("Failed to load Class " + file.getPath());
            }
        }

        return loaded;
    }
}
