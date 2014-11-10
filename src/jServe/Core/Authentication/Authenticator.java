package jServe.Core.Authentication;

import java.util.HashMap;

/**
 * Description Goes Here
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/10/2014
 */
public class Authenticator {

    public static HashMap<String, String> users = new HashMap<String, String>();

    public static AuthenticationResult check(AuthenticationArgs args) {
        users.put("edhurtig", "edhurtig");
        if (users.containsKey(args.getUsername()) && users.get(args.getUsername()).equals(args.getPassword())) {
            return new AuthenticationValidResult();
        } else {
            return new AuthenticationFailedResult();
        }
    }
}
