package io.storage.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Generate unique identifier using Java's internal {@code SecureRandom} implementation and current time (measured in
 * milliseconds) to increase security. This implementation has two core properties that defines the strength and
 * representation form of the identifier:
 * <ul>
 * <li>{@link #setTokenEncodingAlphabet(char[]) tokenEncodingAlphabet} - Define table of characters that are used for
 * representing the generated identifier. Default configuration uses A-Z, a-z, 0-9 (62 characters).</li>
 * <li>{@link #setTokenSize(int) tokenSize} - Define now many digits should be the generated value. Default is 64 characters.</li>
 * </ul>
 * The strength of the generated identifier is defined as {@code tokenEncodingAlphabet ^ setTokenSize}. The larger the
 * table of characters and the longer the generated key, the less likelihood to generate repeating tokens.
 *
 * @author Guy Raz Nir
 * @since 14/06/2016
 */
public class SecureRandomIdGenerator {

    /**
     * Secure random that generates the token.
     */
    private final SecureRandom random = new SecureRandom(java.lang.Long.toString(System.currentTimeMillis()).getBytes());

    /**
     * The alphabet used for encoding the generated value.
     */
    private char[] tokenEncodingAlphabet = DEFAULT_ALPHABET;

    /**
     * Token size, in digits.
     */
    private int tokenSize = DEFAULT_TOKEN_SIZE;

    public static final int DEFAULT_TOKEN_SIZE = 64;

    /**
     * Default alphabet to use for encoding the generated value.
     */
    public static final char[] DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    /**
     * Construct new ID generator with reasonable defaults.
     */
    public SecureRandomIdGenerator() {
    }

    /**
     * Class constructor.
     *
     * @param tokenSize Token size. Must be greater than 0.
     */
    public SecureRandomIdGenerator(int tokenSize) {
        if (tokenSize < 1) {
            throw new IllegalArgumentException("Invalid token size: " + tokenSize + " (must be greater than 0).");
        }
        this.tokenSize = tokenSize;
    }

    /**
     * Generate a new unique identifier composed of unique secure number and lower 24 bits of system milliseconds count.
     *
     * @return A new unique identifier.
     */
    public String generate() {
        //
        // Create a new unique value as BigInteger that is composed of:
        // 64 bytes of secure random.
        // 3.5 bytes (28 bits) taken from system time milliseconds.
        //
        BigInteger buffer = new BigInteger(1, random.generateSeed((tokenSize / 2) - 3));
        buffer = buffer.shiftLeft(28).add(BigInteger.valueOf(System.currentTimeMillis() & 0x0FFFFFFF));

        //
        // Convert the generated value to string representation using given alphabet.
        //
        StringBuilder generatedValue = new StringBuilder(128);
        BigInteger divider = BigInteger.valueOf(tokenEncodingAlphabet.length);
        while (buffer.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] values = buffer.divideAndRemainder(divider);
            generatedValue.append(tokenEncodingAlphabet[values[1].intValue()]);
            buffer = values[0];
        }

        return generatedValue.toString();
    }

    /**
     * Sets the table of characters to use when encoding the identifier.
     *
     * @param tokenEncodingAlphabet New set of characters.
     */
    public void setTokenEncodingAlphabet(char[] tokenEncodingAlphabet) {
        this.tokenEncodingAlphabet = tokenEncodingAlphabet;
    }

    /**
     * Sets the required size of generated token (identifier).
     *
     * @param tokenSize Size of token in characters.
     */
    public void setTokenSize(int tokenSize) {
        this.tokenSize = tokenSize;
    }
}