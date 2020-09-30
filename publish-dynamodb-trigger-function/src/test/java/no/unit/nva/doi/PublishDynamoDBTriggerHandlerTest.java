package no.unit.nva.doi;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import no.unit.nva.doi.lambda.PublishDynamoDBTriggerHandler;
import no.unit.nva.doi.publisher.EventPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PublishDynamoDBTriggerHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws Exception {
        EventPublisher eventPublisher = event -> {
            try {
                new ObjectMapper().writeValue(System.out, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        PublishDynamoDBTriggerHandler handler = new PublishDynamoDBTriggerHandler(eventPublisher);
        Context context = Mockito.mock(Context.class);
        File eventFile = new File("src/test/resources/event.json");
        DynamodbEvent event = objectMapper.readValue(eventFile, DynamodbEvent.class);

        handler.handleRequest(event, context);
    }
}
