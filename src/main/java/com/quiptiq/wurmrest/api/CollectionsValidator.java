package com.quiptiq.wurmrest.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validator for immutable collections representations, to ensure that count and collection are
 * kept consistent.
 */
class CollectionsValidator {
    /**
     * Checks that the collection size and count are both the same. If the collection is null,
     * performs no other checks and returns null.
     * @param count Expected number of items in the collection
     * @param collection Collection to be validated
     * @param <T> Type of object within the collection
     * @return Unmodifiable copy of the collection
     * @throws IllegalArgumentException if the count and collection size are not the same
     */
    <T> List<T> validatedImmutable(Integer count, List<T> collection) {
        if (collection == null) {
            return null;
        } else if (count != null && collection.size() != count) {
            throw new IllegalArgumentException("Collection does not match expected count");
        }
        return Collections.unmodifiableList(new ArrayList<>(collection));
    }

    <T> List<T> validatedImmutable(List<T> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null if a count is not " +
                    "specified");
        }
        return Collections.unmodifiableList(new ArrayList<>(collection));
    }
}
