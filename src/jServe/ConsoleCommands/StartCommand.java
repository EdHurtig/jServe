package jServe.ConsoleCommands;

import jServe.Core.WebServer;
import jServe.Sites.Site;

/**
 * Starts the Server or a specific site
 * 
 * 
 * @example jServe> start
 * @example jServe> start site
 * @example jServe> start
 * @example jServe> start
 * 
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Aug 13, 2014
 */
public class StartCommand extends CLICommand {
    static {
        WebServer.COMMAND_LINE.registerCommand("start", new StartCommand());
    }

    @Override
    public void run(Object arg0) {

        String[] args = ((String) arg0).split(" ");

        if (args.length > 1 && args[0].equals("site")) {
            Integer id = Integer.parseInt(args[1]);

            Site site = WebServer.getSiteByID(id);

            if (site == null) {
                System.err.println("No Site with ID " + id);
            }
            else {
                WebServer.start(site);
            }
        }
        else {
            if (WebServer.getStatus() == jServe.Core.ServerStatus.Started) {
                System.err.println("Server is already started");
            }
            else {
                WebServer.logInfo("Server is starting");

                WebServer.start();

                WebServer.logInfo("Server Started");
            }
        }
    }

}
