package jServe.Core;

/**
 * Describes a Runnable that has a run function that accepts arguments.
 *
 * @param <T> The type of the first argument on the Run function
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 */
public abstract class Runnable1Arg<T> implements Runnable {

    /**
     * The First Argument
     */
    protected T arg0;

    /**
     * Default Constructor
     */
    public Runnable1Arg() {
    }

    /**
     * Constructs this Runnable with the one Arg
     *
     * @param arg0 The First Argument
     */
    public Runnable1Arg(T arg0) {
        this.arg0 = arg0;
    }

    /**
     * This Run function gets run when this Runnable is executed by whatever utilizes it... such as a Thread Class.
     * The Developer should override this function and implement their functionality in that run function with access to
     * their Arguments
     *
     * @param arg0 The First Argument
     */
    public abstract void run(T arg0);

    /**
     * The Run function that the Java Thread class will execute
     */
    public void run() {
        run(this.arg0);
    }

    /**
     * Returns the maximum number of arguments accepted by this Runnable
     *
     * @return The max number of args this can use
     */
    public int getMaxArgs() {
        return 1;
    }
}
