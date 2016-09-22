package com.quiptiq.wurmrest.rmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.quiptiq.wurmrest.Result;
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
                    villagesList.add(new Village(villageId, villages.get(villageId)));
                }
            }
            if (nullvillageEncountered) {
                logger.warn("Null village id or name found in villages list");
            }
            return Result.success(new Villages(villagesList));
        });
    }
}
