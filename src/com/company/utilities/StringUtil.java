package com.company.utilities;

public class StringUtil {

    /**
     * Makes numbers more readable by separating them at every 10^(3*n)
     * @param number ({@code long}): the number to separate
     * @return (String): seperated number
     */
    public static String makeReadableNumber(
            final long number
    ) {
        return makeReadableNumber(number, ' ');
    }

    /**
     * Makes numbers more readable by separating them at every 10^(3*n)
     * @param number ({@code long}): the number to separate
     * @param separation ({@code char}): character to use to separate numbers
     * @return (String): seperated number
     */
    public static String makeReadableNumber(
            final long number,
            final char separation
    ) {
        // initialises the StringBuilder
        final StringBuilder builder = new StringBuilder();
        // gets the default number strong
        final String baseString = Long.toString(number);
        // determines the closest multiple to the baseString's length
        final int closestMultiple = baseString.length() / 3 * 3;
        // determines if the baseString's length is a multiple of 3
        final boolean isMultiple = baseString.length() % 3 == 0;
        // determines the number of digits before the first separation
        final int stop = isMultiple ? 3 : (baseString.length() - closestMultiple) % 3;

        // while there are still separations to add...
        int i = baseString.length() + 3;
        while ((i -= 3) > stop) {
            // ...prepends the 3 next numbers, preceded by a separation
            builder.insert(0, separation + baseString.substring(i - 3, i));
        }
        // prepends all digits before the first separation
        final int lastInsert = isMultiple ? 3 : baseString.length() - closestMultiple;
        builder.insert(0, baseString.substring(0, lastInsert));

        // returns the final String
        return builder.toString();
    }
}
