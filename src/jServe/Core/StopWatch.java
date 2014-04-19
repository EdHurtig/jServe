package jServe.Core;


public class StopWatch {
    private boolean running;
    private boolean paused;
    private long start;
    private long pausedStart;
    private long end;
    public boolean isRunning() {
        return running;
    }
    public boolean isPaused() {
        return paused;
    }
    
    public void start() {
        start = System.nanoTime();
        running = true;
    }
    
    public long stop() {
        end = System.nanoTime();
        running = false;
        return end - start;
    }
    
    public long pause() {
        pausedStart = System.nanoTime();
        paused = true;
        return (pausedStart - start);
    }
    
    public void resume() {
        start = System.nanoTime() - (pausedStart - start);
        paused = false;
    }
    
    public long getEnlapsed() {
        if (isRunning()) {
            if (isPaused())
                return (pausedStart - start);
            return (System.nanoTime() - start);
        } else
            return (end - start);
    }
    
    public String toString() {
        long enlapsed = getEnlapsed();
        return ((double)enlapsed / 1000000000.0) + " Seconds";
    }
    
}
