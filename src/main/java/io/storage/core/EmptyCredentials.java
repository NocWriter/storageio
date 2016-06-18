package io.storage.core;

/**
 * An empty credentials, mainly used for testing purposes. Contains only the bare minimum properties of a credentials
 * object.
 *
 * @author Guy Raz Nir
 * @since 06/12/2017
 */
public class EmptyCredentials extends Credentials {

    /**
     * Construct en empty credentials with <i>emptyStorage</i> as the storage service provider type.
     */
    public EmptyCredentials() {
    }

    /**
     * Class constructor.
     *
     * @param id      Identifier of credentials.
     * @param ownerId Identifier of owner.
     * @throws IllegalArgumentException If <i>storageName</i> is {@code null}.
     */
    public EmptyCredentials(String id, String ownerId) throws IllegalArgumentException {
        super(id, ownerId);
    }
}
