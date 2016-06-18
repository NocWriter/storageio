package io.storage.manager;

import io.storage.core.Credentials;
import io.storage.core.CredentialsException;
import io.storage.utils.Assert;
import io.storage.utils.SecureRandomIdGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of transient (memory-only) credentials repository. The repository is thread-safe. <p> NOTE: This
 * implementation is mainly intended for development and testing.
 *
 * @author Guy Raz Nir
 * @since 26/06/2017
 */
public class MemoryCredentialsRepository implements CredentialsRepository {

    private final SecureRandomIdGenerator idGenerator = new SecureRandomIdGenerator();

    /**
     * In-memory repository.
     */
    private final Map<String, Credentials> credentialsMap = new ConcurrentHashMap<>();

    /**
     * Class constructor.
     */
    public MemoryCredentialsRepository() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Credentials> C getCredentials(String credentialsId)
            throws IllegalArgumentException, CredentialsException {
        Assert.notNull(credentialsId, "Credentials identifier cannot be null.");
        Credentials credentials = credentialsMap.get(credentialsId);
        if (credentials == null) {
            throw new CredentialsException("Unknown credentials: " + credentialsId);
        }

        return (C) credentials;
    }

    @Override
    public String addCredentials(Credentials credentials) throws IllegalArgumentException, CredentialsException {
        Assert.notNull(credentials, "Credentials cannot be null.");
        String id;

        //
        // Add credentials to repository. In rare case of ID collusion (generated identifier is already associated with
        // another credentials object, generate a new one and add it to repository).
        //
        do {
            id = idGenerator.generate();
        } while (credentialsMap.putIfAbsent(id, credentials) != null);

        credentials.id = id;
        return id;
    }
}
