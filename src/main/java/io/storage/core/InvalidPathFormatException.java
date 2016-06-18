package io.storage.core;

import io.storage.StorageException;

/**
 * This exception indicates a caller specified a path with invalid form. An invalid form can be, for example, string not
 * starting with forward slash (e.g.: {@code documents/file.doc} instead of {@code /documents/file.doc}) or contents of
 * invalid characters (e.g.: {@code /documents/me&friends.doc}).
 *
 * @author Guy Raz Nir
 * @since 31/10/2017
 */
public class InvalidPathFormatException extends StorageException {

    /**
     * Class constructor.
     *
     * @param message Error message.
     * @param path    The faulty path string.
     */
    public InvalidPathFormatException(String message, String path) {
        super(message);
    }

    /**
     * Class constructor.
     *
     * @param message Error message.
     * @param path    The faulty path string.
     * @param cause   The original cause of the failure, if there is any.
     */
    public InvalidPathFormatException(String message, String path, Throwable cause) {
        super(message, cause);
    }
}
