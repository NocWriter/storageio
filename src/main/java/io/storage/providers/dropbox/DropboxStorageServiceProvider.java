package io.storage.providers.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import io.storage.StorageException;
import io.storage.core.*;
import io.storage.core.entities.FileEntity;
import io.storage.core.entities.FolderEntity;
import io.storage.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dropbox storage service provider.
 *
 * @author Guy Raz Nir
 * @since 03/07/2017
 */
public class DropboxStorageServiceProvider extends AbstractStorageServiceProvider<DropboxCredentials> {

    /**
     * Cache Dropbox client for optimal performance.
     */
    private final Map<String, DbxClientV2> clientCache = new ConcurrentHashMap<>();

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DropboxStorageServiceProvider.class);

    /**
     * Class constructor.
     */
    public DropboxStorageServiceProvider() {
        super(DropboxCredentials.class);
    }

    @Override
    public FolderEntity listFolderContents(DropboxCredentials credentials, String path)
            throws IllegalArgumentException, EntityNotFoundException, InvalidEntityPathException, CredentialsException {
        //
        // Create new folder entry.
        //
        FolderEntity folder = new FolderEntity();
        folder.name = extractFolderName(path);
        folder.path = path;
        folder.files = new LinkedList<>();
        folder.folders = new LinkedList<>();

        //
        // Query Dropbox service for folder contents.
        //
        logger.info("listFolderContents: Querying path: {}.", path);
        ListFolderResult result = execute("list folder", credentials, path,
                (client, folderPath) -> client.files().listFolder(adjustPath(folderPath)));
        logger.info("listFolderContents: Found {} entries at path {} (has more: {}).",
                result.getEntries().size(),
                path,
                result.getHasMore());

        //
        // Translate each Dropbox record to StorageIO file/folder entities.
        //
        for (Metadata metadata : result.getEntries()) {
            // Process Dropbox file/folder metadata, skip other types (such as 'DeletedMetadata').
            if (metadata instanceof FileMetadata) {
                folder.files.add(createFileEntry(path, (FileMetadata) metadata));
            } else if (metadata instanceof FolderMetadata) {
                folder.folders.add(createFolderEntry(path, (FolderMetadata) metadata));
            }
        }

        return folder;
    }

    @Override
    public boolean exists(DropboxCredentials credentials, String path)
            throws IllegalArgumentException, CredentialsException, InvalidPathFormatException {

        return execute("exists", credentials, path,
                (client, filePath) -> {
                    try {
                        return "/".equals(filePath) || client.files().getMetadata(path) != null;
                    } catch (GetMetadataErrorException ex) {
                        return false;
                    }
                });
    }

    @Override
    public FileEntity readFileMeta(DropboxCredentials credentials, String path)
            throws IllegalArgumentException, EntityNotFoundException, CredentialsException, InvalidPathFormatException {
        Metadata metadata = execute("read file metadata", credentials, path,
                (client, filePath) -> client.files().getMetadata(path));

        if (metadata instanceof FileMetadata) {
            return createFileEntry(path, (FileMetadata) metadata);
        } else {
            throw new EntityNotFoundException(
                    "Invalid entity time: " + path + " (expected file-class entity, found directory-class entity).");
        }
    }

    @Override
    public FileEntity writeFile(DropboxCredentials credentials, String path, InputStream in)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException {
        return writeFileInternal(credentials, path, in, null);
    }

    @Override
    public FileEntity writeFile(DropboxCredentials credentials, String path, InputStream in, String revision)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException {
        return writeFileInternal(credentials, path, in, revision);
    }

    @Override
    public FileEntity writeFile(DropboxCredentials credentials, String path, byte[] data)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException {
        return writeFileInternal(credentials, path, new ByteArrayInputStream(data), null);
    }

    @Override
    public FileEntity writeFile(DropboxCredentials credentials, String path, byte[] data, String revision)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException {
        return writeFileInternal(credentials, path, new ByteArrayInputStream(data), revision);
    }

    @Override
    public void delete(DropboxCredentials credentials, String path)
            throws IllegalArgumentException, EntityNotFoundException, CredentialsException, InvalidPathFormatException {
        execute("delete", credentials, path, (client, filePath) -> {
            client.files().delete(filePath);
            return null;
        });
    }

    @Override
    public void readFile(DropboxCredentials credentials, String path, OutputStream out)
            throws IllegalArgumentException, EntityNotFoundException, CredentialsException, InvalidRevisionException {
        Assert.notNull(out, "Output stream is null.");

        FileMetadata file = execute("read file", credentials, path, (client, filePath) -> {
            DbxDownloader<FileMetadata> downloader = client.files().download(path);
            downloader.download(out);
            downloader.close();
            return downloader.getResult();
        });
        logger.info("Successfully downloaded file {} ({} bytes).", file.getName(), file.getSize());
    }

    private FileEntity writeFileInternal(DropboxCredentials credentials, String path, InputStream in, String revision)
            throws IllegalArgumentException, InvalidEntityPathException, CredentialsException, InvalidRevisionException {
        Assert.notNull(in, "Input stream is null.");

        FileMetadata file = execute("write file", credentials, path, (client, filePath) -> {
            UploadBuilder build = client.files().uploadBuilder(adjustPath(path)).withAutorename(false);
            if (revision != null) {
                build.withMode(WriteMode.update(revision));
            }

            return build.uploadAndFinish(in);
        });
        return createFileEntry(path, file);
    }

    /**
     * Normalize a path by formatting it to always start with forward slash ("/").
     *
     * @param path Path to normalize.
     * @return Normalized path or {@code null} if <i>path</i> is {@code null}.
     */
    private String normalizePath(String path) throws IllegalArgumentException {
        Assert.notNull(path, "Path cannot be null.");

        if (path.isEmpty()) {
            path = "/";
        } else {
            if (!path.startsWith("/") && path.contains("/")) {
                path = "/" + path;
            }
        }

        return path;
    }


    /**
     * Get or create Dropbox client. This method will first lookup in memory cache and if not found, will create a new
     * one.
     *
     * @param credentials Credentials.
     * @return Dropbox V2 client.
     */
    private DbxClientV2 getClient(DropboxCredentials credentials) throws IllegalArgumentException {
        validateCredentials(credentials);

        logger.debug("Looking up Dropbox client for access token {}.", credentials.accessToken);
        DbxClientV2 client = clientCache.get(credentials.accessToken);
        if (client == null) {
            logger.info("Creating new Dropbox client for access token {}.", credentials.accessToken);
            DbxRequestConfig config = new DbxRequestConfig("StorageIO");
            client = new DbxClientV2(config, credentials.accessToken);
            clientCache.put(credentials.accessToken, client);
        }

        return client;
    }

    /**
     * Mark credentials as expired and remove associated Dropbox client (if exist).
     *
     * @param credentials Credentials to expire. If this value is {@code null}, no action is performed and no error is
     *                    generated.
     */
    private void expireClient(DropboxCredentials credentials) {
        if (credentials != null) {
            clientCache.remove(credentials.accessToken);
        }
    }

    /**
     * Handle invalid access token by expiring associate Dropbox client, logging the event and generating exception.
     *
     * @param credentials Credentials that has expired.
     * @param ex          The actual Dropbox exception.
     * @throws CredentialsException Always generated.
     */
    @SuppressWarnings("UnusedReturnValue")
    private CredentialsException handleInvalidAccessTokenException(DropboxCredentials credentials,
                                                                   InvalidAccessTokenException ex) throws CredentialsException {
        logger.error("Invalid access token -- {}", credentials.accessToken, ex);
        expireClient(credentials);
        throw new CredentialsException("Invalid access token", ex);
    }

    /**
     * Creates a new {@code FileEntry} from a given Dropbox file metadata.
     *
     * @param parentPath Path of parent directory.
     * @param source     Dropbox file metadata.
     * @return File entry representing Dropbox file metadata.
     */
    private FileEntity createFileEntry(String parentPath, FileMetadata source) {
        FileEntity file = new FileEntity();
        file.name = source.getName();
        file.path = parentPath + (parentPath.endsWith("/") ? "" : "/") + file.name;
        file.parentPath = parentPath;
        file.creationDate = null;   // Dropbox service does not support file creation date.
        file.modificationDate = source.getServerModified().toInstant();
        file.size = source.getSize();
        file.humanReadableSize = toHumanReadableSize(file.size);
        return file;
    }

    /**
     * Creates a new {@code FolderEntry} from a given Dropbox folder metadata.
     *
     * @param parentPath Parent folder path.
     * @param source     Dropbox folder metadata.
     * @return Folder entry for the given Dropbox folder metadata.
     */
    private FolderEntity createFolderEntry(String parentPath, FolderMetadata source) {
        FolderEntity folder = new FolderEntity();
        folder.name = source.getName();
        folder.path = parentPath + (parentPath.endsWith("/") ? "" : "/") + folder.name;
        folder.parentPath = parentPath.equals("/") ? "" : parentPath;
        return folder;
    }

    /**
     * Adjust a given path to match Dropbox service specifications.
     *
     * @param path Path to adjust.
     * @return Adjusted path.
     */
    private String adjustPath(String path) throws IllegalArgumentException {
        if (path.isEmpty() || path.equals("/")) {
            path = "";
        } else {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
        }

        return path;
    }

    /**
     * Extract a folder name from a given path.
     *
     * @param path Path to extract folder name. <b>NOTE: </b>This parameter must not be {@code null} has this method does
     *             not validate input.
     * @return Folder name.
     */
    private String extractFolderName(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return "/";
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        int index = path.lastIndexOf('/');
        path = path.substring(index);

        return path;
    }

    /**
     * Executes a Dropbox operation providing standard behavior, mainly formatting input parameters, fetching Dropbox
     * client instance and translating exceptions.
     *
     * @param operationName Name of operation. Required for logging and exception handling.
     * @param credentials   Credentials to access the service.
     * @param path          Path to resource.
     * @param consumer      The consumer that executes the operation.
     * @param <T>           Generic type of consumer's return type.
     * @return Result value.
     */
    private <T> T execute(String operationName, DropboxCredentials credentials, String path, Callee<T> consumer) {
        return execute(operationName, credentials, path, consumer, null);
    }

    /**
     * Executes a Dropbox operation providing standard behavior, mainly formatting input parameters, fetching Dropbox
     * client instance and translating exceptions.
     *
     * @param operationName   Name of operation. Required for logging and exception handling.
     * @param credentials     Credentials to access the service.
     * @param path            Path to resource.
     * @param consumer        The consumer that executes the operation.
     * @param cleanupCallback Optional clean-up callback that is executed within the {@code finally} block. Provided to
     *                        reduce code.
     * @param <T>             Generic type of consumer's return type.
     * @return Result value.
     */
    private <T> T execute(String operationName, DropboxCredentials credentials, String path, Callee<T> consumer,
                          Procedure cleanupCallback) {
        // Validate credentials and fetch client.
        DbxClientV2 client = getClient(credentials);

        // Validate and normalize path.
        path = normalizePath(path);
        try {
            return consumer.apply(client, path);
        } catch (InvalidAccessTokenException ex) {
            // Handle invalid credentials, possibly the token has been expired.
            throw handleInvalidAccessTokenException(credentials, ex);
        } catch (UploadErrorException ex) {
            if (ex.errorValue.isPath()) {
                throw new InvalidEntityPathException("Invalid path: " + path, ex);
            } else {
                throw new StorageException("Operation " + operationName + " failed.", ex);
            }
        } catch (DbxException ex) {
            throw new StorageException("Operation " + operationName + " failed.", ex);
        } catch (IOException ex) {
            throw new StorageException("I/O error occurred during " + operationName, ex);
        } finally {
            if (cleanupCallback != null) {
                cleanupCallback.invoke();
            }
        }
    }

    /**
     * Definition of functional callee that executes a Dropbox service operation. The callee accepts a client instance and
     * validated/normalized path.
     *
     * @param <R> Generic return type.
     */
    @FunctionalInterface
    interface Callee<R> {

        /**
         * Execute Dropbox service call.
         *
         * @param client Dropbox V2 client to perform operation.
         * @param path   Normalized path.
         * @return Operation return value.
         * @throws InvalidAccessTokenException If used access token is invalid.
         * @throws DbxException                If any Dropbox-business error occurred.
         * @throws IOException                 If any I/O error occurred during operation.
         */
        R apply(DbxClientV2 client, String path)
                throws InvalidAccessTokenException, DbxException, IOException, StorageException;
    }

    /**
     * A simple no-arguments, no return-type function (simply put: a procedure).
     */
    @FunctionalInterface
    interface Procedure {

        void invoke();
    }
}
