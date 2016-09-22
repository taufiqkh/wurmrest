package com.quiptiq.wurmrest.rmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Deed;
import com.quiptiq.wurmrest.api.Deeds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides services for deeds information
 */
public class DeedService extends RmiGameService {
    private static Logger logger = LoggerFactory.getLogger(DeedService.class);

    /**
     * Constructs the RmiGameService using rmiProvider to provide a WebInterface. The WebInterface
     * is retrieved immediately so that a malformed URL may be discovered on the start of the
     * service.
     *
     * @param rmiProvider Provides the WebInterface for RMI invocation
     */
    public DeedService(RmiProvider rmiProvider) {
        super(rmiProvider);
    }

    public Result<Deeds> getDeedsTotal() {
        return invoke("getDeedsTotal", webInterface -> {
            Map<Integer, String> deeds = webInterface.getDeeds(getPassword());
            if (deeds == null) {
                deeds = new HashMap<>();
            }
            return Result.success(new Deeds(deeds.size()));
        });
    }

    public Result<Deeds> getDeeds() {
        return invoke("getDeeds", webInterface -> {
            Map<Integer, String> deeds = webInterface.getDeeds(getPassword());
            if (deeds == null) {
                deeds = new HashMap<>();
            }
            boolean nullDeedEncountered = false;
            ArrayList<Deed> deedsList = new ArrayList<>(deeds.size());
            for (Integer deedId : new TreeSet<>(deeds.keySet())) {
                if (deedId == null) {
                    nullDeedEncountered = true;
                } else if (deeds.get(deedId) == null){
                    nullDeedEncountered = true;
                } else {
                    deedsList.add(new Deed(deedId, deeds.get(deedId)));
                }
            }
            if (nullDeedEncountered) {
                logger.warn("Null deed id or name found in deeds list");
            }
            return Result.success(new Deeds(deedsList));
        });
    }
}
