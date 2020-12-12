package io.storage.providers.dropbox;

import io.storage.TestCompatibilityKit;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * Dropbox service provider comparability tests.<p>
 * <p>
 * Runs only if {@link DropboxStorageServiceProviderITest#DROPBOX_ACCESS_TOKEN_NAME DROPBOX_ACCESS_TOKEN} is available
 * either via system properties (e.g.: {@code java -DDROPBOX_ACCESS_TOKEN=....} or via environment variable.
 *
 * @author Guy Raz Nir
 * @since 18/12/2017
 */
@EnabledIf("io.storage.providers.dropbox.DropboxStorageServiceProviderITest#accessTokenAvailable()")
public class DropboxStorageServiceProviderITest extends TestCompatibilityKit<DropboxCredentials> {

    /**
     * Name of Dropbox access token system property or environment variable.
     */
    public static final String DROPBOX_ACCESS_TOKEN_NAME = "DROPBOX_ACCESS_TOKEN";

    /**
     * Error message thrown if access key is not found.
     */
    private static final String ERROR_MESSAGE =
            String.format("Missing system property/environment variable '%s'.", DROPBOX_ACCESS_TOKEN_NAME);

    /**
     * Class constructor.
     */
    public DropboxStorageServiceProviderITest() {
        super(new DropboxStorageServiceProvider(), new DropboxCredentials(retrieveAccessToken()));
    }

    /**
     * Search for Dropbox access token in system properties and environment variables.
     *
     * @return Access token value.
     * @throws IllegalStateException If token could not be found in any environment.
     */
    public static String retrieveAccessToken() throws IllegalStateException {
        String accessToken = System.getProperty(DROPBOX_ACCESS_TOKEN_NAME, null);
        if (accessToken == null) {
            accessToken = System.getenv(DROPBOX_ACCESS_TOKEN_NAME);
        }

        if (accessToken == null) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        return accessToken;
    }

    /**
     * @return {@code true} if Dropbox access token is available via system properties / environment variable,
     * {@code false} if not.
     */
    public static boolean accessTokenAvailable() {
        try {
            retrieveAccessToken();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

}
