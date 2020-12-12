package io.storage.manager;

import io.storage.core.Credentials;
import io.storage.core.CredentialsException;
import io.storage.core.StorageServiceProvider;
import io.storage.utils.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation for storage manager. Created with in-memory (transient) credentials repository.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public class DefaultStorageManagerImpl implements StorageManager {

    /**
     * Maintains mapping between each known credential type and its associated service provider.
     */
    private final Map<Class<? extends Credentials>, StorageServiceProvider<? extends Credentials>> providerMap = new ConcurrentHashMap<>();

    /**
     * Credentials repository. Default implementation (if non other specified) is a transient (memory-only) repository.
     * <b>Note:</b> May impose issues in distributed or scalable environments.
     */
    private CredentialsRepository repository = new MemoryCredentialsRepository();

    @Override
    public String addCredentials(Credentials credentials) throws IllegalArgumentException, InvalidStorageTypeException {
        Assert.notNull(credentials, "Credentials cannot be null.");
        Assert.state(credentials.id == null, "Provided credentials already assigned identifier.");

        if (!providerMap.containsKey(credentials.getClass())) {
            throw new InvalidStorageTypeException("No storage service provider found for credentials of type: " + credentials.getClass().getSimpleName());
        }

        return repository.addCredentials(credentials);
    }

    @Override
    public StorageService lookupService(String credentialId) throws IllegalArgumentException, CredentialsException {
        Assert.notNull(credentialId, "Credentials identifier cannot be null.");

        //
        // No cache item found. Need to create a new service provider.
        //
        Credentials credentials = repository.getCredentials(credentialId);
        if (credentials == null) {
            throw new CredentialsException("Unknown credentials.");
        }

        @SuppressWarnings("unchecked")
        StorageServiceProvider<Credentials> provider = (StorageServiceProvider<Credentials>) providerMap.get(credentials.getClass());
        if (provider == null) {
            throw new InvalidStorageTypeException(
                    String.format("Unexpected: Could not find storage service provider for credentials of type '%s'.",
                            credentials.getClass().getSimpleName()));
        }

        return new StorageService(credentials, provider);
    }

    @Override
    public void registerProvider(StorageServiceProvider<? extends Credentials> provider)
            throws IllegalArgumentException, InvalidStorageTypeException {
        Assert.notNull(provider, "Provider cannot be null.");

        // Register the new storage.
        Object existingProvider = providerMap.putIfAbsent(provider.credentialsTypes(), provider);

        // If storage type is already associated with existing implementation, abort with error.
        if (existingProvider != null) {
            throw new InvalidStorageTypeException(
                    String.format("Credential of type '%s' is already associated with existing provider (type: '%s'.",
                            provider.credentialsTypes().getSimpleName(),
                            existingProvider.getClass().getSimpleName()));
        }
    }

    /**
     * Set a new credentials repository.
     *
     * @param repository New repository to set.
     */
    public void setRepository(CredentialsRepository repository) {
        this.repository = repository;
    }
}
