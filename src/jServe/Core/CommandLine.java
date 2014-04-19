package jServe.Core;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jServe.ConsoleCommands.*;

public class CommandLine implements Runnable {
    
    static {
        System.out.println("COMMAND LINE****************************");
    }
    
    public void start() {
        
        PackageLoader.load("jServe.ConsoleCommands");
        
        
        println(" _       __     __                             __             _ _____                    ");
        println("| |     / /__  / /________  ____ ___  ___     / /_____       (_) ___/___  ______   _____ ");
        println("| | /| / / _ \\/ / ___/ __ \\/ __ `__ \\/ _ \\   / __/ __ \\     / /\\__ \\/ _ \\/ ___/ | / / _ \\");
        println("| |/ |/ /  __/ / /__/ /_/ / / / / / /  __/  / /_/ /_/ /    / /___/ /  __/ /   | |/ /  __/");
        println("|__/|__/\\___/_/\\___/\\____/_/ /_/ /_/\\___/   \\__/\\____/  __/ //____/\\___/_/    |___/\\___/ ");
        println("                                                       /___/                             ");
        println();
        println();
        
        println(Utils.pad("List of Registered Commands", 90));

        for (String s : commands.keySet()) {
            println(Utils.pad(s, 90));
        }
        
        Thread clThread = new Thread(this);

        WebServer.registerThread(clThread,this); 
    }
    
    
    private HashMap<String, CLICommand> commands = new HashMap<String, CLICommand>();
    String cmd;
    String cmdl;
    
    @Override
    public void run() {
        while (this.keepRunning()) {
            readCommand("jServe >");
            if (cmdl.startsWith("shell")) {
                if (cmdl.equals("shell")) {
                    while (!cmdl.equals("exit")) {
                        readLine("jServe Shell >");
                        processShellCommand(cmd);
                    }
                } else {
                    processShellCommand(cmd.substring(cmd.indexOf(' ')));
                }    
            }
            proccessCommand(cmd);    
        }    
    }
    
    /**
     * Determines whether to keep the CommandLine running 
     * @return
     */
    public boolean keepRunning() {
        return WebServer.getStatus() == ServerStatus.Started;
    }
    
    /**
     * Runs raw java code
     * @param cmd
     */
    public void processShellCommand(String cmd) {
        println("Shell Not Implemented Yet.");
    }
    
    /**
     * Takes a string command and determines if the command is registered and if so it runs it
     * @param cmd
     */
    public void proccessCommand(String cmd) {
        String name = cmd;
        if (cmd.indexOf(' ') != -1)
            name = cmd.substring(0, cmd.indexOf(' '));
        
        
        if (commands.containsKey(name)) {
            String args = "";
            if (cmd.indexOf(' ') != -1) 
                args = cmd.substring(0, cmd.indexOf(' '));
            try {
                commands.get(name).run(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else {
            println("Command Not Found");
        }
    }
    
    /**
     * Registers the given command (callback) with the given name
     * @param name The Name of the command that will be matched
     * @param callback The CLICommand that will be called back with the arguments
     * @return True if the command was registered, otherwise False
     */
    public boolean registerCommand(String name, CLICommand callback) {
        name = name.toLowerCase();
        if (commands.containsKey(name)) {
            return false;
        } else {
            commands.put(name, callback);
            return true;
        }
    }
    
    /**
     * Removes the command with the given name from the commands list
     * @param name The name of the command to remove
     * @return True if the command was removed, otherwise False
     */
    public boolean unregisterCommand(String name) {
        if (commands.containsKey(name)) {
            commands.remove(name);
            return true;
        }
        return false;
    }
    
    /**
     * Reads a command from the console and parses it into cmd and cmdl
     */
    public void readCommand() {
        readCommand("");
    }
    
    /**
     * Reads a command from the console and parses it into cmd and cmdl
     * @param prompt An object to prompt the user with before requesting the next command
     */
    public void readCommand(Object prompt) {
        cmd = Utils.readLine(prompt).trim();
        cmdl = cmd.toLowerCase();
    }
    
    /**
     * prints the Object o through the WebServer OutputStream
     * @param o The Object to print
     */
    public void print(Object o) {
        WebServer.outputStream.print(o);
    }
    
    /**
     * Prints a newline through the WebServer OutputStream
     * @param o The Object to print
     */
    public void println() {
        WebServer.outputStream.println("");
    }
    
    /**
     * Prints the Object o through the WebServer OutputStream with a newline afterwards
     * @param o The Object to print
     */
    public void println(Object o) {
        WebServer.outputStream.println(o);
    }
    
    /**
     * Reads a single line from the console (Halting)
     * @return The string that was typed/in the stream
     */
    public String readLine() {
        return Utils.readLine(WebServer.inputStream);
    }
    
    /**
     * Reads a single line from the console (Halting)
     * @param prompt 
     * @return The string that was typed/in the stream
     */
    public String readLine(Object prompt) {
        return Utils.readLine(prompt, WebServer.inputStream);
    }
}
