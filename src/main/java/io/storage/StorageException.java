package io.storage;

/**
 * Top-level exception for all <i>storage.io</i> library exceptions.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public class StorageException extends RuntimeException {

    public StorageException() {
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }
}
