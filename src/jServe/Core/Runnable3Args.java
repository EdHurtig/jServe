package jServe.Core;

/**
 * Describes a Runnable that has a run function that accepts arguments.
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @param <T> The type of the first argument on the Run function
 * @param <U> The type of the second argument on the Run function
 * @param <V> The type of the third argument on the Run function
 */
public abstract class Runnable3Args<T,U,V> extends Runnable2Args<T, U> implements Runnable{

    /**
     * The Third Argument
     */
    protected V arg2;

    /**
     * Default Constructor
     */
    public Runnable3Args() {}

    /**
     * Constructs this Runnable with the three Args
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     * @param arg2 The Third Argument
     */
    public Runnable3Args(T arg0, U arg1, V arg2) {
        super(arg0, arg1);
        this.arg2 = arg2;
    }

    /**
     * This Run function gets run when this Runnable is executed by whatever utilizes it... such as a Thread Class.
     * The Developer should override this function and implement their functionality in that run function with access to
     * their Arguments
     *
     * @param arg0 The First Argument
     * @param arg1 The Second Argument
     * @param arg2 The Third Argument
     */
    public abstract void run(T arg0, U arg1, V arg2);

    /**
     * The Run function that the Java Thread class will execute
     */
    public void run() {
        this.run(arg0, arg1, arg2);
    }
    
}