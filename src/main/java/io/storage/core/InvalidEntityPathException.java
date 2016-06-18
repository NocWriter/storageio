package io.storage.core;

import io.storage.StorageException;

/**
 * This exception indicates that a given path is considered invalid in a way. For example, if caller was referring to a
 * file entity while the path actually was resolved to a folder entity, the path is considered invalid.
 *
 * @author Guy Raz Nir
 * @since 27/06/2017
 */
public class InvalidEntityPathException extends StorageException {

    public InvalidEntityPathException() {
    }

    public InvalidEntityPathException(String message) {
        super(message);
    }

    public InvalidEntityPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
