package jServe.ConsoleCommands;

import static jServe.Core.WebServer.COMMAND_LINE;
import static jServe.Core.WebServer.errorStream;
import static jServe.Core.WebServer.inputStream;
import static jServe.Core.WebServer.outputStream;
import jServe.Core.Runnable1Arg;

import java.io.InputStream;
import java.io.PrintStream;

public abstract class CLICommand extends Runnable1Arg {

    /**
     * Registers the given CLICommand cmd with the jServe CommandLine Handler
     * with the given name A user can call your CLICommand by typing the string
     * that you provide to this function Examples:
     * 
     * jServe > list sites Will call the CLICommand with the name "list" if
     * registered and the arg0 of "sites"
     * 
     * jServe > list Will call the CLICommand with the name "list" if registered
     * and the arg0 of ""
     * 
     * jServe > listsites Will call the CLICommand with the name "listsites" if
     * registered and the arg0 of ""
     * 
     * @param name
     * @param cmd
     * @return Whether the registration of the command was successful
     */
    public static boolean register(String name, CLICommand cmd) {
        if (COMMAND_LINE == null) {
            errorStream.println("COMMAND_LINE is undefined, cannot register your Command");
            return false;
        }
        else {
            return COMMAND_LINE.registerCommand(name, cmd);
        }
    }

    /**
     * An Alias of the Primary Output stream
     */
    public PrintStream out = outputStream;

    /**
     * An Alias of the Primary Input stream
     */
    public InputStream in = inputStream;

    /**
     * An Alias of the Primary Error stream
     */
    public PrintStream err = errorStream;
}
