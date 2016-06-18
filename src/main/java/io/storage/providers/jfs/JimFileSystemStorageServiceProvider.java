package io.storage.providers.jfs;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.nio.file.FileSystem;

public class JimFileSystemStorageServiceProvider extends AbstractFileSystemStorageServiceProvider<JimFSCredentials> {

    private SimpleFileSystemRepository repository;

    /**
     * Class constructor.
     */
    public JimFileSystemStorageServiceProvider() {
        super(JimFSCredentials.class, new SimpleFileSystemRepository());
    }

    /**
     * Class constructor.
     *
     * @param repository Repository to use for handling mapping between credentials and file-system.
     */
    private JimFileSystemStorageServiceProvider(SimpleFileSystemRepository repository) {
        super(JimFSCredentials.class, repository);
        this.repository = repository;
    }

    @Override
    public JimFSCredentials createFileSystem() {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        JimFSCredentials credentials = new JimFSCredentials();
        credentials.id = repository.store(fs);
        return credentials;
    }

    @Override
    protected String getFileSystemIdentifier(JimFSCredentials credentials) throws UnknownCredentialsException {
        return credentials.id;
    }

}
