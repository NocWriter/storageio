package io.storage.core;

import io.storage.StorageException;

/**
 * This exception indicates an invalid or unknown credentials or credentials identification.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public class CredentialsException extends StorageException {

    public CredentialsException() {
    }

    public CredentialsException(String message) {
        super(message);
    }

    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
