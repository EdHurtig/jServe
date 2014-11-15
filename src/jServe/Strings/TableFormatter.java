package jServe.Strings;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Formats data in a table
 *
 * @author Eddie Hurtig <hurtige@ccs.neu.edu>
 * @version 1.0, 11/10/2014
 */
public class TableFormatter implements Formatter<String[][]> {

    /**
     * Formats a console table given the 2 dimensional string array as the table's data
     *
     * @param data THe data to represent in the table
     * @return The Table represented as a string
     */
    public String format(List<List<String>> data) {
        if (data.size() == 0) {
            return "";
        }
        String[][] dataArr = new String[data.size()][data.get(0).size()];
        int row_num = 0;

        for (List<String> row : data) {
            dataArr[row_num] = data.get(row_num).toArray(new String[data.get(0).size()]);
            row_num++;
        }

        return format(dataArr);

    }

    /**
     * Formats a console table given the 2 dimensional string array as the table's data
     *
     * @param data THe data to represent in the table
     * @return The Table represented as a string
     */
    public String format(String[][] data) {

        if (data.length == 0) {
            return "";
        }

        String[] headers = data[0];

        int num_cols = headers.length;

        // Keeps track of the width of each column
        int[] widths = new int[num_cols];
        for (int i = 0; i < num_cols; i++) {
            widths[i] = headers[i].length();
        }

        // Loop through each row and validate data and compute max widths for colums
        for (int i = 1; i < data.length; i++) {

            if (data[i].length != num_cols) {
                throw new IllegalArgumentException("Row " + i + " has " + data[i].length + " columns, expecting " + num_cols + " Columns");
            }

            // Check each cell and calculate largest string
            for (int j = 0; j < num_cols; j++) {
                widths[j] = Math.max(widths[j], data[i][j].length());
            }
        }

        String table = "";
        String horz_line = "+";
        for (int width : widths) {
            horz_line += StringUtils.repeat("-", width + 2) + "+";
        }
        horz_line += "\n";
        table += horz_line;
        for (int i = 0; i < data.length; i++) {
            if (i == 1) {
                table += horz_line;
            }
            table += "| ";
            for (int j = 0; j < num_cols; j++) {
                table += StringUtils.rightPad(data[i][j], widths[j]);
                if (j != num_cols - 1) {
                    table += " | ";
                }
            }
            table += " |\n";
        }
        table += horz_line;
        return table;
    }
}
