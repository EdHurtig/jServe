package jServe.ConsoleCommands;

import jServe.Core.CommandLine;
import jServe.Core.Utils;
import jServe.Core.WebServer;
import jServe.Sites.Site;
import org.apache.commons.lang.StringUtils;

public class ListSitesCommand extends CLICommand {
    static {
        register("list", new ListSitesCommand());
    }

    /**
     * Hook function for whenever the user calls this command through the
     * CommandLine
     *
     * @param arg0 Normally a String
     */
    @Override
    public void run(CommandArgs arg0) {

        CommandLine c = WebServer.COMMAND_LINE;
        String[][] data = new String[WebServer.sites.size() + 1][4];
        data[0] = new String[]{"#", "ID", "Name", "Status"};

        int row_num = 1;
        for (Site s : WebServer.sites) {
            data[row_num] = new String[]{row_num + "", s.getID() + "", s.getName(), s.getStatus().toString()};

            row_num++;

        }

        c.println(c.getTableFormmatter().format(data));


        c.println("\nTotal of " + WebServer.sites.size() + " Sites Registered");

    }
}
