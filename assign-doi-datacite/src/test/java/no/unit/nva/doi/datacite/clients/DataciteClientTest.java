package no.unit.nva.doi.datacite.clients;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import no.unit.nva.doi.datacite.clients.exception.ClientException;
import no.unit.nva.doi.datacite.clients.models.Doi;
import no.unit.nva.doi.datacite.config.DataciteConfigurationFactory;
import no.unit.nva.doi.datacite.config.DataciteConfigurationFactoryForSystemTests;
import no.unit.nva.doi.datacite.mdsclient.DataCiteMdsConnection;
import no.unit.nva.doi.datacite.mdsclient.DataciteMdsConnectionFactory;
import no.unit.nva.doi.datacite.models.DataCiteMdsClientSecretConfig;
import nva.commons.utils.log.LogUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataciteClientTest extends DataciteClientTestBase {

    public static final String ERROR_DOICLIENT_METHOD_DELETE_DRAFT_DOI = "deleteDraftDoi";
    private static final String EXAMPLE_CUSTOMER_ID = "https://example.net/customer/id/4512";
    private static final String DEMO_PREFIX = "10.5072";
    private static final String INSTITUTION_PREFIX = DEMO_PREFIX;
    private static final String EXAMPLE_MDS_USERNAME = "exampleUserName";
    private static final String EXAMPLE_MDS_PASSWORD = "examplePassword";
    private final String mdsHost = "example.net";
    private DataCiteMdsClientSecretConfig validSecretConfig;

    private DataciteConfigurationFactory configurationFactory;

    private DataciteClient sut;
    private DataciteMdsConnectionFactory mdsConnectionFactory;

    private DataCiteMdsConnection mdsConnectionThrowingIoException;

    @BeforeEach
    void setUp() throws InterruptedException, IOException, URISyntaxException {
        configurationFactory = createDataConfigurationFactoryForTest();

        mdsConnectionFactory = mock(DataciteMdsConnectionFactory.class);
        mdsConnectionThrowingIoException = mock(DataCiteMdsConnection.class);
        when(mdsConnectionFactory.getAuthenticatedConnection(anyString())).thenReturn(mdsConnectionThrowingIoException);

        when(mdsConnectionThrowingIoException.deleteDoi(anyString())).thenThrow(IOException.class);

        sut = new DataciteClient(configurationFactory, mdsConnectionFactory);
    }

    @Test
    void deleteDraftDoiForCustomerWhereTransportExceptionHappensThenThrowsClientException() {
        Doi doi = createDoiWithDemoPrefixAndExampleSuffix();

        var actualException = assertThrows(ClientException.class, () -> sut.deleteDraftDoi(EXAMPLE_CUSTOMER_ID, doi));
        assertThat(actualException.getMessage(), containsString(ERROR_DOICLIENT_METHOD_DELETE_DRAFT_DOI));
    }

    @Test
    void transExceptionWhichThrowsClientExceptionIsLogged() {
        var appender = LogUtils.getTestingAppender(DataciteClient.class);
        Doi doi = createDoiWithDemoPrefixAndExampleSuffix();

        assertThrows(ClientException.class, () -> sut.deleteDraftDoi(EXAMPLE_CUSTOMER_ID, doi));
        assertThat(appender.getMessages(), containsString(ERROR_DOICLIENT_METHOD_DELETE_DRAFT_DOI));
    }

    private DataciteConfigurationFactoryForSystemTests createDataConfigurationFactoryForTest() {
        validSecretConfig = new DataCiteMdsClientSecretConfig(EXAMPLE_CUSTOMER_ID,
            INSTITUTION_PREFIX, mdsHost, EXAMPLE_MDS_USERNAME, EXAMPLE_MDS_PASSWORD);
        return new DataciteConfigurationFactoryForSystemTests(
            Map.of(EXAMPLE_CUSTOMER_ID, validSecretConfig));
    }
}