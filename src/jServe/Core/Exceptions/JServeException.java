package jServe.Core.Exceptions;

/**
 * The base Exception class for all JServe Specific Exceptions
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/06/2014
 */
public class JServeException extends RuntimeException {

    public JServeException() {
        super();
    }

    public JServeException(String msg) {
        super(msg);
    }

    public JServeException(String message, Throwable cause) {
        super(message, cause);
    }


    public JServeException(Throwable cause) {
        super(cause);
    }

    protected JServeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
