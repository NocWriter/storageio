package io.storage.providers.jfs;

import io.storage.StorageException;

/**
 * This exception indicates an attempt to perform operation with an unknown credentials (or perhaps expired
 * credentials).
 *
 * @author Guy Raz Nir
 * @since 14/11/2017
 */
public class UnknownCredentialsException extends StorageException {

    /**
     * Class constructor.
     */
    public UnknownCredentialsException() {
    }

    /**
     * Class constructor.
     *
     * @param message Error message.
     */
    public UnknownCredentialsException(String message) {
        super(message);
    }
}
