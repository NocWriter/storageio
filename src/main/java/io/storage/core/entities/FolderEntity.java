package io.storage.core.entities;

import java.util.List;

/**
 * Representation of a folder properties and contents.
 *
 * @author Guy Raz Nir
 * @since 26/06/2017
 */
public class FolderEntity extends BaseEntity {

    /**
     * List of files contained within this folder. This field is optional, as some API calls will commit this field.
     */
    public List<FileEntity> files;

    /**
     * List of sub-folders contained within this folder. This filed is optional, as some API calls will omit this field.
     */
    public List<FolderEntity> folders;

}
