package jServe.Strings;

import jServe.Core.Utils;

import java.util.ArrayList;

/**
 * Description Goes Here
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/10/2014
 */
public class StringFormatting {

    private static String repeat(char c, Integer i) {
        if (i <= 0) {
            return "";
        }
        return c + repeat(c, --i);
    }

    private static String repeat(String c, Integer i) {
        if (i <= 0) {
            return "";
        }
        return c + repeat(c, --i);
    }

    public static String truncate(String s, int len) {
        return s.substring(0, len);
    }

    public static String consoleTable(String[][] data, String[] headers) {
        // int[] maxlengths = new int[rawHeaders.length];
        //
        // boolean break2 = false;
        // for (String[] row : data) {
        // for (String cell : row) {
        // if (cell.length() > maxline && cell.length() < maxlength) {
        // maxline = cell.length();
        // } else if (cell.length() >= maxlength) {
        // maxline = maxlength;
        // break2 = true;
        // break;
        // }
        //
        // }
        // if (break2)
        // break;
        // }

        String leftAlignFormat = "| %-15s | %-4d |%n";

        // int ncols = rawHeaders.length;
        // int nrows = data.length;
        //
        // int width = maxline * ncols + ncols + 2;

        // String table = "";
        // table = "+" + repeat("-", width - 2) + "+";
        // table = "|";
        // for (String h : rawHeaders)
        // table =
        return "";
    }

    public static String join(String delimiter, Object[] arr) {
        String str = "";
        for (int i = 0; i < arr.length - 1; i++) {
            str += arr[i] + delimiter;
        }
        str += arr[arr.length - 1];
        return str;
    }

    public static String join(String delimiter, ArrayList<String> arr) {
        String str = "";
        for (String s : arr) {
            str += s + delimiter;
        }

        return str.substring(0, -delimiter.length());
    }

    public static String join(char delimiter, ArrayList<String> arr) {
        String str = "";
        for (String s : arr) {
            str += s + delimiter;
        }

        return str.substring(0, -1);
    }

    public static String surroundLR(String s, String lr) {
        String str = "";
        String[] lines = s.split("\n");
        for (String l : lines) {
            str += lr + s + lr + "\n";
        }

        return str;
    }

    public static String surroundLR(String s, char lr) {
        String str = "";
        String[] lines = s.split("\n");
        for (String l : lines) {
            str += lr + s + lr + "\n";
        }

        return str;
    }

    public static String surroundLRCentered(String s, char lr) {
        return surroundLRCentered(s, lr, longestLine(s.split("\n")) + 2);
    }

    public static String surroundLRCentered(String s, char lr, int width) {
        String str = "";

        String[] lines = s.split("\n");
        for (String l : lines) {
            str += lr + Utils.pad(l, width) + lr + "\n";
        }

        return str;
    }

    /**
     * Returns the length of the longest String in the given String Array
     *
     * @param lines The Array to look through
     * @return The Length of the longest line in the array
     */
    public static int longestLine(String[] lines) {
        int max = 0;

        for (String l : lines) {
            if (l.length() > max) {
                max = l.length();
            }
        }

        return max;
    }

    public static String surroundAll(String s, char c) {
        String str;
        String[] lines = s.split("\n");

        int longest = longestLine(lines);

        String tb = repeat(c, longest);
        str = tb;

        for (String l : s.split("\n")) {
            str += c + l + c;
        }

        return "Not Implemented";
    }

    public static String surroundAll(String s, char c, int padding) {

        String[] lines = s.split("\n");

        int longest = longestLine(lines);

        String tb = repeat(c, longest + 2);

        String ws = c + repeat(" ", s.length() - 2) + c + "\n";

        String tbp = repeat(ws, padding);

        String body = surroundLRCentered(s, ' ');

        return "Not Implemented";
    }


    public static String padRight(String s, int len) {
        return padRight(s, len, " ");
    }

    public static String padRight(String s, int len, String padstr) {
        if (s.length() >= len) {
            return s;
        }

        int nPad = len - s.length();

        String pad = repeat(padstr, nPad / padstr.length());
        s = s + pad;

        if (s.length() < len) {
            s += padstr.substring(0, len - s.length());
        }

        return s;
    }

    public static String padLeft(String s, int len) {
        return padLeft(s, len, " ");
    }

    public static String padLeft(String s, int len, String padstr) {
        if (s.length() > len) {
            return s;
        }

        int nPad = len - s.length();

        String pad = repeat(padstr, nPad / padstr.length());
        s = s + pad;

        if (s.length() < len) {
            s += padstr.substring(0, s.length() - len);
        }

        return s;
    }


}
