package io.storage.core;

import io.storage.utils.Assert;
import io.storage.utils.StringUtils;

/**
 * An abstract implementation of storage service provider. Provides a common facilities and utilities to all service
 * providers.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public abstract class AbstractStorageServiceProvider<C extends Credentials> implements StorageServiceProvider<C> {

    /**
     * Credentials type supported by this implementation.
     */
    protected final Class<C> credentialsType;

    /**
     * Class constructor.
     *
     * @param credentialsType Credentials type required by this implementation.
     */
    protected AbstractStorageServiceProvider(Class<C> credentialsType) {
        Assert.notNull(credentialsType, "Credentials type cannot be null.");
        this.credentialsType = credentialsType;
    }

    /**
     * Validate a given path is considered valid. A valid path must adhere to the following rules: <ul> <li>Must be
     * non-null value.</li> <li>Must start with forward slash -- /.</li> <li>All path elements separator must be forward
     * slashes.</li> <li>Only the following characters are allowed: 0-9, a-z, A-Z, !, dot (.)</li> </ul>
     *
     * @param path Path to validate.
     * @throws InvalidPathFormatException If path is not validate based on the rules above.
     */
    protected void validatePath(String path) throws InvalidPathFormatException {
        if (path == null || !path.startsWith("/")) {
            throw new InvalidPathFormatException("Invalid path format.", path);
        }
    }

    /**
     * Check if a given path is considered a folder or not. A folder path always ends with a forward slash.
     *
     * @param path Path to test.
     * @return {@code true} if <i>path</i> is considered a folder path, {@code false} if not.
     */
    protected boolean isFolder(String path) {
        return path != null && (path.isEmpty() || path.endsWith("/"));
    }

    /**
     * Perform general-type validation to given credentials.
     *
     * @param credentials Credentials to validate.
     * @throws IllegalArgumentException If <i>credentials</i> is {@code null}.
     * @throws CredentialsException     If either credential's storage type is missing or does not match
     *                                  <i>expectedStorageType</i>.
     */
    protected void validateCredentials(C credentials) throws IllegalArgumentException, CredentialsException {
        Assert.notNull(credentials, "Missing credentials (null value).");
    }

    @Override
    public Class<C> credentialsTypes() {
        return credentialsType;
    }

    /**
     * Format a given 64-bit value into human readable string.
     *
     * @param size Size to format.
     * @return Human-readable size.
     */
    protected String toHumanReadableSize(long size) {
        return StringUtils.formatNumber(size);
    }
}
