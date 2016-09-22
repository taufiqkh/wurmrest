package com.quiptiq.wurmrest.api;

import javax.annotation.Nullable;
import java.util.List;

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
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Villages)) {
            return false;
        }
        Villages other = (Villages) obj;
        return total == other.total
                && (count == null || count.equals(other.count))
                && validator.listEquals(villages, other.villages);
    }

    @Override
    public int hashCode() {
        int result = validator.listHashCode(villages);
        result = 31 * result + ((count == null) ? 0 : count);
        return 31 * result + total;
    }
}
