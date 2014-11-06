package jServe.Core;

import java.util.concurrent.Callable;

/**
 * Describes a Callable that has a call function that accepts arguments.
 *
 * @param <R> The return type of the call function
 * @param <T> The type of the first argument on the call function
 * @param <U> The type of the second argument on the call function
 * @param <V> The type of the third argument on the call function
 * @param <W> The type of the fourth argument on the call function
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 */
public abstract class Callable4Args<R, T, U, V, W> extends Callable3Args<R, T, U, V> implements Callable {

    /**
     * The Third Argument
     */
    protected W arg3;

    /**
     * Constructs this Callable
     *
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     * @param arg2 The Third Argument
     * @param arg3 The Fourth Argument
     */
    public Callable4Args(T arg0, U arg1, V arg2, W arg3) {
        super(arg0, arg1, arg2);
        this.arg3 = arg3;
    }

    /**
     * This Call function gets run when this Callable is executed by whatever utilizes it.
     * The Developer should override this function and implement their functionality in that call function with access to
     * their Arguments
     *
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     * @param arg2 The Third Argument
     * @param arg3 The Fourth Argument
     */
    public abstract R call(T arg0, U arg1, V arg2, W arg3);

    /**
     * The Call function that the Java will execute
     */
    public R call() {
        return this.call(this.arg0, this.arg1, this.arg2, this.arg3);
    }

}