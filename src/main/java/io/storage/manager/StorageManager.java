package io.storage.manager;

import io.storage.core.Credentials;
import io.storage.core.CredentialsException;
import io.storage.core.StorageServiceProvider;

/**
 * Storage manager is a top-level service providing easier access to storage services via string-based identifier.<p>
 * Typically, a caller will register a newly created credentials within the storage manager, and later on will request
 * the {@link StorageService storage service} associated with that credentials, for example:
 * <pre>
 *   AccessTokenCredentials credentials = new AccessTokenCredentials();
 *
 * </pre>
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public interface StorageManager {

    /**
     * Add a new credentials to the storage manager. The credentials will be associated with a
     * {@link #registerProvider(StorageServiceProvider) registered provider}.
     *
     * @param credentials Credentials to add.
     * @return Unique identifier assigned to the credentials.
     * @throws IllegalArgumentException    If argument is {@code null}.
     * @throws InvalidStorageTypeException If provided <i>credentials</i> does not match any known storage service
     *                                     provider.
     */
    String addCredentials(Credentials credentials) throws IllegalArgumentException, InvalidStorageTypeException;

    /**
     * Fetch a storage service for the given credentials.
     *
     * @param credentialId Identifier of credentials.
     * @return Storage service for the given credentials.
     * @throws IllegalArgumentException If <i>credentialId</i> is {@code null}.
     * @throws CredentialsException     If <i>credentialId</i> is unknown.
     */
    StorageService lookupService(String credentialId) throws IllegalArgumentException, CredentialsException;

    /**
     * Register a new service provider.
     *
     * @param provider Provider to add.
     * @throws IllegalArgumentException    If either arguments are {@code null}.
     * @throws InvalidStorageTypeException If <i>storageId</i> is already in use.
     */
    void registerProvider(StorageServiceProvider<? extends Credentials> provider)
            throws IllegalArgumentException, InvalidStorageTypeException;
}
