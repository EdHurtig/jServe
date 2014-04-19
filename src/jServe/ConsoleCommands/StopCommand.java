package jServe.ConsoleCommands;

import jServe.Core.WebServer;

public class StopCommand extends CLICommand {
    static {
        WebServer.COMMAND_LINE.registerCommand("stop", new StopCommand());
    }
    
    
    @Override
    public void run(Object arg0) {
        WebServer.logInfo("Server is going down");

        WebServer.stop();
        
        WebServer.logInfo("Server Exiting");

        System.exit(0);
    }

}
