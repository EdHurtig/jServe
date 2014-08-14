package jServe.ConsoleCommands;

import jServe.Core.WebServer;
import jServe.Sites.Site;

public class StopCommand extends CLICommand {
    static {
        WebServer.COMMAND_LINE.registerCommand("stop", new StopCommand());
    }

    @Override
    public void run(Object arg0) {

        String[] args = (String[]) arg0;

        if (args.length > 1 && args[0].equals("site")) {
            Integer id = Integer.parseInt(args[1]);

            Site site = WebServer.getSiteByID(id);

            if (site == null) {
                System.err.println("No Site with ID " + id);
            }
            else {
                WebServer.stop(site);
            }
        }
        else {
            WebServer.logInfo("Server is going down");

            WebServer.stop();

            WebServer.logInfo("Server Exiting");

            System.exit(0);
        }
    }

}
