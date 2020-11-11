package no.unit.nva.datacite.mdsclient;

import static nva.commons.utils.JsonUtils.objectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

/**
 * DataCiteMdsConnect instance for handling the HTTP communication with Datacite MDS API.
 *
 * <p>The HttpClient provided should have a {@link java.net.Authenticator} associated to do pre-emptive
 * authentication towards the API server.
 *
 * <p>Use the {@link DataciteMdsConnectionFactory#getAuthenticatedConnection(String)} to construct new instances.
 */
public class DataCiteMdsConnection {

    public static final String HTTPS = "https";
    public static final String DATACITE_PATH_DOI = "doi";
    public static final String DATACITE_PATH_METADATA = "metadata";
    public static final String FORM_PARAM_DOI = "doi";
    public static final String FORM_PARAM_URL = "url";

    public static final String CHARACTER_SLASH = "/";
    public static final String APPLICATION_XML_CHARSET_UTF_8 = "application/xml; charset=UTF-8";

    private final transient HttpClient httpClient;
    private final String host;
    private final int port;

    /**
     * Constructor for testability reasons.
     *
     * @param httpClient HttpClient
     */
    public DataCiteMdsConnection(HttpClient httpClient, String host, int port) {
        this.httpClient = httpClient;
        this.host = host;
        this.port = port;
    }

    /**
     * This request stores a new version of metadata.
     *
     * @param doi         prefix/suffix
     * @param dataciteXml resource metadata as Datacite XML, encoded with UTF-8.
     * @return HttpResponse
     * @throws IOException          IOException
     * @throws URISyntaxException   URISyntaxException
     * @throws InterruptedException InterruptedException
     */
    public HttpResponse<String> postMetadata(String doi, String dataciteXml) throws IOException,
                                                                                    URISyntaxException,
                                                                                    InterruptedException {

        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(host)
            .setPort(port)
            .setPath(DATACITE_PATH_METADATA + CHARACTER_SLASH + doi)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(dataciteXml))
            .uri(uri)
            .header(HttpHeaders.CONTENT_TYPE, APPLICATION_XML_CHARSET_UTF_8)
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * This request requests the most recent version of metadata associated with a given DOI.
     *
     * @param doi prefix/suffix
     * @return CloseableHttpResponse
     * @throws IOException          IOException
     * @throws URISyntaxException   URISyntaxException
     * @throws InterruptedException InterruptedException
     */
    public HttpResponse<String> getMetadata(String doi) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(host)
            .setPort(port)
            .setPath(DATACITE_PATH_METADATA + CHARACTER_SLASH + doi)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * This request marks a dataset as inactive. To activate it again, add new metadata.
     *
     * @param doi prefix/suffix
     * @return HttpResponse
     * @throws IOException          IOException
     * @throws URISyntaxException   URISyntaxException
     * @throws InterruptedException InterruptedException
     */
    public HttpResponse<String> deleteMetadata(String doi) throws IOException, URISyntaxException,
                                                                  InterruptedException {
        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(host)
            .setPort(port)
            .setPath(DATACITE_PATH_METADATA + CHARACTER_SLASH + doi)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .DELETE()
            .uri(uri)
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * This requests the URL associated with a given DOI.
     *
     * @param doi prefix/suffix
     * @return HttpResponse
     * @throws IOException          IOException
     * @throws URISyntaxException   URISyntaxException
     * @throws InterruptedException InterruptedException
     */
    public HttpResponse<String> getDoi(String doi) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(host)
            .setPort(port)
            .setPath(DATACITE_PATH_DOI + CHARACTER_SLASH + doi)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Deletes a DOI if DOI is in draft status.
     *
     * @param doi prefix/suffix
     * @return HttpResponse
     * @throws IOException          IOException
     * @throws URISyntaxException   URISyntaxException
     * @throws InterruptedException InterruptedException
     */
    public HttpResponse<String> deleteDoi(String doi) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(host)
            .setPort(port)
            .setPath(DATACITE_PATH_DOI + CHARACTER_SLASH + doi)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .DELETE()
            .uri(uri)
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Will register a new DOI if the specified DOI doesn’t exist. This method will attempt to update the URL if you
     * specify an existing DOI.
     *
     * @param doi         prefix/suffix
     * @param landingPage landing page landingPage
     * @return HttpResponse
     * @throws IOException          IOException
     * @throws URISyntaxException   URISyntaxException
     * @throws InterruptedException InterruptedException
     */
    public HttpResponse<String> registerUrl(String doi, String landingPage) throws IOException, URISyntaxException,
                                                                                   InterruptedException {
        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(host)
            .setPort(port)
            .setPath(DATACITE_PATH_DOI)
            .build();

        HashMap<String, String> formParams = new HashMap<>();
        formParams.put(FORM_PARAM_DOI, doi);
        formParams.put(FORM_PARAM_URL, landingPage);

        String requestBody = objectMapper.writeValueAsString(formParams);

        HttpRequest request = HttpRequest.newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
            .uri(uri)
            .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpClient getHttpClient() {
        return httpClient;
    }
}
