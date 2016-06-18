package io.storage.providers.dropbox;

import io.storage.core.AccessTokenCredentials;

/**
 * Dropbox credentials for accessing service.
 *
 * @author Guy Raz Nir
 * @since 06/12/2017
 */
public class DropboxCredentials extends AccessTokenCredentials {

    protected DropboxCredentials(String accessToken) throws IllegalArgumentException {
        super(accessToken);
    }

    protected DropboxCredentials(String id, String accessToken) throws IllegalArgumentException {
        super(id, null, accessToken);
    }

    protected DropboxCredentials(String id, String ownerId, String accessToken)
            throws IllegalArgumentException {
        super(id, ownerId, accessToken);
    }
}
