package jServe.ConsoleCommands;

import jServe.Core.WebServer;

public class CrashCommand extends CLICommand {

    /**
     * Register this command
     */
    static {
        WebServer.COMMAND_LINE.registerCommand("crash", new CrashCommand());
    }


    /**
     * The Handler for the crash command
     *
     * @param arg0 The First Argument
     */
    public void run(CommandArgs arg0) {
        WebServer.logInfo("[Crash] User Initiated Crash of this Server");

        WebServer.triggerInternalError("[Crash] Server Crash From Crash CommandR!");

        WebServer.logInfo("[Crash] Server did not crash on internal error! Bringing Down Manually");

        System.exit(1);
    }

}
