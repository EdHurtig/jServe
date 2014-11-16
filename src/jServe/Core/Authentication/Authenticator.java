package jServe.Core.Authentication;

import java.util.HashMap;

/**
 * Authenticates a User against the configured Authentication sources
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/10/2014
 */
public class Authenticator {

    /**
     * A silly placeholder Hash of users
     */
    public static HashMap<String, String> users = new HashMap<String, String>();

    public static AuthenticationResult check(AuthenticationArgs args) {
        // test data
        // TODO: lets setup an API for interacting with Authentication Sources... I see an AuthenticationSource interface coming
        users.put("edhurtig", "edhurtig");

        if (users.containsKey(args.getUsername()) && users.get(args.getUsername()).equals(args.getPassword())) {
            // Successful
            return new AuthenticationValidResult();
        } else {
            // Failed
            return new AuthenticationFailedResult();
        }
    }
}
