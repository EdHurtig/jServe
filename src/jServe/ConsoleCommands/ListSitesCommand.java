package jServe.ConsoleCommands;

import jServe.Core.CommandLine;
import jServe.Core.Utils;
import jServe.Core.WebServer;
import jServe.Sites.Site;

public class ListSitesCommand extends CLICommand {
    static {
        register("list", new ListSitesCommand());
    }

    /**
     * Hook function for whenever the user calls this command through the
     * CommandLine
     * 
     * @param arg0
     *            Normally a String
     */
    @Override
    public void run(Object arg0) {

        String[] args = ((String) arg0).split(" ");

        CommandLine c = WebServer.COMMAND_LINE;
        for (Site s : WebServer.sites) {
            c.println(Utils.padRight("" + s.getID(), 5) + " | " + Utils.padTruncateRight(s.getName(), 15) + " | "
                      + s.getStatus().toString());
        }
        c.println("Total of " + WebServer.sites.size() + " Sites Registered");
    }
}
