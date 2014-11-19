package jServe.Core;

/**
 * Provides one of the Core elements for integrating plugins into jServe.
 * Much like WordPress, this class along with the Plugins class allows
 * methods to be called when invoked via the Plugins.do_action('event_tag', args)
 */
public class ActionHook implements Comparable<ActionHook> {

    /**
     * Constructor with a callback
     *
     * @param callback The callback function
     */
    public ActionHook(Runnable callback) {
        setCallback(callback);
        setPriority(10);
    }

    /**
     * Constructor with a callback and a priority
     *
     * @param callback The callback function
     * @param priority The priority of this hook when the event is fired (low-first)
     */
    public ActionHook(Runnable callback, double priority) {
        setCallback(callback);
        setPriority(priority);
    }

    /**
     * * Constructor with a callback and a priority
     *
     * @param callback The callback function
     * @param priority The priority of this hook when the event is fired (low-first)
     * @param num_args The number of args that this hook accepts
     */
    public ActionHook(Runnable callback, double priority, int num_args) {
        setCallback(callback);
        setPriority(priority);
    }

    /**
     * THe callback function
     */
    private Runnable callback;

    /**
     * The priority of this hook.  A low number will fire earlier than a higher number
     */
    private double priority;

    /**
     * Gets the Callback function
     *
     * @return the callback function
     */
    public Runnable getCallback() {
        return callback;
    }

    /**
     * Updates the callback function
     *
     * @param callback the new callback function
     */
    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    /**
     * Gets the priority of this hook
     *
     * @return The priority
     */
    public double getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this hook
     *
     * @param priority the new priority
     */
    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * Gets the number of args that this action hook accepts
     *
     * @return The number of args accepted
     */
    public int getNumArgs() {

        if (callback instanceof Runnable1Arg) {
            return ((Runnable1Arg) callback).getMaxArgs();
        }

        return 0;

    }


    /**
     * Compares this hook to another by priority
     *
     * @param other The Other acton hook
     * @return Whether this ActonHook has a Lower priority number (fires earlier) than the other
     */
    @Override
    public int compareTo(ActionHook other) {
        if (other == null) {
            return 1;
        }
        if (this.getPriority() > other.getPriority()) {
            return -1;
        } else if (this.getPriority() == other.getPriority()) {
            return 0;
        } else {
            return 1;
        }
    }
}