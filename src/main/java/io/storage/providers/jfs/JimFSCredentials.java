package io.storage.providers.jfs;

import io.storage.core.Credentials;

/**
 * Credentials for Google's JimFS (Java-based in-memory file system) service provider.
 *
 * @author Guy Raz Nir
 * @since 19/12/2017
 */
public class JimFSCredentials extends Credentials {

    public JimFSCredentials() {
    }

    public JimFSCredentials(String id, String ownerId) throws IllegalArgumentException {
        super(id, ownerId);
    }
}
