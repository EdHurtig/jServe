package jServe.Core;
import java.util.concurrent.Callable;


public abstract class Callable1Arg implements Callable {
    
    public abstract Object call(Object o);
    
    public Object call() {
        return null;
    }
    
}
