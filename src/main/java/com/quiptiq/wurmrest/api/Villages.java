package com.quiptiq.wurmrest.api;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ordered collection object for villages information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Villages {
    private final static CollectionsValidator validator = new CollectionsValidator();
    /**
     * Total number in the system matching a given filter
     */
    private final int total;
    /**
     * Count of all villages in the current collection
     */
    private final Integer count;
    private final List<Village> villages;

    @JsonCreator
    public Villages(@JsonProperty("total") int total,
                    @JsonProperty("count") @Nullable Integer count,
                    @JsonProperty("villages") @Nullable List<Village> villages) {
        this.villages = validator.validatedImmutable(count, villages);
        this.total = total;
        if (count == null && villages != null) {
            this.count = villages.size();
        } else {
            this.count = count;
        }
    }

    public Villages(int total) {
        this.total = total;
        this.count = null;
        this.villages = null;
    }

    public Villages(List<Village> villages) {
        this.villages = validator.validatedImmutable(villages);
        this.count = villages.size();
        this.total = villages.size();
    }

    public int getTotal() {
        return total;
    }

    @Nullable
    public Integer getCount() {
        return count;
    }

    @Nullable
    public List<Village> getVillages() {
        return villages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Villages villages1 = (Villages) o;
        return total == villages1.total &&
                Objects.equals(count, villages1.count) &&
                Objects.equals(villages, villages1.villages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, count, villages);
    }
}
