package jServe.Core.Exceptions;

import jServe.Core.ServerControlAction;

/**
 * Describes an Exception that is to be thrown when a server control action fails (start, stop, restart, reload, ect)
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/06/2014
 */
public class JServeServerControlActionFailed extends JServeException {
    private ServerControlAction action;

    public JServeServerControlActionFailed(ServerControlAction action) {
        super();
        this.action = action;
    }

    public JServeServerControlActionFailed(String message) {
        super(message);
    }

    public JServeServerControlActionFailed(ServerControlAction action, String message) {
        super(message);
        this.action = action;
    }

    public ServerControlAction getAction() {
        return action;
    }
}
