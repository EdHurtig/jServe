package jServe.Plugins.HTTPErrors;

import jServe.Core.JServeError;

/**
 * Abstract exception for HTTP Errors
 *
 * @author Edward Hurtig <hurtige@ccs.neu.edu>
 * @version Oct 16, 2014
 */
public abstract class HTTPError extends JServeError {

    /**
     * Serial Long
     * <p/>
     * Why does eclipse want this???
     */
    private static final long serialVersionUID = 3746919376275332123L;

    /**
     * The HTTP Error Code, supports decimals for more precise interpretations
     * of the errors
     */
    private Double code;

    /**
     * The html for this error that should be displayed for the user
     */
    private String html;

    /**
     * Constructor that constructs an HTTPError with the specified code and
     * message
     *
     * @param code
     * @param message
     */
    public HTTPError(double code, String message) {
        this.code = code;
        this.message = message;
        this.html = "";
    }

    /**
     * Generates the status header for this Error given a specified prefix such
     * as "HTTP/1.1"
     *
     * @param prefix The first part of the status header regarding protocol
     *               information
     * @return The full status header for a response with this error
     */
    public String getStatusHeader(String prefix) {
        return prefix + " " + this.code + " " + this.message;
    }

    /**
     * Gets the HTTP Error Code
     *
     * @return The HTTP Error Code
     */
    public Double getCode() {
        return this.code;
    }

    /**
     * Sets the HTTP Error Code
     *
     * @param newCode The New HTTP Error Code
     */
    public void setCode(Double newCode) {
        this.code = newCode;
    }

    /**
     * Gets the HTTP Status Message
     *
     * @return The HTTP Status Message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the HTTP Status Message
     *
     * @param newMessage The New HTTP Status Message
     */
    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    /**
     * Gets the HTML for the generic Error page when this error is generated
     *
     * @return The HTML to send to the user if this error is encountered
     */
    public String getHTML() {
        // Lets be generic by default and replace all occurances of {{code}} and
        // {{message}} with their variables
        return this.html.replaceAll("{{code}}", this.getCode() + "").replaceAll("{{message}}", this.getMessage());
    }

    /**
     * Sets the HTML to be rendered to the user
     *
     * @param newHTML The New HTML to use
     */
    public void setHTML(String newHTML) {
        this.html = newHTML;
    }

    /**
     * Handles the error
     */
    public boolean handle() {
        return false;

    }
}
