package jServe.Core;

import jServe.Sites.Site;

import java.util.ArrayList;

/**
 * Defines a generalized Error Object that provides information about Error Events while the server is running.
 */
public class JServeError extends RuntimeException {
    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = -1897772501770308433L;

    /**
     * The List of exceptions that led up to this error
     */
    protected ArrayList<Exception> exceptions = new ArrayList<Exception>();

    /**
     * The message to show to humans
     */
    protected String message;

    /**
     * The action to take when the server encounters this error
     */
    protected ServerErrorAction action = ServerErrorAction.none;

    /**
     * The Request that this ServerError is a direct result of
     */
    protected Request request;

    /**
     * The Site that this ServerError is a direct result of
     */
    protected Site site;

    /**
     * Whether this Error was handled yet
     */
    protected boolean handled = false;

    /**
     * Adds an Exception to the List of exceptions that caused this error
     *
     * @param e The Exception
     */
    public void add(Exception e) {
        this.exceptions.add(e);
    }

    /**
     * Clears the list of exceptions to an empty state
     */
    public void clear() {
        this.exceptions.clear();
    }

    /**
     * Counts the number of Exceptions
     */
    public int count() {
        return this.exceptions.size();
    }

    /**
     * Counts the number of Exceptions Recursively through any ServerError Objects recursively.
     *
     * @return the number of Exceptions and ServerError Objects in exceptions
     */
    public int countRecursive() {
        int count = 0;
        for (Exception e : exceptions) {
            if (e instanceof JServeError) {
                count += ((JServeError) e).count();
            }
            count += 1;

        }
        return count;
    }

    /**
     * Returns the First Exception that was registered with this ServerError
     *
     * @return The First Exception that was registered with this ServerError
     */
    public Exception getFirstException() {
        if (this.count() == 0) {
            return null;
        }
        return this.exceptions.get(0);
    }

    /**
     * Returns the Last Exception that was registered with this ServerError
     *
     * @return The Last Exception that was registered with this ServerError
     */
    public Exception getLastException() {
        if (this.count() == 0) {
            return null;
        }
        return this.exceptions.get(this.exceptions.size() - 1);
    }

    /**
     * Returns a List of the registered Exceptions in the same order in which they were registered
     *
     * @return the list of exceptions
     */
    public ArrayList<Exception> getExceptions() {
        return this.exceptions;
    }

    /**
     * Attempts to handle the error
     *
     * @return True if the error was handled to the specifications of the error thrower, otherwise false
     */
    public boolean handle() {
        return false;
    }

    /**
     * Determines if there were any errors that were encountered during this ServerError's Watch
     *
     * @return True if any exceptions were encountered, otherwise false
     */
    public boolean any() {
        return exceptions.size() > 0;
    }

    /**
     * Determines if this ServerError is empty or if it was handled or if it was
     *
     * @return True if the ServerError is now in an 'ok' state for continued Execution
     */
    public boolean ok() {
        return exceptions.size() == 0 || this.handled();
    }

    /**
     * Determines if an action to handle this error has already been taken
     *
     * @return True if this error has been handled
     */
    public boolean handled() {
        if (this.action == ServerErrorAction.none) {
            this.handled = true;
        }
        return this.handled;
    }
}
