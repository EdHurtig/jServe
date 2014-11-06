package jServe.Plugins.HTTPErrors;

/**
 * An Exception for when the server encounters a 402 error. An instance of this
 * class will get attached to the request that raised the exception and server
 * can then use the information provided by this class to respond correctly to
 * the Client and report the problem
 * 
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Oct 16, 2014
 */
public class HTTP402Error extends HTTPError {

    /**
     * Serial Version
     */
    private static final long serialVersionUID = - 7132270267691748896L;

    /**
     * Constructs an HTTP 402 Error Object to be used with a given request
     */
    public HTTP402Error() {
        super(402.0, "Payment required");
    }

}
