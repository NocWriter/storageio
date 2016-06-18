package io.storage.core;

/**
 * Credentials contains the information required to access a certain storage service. A credentials object should only
 * contain serializable state only and not OS-specific resources.
 *
 * @author Guy Raz Nir
 * @since 25/06/2017
 */
public abstract class Credentials {

    /**
     * Unique identifier.
     */
    public String id;

    /**
     * Identifier of the credentials owner. This field is a free-text field providing a way to create correlation between
     * this credentials instance and an external entity, such as user.
     */
    public String ownerId;

    /**
     * Class constructor.
     */
    protected Credentials() throws IllegalArgumentException {
        this(null, null);
    }

    /**
     * Class constructor.
     *
     * @param id      Identifier of this instance. May be {@code null} if unknown at the time of creation.
     * @param ownerId A free-text field that can holds identification information to external entities (such as
     *                corporation or user). This field is optional (may be {@code null}).
     * @throws IllegalArgumentException If <i>storageName</i> is {@code null}.
     */
    protected Credentials(String id, String ownerId) throws IllegalArgumentException {
        this.id = id;
        this.ownerId = ownerId;
    }
}
