package ru.bia.process.handler;

import client.RestClient;
import client.impl.RestClientImpl;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Wid(
        widfile = "PutToWarehouse.wid",
        name = "PutToWarehouse",
        displayName = "Запрос на подготовку склада к приему груза",
        description = "Запрос на подготовку склада к приему груза",
        defaultHandler = "mvel: new ru.bia.process.handler.PutToWarehouseHandler()",
        category = "dellin",
        parameters = {
                @WidParameter(name = "order", type = "new ObjectDataType()", runtimeType = "ru.bia.process.model.Order" ),
                @WidParameter(name = "arriveAt", type = "new ObjectDataType()", runtimeType = "java.util.Date" ),
                @WidParameter(name = "url" )
        },
        results = {
                @WidResult(name = "status" )
        }
)
public class PutToWarehouseHandler extends AbstractLogOrThrowWorkItemHandler {
    private final static Logger logger = LoggerFactory.getLogger(PutToWarehouseHandler.class);

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        RestClient restClient = new RestClientImpl();

        final String s = String.format("Process Id %s, workItem id %s, workItem name %s workItemParams :",
                workItem.getProcessInstanceId(), workItem.getId(), workItem.getName());
        logger.info(s);
        workItem.getParameters().forEach((key, value) -> logger.info("Name: {}, Value: {}", key, value));

        Map<String, Object> response = Collections.emptyMap();
        try {
            response = restClient.checkPutWhOperation(workItem.getParameters());
        } catch (Exception e) {
            handleException(e);
        }

        String status = (String) response.get("putWhStatus");

        Map<String, Object> result = new HashMap<String, Object>() {{
            put("status", status);
        }};

        result.forEach((key, value) -> logger.info("Result key:{}, Result value:{}", key, value));

        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
