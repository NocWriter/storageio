package io.storage.manager;

import io.storage.core.EmptyCredentials;
import io.storage.core.StorageServiceProvider;
import io.storage.core.entities.FolderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Test suite for {@link DefaultStorageManagerImpl}.
 *
 * @author Guy Raz Nir
 * @since 06/12/2017
 */
@SuppressWarnings("rawtypes")
public class DefaultStorageManagerImplTest {

    /**
     * Manager to test.
     */
    private final DefaultStorageManagerImpl manager = new DefaultStorageManagerImpl();

    /**
     * Test the process of registering storage service provider and credentials. Then test creation of a new storage
     * service during lookup.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStorageServiceCreation() {
        //
        // Setup test -- create dummy credentials and create/register mock storage service provider.
        //
        EmptyCredentials credentials = new EmptyCredentials();
        StorageServiceProvider<EmptyCredentials> mockService = Mockito.mock(StorageServiceProvider.class);
        when(mockService.credentialsTypes()).thenReturn(EmptyCredentials.class);
        manager.registerProvider(mockService);

        // Add credentials to manager.
        String credentialsId = manager.addCredentials(credentials);

        // Lookup storage service via credential's identifier.
        StorageService service = manager.lookupService(credentialsId);

        // Perform an operation on the service (should be propagated internally to our mock object).
        FolderEntity folder = service.listFolderContents("/");

        // Verify the mock object was used.
        Mockito.verify(mockService).listFolderContents(Mockito.eq(credentials), Mockito.eq("/"));
    }

    /**
     * Test adding credentials with an unknown storage type.
     */
    @Test
    @DisplayName("Test should fail on registration of unknown credentials type")
    public void testShouldFailUponUnrecognizedCredentialsType() {
        assertThrows(InvalidStorageTypeException.class, () -> manager.addCredentials(new EmptyCredentials()));
    }

    /**
     * Test adding credentials with existing identifier. The process should fail, since the implementation should not
     * allow adding credentials potentially associated with another storage manager/repository.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAddingCredentialsWithExistingIdentifier() {
        StorageServiceProvider mock = Mockito.mock(StorageServiceProvider.class);
        when(mock.credentialsTypes()).thenReturn(EmptyCredentials.class);
        manager.registerProvider(mock);

        assertThrows(IllegalStateException.class, () -> manager.addCredentials(new EmptyCredentials("storage", "id1")));
    }
}
