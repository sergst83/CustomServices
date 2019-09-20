package ru.bia.process.handler;

import client.RestClient;
import client.impl.RestClientImpl;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Wid(
        widfile = "CheckPickUpService.wid",
        name = "CheckPickUpService",
        displayName = "Проверка статуса забора груза у отправителя",
        description = "Проверка статуса забора груза у отправителя",
        defaultHandler = "mvel: new ru.bia.process.handler.CheckPickUpHandler()",
        category = "dellin",
        parameters = {
                @WidParameter(name = "order", type = "new ObjectDataType()", runtimeType = "ru.bia.process.model.Order" ),
                @WidParameter(name = "url" )
        },
        results = {
                @WidResult(name = "status" ),
                @WidResult(name = "numberPlate" ),
                @WidResult(name = "arriveAt", type = "new ObjectDataType()", runtimeType = "java.util.Date" ),
                @WidResult(name = "pickUpAt", type = "new ObjectDataType()", runtimeType = "java.util.Date" ),
                @WidResult(name = "order", type = "new ObjectDataType()", runtimeType = "ru.bia.process.model.Order" )
        }
)
public class CheckPickUpHandler extends AbstractLogOrThrowWorkItemHandler {
    private final static Logger logger = LoggerFactory.getLogger(CheckPickUpHandler.class);

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        RestClient restClient = new RestClientImpl();

        final String s = String.format("Process Id %s, workItem id %s, workItem name %s workItemParams :",
                workItem.getProcessInstanceId(), workItem.getId(), workItem.getName());
        logger.info(s);

        workItem.getParameters().forEach((key, value) -> logger.info("Name: " + key + ", Value: " + value));

        Map<String, Object> response = Collections.emptyMap();
        try {
            response = restClient.checkPickUpOperation(workItem.getParameters());
        } catch (Exception e) {
            handleException(e);
        }

        String status = (String) response.get("status" );
        String numberPlate = (String) response.get("numberPlate");
        Date arriveAt = (Date) response.get("arriveAt");
        Date pickUpAt = (Date) response.get("pickUpAt");

        Map<String, Object> result = new HashMap<String, Object>() {{
            put("status", status);
            put("arriveAt", arriveAt);
            put("numberPlate", numberPlate);
            put("pickUpAt", pickUpAt);
            put("order", workItem.getParameter("order"));
        }};

        result.forEach((key, value) -> logger.info("Result key:{}, Result value:{}", key, value));

        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
