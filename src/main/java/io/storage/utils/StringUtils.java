package io.storage.utils;

import java.text.NumberFormat;
import java.util.Formatter;
import java.util.regex.Pattern;

/**
 * A collection of general service related to strings.
 *
 * @author Guy Nir
 * @since 29/11/2011
 */
@SuppressWarnings("SpellCheckingInspection")
public class StringUtils {

    /**
     * Number formatting utilities.
     */
    private static final NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * Regular expression to validate E-mail string.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    /**
     * Hexadecimal digits.
     */
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * Enumeration units.
     */
    enum SizeUnits {
        BYTES(1L),
        KB(1024L),
        MB(1024L * 1024L),
        GB(1024L * 1024L * 1024L),
        TB(1024L * 1024L * 1024L * 1024L);

        /**
         * No. of bytes per this unit.
         */
        public final long magnitude;

        /**
         * Class constructor.
         *
         * @param magnitude Number of bytes per unit.
         */
        SizeUnits(long magnitude) {
            this.magnitude = magnitude;
        }
    }

    /**
     * Build a string based on a given <i>format</i> and a collection of <i>arguments</i>. The specific
     * format can be reviewed in {@link java.util.Formatter} documentation.
     *
     * @param format    Format of string (e.g.: 'Hello there Mr. %s').
     * @param arguments Variable argument with values.
     * @return The string generated from <i>format</i> and <i>arguments</i>.
     * @throws IllegalArgumentException If <i>format</i> is {@code null}.
     * @see Formatter
     */
    public static String formats(String format, Object... arguments) throws IllegalArgumentException {
        Assert.notNull(format, "Format cannot be null.");
        return formats(null, format, arguments).toString();
    }

    /**
     * Build a string based on a given <i>format</i> and a collection of <i>arguments</i>. The specific
     * format can be reviewed in {@link java.util.Formatter} documentation.
     *
     * @param targetBuf Target buffer to place generated message. If this value is {@code null}, a new buffer
     *                  is created.
     * @param format    Format of string (e.g.: 'Hello there Mr. %s').
     * @param arguments Variable argument with values.
     * @return The {@code StringBuilder} where message is placed (either the one supplied by the caller or ane instance
     * if caller passed {@code null} for the <i>targetBuf</i> arguments).
     * @throws IllegalArgumentException If <i>format</i> is {@code null}.
     * @see Formatter
     */
    public static StringBuilder formats(StringBuilder targetBuf, String format, Object... arguments) throws IllegalArgumentException {
        Assert.notNull(format, "Format cannot be null.");
        if (targetBuf == null) {
            targetBuf = new StringBuilder(format.length());
        }

        Formatter formatter = new Formatter(targetBuf);
        formatter.format(format, arguments);

        return targetBuf;
    }

    /**
     * Check if a given string is empty or not. An empty string is one that is either {@code null}, has no characters or
     * filled with spaces.
     *
     * @param string String to check.
     * @return {@code true} is string is empty, {@code false} otherwise.
     */
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }

    /**
     * Format a given value to convenient human-readable string including thousands separator.
     *
     * @param value Value to format.
     * @return Formatted value.
     */
    public static String formatNumber(long value) {
        return numberFormat.format(value);
    }

    /**
     * Test if a given <i>email</i> string is a valid E-mail address. The matching is based on RFC-5322 with some
     * extensions.
     *
     * @param email E-mail address to validate.
     * @return {@code true} if provided string is a valid E-mail address, {@code false} if not.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Convert an array of bytes to hexadecimal-based string representation.
     *
     * @param data Data to convert.
     * @return String representation of <i>data</i> in hexadecimal encoding. If <i>data</i> is {@code null}, a {@code null}
     * value is returned. If <i>data.length == 0</i>, the returned value is empty string.
     */
    public static String toString(byte[] data) {
        if (data == null) {
            return null;
        }

        char[] characters = new char[data.length * 2];
        for (int index = 0; index < data.length; index++) {
            int value = ((int) data[index]) & 0xFF;
            characters[index * 2] = HEX_DIGITS[value >> 4];
            characters[index * 2 + 1] = HEX_DIGITS[value & 0x0F];
        }

        return new String(characters);
    }
}