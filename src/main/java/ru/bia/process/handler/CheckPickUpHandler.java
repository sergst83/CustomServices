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

public class CheckPickUpHandler implements WorkItemHandler {
    private final static Logger logger = LoggerFactory.getLogger(CheckPickUpHandler.class);

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        final String s = String.format("Process Id %s, workItem id %s, workItem name %s workItemParams :",
                workItem.getProcessInstanceId(), workItem.getId(), workItem.getName());
        logger.info(s);
        for (Map.Entry<String, Object> parameter : workItem.getParameters().entrySet()) {
            logger.info("Name: " + parameter.getKey() + ", Value: " + parameter.getValue());
        }
        Map<String, Object> result = new HashMap<String, Object>() {{
            put("status", Math.random() < 0.5 ? "succeeded" : "running"); // отдаем рандомный булеан
            put("arriveAt", workItem.getParameter("arriveAt"));
            put("numberPlate", workItem.getParameter("numberPlate"));
        }};
        result.forEach((key, value) -> logger.info("Result key:{}, Result value:{}", key, value));
        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
