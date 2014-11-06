package jServe.ConsoleCommands;

import jServe.Core.CommandLine;
import jServe.Core.WebServer;
import jServe.Sites.Site;

/**
 * A CLI Command that will Stop a given site by its ID or the entire server
 * <p/>
 * Usage:
 * <p/>
 * 1 Stop site with ID 2
 * <p/>
 * jServe> stop site 2
 * <p/>
 * <p/>
 * 2. Stop the entire server
 * <p/>
 * jServe> stop
 * <p/>
 * jServe> exit
 *
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 */
public class StopCommand extends CLICommand {

    /**
     * Register this command with the Command Line System on Application Startup
     */
    static {
        WebServer.COMMAND_LINE.registerCommand(new String[]{"exit", "stop"}, new StopCommand());
    }

    /**
     * Runs the stop command given some args
     */
    @Override
    public void run(CommandArgs arg0) {
        CommandLine c = arg0.getCommandline();

        String[] args = arg0.getRawArgs().split(" ");

        if (args.length > 1 && args[0].equals("site")) {
            Integer id = Integer.parseInt(args[1]);

            // Get the site
            Site site = WebServer.getSiteByID(id);

            if (site == null) {
                WebServer.triggerPluggableError("No Site with ID " + id);
            } else {
                c.println("Stopping site: '" + site.getName() + "'");
                WebServer.stop(site);
                c.println("Site Successfully Stopped");

            }
        } else {
            c.println("Server is going down");

            // Stop the Server
            WebServer.stop();

            c.println("Server Exiting");

            // Quit
            System.exit(0);
        }
    }
}
