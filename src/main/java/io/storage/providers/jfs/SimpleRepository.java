package io.storage.providers.jfs;

/**
 * A simple repository that allows a lookup of resource based on a string identifier.
 *
 * @param <R> Type of resource managed by this repository.
 * @author Guy Raz Nir
 * @since 15/11/2017
 */
public interface SimpleRepository<R> {

    /**
     * Fetch a resource denoted by a given identifier.
     *
     * @param id Identifier of resource.
     * @return Resource.
     */
    R get(String id) throws IllegalArgumentException;

}
