package com.quiptiq.wurmrest.rmi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.rmi.RemoteException;
import java.util.*;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.TilePosition;
import com.quiptiq.wurmrest.api.Village;
import com.quiptiq.wurmrest.api.Villages;
import com.wurmonline.server.webinterface.WebInterface;
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

    /**
     * @return Successful result containing total number of villages on the server.
     */
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

    /**
     * Attempts to get the value for the given key on the village map. If the value found cannot
     * be cast to the expected type or if the value is required but null, logs an error and
     * throws a {@link WebApplicationException}.
     * @param villageMap Map of a single village's data
     * @param key Key for which to retrieve a value
     * @param isRequired Whether or not the value is required. Values that are not required do
     *                   not log an exception if they are null
     * @param <T> Type of value to expect
     * @return The value at the given key.
     */
    @Nullable
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

    private Optional<Village> summaryToVillage(int id, @Nullable Map<String, ?> villageMap) {
        if (villageMap == null) {
            return Optional.empty();
        }
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
        return Optional.of(new Village(id, name, deedId, motto, kingdom, size, founder, mayor,
                disbandTime,disbander, numCitizens, numAllies, numGuards, tokenPosition));
    }

    /**
     * @return Successful result containing all villages on the server. If the villages returned
     * was null, returns an error result.
     */
    public Result<Villages> getVillages() {
        return invoke("getVillages", webInterface -> {
            Map<Integer, String> villages = webInterface.getDeeds(getPassword());
            if (villages == null) {
                return Result.error("Null result");
            }
            boolean nullVillageEncountered = false;
            ArrayList<Village> villagesList = new ArrayList<>(villages.size());
            for (Integer villageId : new TreeSet<>(villages.keySet())) {
                if (villageId == null) {
                    nullVillageEncountered = true;
                } else if (villages.get(villageId) == null){
                    nullVillageEncountered = true;
                } else {
                    Map<String, ?> villageMap = webInterface.getDeedSummary(getPassword(),
                            villageId);
                    Optional<Village> village = summaryToVillage(villageId, villageMap);
                    if (village.isPresent()) {
                        villagesList.add(village.get());
                    } else {
                        nullVillageEncountered = true;
                    }
                }
            }
            if (nullVillageEncountered) {
                logger.warn("Null village id or name found in villages list");
            }
            return Result.success(new Villages(villagesList));
        });
    }

    /**
     * Helper method to retrieve a village by id.
     * @param webInterface Web interface on which to invoke the deed summary call
     * @param villageId Id of the village to retrieve
     * @return Successul result with an optional village that may contain the village if the
     * response was a map formed in the expected way. The optional will contain the village if it
     * was found, or will be empty if it was not. If the villages returned contained a null, will
     * return an error result.
     * @throws RemoteException if an error occurs while calling the web interface
     */
    private Result<Optional<Village>> getVillageById(WebInterface webInterface, int villageId)
            throws RemoteException {
        Map<String, ?> villageMap =
                webInterface.getDeedSummary(getPassword(), villageId);
        Optional<Village> village = summaryToVillage(villageId, villageMap);
        if (village.isPresent()) {
            return Result.success(village);
        } else {
            return Result.error("Null village found for id " + villageId);
        }
    }

    /**
     * Retrieves the summary for the village with the given name.
     * @param name of the village whose summary should be retrieved.
     * @return Result containing an Optional village. Result success is dependent on the
     * underlying service call, while optional is dependent on whether or not a village was
     * found.
     */

    public Result<Optional<Village>> getVillage(@Nonnull String name) {
        return invoke("getVillage(String)", webInterface -> {
            Map<Integer, String> villages = webInterface.getDeeds(getPassword());
            if (villages == null) {
                return Result.error("Null result");
            }
            boolean nullVillageEncountered = false;
            for (Integer villageId : villages.keySet()) {
                String villageName = villages.get(villageId);
                if (villageName == null) {
                    nullVillageEncountered = true;
                    continue;
                }
                if (name.equals(villageName)) {
                    return getVillageById(webInterface, villageId);
                }
            }
            if (nullVillageEncountered) {
                logger.warn("Null village id or name found in villages list");
            }
            // Call itself was successful, even if no values were returned
            return Result.success(Optional.empty());
        });
    }

    /**
     * Retrieves the summary for the village with the given id.
     * @param villageId Id of the village whose summary should be retrieved.
     * @return Result containing an Optional village. Result success is dependent on the
     * underlying service call, while optional is dependent on whether or not a village was
     * found.
     */
    public Result<Optional<Village>> getVillage(int villageId) {
        return invoke("getVillage(int)", webInterface -> getVillageById(webInterface, villageId));
    }
}
