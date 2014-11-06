package jServe.Core;
import java.util.concurrent.Callable;

/**
 * Describes a Callable that has a call function that accepts arguments.
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @param <R> The return type of the call function
 * @param <T> The type of the first argument on the call function
 */
public abstract class Callable1Arg<R, T> implements Callable {

    /**
     * The First Argument
     */
    protected T arg0;

    /**
     * Constructs this Callable
     * @param arg0 The First Argument
     */
    public Callable1Arg(T arg0) {
        this.arg0 = arg0;
    }

    /**
     * This Call function gets run when this Callable is executed by whatever utilizes it.
     * The Developer should override this function and implement their functionality in that call function with access to
     * their Arguments
     *
     * @param arg0 The First Argument
     */
    public abstract R call(T arg0);

    /**
     * The Call function that the Java will execute
     */
    public R call() {
        return this.call(this.arg0);
    }
    
}
