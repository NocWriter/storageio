package io.storage.manager;

import io.storage.core.*;
import io.storage.core.entities.FileEntity;
import io.storage.core.entities.FolderEntity;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Storage service is an encapsulation of both the credentials to access the storage and the actual implementation.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public class StorageService {

    /**
     * Credentials to access the actual storage service.
     */
    private final Credentials credentials;

    /**
     * The actual implementation to access the storage service.
     */
    private final StorageServiceProvider<Credentials> provider;

    /**
     * Class constructor.
     */
    StorageService(Credentials credentials, StorageServiceProvider<Credentials> provider) {
        this.credentials = credentials;
        this.provider = provider;
    }

    /**
     * Read folder meta data (without including children).
     *
     * @param path Path of folder.
     * @return Folder meta data.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If <i>path</i> does not exist.
     * @throws InvalidEntityPathException If <i>path</i> reference a non-folder entity.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FolderEntity listFolderContents(String path) throws
            IllegalArgumentException,
            EntityNotFoundException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidPathFormatException {
        return provider.listFolderContents(credentials, path);
    }

    /**
     * Check if file or folder exists.
     *
     * @param path Path to file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If entity does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    boolean exists(String path) throws
            IllegalArgumentException,
            CredentialsException,
            InvalidPathFormatException {
        return provider.exists(credentials, path);
    }

    /**
     * Read a file meta data.
     *
     * @param path Path to file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If entity does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity readFileMeta(String path) throws
            IllegalArgumentException,
            EntityNotFoundException,
            CredentialsException,
            InvalidPathFormatException {
        return provider.readFileMeta(credentials, path);
    }

    /**
     * Read a file.
     *
     * @param path Path to file.
     * @param out  Output stream to write file content.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If entity does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    void readFile(String path, OutputStream out) throws
            IllegalArgumentException,
            EntityNotFoundException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException {
        provider.readFile(credentials, path, out);
    }

    /**
     * Create new or overwrite existing file.
     *
     * @param path Path to file.
     * @param in   Input stream to read file data.
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(String path, InputStream in) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException {
        return provider.writeFile(credentials, path, in);
    }

    /**
     * Create new or overwrite existing file. This method supports writing file with revision verification, i.e. - if
     * <i>revision</i> is specified, the process first validates that the last file revision is the same as specified
     * revision.<br> If revisions match, a file is written normally, otherwise (if no match), an exception is
     * generated.<p> <p> This facility provide a collusion prevention mechanism in an environment when multiple writers
     * address the same file. In such a way, a caller can make sure he/she is overwriting the latest file version.
     *
     * @param path     Path to file.
     * @param in       Input stream to read file data.
     * @param revision Optional file revision (may be {@code null}).
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(String path, InputStream in, String revision) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException {
        return provider.writeFile(credentials, path, in, revision);
    }

    /**
     * Create new or overwrite existing file.
     *
     * @param path Path to file.
     * @param data Data to write.
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(String path, byte[] data) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException {
        return provider.writeFile(credentials, path, data);
    }

    /**
     * Create new or overwrite existing file. This method supports writing file with revision verification, i.e. - if
     * <i>revision</i> is specified, the process first validates that the last file revision is the same as specified
     * revision.<br> If revisions match, a file is written normally, otherwise (if no match), an exception is
     * generated.<p> <p> This facility provide a collusion prevention mechanism in an environment when multiple writers
     * address the same file. In such a way, a caller can make sure he/she is overwriting the latest file version.
     *
     * @param path     Path to file.
     * @param data     Data to write.
     * @param revision Optional file revision (may be {@code null}).
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(String path, byte[] data, String revision) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException {
        return provider.writeFile(credentials, path, data, revision);
    }

    /**
     * Delete a file or directory.
     *
     * @param path Path to file or directory.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If path to file or folder does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    void delete(String path) throws
            IllegalArgumentException,
            EntityNotFoundException,
            CredentialsException,
            InvalidPathFormatException {
        provider.delete(credentials, path);
    }


}
