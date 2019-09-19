package ru.bia.process.handler;

import client.RestClient;
import client.impl.RestClientImpl;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bia.process.model.Order;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Wid(
        widfile = "PickUpCargoService.wid",
        name = "PickUpCargoService",
        displayName = "Запрос на забор груза у уотправителя",
        defaultHandler = "mvel: new ru.bia.process.handler.PickUpCargoHandler()",
        category = "dellin",
        parameters = {
                @WidParameter(name = "order", type = "new ObjectDataType()", runtimeType = "ru.bia.process.model.Order" ),
                @WidParameter(name = "url" )
        },
        results = {
                @WidResult(name = "status" ),
                @WidResult(name = "numberPlate" ),
                @WidResult(name = "arriveAt", type = "new ObjectDataType()", runtimeType = "java.util.Date" ),
                @WidResult(name = "order", type = "new ObjectDataType()", runtimeType = "ru.bia.process.model.Order" )
        },
        mavenDepends = {
                @WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}" )
        }
)
public class PickUpCargoHandler extends AbstractLogOrThrowWorkItemHandler {
    private RestClient restClient = new RestClientImpl();
    private final static Logger logger = LoggerFactory.getLogger(PickUpCargoHandler.class);

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        final String s = String.format("Process Id %s, workItem id %s, workItem name %s workItemParams :",
                workItem.getProcessInstanceId(), workItem.getId(), workItem.getName());
        logger.info(s);
        workItem.getParameters().forEach((key, value) -> logger.info("Name: {}, Value: {}", key, value));

        Map<String, Object> response = Collections.emptyMap();
        try {
            response = restClient.createPickUpOperation(workItem.getParameters());
        } catch (Exception e) {
            handleException(e);
        }

        String status = (String) response.get("status" );
        String orderId = (String) response.get("id" );

        Order order = (Order) workItem.getParameter("order");
        order.setId(orderId);

        Map<String, Object> result = new HashMap<String, Object>() {{
            put("status", status);
            put("order", order);
            put("numberPlate", "A111AA777" );
            put("puttedAt", Date.from(
                    LocalDateTime.now()
                            .plus(2, ChronoUnit.HOURS)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            ));
        }};

        result.forEach((key, value) -> logger.info("Result key:{}, Result value:{}", key, value));
        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }
}
