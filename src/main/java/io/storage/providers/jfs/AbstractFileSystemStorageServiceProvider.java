package io.storage.providers.jfs;

import io.storage.StorageException;
import io.storage.core.*;
import io.storage.core.entities.FileEntity;
import io.storage.core.entities.FolderEntity;
import io.storage.utils.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

/**
 * An abstract {@link FileSystem} based storage service provider. Actual implementations can derive from this
 * implementation to provide {@code FileSystem}-based service.
 *
 * @author Guy Raz Nir
 * @since 10/11/2017
 */
public abstract class AbstractFileSystemStorageServiceProvider<C extends Credentials> extends AbstractStorageServiceProvider<C> {

    /**
     * Repository that holds {@code FileSystem}s.
     */
    private final SimpleRepository<FileSystem> fileSystemSimpleRepository;

    /**
     * Class constructor.
     *
     * @param credentialsType            Type of credentials required for this provider.
     * @param fileSystemSimpleRepository File system repository, to resolve file-system internal identifiers to actual
     *                                   {@code FileSystem} instances.
     */
    protected AbstractFileSystemStorageServiceProvider(Class<C> credentialsType, SimpleRepository<FileSystem> fileSystemSimpleRepository) {
        super(credentialsType);
        Assert.notNull(fileSystemSimpleRepository, "File system repository cannot be null.");
        this.fileSystemSimpleRepository = fileSystemSimpleRepository;
    }

    @Override
    public boolean exists(C credentials, String path) throws IllegalArgumentException, CredentialsException, InvalidPathFormatException {
        Path file = getPathFor(credentials, path, null);
        return Files.exists(file);
    }

    @Override
    public FileEntity readFileMeta(C credentials, String path) throws IllegalArgumentException, EntityNotFoundException, CredentialsException, InvalidPathFormatException {
        Path file = getPathFor(credentials, path, PathType.REGULAR_FILE);
        return toFileEntity(file);
    }

    @Override
    public FolderEntity listFolderContents(C credentials, String path)
            throws IllegalArgumentException, EntityNotFoundException, InvalidEntityPathException, CredentialsException, InvalidPathFormatException {
        Path fsPath = getPathFor(credentials, path, PathType.DIRECTORY);

        // Generate folder entry.
        return toFolderEntry(fsPath);
    }

    @Override
    public void readFile(C credentials, String path, OutputStream out)
            throws IllegalArgumentException, EntityNotFoundException, CredentialsException, InvalidRevisionException, InvalidPathFormatException {
        Path file = getPathFor(credentials, path, PathType.REGULAR_FILE);
        try {
            Files.copy(file, out);
        } catch (IOException ez) {
            throw new StorageException("File read I/O error (path: " + path + ").");
        }

    }

    @Override
    public FileEntity writeFile(C credentials, String path, InputStream in)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException, InvalidPathFormatException {

        Path file = getPathFor(credentials, path, PathType.REGULAR_FILE);
        try {
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ez) {
            throw new StorageException("File write I/O error (path: " + path + ").");
        }

        return toFileEntity(file);
    }

    @Override
    public FileEntity writeFile(C credentials, String path, InputStream in, String revision)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException, InvalidPathFormatException {
        return writeFile(credentials, path, in);
    }

    @Override
    public FileEntity writeFile(C credentials, String path, byte[] data)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException, InvalidPathFormatException {
        Assert.notNull(data, "Data cannot be null.");
        Path file = getPathFor(credentials, path, PathType.REGULAR_FILE);
        try {
            Files.write(file, data, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        } catch (IOException ez) {
            throw new StorageException("File write I/O error (path: " + path + ").");
        }

        return toFileEntity(file);
    }

    @Override
    public FileEntity writeFile(C credentials, String path, byte[] data, String revision)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException, InvalidPathFormatException {
        return writeFile(credentials, path, data);
    }

    @Override
    public void delete(C credentials, String path)
            throws IllegalArgumentException, EntityNotFoundException, CredentialsException, InvalidPathFormatException {
        Path file = getPathFor(credentials, path, null);
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            throw new StorageException("Could not delete entry (file or directory) " + path);
        }
    }

    /**
     * Creates a new file system, register within the internal repository and return the caller credentials for accessing
     * it.
     *
     * @return Credentials for a new file system.
     */
    public abstract C createFileSystem();

    /**
     * Validate a given credential and path are non-{@code null} and construct a new {@link Path} object for the given
     * <i>path</i>.
     *
     * @param credentials Credentials to use.
     * @param path        Path to resource.
     * @param validateAs  If set to, will verify the path reference a resource of certain type (e.g.: file or directory).
     *                    If this argument is {@code null}, no action will be performed.
     * @return New path
     * @throws IllegalArgumentException    If either arguments are {@code null}.
     * @throws UnknownCredentialsException If provided <i>credentials</i> are unknown.
     */
    protected Path getPathFor(C credentials, String path, PathType validateAs)
            throws IllegalArgumentException, UnknownCredentialsException {
        Path fsPath = getFileSystem(credentials).getPath(path);

        //
        // If caller requested validation of path, first make sure it exists. Afterwards, check the type of the resource.
        //
        if (validateAs != null) {
            if (!Files.exists(fsPath)) {
                throw new EntityNotFoundException("Unknown entity path: " + path);
            }

            switch (validateAs) {
                case REGULAR_FILE:
                    if (!Files.isRegularFile(fsPath)) {
                        throw new InvalidEntityPathException("Path " + path + " is not a regular file.");
                    }
                    break;

                case DIRECTORY:
                    if (Files.isRegularFile(fsPath)) {
                        throw new InvalidEntityPathException("Path " + path + " is not a directory.");
                    }
                    break;

                default:
                    // Reaching this point of the code indicates a bug -- someone added a new path type and forgot to
                    // map it to an appropriate action.
                    throw new IllegalStateException("Unsupported path type: " + validateAs.name());
            }
        }
        return fsPath;
    }

    /**
     * Fetch cached copy of a file system or create a new instance (if implementation supports the action) for a given
     * credentials.
     *
     * @param credentials Credentials identifying the file system.
     * @return File system.
     * @throws IllegalArgumentException    If argument is {@code null}.
     * @throws UnknownCredentialsException If credentials are unknown.
     */
    protected FileSystem getFileSystem(C credentials) throws IllegalArgumentException, UnknownCredentialsException {
        Assert.notNull(credentials, "Credentials cannot be null.");

        // Extract internal file-system identifier.
        String fsId = getFileSystemIdentifier(credentials);

        // Load file-system instance from repository.
        FileSystem fs = fileSystemSimpleRepository.get(fsId);
        if (fs == null) {
            throw new UnknownCredentialsException("Unknown credentials (file system ID: " + fsId + ").");
        }

        return fs;
    }

    /**
     * Convert a Java NIO {@code File} instance to <i>StorageIO</i> {@link FileEntity}.
     *
     * @param file Java NIO file to convert.
     * @return New file entry.
     */
    protected FileEntity toFileEntity(Path file) {
        return null;
    }

    /**
     * Convert Java NIO {@code Path} to a <i>StorageIO</i> {@link FolderEntity}.
     *
     * @param folder Path to folder.
     * @return New folder entry.
     */
    protected FolderEntity toFolderEntry(Path folder) {
        return null;
    }

    /**
     * Provide an underlying implementations the ability to normalize the path before usage. Actual implementation may
     * choose to limit the scope of the file system (e.g.: all paths start from base-directory such as {@code /tmp})).<p>
     * <p>
     * Default implementation returns the exact path passed as argument.
     *
     * @param path Path to normalize. May be {@code null}.
     * @return Normalized path.
     */
    protected Path normalizePath(Path path) {
        return path;
    }

    /**
     * Resolve a string-class identifier from a given credentials. The actual implementation, with specific knowledge in
     * exact credentials type can provide file-system internal identifier associated with the credentials.
     *
     * @param credentials Credentials to extract internal file-system identifier.
     * @return Internal file-system identifier.
     * @throws UnknownCredentialsException If provided credentials is not known by this instance.
     */
    protected abstract String getFileSystemIdentifier(C credentials) throws UnknownCredentialsException;

    /**
     * Types of files.
     */
    protected enum PathType {

        REGULAR_FILE,
        DIRECTORY

    }
}
