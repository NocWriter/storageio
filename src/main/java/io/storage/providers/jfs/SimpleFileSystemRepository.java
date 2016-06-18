package io.storage.providers.jfs;

import java.nio.file.FileSystem;

/**
 * Simple map-based memory repository for {@code FileSystem}s.
 *
 * @author Guy Raz Nir
 * @since 17/11/2017
 */
public class SimpleFileSystemRepository extends SimpleMapRepository<FileSystem> {

}
