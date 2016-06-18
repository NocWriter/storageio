package io.storage.manager;

import io.storage.StorageException;

/**
 * This exception indicates a misuse of storage identifier, whether a given identifier is unknown, or the identifier is
 * already in use by another provider (depends on the context).
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public class InvalidStorageTypeException extends StorageException {

    public InvalidStorageTypeException() {
    }

    public InvalidStorageTypeException(String message) {
        super(message);
    }
}
