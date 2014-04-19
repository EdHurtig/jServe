package jServe.Core;
import java.util.concurrent.Callable;

public abstract class Callable3Args implements Callable {
    
    public abstract Object call(Object arg0, Object arg1, Object arg2);
    
    public Object call() {
        return null;
    }
    
}