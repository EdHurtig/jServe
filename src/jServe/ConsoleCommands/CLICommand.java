package jServe.ConsoleCommands;

import static jServe.Core.WebServer.COMMAND_LINE;
import static jServe.Core.WebServer.errorStream;
import static jServe.Core.WebServer.inputStream;
import static jServe.Core.WebServer.outputStream;
import jServe.Core.Runnable1Arg;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * A CLICommand is the Base class for any jServe Command Line Command.
 */
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
     * @param name The name of the command
     * @param cmd The CLICommand to register
     * @return Whether the registration of the command was successful
     */
    public static void register(String name, CLICommand cmd) {
        if (COMMAND_LINE == null) {
            errorStream.println("COMMAND_LINE is undefined, cannot register your Command");
        }
        else {
            COMMAND_LINE.registerCommand(name, cmd);
        }
    }

    /**
     * Registers the given CLICommands cmd with the jServe CommandLine Handler
     * with the given list of names a user can use to call your CLICommand by typing
     * the any of the strings that you provide to this function Examples:
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
     * @param names The name of the command
     * @param cmd The CLICommand to register
     * @return Whether the registration of the command was successful
     */
    public static void register(String[] names, CLICommand cmd) {
        if (COMMAND_LINE == null) {
            errorStream.println("COMMAND_LINE is undefined, cannot register your Command");
        }
        else {
            COMMAND_LINE.registerCommand(names, cmd);
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
