package jServe.ConsoleCommands;

/**
 * Defines the different ways that the CommandArgs Argument Parser can parse an
 * argument string
 * 
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Oct 15, 2014
 */
public enum ArgumentParseMode {
    /**
     * Parses the string by splitting it up by space characters only where the
     * keys are Interger(0) to n
     */
    SpaceDelimited,

    /**
     * Parses the string by splitting it up by space characters but ignores
     * spaces within quotes where the keys are Interger(0) to n
     */
    SpaceDelimitedQuoted,

    /**
     * Parses the string in a key-value way using the --key=value system.
     */
    DoubleDashKeyVal,

    /**
     * Parses the string in a key-value way using the --key="spaced value"
     * formats
     */
    DoubleDashKeyValQuoted,
    /**
     * Parses the string in a key-value way using the -key="spaced value"
     * formats
     */
    SingleDashKeyVal,

    /**
     * Parses the string in a key-value way using the -key="spaced value"
     * formats
     */
    SingleDashKeyValQuoted,

    /**
     * No Parsing takes place, the parsed argument is a hashmap where Integer(0)
     * maps to the raw string
     */
    None
}
