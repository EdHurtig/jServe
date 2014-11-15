package jServe.Core;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    static {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public static String cPath(String path) {
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/' && File.separatorChar != '/') {
                path = path.substring(0, i) + File.separator + path.substring(i + 1);
            }
        }

        return path;
    }

    public static byte[] readBytes(String fileName) {
        try {

            // shows what the path is (for testing/debugging)
            // System.out.println(System.getProperty("user.dir"));

            // longer form of file name w/ path (if needed)
            // FileInputStream fstream = new
            // FileInputStream(System.getProperty("user.dir") + "/" + fileName);

            // shorter form of file name assumes file in current directory
            // (which is the project directory
            // if no subdirectory option used)

            File f = new File(fileName);
            if (f.exists() && !f.isDirectory()) {
                FileInputStream fstream = new FileInputStream(fileName);

                // Create a stream and reader for the file
                DataInputStream in = new DataInputStream(fstream);

                return org.apache.commons.io.IOUtils.toByteArray(in);
            }
            return null;

        } catch (Exception e) { // Catch exception if any
            WebServer.triggerInternalError("Error Loading File: " + e.getMessage());
            return null;
        }

    }

    public static boolean writeTextFile(String fileName, String content) {
        try {
            File f = new File(fileName);
            if (f.exists() && !f.isDirectory() && f.canWrite()) {

                FileWriter fw = new FileWriter(fileName);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();

                return true;
            }
        } catch (Exception e) {
            WebServer.triggerInternalError("Error Writing to File: " + fileName + " error: " + e.getMessage());
        }
        return false;
    }

    public static String readTextFile(String fileName) {
        try {
            File f = new File(fileName);

            WebServer.logDebug(f.getPath());

            if (f.exists() && !f.isDirectory()) {
                FileInputStream fstream = new FileInputStream(fileName);

                // Create a stream and reader for the file
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String strLine;
                String content = "";

                // Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    content += strLine + "\n";
                }

                // Close the input stream
                in.close();
                return content;
            }
            WebServer.logInfo("Couldn't Find File: " + fileName);
            return null;
        } catch (Exception e) { // Catch exception if any
            WebServer.triggerInternalError("Error Loading File: " + fileName + " error: " + e.getMessage());
            return null;
        }

    }

    public static long sum(Long[] longs) {
        long sum = 0;
        for (long i : longs) {
            sum += i;
        }
        return sum;
    }

    public static long sum(int[] arr) {
        long sum = 0;
        for (int i : arr) {
            sum += i;
        }
        return sum;
    }

    public static double sum(double[] arr) {
        double sum = 0.0;
        for (double d : arr) {
            sum += d;
        }
        return sum;
    }

    public static double sum(Double[] arr) {
        double sum = 0.0;
        for (double d : arr) {
            sum += d;
        }
        return sum;
    }

    public static int nthIndexOf(String str, char ch, int n) {
        int index = -1;
        for (int i = 0; i < n; i++) {
            index = str.substring(index + 1).indexOf(ch) + index + 1;
        }
        return index;
    }

    public static boolean isUniqueID(Integer id, List<Integer> ids) {
        return (ids.indexOf(id) == -1);

    }

    /**
     * returns the first integer from 1 on up that is not in the given List
     *
     * @param ids The List of ids to look for a unique id in
     * @return The first integer from 1 on up that is not in the given List
     */
    public static int getUniqueID(List<Integer> ids) {
        for (int i = 1; i <= ids.size(); i++) {
            if (isUniqueID(i, ids)) {
                return i;
            }
        }
        return 1;
    }

    /**
     * Reads a Line from the console
     *
     * @return The Line that the User Input to the console
     */
    public static String readLine() {
        return readLine("", WebServer.inputStream);
    }

    /**
     * Reads a Line from the console
     *
     * @param prompt The prompt to display for the user
     * @return The Line from the console
     */
    public static String readLine(Object prompt) {
        return readLine(prompt, WebServer.inputStream);
    }

    public static String readLine(InputStream stream) {
        return readLine("", stream);
    }

    public static String readLine(Object prompt, InputStream stream) {
        return readLine(prompt, stream, false);
    }

    public static String readLine(Object prompt, InputStream stream, boolean hidden) {
        String line = null;

        Console c = System.console();
        if (c != null) {
            if (hidden) {
                line = new String(c.readPassword("%s", prompt.toString()));
            } else {
                line = c.readLine(prompt.toString());
            }
        } else {
            System.out.print(prompt);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                // Ignore
            }
        }
        return line;
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Strips the Query string from a given url or path
     *
     * @param path the URL or path
     * @return The path without the query string
     */
    public static String stripQueryString(String path) {
        int index = 0;
        if ((index = path.indexOf('?')) != -1) {
            return path.substring(0, index);
        }
        return path;
    }




    /***************************
     *         Strings         *
     ***************************/

    public static String pad(String s, int len) {
        return pad(s, len, " ");
    }

    public static String pad(String s, int len, String padstr) {
        if (s.length() > len) {
            return s;
        }

        int nPad = len - s.length();

        String pad = StringUtils.repeat(padstr, nPad / 2 / padstr.length());
        s = pad + s + pad;

        if (s.length() < len) {
            s = padstr.substring(0, len - s.length()) + s;
        }

        return s;
    }


}
