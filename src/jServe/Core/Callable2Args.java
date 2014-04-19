package jServe.Core;
import java.util.concurrent.Callable;

public abstract class Callable2Args implements Callable {
    
    public abstract Object call(Object arg0, Object arg1);
    
    public Object call() {
        return null;
    }
}