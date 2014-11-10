package jServe.ConsoleCommands;

import jServe.Core.Authentication.AuthenticationArgs;
import jServe.Core.Authentication.AuthenticationResult;
import jServe.Core.Authentication.AuthenticationValidResult;
import jServe.Core.Authentication.Authenticator;
import jServe.Core.CommandLine;
import jServe.Core.WebServer;

public class AuthCommand extends CLICommand {

    /**
     * Register this command
     */
    static {
        WebServer.COMMAND_LINE.registerCommand("auth", new AuthCommand());
    }


    /**
     * The Handler for the crash command
     *
     * @param arg0 The First Argument
     */
    public void run(CommandArgs arg0) {
        CommandLine cmdl = arg0.getCommandline();
        String username = cmdl.readLine("Username> ");
        String password = cmdl.readLine("Password> ", true);

        AuthenticationArgs authArgs = new AuthenticationArgs();

        authArgs.setUsername(username);
        authArgs.setPassword(password);


        AuthenticationResult result;

        result = Authenticator.check(authArgs);

        if (result instanceof AuthenticationValidResult) {
            cmdl.println("Authentication Successful.  Welcome " + username);
        } else {
            cmdl.println("Authentication Failed.");
        }
    }
}