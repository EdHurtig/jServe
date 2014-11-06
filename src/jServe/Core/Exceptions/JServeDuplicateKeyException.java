package jServe.Core.Exceptions;

/**
 * Represents an Exception that is thrown when some function encounters a key
 * that already exists in some key-value structure and that key cannot be
 * overwritten or the function has not been informed to that overwriting is
 * allowed if it exposes that functionality
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/05/2014
 */
public class JServeDuplicateKeyException extends JServeException {
    public JServeDuplicateKeyException() {
        super("A Duplicate key was found");
    }

    public JServeDuplicateKeyException(Object key) {
        super("A Duplicate key '" + key.toString() + "' was found");
    }
}
