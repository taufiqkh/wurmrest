package com.quiptiq.wurmrest.rmi;

import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.TilePosition;
import com.quiptiq.wurmrest.api.Village;
import com.quiptiq.wurmrest.api.Villages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides services for villages information
 */
public class VillageService extends RmiGameService {
    private static Logger logger = LoggerFactory.getLogger(VillageService.class);

    /**
     * Constructs the RmiGameService using rmiProvider to provide a WebInterface. The WebInterface
     * is retrieved immediately so that a malformed URL may be discovered on the start of the
     * service.
     *
     * @param rmiProvider Provides the WebInterface for RMI invocation
     */
    public VillageService(RmiProvider rmiProvider) {
        super(rmiProvider);
    }

    public Result<Villages> getVillagesTotal() {
        return invoke("getVillagesTotal", webInterface -> {
            Map<Integer, String> villages = webInterface.getDeeds(getPassword());
            if (villages == null) {
                villages = new HashMap<>();
            }
            return Result.success(new Villages(villages.size()));
        });
    }

    private String getNameOrMsg(@Nonnull Map<String, ?> villageMap) {
        Object nameObject = villageMap.get("Name");
        if (nameObject == null) {
            return "[null]";
        } else if (nameObject instanceof String) {
            return (String) nameObject;
        }
        return "[Invalid Object]";
    }

    private <T> T attemptMapGet(@Nonnull Map<String, ?> villageMap, String key,
                                boolean isRequired) {
        T result;
        try {
            result = (T) villageMap.get(key);
        } catch (ClassCastException e) {
            String name = getNameOrMsg(villageMap);
            String errorMsg = "Cast exception while attempting to retrieve village " + name +
                    ", key " + key;
            logger.error(errorMsg);
            throw new WebApplicationException(errorMsg, Response.Status.BAD_GATEWAY);
        }
        if (result == null && isRequired) {
            String name = getNameOrMsg(villageMap);
            String errorMsg = "Null value found for village " + name + ", key " + key;
            throw new WebApplicationException(errorMsg, Response.Status.BAD_GATEWAY);
        }
        return result;
    }
    private <T> T attemptMapGet(@Nonnull Map<String, ?> villageMap, String key) {
        return attemptMapGet(villageMap, key, true);
    }

    private Village summaryToVillage(int id, @Nonnull Map<String, ?> villageMap) {
        String name = attemptMapGet(villageMap, "Name");
        Long deedId = attemptMapGet(villageMap, "Deedid", false);
        String motto = attemptMapGet(villageMap, "Motto");
        String kingdom = attemptMapGet(villageMap, "Location");
        Integer size = attemptMapGet(villageMap, "Size");
        String founder = attemptMapGet(villageMap, "Founder");
        String mayor = attemptMapGet(villageMap, "Mayor");
        String disbandTime = attemptMapGet(villageMap, "Disbanding in", false);
        String disbander = attemptMapGet(villageMap, "Disbander", false);
        Integer numCitizens = attemptMapGet(villageMap, "Citizens");
        Integer numAllies = attemptMapGet(villageMap, "Allies");
        Integer numGuards = attemptMapGet(villageMap, "guards", false);
        Integer tokenX = attemptMapGet(villageMap, "Token coord x", false);
        Integer tokenY = attemptMapGet(villageMap, "Token coord y", false);
        if (tokenX == null ^ tokenY == null) {
            throw new WebApplicationException("Token x and y coordinates must both be specified, " +
                    "or neither");
        }
        TilePosition tokenPosition;
        if (tokenX == null) {
            tokenPosition = null;
        } else {
            tokenPosition = new TilePosition(tokenX, tokenY);
        }
        return new Village(id, name, deedId, motto, kingdom, size, founder, mayor, disbandTime,
                disbander, numCitizens, numAllies, numGuards, tokenPosition);
    }

    public Result<Villages> getVillages() {
        return invoke("getVillages", webInterface -> {
            Map<Integer, String> villages = webInterface.getDeeds(getPassword());
            if (villages == null) {
                villages = new HashMap<>();
            }
            boolean nullvillageEncountered = false;
            ArrayList<Village> villagesList = new ArrayList<>(villages.size());
            for (Integer villageId : new TreeSet<>(villages.keySet())) {
                if (villageId == null) {
                    nullvillageEncountered = true;
                } else if (villages.get(villageId) == null){
                    nullvillageEncountered = true;
                } else {
                    Map<String, ?> villageMap = webInterface.getDeedSummary(getPassword(),
                            villageId);
                    if (villageMap == null) {
                        nullvillageEncountered = true;
                        continue;
                    }
                    villagesList.add(summaryToVillage(villageId, villageMap));
                }
            }
            if (nullvillageEncountered) {
                logger.warn("Null village id or name found in villages list");
            }
            return Result.success(new Villages(villagesList));
        });
    }
}
