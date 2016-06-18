package io.storage.core;

import io.storage.StorageException;

/**
 * This exception indicate an attempt to access an entity (e.g.: folder or file) that does not exist.
 *
 * @author Guy Raz Nir
 * @since 27/06/2017
 */
public class EntityNotFoundException extends StorageException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
