package jServe.Core;

/**
 * Describes an action to take when an error is encountered on the server
 * @author ehurtig
 */
public enum ServerErrorAction {
	none, // perform no action.. continue with your life
	stop, // stops the entire server
	stop_site, // stops the current site
	exit, // quit the entire application gracefully
	crash, // quit the application hard
	restart, // restart the server
	restart_site, // restart the current site
}
