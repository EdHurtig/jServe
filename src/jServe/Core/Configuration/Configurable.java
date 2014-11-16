package jServe.Core.Configuration;

/**
 * An Interface for defining classes that can be passed a configuration setup
 * and use that to automatically configure themselves
 */
public interface Configurable {
    /**
     * Configures the instance
     *
     * @param c The Configuration
     * @return Whether the configuration given was successfully applied
     */
    public boolean configure(Configuration c);
}
