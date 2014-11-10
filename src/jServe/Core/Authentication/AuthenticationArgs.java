package jServe.Core.Authentication;

/**
 * Description Goes Here
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/10/2014
 */
public class AuthenticationArgs {
    private String username;
    private String password;
    private String token;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
