package jServe.Core.Authentication;

/**
 * Arguments to be passed to the Authenticator for Authentication
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/10/2014
 */
public class AuthenticationArgs {

    /**
     * A Username
     */
    private String username;

    /**
     * A Password
     */
    private String password;

    /**
     * A Token
     */
    private String token;

    /**
     * Gets the Username
     *
     * @return The Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the Username
     *
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the Password
     *
     * @return The Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the Password
     *
     * @param password The new Password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the Token
     *
     * @return The Token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the Token
     *
     * @param token The Token
     */
    public void setToken(String token) {
        this.token = token;
    }
}
