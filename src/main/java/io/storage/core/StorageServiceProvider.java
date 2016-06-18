package io.storage.core;

import io.storage.core.entities.FileEntity;
import io.storage.core.entities.FolderEntity;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * //@formatter:off
 * Definition of Storage Service Provider Interface (SPI). <p>
 * Each of the operations implemented by a storage
 * service provider may generate one of the following exceptions:
 * <ul>
 * <li>IllegalArgumentException - If one of the required arguments is {@code null} or contains invalid value.</li>
 * <li>EntryNotFoundException - If resource (file or directory) specified via <i>path</i> argument does not exist.</li>
 * <li>InvalidEntityPathException - If path of a
 * resource does not exist.</li> <li>CredentialsException - If provided credentials are not supported by the underlying
 * service provider or are no longer valid.</li> <li>StorageException - General exception indicating there was a
 * failure.</li> </ul> <p> The resource path reference follows Unix-style path, following these conventions: <ul>
 * <li>All paths start with forward slash -- /</li> <li>All path elements are separated by forward slash.</li> <li>In
 * cases where distinction is required, a path ending with forward slash -- / -- is considered to be a folder.</li>
 * </ul>
 * <p>
 * //@formatter:on
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public interface StorageServiceProvider<C extends Credentials> {

    /**
     * Read folder meta data (without including children).
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path of folder.
     * @return Folder meta data.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If <i>path</i> does not exist.
     * @throws InvalidEntityPathException If <i>path</i> reference a non-folder entity.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FolderEntity listFolderContents(C credentials, String path) throws
            IllegalArgumentException,
            EntityNotFoundException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidPathFormatException;

    /**
     * Check if file or folder exists.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @return {@code true} if entity exists at the given path (either file or folder), {@code false} if not.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If entity does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    boolean exists(C credentials, String path) throws
            IllegalArgumentException,
            CredentialsException,
            InvalidPathFormatException;

    /**
     * Read a file meta data.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @return File entity metadata.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If entity does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity readFileMeta(C credentials, String path) throws
            IllegalArgumentException,
            EntityNotFoundException,
            CredentialsException,
            InvalidPathFormatException;

    /**
     * Read a file.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @param out         Output stream to write file content.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If entity does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    void readFile(C credentials, String path, OutputStream out) throws
            IllegalArgumentException,
            EntityNotFoundException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException;

    /**
     * Create new or overwrite existing file.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @param in          Input stream to read file data.
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(C credentials, String path, InputStream in) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException;

    /**
     * Create new or overwrite existing file. This method supports writing file with revision verification, i.e. - if
     * <i>revision</i> is specified, the process first validates that the last file revision is the same as specified
     * revision.<br> If revisions match, a file is written normally, otherwise (if no match), an exception is
     * generated.<p> This facility provide a collusion prevention mechanism in an environment when multiple writers
     * address the same file. In such a way, a caller can make sure he/she is overwriting the latest file version.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @param in          Input stream to read file data.
     * @param revision    Optional file revision (may be {@code null}).
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(C credentials, String path, InputStream in, String revision) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException;

    /**
     * Create new or overwrite existing file.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @param data        Data to write.
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(C credentials, String path, byte[] data) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException;

    /**
     * Create new or overwrite existing file. This method supports writing file with revision verification, i.e. - if
     * <i>revision</i> is specified, the process first validates that the last file revision is the same as specified
     * revision.<br> If revisions match, a file is written normally, otherwise (if no match), an exception is
     * generated.<p> This facility provide a collusion prevention mechanism in an environment when multiple writers
     * address the same file. In such a way, a caller can make sure he/she is overwriting the latest file version.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file.
     * @param data        Data to write.
     * @param revision    Optional file revision (may be {@code null}).
     * @return File entry representing the file.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws InvalidEntityPathException If path to entity is invalid.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidRevisionException   If <i>revision</i> is not {@code null} and it does not the latest file revision
     *                                    managed by the underlying storage service.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    FileEntity writeFile(C credentials, String path, byte[] data, String revision) throws
            IllegalArgumentException,
            InvalidEntityPathException,
            CredentialsException,
            InvalidRevisionException,
            InvalidPathFormatException;

    /**
     * Delete a file or directory.
     *
     * @param credentials Credentials to access storage service.
     * @param path        Path to file or directory.
     * @throws IllegalArgumentException   If either arguments are {@code null}.
     * @throws EntityNotFoundException    If path to file or folder does not exist.
     * @throws CredentialsException       If provided credentials are not supported by the underlying implementation or it has
     *                                    expired.
     * @throws InvalidPathFormatException If <i>path</i> has invalid format.
     */
    void delete(C credentials, String path) throws
            IllegalArgumentException,
            EntityNotFoundException,
            CredentialsException,
            InvalidPathFormatException;

    /**
     * @return The type credentials this provider requires.
     */
    Class<C> credentialsTypes();
}
