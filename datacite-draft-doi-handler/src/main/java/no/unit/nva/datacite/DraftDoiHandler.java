package no.unit.nva.datacite;

import com.amazonaws.services.lambda.runtime.Context;
import java.net.URI;
import java.time.Instant;
import javax.xml.bind.JAXBException;
import no.unit.nva.datacite.model.DoiUpdateDto;
import no.unit.nva.events.handlers.DestinationsEventBridgeEventHandler;
import no.unit.nva.events.models.AwsEventBridgeDetail;
import no.unit.nva.events.models.AwsEventBridgeEvent;
import no.unit.nva.publication.doi.dto.Publication;
import no.unit.nva.publication.doi.dto.PublicationHolder;
import no.unit.nva.transformer.Transformer;
import no.unit.nva.transformer.dto.DynamoRecordDto;
import nva.commons.utils.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DraftDoiHandler extends DestinationsEventBridgeEventHandler<PublicationHolder, DoiUpdateDto> {

    private DoiClient doiClient;

    private static final Logger logger = LoggerFactory.getLogger(DraftDoiHandler.class);

    /**
     * Default constructor for DraftDoiHandler.
     */
    @JacocoGenerated
    public  DraftDoiHandler() {
        this(defaultDoiClient());
    }

    @JacocoGenerated
    private static DoiClient defaultDoiClient() {
        // TODO: replace with real datacite client
        return new DoiClient() {
            @JacocoGenerated
            @Override
            public String createDoi(String customerId, String metadataDataciteXml) {
                return "http://example.doi";
            }

            @JacocoGenerated
            @Override
            public void updateMetadata(String customerId, String doi, String metadataDataciteXml) {

            }

            @JacocoGenerated
            @Override
            public void setLandingPage(String customerId, String doi, URI url) {

            }
        };
    }

    /**
     * Constructor for DraftDoiHandler.
     *
     * @param doiClient doiClient
     */
    public DraftDoiHandler(DoiClient doiClient) {
        super(PublicationHolder.class);
        this.doiClient = doiClient;
    }

    private DoiUpdateDto createUpdateDoi(Publication input, String doi) {
        return new DoiUpdateDto.Builder()
            .withDoi(doi)
            .withPublicationId(input.getId())
            .withModifiedDate(Instant.now())
            .build();
    }

    @Override
    protected DoiUpdateDto processInputPayload(PublicationHolder input,
                                               AwsEventBridgeEvent<AwsEventBridgeDetail<PublicationHolder>> event,
                                               Context context) {
        Publication publication = input.getItem();
        String customerId = publication.getInstitutionOwner().toString();
        logger.debug("Received request to create draft new DOI for {}", customerId);

        String dataciteXml = getDataciteXml(publication);
        String doi = doiClient.createDoi(customerId, dataciteXml);
        logger.debug("Drafted new DOI: {}", doi);

        return createUpdateDoi(publication, doi);
    }

    private String getDataciteXml(Publication publication) {
        DynamoRecordDto dynamoRecordDto = DynamoRecordDtoMapper.fromPublication(publication);
        try {
            return new Transformer(dynamoRecordDto).asXml();
        } catch (JAXBException e) {
            throw new RuntimeException("Error transforming publication to datacite XML", e);
        }
    }
}
