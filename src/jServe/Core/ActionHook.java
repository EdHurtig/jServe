package jServe.Core;
public class ActionHook implements Comparable<ActionHook> {
    
    public ActionHook(Runnable callback) {
        setCallback(callback);
    }
    
    public ActionHook(Runnable callback, int priority) {
        setCallback(callback);
        setPriority(priority);
    }
    
    public ActionHook(Runnable callback, int priority, int num_args) {
        setCallback(callback);
        setPriority(priority);
        setNum_args(num_args);
    }
    
    private Runnable callback;
    
    private int priority = 10;
    
    private int num_args = 0;

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getNum_args() {
        return num_args;
    }

    public void setNum_args(int num_args) {
        this.num_args = num_args;
    }

    @Override
    public int compareTo(ActionHook other) {
        return (this.getPriority() - other.getPriority());
    }
}