package no.unit.nva.events.handlers;

import static nva.commons.utils.StringUtils.stackTraceInSingleLine;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import no.unit.nva.events.models.AwsEventBridgeEvent;
import nva.commons.utils.IoUtils;
import nva.commons.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  Implemented as RequestStreamHandler because RequestHandler has problem with java.time.Instant class.
  Probably the class RequestHandler does not include the java-8-module.
 */
public abstract class EventHandler<I, O> implements RequestStreamHandler {

    public static final String HANDLER_INPUT = "Handler input:\n";
    public static final String ERROR_WRITING_TO_OUTPUT_STREAM = "Error writing output to output stream. Output is: ";
    private final Class<I> iclass;
    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    /*
      Raw class usage in order to support parameterized types when EventHandler is extended by another class.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected EventHandler(Class iclass) {
        super();
        this.iclass = (Class<I>) iclass;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        try {
            String inputString = IoUtils.streamToString(inputStream);
            logger.trace(HANDLER_INPUT + inputString);
            AwsEventBridgeEvent<I> input = parseEvent(inputString);
            O output = processInput(input.getDetail(), input, context);

            writeOutput(outputStream, output);
        } catch (Exception e) {
            handleError(e);
            throw e;
        }
    }

    protected void writeOutput(OutputStream outputStream, O output) {
        {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                String responseJson = JsonUtils.objectMapper.writeValueAsString(output);
                writer.write(responseJson);
            } catch (IOException e) {
                logger.error(ERROR_WRITING_TO_OUTPUT_STREAM + output.toString());
                throw new UncheckedIOException(e);
            }
        }
    }

    protected abstract O processInput(I input, AwsEventBridgeEvent<I> event, Context context);

    protected AwsEventBridgeEvent<I> parseEvent(String input) {
        return new EventParser<I>(input).parse(iclass);
    }

    private void handleError(Exception e) {
        logger.error(stackTraceInSingleLine(e));
    }
}