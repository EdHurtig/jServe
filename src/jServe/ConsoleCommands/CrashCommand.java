package jServe.ConsoleCommands;

import jServe.Core.WebServer;

public class CrashCommand extends CLICommand {
    static {
        WebServer.COMMAND_LINE.registerCommand("crash", new CrashCommand());
    }
    
    
    @Override
    public void run(Object arg0) {
        WebServer.logInfo("[Crash] SERVER IS CRASHING: WE'RE GOING DOWN!!!!!");

        WebServer.triggerInternalError("[Crash] SERVER ERROR!");
        
        WebServer.logInfo("[Crash] SERVER GONE AWAY!");

        System.exit(1);
    }

}
