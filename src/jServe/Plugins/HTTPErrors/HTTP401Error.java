package jServe.Plugins.HTTPErrors;

/**
 * An Exception for when the server encounters a 401 error. An instance of this
 * class will get attached to the request that raised the exception and server
 * can then use the information provided by this class to respond correctly to
 * the Client and report the problem
 * 
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Oct 16, 2014
 */
public class HTTP401Error extends HTTPError {
    /**
     * Serial Version
     */
    private static final long serialVersionUID = - 8672506672396390007L;

    /**
     * Constructs an HTTP 401 Error Object to be used with a given request
     */
    public HTTP401Error() {
        super(401.0, "Unauthorized");
    }

}
