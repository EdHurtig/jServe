package jServe.Core;

/**
 * Describes a Runnable that has a run function that accepts arguments.
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @param <T> The type of the first argument on the Run function
 * @param <U> The type of the second argument on the Run function
 */
public abstract class Runnable2Args<T,U> extends Runnable1Arg<T> implements Runnable {

    /**
     * The Second Argument
     */
    protected U arg1;

    /**
     * Constructs this Runnable with the two Args
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     */
    public Runnable2Args(T arg0, U arg1) {
        super(arg0);
        this.arg1 = arg1;
    }

    /**
     * This Run function gets run when this Runnable is executed by whatever utilizes it... such as a Thread Class.
     * The Developer should override this function and implement their functionality in that run function with access to
     * their Arguments
     *
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     */
    public abstract void run(T arg0, U arg1);

    /**
     * The Run function that the Java Thread class will execute
     */
    public void run() {
        run(this.arg0, this.arg1);
    }
    
}