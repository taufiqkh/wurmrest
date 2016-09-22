package com.quiptiq.wurmrest.api;

import javax.annotation.Nullable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ordered collection object for deeds information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Deeds {
    private final static CollectionsValidator validator = new CollectionsValidator();
    /**
     * Total number in the system matching a given filter
     */
    private final int total;
    /**
     * Count of all deeds in the current collection
     */
    private final Integer count;
    private final List<Deed> deeds;

    @JsonCreator
    public Deeds(@JsonProperty("total") int total,
                 @JsonProperty("count") @Nullable Integer count,
                 @JsonProperty("deeds") @Nullable List<Deed> deeds) {
        this.deeds = validator.validatedImmutable(count, deeds);
        this.total = total;
        if (count == null && deeds != null) {
            this.count = deeds.size();
        } else {
            this.count = count;
        }
    }

    public Deeds(int total) {
        this.total = total;
        this.count = null;
        this.deeds = null;
    }

    public Deeds(List<Deed> deeds) {
        this.deeds = validator.validatedImmutable(deeds);
        this.count = deeds.size();
        this.total = deeds.size();
    }

    public int getTotal() {
        return total;
    }

    @Nullable
    public Integer getCount() {
        return count;
    }

    @Nullable
    public List<Deed> getDeeds() {
        return deeds;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Deeds)) {
            return false;
        }
        Deeds other = (Deeds) obj;
        return total == other.total
                && (count == null || count.equals(other.count))
                && validator.listEquals(deeds, other.deeds);
    }

    @Override
    public int hashCode() {
        int result = validator.listHashCode(deeds);
        result = 31 * result + ((count == null) ? 0 : count);
        return 31 * result + total;
    }
}
