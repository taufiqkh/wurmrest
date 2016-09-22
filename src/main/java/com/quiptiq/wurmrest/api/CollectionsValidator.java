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

    /**
     * Performs a comparison between the two lists provided. Returns true if and only if:
     * <ul>
     *     <li>Both OR neither of the lists are null</li>
     *     <li>The size of the two lists is equal</li>
     *     <li>Each of the elements of the lists are equal, either through both being null OR by
     *     calling .equals on two non-null elements</li>
     * </ul>
     * @param aList First list to compare
     * @param other Second list to compare
     * @param <T> Type of element
     * @return True if the two lists are equal according to the above rules
     */
    <T> boolean listEquals(List<T> aList, List<T> other) {
        if (aList == null) {
            return other == null;
        }
        if (aList.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < aList.size(); i++) {
            if (aList.get(i) == null) {
                if (other.get(i) != null) {
                    return false;
                }
            } else if (!aList.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    <T> int listHashCode(List<T> list) {
        int result = 0;
        if (list == null) {
            return result;
        }
        for (T elem : list) {
            result = 31 * result + elem.hashCode();
        }
        return result;
    }
}
