package io.storage.core.entities;

import java.time.Instant;

/**
 * An entity representing a file.
 *
 * @author Guy Raz Nir
 * @since 26/06/2017
 */
public class FileEntity extends BaseEntity {

    /**
     * File size, in bytes.
     */
    public long size;

    /**
     * Size of file in human readable format (e.g.: 1,455 bytes, 1.8GB, etc....).
     */
    public String humanReadableSize;

    /**
     * Timestamp when file was last modified. This field is optional, since not all storage service providers support file
     * modification timestamp.
     */
    public Instant modificationDate;

}
