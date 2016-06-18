package io.storage.providers.jfs;

import io.storage.utils.Assert;
import io.storage.utils.SecureRandomIdGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple repository based on in-memory hash map ({@code ConcurrentHashMap}). This implementation also provides a unique
 * identifier generator.
 *
 * @author Guy Raz Nir
 * @since 16/11/2017
 */
public class SimpleMapRepository<R> implements SimpleRepository<R> {

    /**
     * Generator of unique identifiers.
     */
    private final SecureRandomIdGenerator idGenerator = new SecureRandomIdGenerator();

    /**
     * Memory-based map for storing resources.
     */
    protected final ConcurrentMap<String, R> resources = new ConcurrentHashMap<>();

    @Override
    public R get(String id) throws IllegalArgumentException {
        Assert.notNull(id, "Identifier cannot be null.");
        return resources.get(id);
    }

    /**
     * Store resource in memory map.
     *
     * @param resource Resource to store.
     * @return The resource's identifier.
     * @throws IllegalArgumentException If <i>resource</i> is {@code null}.
     */
    protected String store(R resource) throws IllegalArgumentException {
        Assert.notNull(resource, "Resource cannot be null.");
        String id;

        //
        // Generate unique identifier and store resource in memory map.
        //
        do {
            id = idGenerator.generate();
        } while (resources.putIfAbsent(id, resource) != null);
        return id;
    }
}
