package no.unit.nva.datacite.mdsclient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import nva.commons.utils.IoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DataCiteMdsConnectionTest {

    public static final String DATACITE_MDS_OK_RESPONSE = "/dataciteMdsOkResponse.txt";
    public static final String DATACITE_MDS_POST_METADATA_RESPONSE = "/dataciteMdsPostMetadataResponse.txt";
    public static final String DATACITE_MDS_GET_DOI_RESPONSE = "/dataciteMdsGetDoiResponse.txt";
    public static final String DATACITE_XML_RESOURCE_EXAMPLE = "/dataciteXmlResourceExample.xml";

    public static final String MOCK_HOST = "nva-mock.unit.no";
    public static final String MOCK_LANDING_PAGE_URL = "https://nva-mock.unit.no/123456789";
    public static final String MOCK_DOI_PREFIX = "prefix";
    public static final String MOCK_DOI = "prefix/suffix";
    public static final String MOCK_DATACITE_XML = "mock-xml";
    public static final String MOCK_USER = "MOCK_USER";
    public static final String MOCK_PASSWORD = "MOCK_PASSWORD";
    private static final int MOCK_PORT = 8888;

    @Mock
    HttpClient httpClient;

    @Mock
    HttpResponse<String> httpResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDataCiteMdsConnection() {
        DataCiteMdsConnection dataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);
        assertNotNull(dataCiteMdsConnection);
    }

    @Test
    public void testDataCiteMdsConnectionConfigure() {
        DataCiteMdsConnection dataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);
        assertNotNull(dataCiteMdsConnection);
    }

    @Test
    public void testPostMetadata() throws IOException, URISyntaxException, InterruptedException {
        InputStream stream = DataCiteMdsConnectionTest.class.getResourceAsStream(DATACITE_MDS_POST_METADATA_RESPONSE);
        String body = IoUtils.streamToString(stream);
        when(httpResponse.body()).thenReturn(body);
        when(httpClient.send(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

        DataCiteMdsConnection dataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);

        HttpResponse<String> httpResponse = dataCiteMdsConnection.postMetadata(MOCK_DOI_PREFIX, MOCK_DATACITE_XML);

        assertNotNull(httpResponse);
        assertNotNull(httpResponse.body());
    }

    @Test
    public void testGetMetadata() throws IOException, URISyntaxException, InterruptedException {
        InputStream stream = DataCiteMdsConnectionTest.class.getResourceAsStream(DATACITE_XML_RESOURCE_EXAMPLE);
        String body = IoUtils.streamToString(stream);
        when(httpResponse.body()).thenReturn(body);
        when(httpClient.send(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

        DataCiteMdsConnection dataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);

        HttpResponse<String> httpResponse = dataCiteMdsConnection.getMetadata(MOCK_DOI);

        assertNotNull(httpResponse);
        assertNotNull(httpResponse.body());
    }

    @Test
    public void testDeleteMetadata() throws IOException, URISyntaxException, InterruptedException {
        InputStream stream = DataCiteMdsConnectionTest.class.getResourceAsStream(DATACITE_MDS_OK_RESPONSE);
        String body = IoUtils.streamToString(stream);
        when(httpResponse.body()).thenReturn(body);
        when(httpClient.send(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

        DataCiteMdsConnection mockDataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);

        HttpResponse<String> httpResponse = mockDataCiteMdsConnection.deleteMetadata(MOCK_DOI);

        assertNotNull(httpResponse);
        assertNotNull(httpResponse.body());
    }

    @Test
    public void testGetDoi() throws IOException, URISyntaxException, InterruptedException {
        InputStream stream = DataCiteMdsConnectionTest.class.getResourceAsStream(DATACITE_MDS_GET_DOI_RESPONSE);
        String body = IoUtils.streamToString(stream);
        when(httpResponse.body()).thenReturn(body);
        when(httpClient.send(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

        DataCiteMdsConnection dataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);

        HttpResponse<String> httpResponse = dataCiteMdsConnection.getDoi(MOCK_DOI);

        assertNotNull(httpResponse);
        assertNotNull(httpResponse.body());
    }

    @Test
    public void testPostDoi() throws IOException, URISyntaxException, InterruptedException {
        InputStream stream = DataCiteMdsConnectionTest.class.getResourceAsStream(DATACITE_MDS_OK_RESPONSE);
        String body = IoUtils.streamToString(stream);
        when(httpResponse.body()).thenReturn(body);
        when(httpClient.send(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

        DataCiteMdsConnection mockDataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);

        HttpResponse<String> httpResponse = mockDataCiteMdsConnection.postDoi(MOCK_DOI, MOCK_LANDING_PAGE_URL);

        assertNotNull(httpResponse);
        assertNotNull(httpResponse.body());
    }

    @Test
    public void testDeleteDoi() throws IOException, URISyntaxException, InterruptedException {
        InputStream stream = DataCiteMdsConnectionTest.class.getResourceAsStream(DATACITE_MDS_OK_RESPONSE);
        String body = IoUtils.streamToString(stream);
        when(httpResponse.body()).thenReturn(body);
        when(httpClient.send(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

        DataCiteMdsConnection dataCiteMdsConnection = new DataCiteMdsConnection(httpClient, MOCK_HOST, MOCK_PORT);

        HttpResponse<String> httpResponse = dataCiteMdsConnection.deleteDoi(MOCK_DOI);

        assertNotNull(httpResponse);
        assertNotNull(httpResponse.body());
    }
}