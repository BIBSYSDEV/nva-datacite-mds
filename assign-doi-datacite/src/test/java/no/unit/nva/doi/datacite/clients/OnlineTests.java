package no.unit.nva.doi.datacite.clients;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import java.net.URI;
import java.util.Map;
import no.unit.nva.doi.datacite.clients.exception.ClientException;
import no.unit.nva.doi.datacite.clients.models.Doi;
import no.unit.nva.doi.datacite.config.DataCiteConfigurationFactory;
import no.unit.nva.doi.datacite.config.DataCiteConfigurationFactoryForSystemTests;
import no.unit.nva.doi.datacite.config.PasswordAuthenticationFactory;
import no.unit.nva.doi.datacite.mdsclient.DataCiteConnectionFactory;
import no.unit.nva.doi.datacite.models.DataCiteMdsClientSecretConfig;
import nva.commons.utils.Environment;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class OnlineTests {

    public static final URI DATACITE_DRAFT_DOI_REST_API = URI.create("https://api.test.datacite.org/dois");
    public static final int DEFAULT_HTTPS_PORT = 443;
    private static final URI EXAMPLE_CUSTOMER_ID = URI.create("https://example.net/customer/id/4512");

    @Test
    @Tag("online")
    void createDoiTest() throws ClientException {
        DataCiteConfigurationFactory configFactory = mockConfigFactory();

        var passwordFactory = new PasswordAuthenticationFactory(configFactory);

        var connectionFactory = new DataCiteConnectionFactory(passwordFactory, DATACITE_DRAFT_DOI_REST_API.getHost(),
            DEFAULT_HTTPS_PORT);
        var doiClient = new DataCiteClient(configFactory, connectionFactory);
        Doi doi = doiClient.createDoi(EXAMPLE_CUSTOMER_ID);
        assertThat(doi, is(not(nullValue())));
    }

    private DataCiteConfigurationFactory mockConfigFactory() {
        String password = new Environment().readEnv("TESTTO_NVA_PASSWORD");
        String unitDoiPrefix = "10.16903";
        String nvaTestDataciteAccount = "TESTTO.NVA";
        var config = new DataCiteMdsClientSecretConfig(EXAMPLE_CUSTOMER_ID,
            unitDoiPrefix,
            DATACITE_DRAFT_DOI_REST_API,
            nvaTestDataciteAccount,
            password);

        return new DataCiteConfigurationFactoryForSystemTests(Map.of(EXAMPLE_CUSTOMER_ID, config));
    }
}
