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
public abstract class CLICommand extends Runnable1Arg<CommandArgs> {

    /**
     * Default Constructor. No Arguments Set for CLICommand
     */
    public CLICommand() {
        super();
    }

    /**
     * Initialize Command With the Args to pass
     *
     * @param args The CommandArgs for the command
     */
    public CLICommand(CommandArgs args) {
        super(args);
    }

    /**
     * Registers the given CLICommand cmd with the jServe CommandLine Handler
     * with the given name A user can call your CLICommand by typing the string
     * that you provide to this function Examples:
     * <p/>
     * jServe > list sites Will call the CLICommand with the name "list" if
     * registered and the arg0 of "sites"
     * <p/>
     * jServe > list Will call the CLICommand with the name "list" if registered
     * and the arg0 of ""
     * <p/>
     * jServe > listsites Will call the CLICommand with the name "listsites" if
     * registered and the arg0 of ""
     *
     * @param name The name of the command
     * @param cmd  The CLICommand to register
     */
    public static void register(String name, CLICommand cmd) {
        if (COMMAND_LINE == null) {
            errorStream.println("COMMAND_LINE is undefined, cannot register your Command");
        } else {
            COMMAND_LINE.registerCommand(name, cmd);
        }
    }

    /**
     * Registers the given CLICommands cmd with the jServe CommandLine Handler
     * with the given list of names a user can use to call your CLICommand by typing
     * the any of the strings that you provide to this function Examples:
     * <p/>
     * jServe > list sites Will call the CLICommand with the name "list" if
     * registered and the arg0 of "sites"
     * <p/>
     * jServe > list Will call the CLICommand with the name "list" if registered
     * and the arg0 of ""
     * <p/>
     * jServe > listsites Will call the CLICommand with the name "listsites" if
     * registered and the arg0 of ""
     *
     * @param names The name of the command
     * @param cmd   The CLICommand to register
     */
    public static void register(String[] names, CLICommand cmd) {
        if (COMMAND_LINE == null) {
            errorStream.println("COMMAND_LINE is undefined, cannot register your Command");
        } else {
            COMMAND_LINE.registerCommand(names, cmd);
        }
    }

    /**
     * Executes the CLICommand with the given args
     *
     * @param args The args
     */
    public void execute(CommandArgs args) {
        // Set the args
        this.arg0 = args;

        // Call the run function... just as the Thread class does
        this.run();
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
