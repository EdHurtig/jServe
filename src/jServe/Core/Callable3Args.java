package jServe.Core;
import java.util.concurrent.Callable;

/**
 * Describes a Callable that has a call function that accepts arguments.
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @param <R> The return type of the call function
 * @param <T> The type of the first argument on the call function
 * @param <U> The type of the second argument on the call function
 * @param <V> The type of the third argument on the call function
 */
public abstract class Callable3Args<R, T, U, V> extends Callable2Args<R, T, U> implements Callable {
    /**
     * The Third Argument
     */
    protected V arg2;

    /**
     * Constructs this Callable
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     * @param arg2 The Third Argument
     */
    public Callable3Args(T arg0, U arg1, V arg2) {
        super(arg0, arg1);
        this.arg2 = arg2;
    }

    /**
     * This Call function gets run when this Callable is executed by whatever utilizes it.
     * The Developer should override this function and implement their functionality in that call function with access to
     * their Arguments
     *
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     * @param arg2 The Third Argument
     */
    public abstract R call(T arg0, U arg1, V arg2);

    /**
     * The Call function that the Java will execute
     */
    public R call() {
        return this.call(this.arg0, this.arg1, this.arg2);
    }
    
}