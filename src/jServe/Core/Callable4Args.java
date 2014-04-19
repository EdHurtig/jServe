package jServe.Core;
import java.util.concurrent.Callable;

public abstract class Callable4Args implements Callable {
    
    public abstract Object call(Object arg0, Object arg1, Object arg2, Object arg3);
    
    public Object call() {
        return null;
    }
    
}