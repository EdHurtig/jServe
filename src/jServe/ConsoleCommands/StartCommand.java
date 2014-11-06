package jServe.ConsoleCommands;

import jServe.Core.WebServer;
import jServe.Sites.Site;

/**
 * Starts the Server or a specific site
 * <p/>
 * <p/>
 * jServe> start
 * jServe> start site <id>
 *
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Aug 13, 2014
 */
public class StartCommand extends CLICommand {

    /**
     * Register the command
     */
    static {
        register("start", new StartCommand());
    }

    /**
     * The Run function.  Starts the entire server or a specific site
     *
     * @param arg0 The Arguments for this command
     */
    @Override
    public void run(CommandArgs arg0) {

        String[] args = arg0.getRawArgs().split(" ");

        if (args.length > 1 && args[0].equals("site")) {
            Integer id = Integer.parseInt(args[1]);

            Site site = WebServer.getSiteByID(id);

            if (site == null) {
                System.err.println("No Site with ID " + id);
            } else {
                WebServer.start(site);
            }
        } else {
            if (WebServer.getStatus() == jServe.Core.ServerStatus.Started) {
                System.err.println("Server is already started");
            } else {
                WebServer.logInfo("Server is starting");

                WebServer.start();

                WebServer.logInfo("Server Started");
            }
        }
    }

}
