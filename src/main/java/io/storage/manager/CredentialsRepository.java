package io.storage.manager;

import io.storage.core.Credentials;
import io.storage.core.CredentialsException;

/**
 * A repository that is responsible of managing credentials.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public interface CredentialsRepository {

    /**
     * Locate credentials denoted by <i>credentialsId</i>.
     *
     * @param credentialsId Identifier of credentials to locate.
     * @param <C>           Generic type of credentials.
     * @return Credentials denoted by <i>credentialsId</i>.
     * @throws IllegalArgumentException If argument is {@code null}.
     * @throws CredentialsException     If provided credentials identification is unknown.
     */
    <C extends Credentials> C getCredentials(String credentialsId) throws IllegalArgumentException, CredentialsException;

    /**
     * Add a new credentials to the repository.
     *
     * @param credentials Credentials to add.
     * @return Identifier for credentials.
     * @throws IllegalArgumentException If <i>credentials</i> is {@code null}.
     * @throws CredentialsException     If the provided credentials is invalid and cannot be added to repository (some
     *                                  implementations of credentials may provide validation mechanism).
     */
    String addCredentials(Credentials credentials) throws IllegalArgumentException, CredentialsException;
}
