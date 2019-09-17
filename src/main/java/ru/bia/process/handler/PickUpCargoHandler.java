package ru.bia.process.handler;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PickUpCargoHandler implements WorkItemHandler {
    private final static Logger logger = LoggerFactory.getLogger(PickUpCargoHandler.class);

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        final String s = String.format("Process Id %s, workItem id %s, workItem name %s workItemParams :",
                workItem.getProcessInstanceId(), workItem.getId(), workItem.getName());
        logger.info(s);
        workItem.getParameters().forEach((key, value) -> logger.info("Name: {}, Value: {}", key, value));

        Map<String, Object> result = new HashMap<String, Object>() {{
            put("status", "running"); //succeeded
            put("numberPlate", "A111AA777");
            put("arriveAt", Date.from(
                    LocalDateTime.now()
                            .plus(2, ChronoUnit.HOURS)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                    )
            );
        }};

        result.forEach((key, value) -> logger.info("Result key:{}, Result value:{}", key, value));
        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
