package jServe.Core;

import com.sun.tools.internal.ws.wsdl.framework.DuplicateEntityException;
import jServe.ConsoleCommands.CLICommand;
import jServe.ConsoleCommands.CommandArgs;
import jServe.Core.Exceptions.JServeDuplicateKeyException;
import jServe.Strings.Formatter;
import jServe.Strings.TableFormatter;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CommandLine implements Runnable {

    private Formatter<String[][]> tableFormmatter;

    public void start() {

        // Initialize this CommandLine with fomatters

        this.tableFormmatter = new TableFormatter();

        // Import all commands in the ConsoleCommands package
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

        WebServer.registerThread(clThread, this);
    }

    private final HashMap<String, CLICommand> commands = new HashMap<String, CLICommand>();
    String cmd;
    String cmdl;

    @Override
    public void run() {
        while (this.keepRunning()) {
            readCommand("jServe >");
            try {

                if (cmdl.startsWith("shell")) {
                    if (cmdl.equals("shell")) {
                        System.out.println("+----------------------------+");
                        System.out.println("|   Shell Access to System   |");
                        System.out.println("|     type 'exit' to quit    |");
                        System.out.println("+----------------------------+");
                        while (true) {
                            readCommand("jServe Shell >");
                            if (cmd.equals("exit")) {
                                break;
                            }
                            processShellCommand(cmd);
                        }
                    } else {
                        processShellCommand(cmd.substring(cmd.indexOf(' ')));
                    }
                } else {
                    proccessCommand(cmd);
                }

            } catch (Exception e) {
                System.err.println("An Error Occured In the System");
                e.printStackTrace();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Determines whether to keep the CommandLine running
     *
     * @return Whether to keep the server running for another tick
     */
    public boolean keepRunning() {
        return WebServer.getStatus() == ServerStatus.Started;
    }

    /**
     * Runs raw command line code
     *
     * @param cmd The Shell cmd to execute
     */
    public void processShellCommand(String cmd) {

        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Takes a string command and determines if the command is registered and if
     * so it runs it
     *
     * @param cmd The command to process
     */
    public void proccessCommand(String cmd) {
        String name = cmd;
        if (cmd.indexOf(' ') != -1) {
            name = cmd.substring(0, cmd.indexOf(' '));
        }

        if (commands.containsKey(name)) {
            String args = "";
            if (cmd.indexOf(' ') != -1) {
                args = cmd.substring(0, cmd.indexOf(' '));
            }
            try {
                CommandArgs arg0 = new CommandArgs(name, cmd.substring(name.length()));
                arg0.setCommandline(this);
                commands.get(name).execute(arg0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            println("Command Not Found");
        }
    }

    /**
     * Registers the given command (callback) with the given name
     *
     * @param name     The Name of the command that will be matched
     * @param callback The CLICommand that will be called back with the arguments
     * @return True if the command was registered, otherwise False
     */
    public void registerCommand(String name, CLICommand callback) {
        name = name.toLowerCase();
        if (commands.containsKey(name)) {
            throw new JServeDuplicateKeyException(name);
        } else {
            commands.put(name, callback);
        }
    }

    /**
     * Registers the given command (callback) with the given names
     *
     * @param names    The Name of the command that will be matched
     * @param callback The CLICommand that will be called back with the arguments
     * @return True if all names for the command were successfully registered, false if no names were provided
     * or on failure
     */
    public void registerCommand(String[] names, CLICommand callback) {
        // Not successful if names is empty.
        if (names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                // attempt to register the command
                try {
                    this.registerCommand(names[i], callback);
                } catch (JServeDuplicateKeyException e) {

                    // Failed to register this command, unregister anything we did before it
                    for (int j = 0; j < i; i++) {
                        // Unregister the previous command
                        this.unregisterCommand(names[j]);
                    }

                    throw e;
                }
            }

        }

    }

    /**
     * Removes the command with the given name from the commands list
     *
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
     *
     * @param prompt An object to prompt the user with before requesting the next
     *               command
     */
    public void readCommand(Object prompt) {
        cmd = Utils.readLine(prompt).trim();
        cmdl = cmd.toLowerCase();
    }

    /**
     * prints the Object o through the WebServer OutputStream
     *
     * @param o The Object to print
     */
    public void print(Object o) {
        WebServer.outputStream.print(o);
    }

    /**
     * Prints a newline through the WebServer OutputStream
     */
    public void println() {
        WebServer.outputStream.println("");
    }

    /**
     * Prints the Object o through the WebServer OutputStream with a newline
     * afterwards
     *
     * @param o The Object to print
     */
    public void println(Object o) {
        WebServer.outputStream.println(o);
    }

    /**
     * Reads a single line from the console (Halting)
     *
     * @return The string that was typed/in the stream
     */
    public String readLine() {
        return Utils.readLine(WebServer.inputStream);
    }

    /**
     * Reads a single line from the console (Halting)
     *
     * @param prompt
     * @return The string that was typed/in the stream
     */
    public String readLine(Object prompt) {
        return Utils.readLine(prompt, WebServer.inputStream);
    }

    /**
     * Reads a single line from the console (Halting)
     *
     * @param prompt
     * @return The string that was typed/in the stream
     */
    public String readLine(Object prompt, boolean hidden) {
        return Utils.readLine(prompt, WebServer.inputStream, hidden);
    }

    /**
     * sends a pluggable error message to the user when a command was called
     * with an unsupported parameter
     */
    public void unsupportedArg() {
        WebServer.triggerPluggableError("[CLI] Command was passed an unsupported parameter");
    }

    public Formatter<String[][]> getTableFormmatter() {
        return tableFormmatter;
    }
}
