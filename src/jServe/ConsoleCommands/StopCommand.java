package jServe.ConsoleCommands;

import jServe.Core.WebServer;
import jServe.Sites.Site;

/**
 * A CLI Command that will Stop a given site by its ID or the entire server
 * 
 * Usage:
 * 
 * 1 Stop site with ID 2
 * 
 * jServe> stop site 2
 * 
 * 
 * 2. Stop the entire server
 * 
 * jServe> stop
 * 
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 */
public class StopCommand extends CLICommand {

    /**
     * Register this command with the Command Line System on Application Startup
     */
    static {
        WebServer.COMMAND_LINE.registerCommand("stop", new StopCommand());
    }

    @Override
    /**
     * Runs the stop command given some args
     */
    public void run(Object arg0) {

        // Must be given a String[]
        if ( ! (arg0 instanceof String[])) {
            WebServer.COMMAND_LINE.unsupportedArg();
            return;
        }

        String[] args = (String[]) arg0;

        if (args.length > 1 && args[0].equals("site")) {
            Integer id = Integer.parseInt(args[1]);

            // Get the site
            Site site = WebServer.getSiteByID(id);

            if (site == null) {
                WebServer.triggerPluggableError("No Site with ID " + id);
            }
            else {
                WebServer.COMMAND_LINE.println("Stopping site: '" + site.getName() + "'");
                WebServer.stop(site);
                WebServer.COMMAND_LINE.println("Site Successfully Stopped");

            }
        }
        else {
            WebServer.COMMAND_LINE.println("Server is going down");

            // Stop the Server
            WebServer.stop();

            WebServer.COMMAND_LINE.println("Server Exiting");

            // Quit
            System.exit(0);
        }
    }

}
