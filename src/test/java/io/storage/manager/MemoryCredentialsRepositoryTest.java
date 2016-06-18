package io.storage.manager;

import io.storage.core.Credentials;
import io.storage.core.EmptyCredentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for {@link MemoryCredentialsRepository}.
 *
 * @author Guy Raz Nir
 * @since 05/12/2017
 */
public class MemoryCredentialsRepositoryTest {

    /**
     * Repository to test.
     */
    private final MemoryCredentialsRepository repository = new MemoryCredentialsRepository();

    /**
     * Test adding a new credentials and performing lookup.
     */
    @Test
    @DisplayName("Test should add credentials to repository")
    public void testShouldAddCredentialsToRepository() {
        Credentials credentials = new EmptyCredentials();
        assertThat(credentials.id).isNull();

        // Add credentials to repository.
        String id = repository.addCredentials(credentials);

        // Assert that the credentials were assigned proper identifier.
        assertThat(id).isNotNull().isEqualTo(credentials.id);

        // Assert that repository contains the credentials.
        assertThat((Credentials) repository.getCredentials(id)).isEqualTo(credentials);
    }
}
