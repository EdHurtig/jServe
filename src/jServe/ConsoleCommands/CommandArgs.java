package jServe.ConsoleCommands;

import jServe.Core.CommandLine;

import java.util.HashMap;

/**
 * CommandArgs Objects get passed to each command handler when they need to be
 * invoked.
 * <p/>
 * They contain a Full String of the Command Typed, A parsed Args String, and
 * some more information
 *
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Oct 15, 2014
 */
public class CommandArgs {

    /**
     * The full string typed by the user to invoke this command
     */
    private String command;

    /**
     * The Raw Args String
     */
    private String rawArgs;

    /**
     * The Command Line this Command is being executed on
     */
    private CommandLine commandline;

    /**
     * The mode that should be used for parsing the rawArgs String into a
     * collection/map of args
     */
    private ArgumentParseMode parseMode;

    private HashMap<Object, String> args;

    /**
     * Constructs a new CommandArgs Object
     *
     * @param command The command that the user is invoking
     * @param args    The args that are being passed into this command
     */
    public CommandArgs(String command, String args) {
        this.setCommand(command);
        this.setRawArgs(args.trim());
        this.setParseMode(ArgumentParseMode.DoubleDashKeyValQuoted);
        this.setArgs(new HashMap<Object, String>());
    }

    /**
     * Parses a given String into a hashmap using the given mode
     *
     * @param raw  The String to parse
     * @param mode The Parsing Mode to use
     * @return the hashmap representing the arguments
     */
    public static HashMap<Object, String> parseArgs(String raw, ArgumentParseMode mode) {

        HashMap<Object, String> args = new HashMap<Object, String>();

        switch (mode) {

            case SpaceDelimited:
                String[] list = raw.split(" ");
                for (int i = 0; i < list.length; i++) {
                    args.put(new Integer(i), list[i]);
                }
                break;
            case SpaceDelimitedQuoted:

                boolean inQuote = false;
                char prevChar = 0;
                HashMap<Integer, String> params = new HashMap<Integer, String>();
                int argnum = 0;
                String currentParam = "";
                for (int i = 0; i < raw.length(); i++) {

                    if (raw.charAt(i) == '"' && prevChar != '\\') {
                        // If we were in a quote... we are no longer in a quote
                        inQuote = !inQuote;
                    } else if (raw.charAt(i) == '\\' && prevChar == '\\') {
                        // Reset the Escape sequence so that we don't escape the
                        // next character
                        prevChar = 0;
                        currentParam += '\\';
                    } else if (raw.charAt(i) == ' ' && !inQuote) {
                        // Don't add if there is nothing in the currentParam (i.e.
                        // double spaces between params)
                        if (!currentParam.equals("")) {
                            params.put(argnum, currentParam);

                            // Increment Arg Number
                            argnum++;

                            // Reset current Param to "" because we are about to
                            // start the next param
                            currentParam = "";
                        }
                    } else {
                        currentParam += raw.charAt(i);
                    }
                }

                break;
            default:
                break;
        }

        return args;

    }

    /**
     * Gets the Command name being invoked
     *
     * @return The command name being invoked
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the Command name being invoked
     *
     * @param command The new command name being invoked
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns the Raw Args String that the User entered (everything after the
     * Command)
     *
     * @return Returns the Raw Args String for this invokation of the command
     */
    public String getRawArgs() {
        return rawArgs;
    }

    /**
     * Sets the Raw Args String for the command and reparses the Args
     *
     * @param rawArgs The New Raw Args String
     */
    public void setRawArgs(String rawArgs) {
        this.rawArgs = rawArgs;
    }

    public ArgumentParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ArgumentParseMode parseMode) {
        this.parseMode = parseMode;
    }

    public HashMap<Object, String> getArgs() {
        return args;
    }

    public void setArgs(HashMap<Object, String> args) {
        this.args = args;
    }

    public CommandLine getCommandline() {
        return commandline;
    }

    public void setCommandline(CommandLine commandline) {
        this.commandline = commandline;
    }
}
