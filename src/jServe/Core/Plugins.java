package jServe.Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;

/* Codded Wordpress Style because I love the wordpress Plugin API */
public class Plugins {

    private static HashMap<String, ArrayList<ActionHook>> actions = new HashMap<String, ArrayList<ActionHook>>();

    public static void do_action(String hook) {
        do_action(hook, new Object[]{});
    }

    public static void do_action(String hook, Object arg0) {
        do_action(hook, new Object[]{arg0});
    }

    public static void do_action(String hook, Object arg0, Object arg1) {
        do_action(hook, new Object[]{arg0, arg1});
    }

    public static void do_action(String hook, Object arg0, Object arg1,
                                 Object arg2) {
        do_action(hook, new Object[]{arg0, arg1, arg2});
    }

    public static void do_action(String hook, Object arg0, Object arg1,
                                 Object arg2, Object arg3) {
        do_action(hook, new Object[]{arg0, arg1, arg2, arg3});
    }

    public static void do_action(String hook, Object[] args) {
        if (actions.containsKey(hook) && actions.get(hook).size() > 0) {
            ArrayList<ActionHook> actionHooks = actions.get(hook);

            Arrays.sort(actionHooks.toArray());

            for (ActionHook ah : actionHooks) {
                Object[] ah_args = Arrays.copyOf(args, ah.getNum_args());

                invoke_callback(ah.getCallback(), ah_args);
            }
        }
    }

    public static void add_action(String hook, Runnable callback) {
        add_action(hook, callback, 10, 0);
    }

    public static void add_action(String hook, Runnable callback, int priority) {
        add_action(hook, callback, priority, 0);
    }

    public static void add_action(String hook, Runnable callback,
                                  int priority, int num_args) {
        if (actions.containsKey(hook)) {
            actions.get(hook)
                    .add(new ActionHook(callback, priority, num_args));
        } else {
            ArrayList<ActionHook> actionhooks = new ArrayList<ActionHook>();
            actionhooks.add(new ActionHook(callback, priority, num_args));
            actions.put(hook, actionhooks);
        }
    }

    /**
     * Determines if there are any callbacks registered for the given hook
     *
     * @param hook
     * @return boolean Whether an action with the specified hook is registered
     */
    public static boolean has_action(String hook) {
        return (actions.containsKey(hook) && actions.get(hook).size() > 0);
    }

    /**
     * Determines if the given callback is registered for the given hook
     *
     * @param hook
     * @param callback
     * @return boolean Whether an action with the specified hook is registered
     */
    public static boolean has_action(String hook, Runnable callback) {
        return (actions.containsKey(hook) && actions.get(hook).contains(
                callback));
    }

    /**
     * removed the given callback from the given hook. Returns true if the
     * action was removed
     *
     * @param hook
     * @param callback
     * @return Whether the action was removed
     */
    public static boolean remove_action(String hook, Runnable callback) {
        if (has_action(hook, callback)) {
            while (actions.get(hook).contains(callback)) {
                actions.get(hook).remove(callback);
            }
            return true;
        }
        return false;
    }

    private static void invoke_callback(Runnable callback, Object[] args) {

        switch (args.length) {
            case 0:
                callback.run();
                break;
            case 1:
                ((Runnable1Arg) callback).run(args[0]);
                break;
            case 2:
                ((Runnable2Args) callback).run(args[0], args[1]);
                break;
            case 3:
                ((Runnable3Args) callback).run(args[0], args[1], args[2]);
                break;
            case 4:
                ((Runnable4Args) callback).run(args[0], args[1], args[2], args[3]);
                break;
            default:
                WebServer
                        .triggerPluggableError("[invoke_callback] invoking callbacks with more than 4 arguments is not supported");
        }
    }

    /**
     * Invokes the given callback with the given args and returns the result
     *
     * @param callback
     * @param args
     * @return The result of invoking the callback
     */
    private static Object invoke_callback(Callable callback, Object[] args) {
        switch (args.length) {
            case 0:
                try {
                    return callback.call();
                } catch (Exception e) {
                    return null;
                }
            case 1:
                if (!(callback instanceof Callable1Arg)) {
                    return null;
                }
                return ((Callable1Arg) callback).call(args[0]);
            case 2:
                if (!(callback instanceof Callable2Args)) {
                    return null;
                }
                return ((Callable2Args) callback).call(args[0], args[1]);
            case 3:
                if (!(callback instanceof Callable3Args)) {
                    return null;
                }
                return ((Callable3Args) callback).call(args[0], args[1], args[2]);
            case 4:
                if (!(callback instanceof Callable4Args)) {
                    return null;
                }
                return ((Callable4Args) callback).call(args[0], args[1], args[2],
                        args[3]);
            default:
                WebServer
                        .triggerPluggableError("[invoke_callback] invoking callbacks with more than 4 arguments is not supported");
        }
        return null;
    }
}
