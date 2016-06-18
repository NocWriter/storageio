package io.storage.core;

import io.storage.StorageException;

/**
 * This exception indicates a case where a user specified a file revision number that is not supported by the
 * operation.
 *
 * @author Guy Raz Nir
 * @since 15/07/2017
 */
public class InvalidRevisionException extends StorageException {

    public InvalidRevisionException() {
    }

    public InvalidRevisionException(String message) {
        super(message);
    }
}
