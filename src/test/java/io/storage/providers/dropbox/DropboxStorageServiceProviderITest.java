package io.storage.providers.dropbox;

import io.storage.TestCompatibilityKit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

/**
 * Dropbox service provider comparability tests.
 *
 * @author Guy Raz Nir
 * @since 18/12/2017
 */
@Disabled
public class DropboxStorageServiceProviderITest extends TestCompatibilityKit<DropboxCredentials> {

    private static String DROPBOX_ACCESS_TOKEN = null;

    @BeforeAll
    public static void setUp() {
        DROPBOX_ACCESS_TOKEN = System.getProperty("DROPBOX_ACCESS_TOKEN", null);
        if (DROPBOX_ACCESS_TOKEN == null) {
            System.out.println("In order to run this test, you must define 'DROPBOX_ACCESS_TOKEN' via environment variable or via -D switch.");
            throw new Error();
        }
    }

    /**
     * Class constructor.
     */
    public DropboxStorageServiceProviderITest() {
        super(
                new DropboxStorageServiceProvider(),
                new DropboxCredentials(DROPBOX_ACCESS_TOKEN)
        );
    }

}
