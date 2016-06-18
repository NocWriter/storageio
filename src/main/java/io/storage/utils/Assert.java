package io.storage.utils;

/**
 * Assertion utils.
 *
 * @author Guy Raz Nir
 * @since 09/12/2020
 */
public abstract class Assert {

    /**
     * Assert that a given object - <i>o</i> - is not {@code null}. If object is {@code null} then
     * {@code IllegalArgumentException} is thrown.
     *
     * @param o       Object to assert.
     * @param message Message to use when throwing exception.
     * @throws IllegalArgumentException If <i>o</i> is {@code null}.
     */
    public static void notNull(Object o, String message) throws IllegalArgumentException {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a given <i>state</i> is {@code true}. If <i>state</i> is {@code false} then
     * {@code IllegalStateException} is thrown.
     *
     * @param state   State to assert.
     * @param message Message to use when throwing exception.
     * @throws IllegalStateException If <i>state</i> is {@code false}.
     */
    public static void state(boolean state, String message) throws IllegalStateException {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }
}
