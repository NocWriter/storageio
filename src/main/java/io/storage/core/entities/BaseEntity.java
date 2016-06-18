package io.storage.core.entities;

import java.time.Instant;

/**
 * Common traits of folder/file/references.
 *
 * @author Guy Raz Nir
 * @since 26/06/2017.
 */
public abstract class BaseEntity {

    /**
     * Name of entity (name of file or folder).
     */
    public String name;

    /**
     * Full path to the entity, e.g.: {@code /contents/documents/logs/trace.txt}.
     */
    public String path;

    /**
     * Path to parent. For example, if this entity's {@link #path} is {@code /contents/documents/logs/trace.txt}, than the
     * {@code parentPath} is {@code /contents/documents/logs/}.
     */
    public String parentPath;

    /**
     * Timestamp when entity was created.
     */
    public Instant creationDate;

    /**
     * Class constructor.
     */
    public BaseEntity() {
    }

    /**
     * Copy the state of this object to another object.
     *
     * @param target Target object to copy to.
     */
    protected void copyTo(BaseEntity target) {
        target.name = this.name;
        target.path = this.path;
        target.parentPath = this.parentPath;
    }

}
