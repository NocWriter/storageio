package io.storage;

import io.storage.core.Credentials;
import io.storage.core.StorageServiceProvider;
import io.storage.core.entities.FileEntity;
import io.storage.core.entities.FolderEntity;
import io.storage.utils.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

/**
 * Storage service providers test comparability kit. Specific storage providers integration test can inherit or
 * encapsulate this TCK within them in order to verify that they are compatible with the provider's specifications.
 *
 * @param <C> Generic type of credentials.
 * @author Guy Raz Nir
 * @since 02/12/2017
 */
public abstract class TestCompatibilityKit<C extends Credentials> {

    /**
     * Storage service provider to test.
     */
    private final StorageServiceProvider<C> provider;

    /**
     * Credentials to use for testing.
     */
    private final C credentials;

    /**
     * An empty contents required for testing.
     */
    private static final byte[] EMPTY_CONTENTS = new byte[0];

    /**
     * Class constructor.
     *
     * @param provider    Provider to test.
     * @param credentials Credentials to use for test.
     */
    public TestCompatibilityKit(StorageServiceProvider<C> provider, C credentials) {
        Assert.notNull(provider, "Provider cannot be null.");
        Assert.notNull(credentials, "Credentials cannot be null.");
        this.provider = provider;
        this.credentials = credentials;
    }

    public void runAllTests() {
        testAccessRootFolder();
        testEntryExists();
        testDelete();
    }

    /**
     * Test querying root folder ({@link StorageServiceProvider#listFolderContents(Credentials, String)}).
     */
    @Test
    public void testAccessRootFolder() {
        FolderEntity folder = provider.listFolderContents(credentials, "/");

        // Returned value cannot be nul.
        Assertions.assertThat(folder).isNotNull();

        // Name of root folder must be "/".
        Assertions.assertThat(folder.name).isEqualTo("/");

        // Root's parent path should be "null", since the root folder has no actual parent.
        Assertions.assertThat(folder.parentPath).isNull();

        // Root's path is... root ("/").
        Assertions.assertThat(folder.path).isEqualTo("/");

        // Folder's file list must never be "null", even if there are no children.
        Assertions.assertThat(folder.files).isNotNull();

        // Folder's sub-folder list must never be "null", even if there are not sub-folders.
        Assertions.assertThat(folder.folders).isNotNull();
    }

    /**
     * Test file read/write operations.
     */
    @Test
    public void testFileReadWrite() {
        final byte[] contents = Long.toString(System.currentTimeMillis()).getBytes();
        ByteArrayOutputStream input = new ByteArrayOutputStream();

        String filename = generateTemporaryFilename();
        FileEntity writtenFile = provider.writeFile(credentials, filename, contents);

        Assertions.assertThat(writtenFile).isNotNull();
        Assertions.assertThat(writtenFile.modificationDate).isNotNull();
        Assertions.assertThat(writtenFile.humanReadableSize).isNotNull();
        Assertions.assertThat(writtenFile.size).isEqualTo(contents.length);

        provider.readFile(credentials, filename, input);
        Assertions.assertThat(input.toByteArray()).isEqualTo(contents);
    }

    /**
     * Test entry existence ({@link StorageServiceProvider#exists(Credentials, String)}).
     */
    @Test
    public void testEntryExists() {
        // Test that file does not exists.
        Assertions.assertThat(provider.exists(credentials, generateTemporaryFilename())).isFalse();

        // Create a new file and assert it exists.
        String existingFile = createTemporaryFile();
        Assertions.assertThat(provider.exists(credentials, existingFile)).isTrue();

        // Cleanup.
        provider.delete(credentials, existingFile);
    }

    /**
     * Test entry existence ({@link StorageServiceProvider#delete(Credentials, String)}).
     */
    @Test
    public void testDelete() {
        String path = createTemporaryFile();

        Assertions.assertThat(provider.exists(credentials, path)).isTrue();

        // Remove file and assert it is not exists anymore.
        provider.delete(credentials, path);
        Assertions.assertThat(provider.exists(credentials, path)).isFalse();
    }

    /**
     * Creates a new temporary file with no contents.
     *
     * @return File name.
     */
    private String createTemporaryFile() {
        return createTemporaryFile(new byte[0]);
    }

    /**
     * Creates temporary file with given contents.
     *
     * @param contents Contents to use. If this value is {@code null}, an empty file is created.
     * @return File name.
     */
    private String createTemporaryFile(byte[] contents) {
        String tempFile = generateTemporaryFilename();
        provider.writeFile(credentials, tempFile, contents != null ? contents : EMPTY_CONTENTS);
        return tempFile;
    }

    /**
     * @return Temporary file name.
     */
    private String generateTemporaryFilename() {
        return "/tck_" + System.currentTimeMillis() + ".tmp";
    }

}
