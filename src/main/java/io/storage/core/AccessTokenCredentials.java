package io.storage.core;

/**
 * Basic access-token based credentials.
 *
 * @author Guy Raz Nir
 * @since 26/06/2017
 */
public abstract class AccessTokenCredentials extends Credentials {

    /**
     * Access token.
     */
    public final String accessToken;

    protected AccessTokenCredentials(String accessToken) throws IllegalArgumentException {
        this.accessToken = accessToken;
    }

    protected AccessTokenCredentials(String id, String ownerId, String accessToken) throws IllegalArgumentException {
        super(id, ownerId);
        this.accessToken = accessToken;
    }

}
