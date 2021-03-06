package no.unit.nva.doi.datacite.restclient;

import static nva.commons.core.attempt.Try.attempt;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Base64;
import no.unit.nva.doi.datacite.restclient.models.DraftDoiDto;
import no.unit.nva.doi.datacite.models.DataCiteMdsClientSecretConfig;

public class DataCiteRestConnection {

    public static final String HTTPS = "https";
    public static final String DOIS_PATH = "dois";
    public static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String COLON = ":";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SEPARATOR = "/";
    private final HttpClient httpClient;
    private final String host;
    private final int port;
    private final DataCiteMdsClientSecretConfig configWithSecretes;

    /**
     * A DataCite connection for the RestApi.
     *
     * @param httpClient the httpclient to be used.
     * @param host       the host address without scheme and path
     * @param port       the port (for https 443)
     */
    public DataCiteRestConnection(HttpClient httpClient, String host, int port,
                                  DataCiteMdsClientSecretConfig configWithSecrets) {
        this.httpClient = httpClient;
        this.host = host;
        this.port = port;
        this.configWithSecretes = configWithSecrets;
    }

    /**
     * This request stores a new version of metadata.
     *
     * @return HttpResponse
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    // TODO: remove the Authorization Header when DataCite REST-API prompts for Authentication
    public HttpResponse<String> createDoi()
        throws IOException, InterruptedException {

        String bodyJson = requestBodyContainingTheDoiPrefix();
        HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(requestTargetUri())
            .POST(BodyPublishers.ofString(bodyJson))
            .header(CONTENT_TYPE, JSON_API_CONTENT_TYPE)
            .headers(AUTHORIZATION_HEADER, authorizationString())
            .build();
        return httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getDoi(String id)
            throws IOException, InterruptedException {

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(requestTargetUriToDoi(id))
                .GET()
                .header(ACCEPT, JSON_API_CONTENT_TYPE)
                .headers(AUTHORIZATION_HEADER, authorizationString())
                .build();
        return httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }


    private String requestBodyContainingTheDoiPrefix() {
        DraftDoiDto bodyObject = DraftDoiDto.fromPrefix(configWithSecretes.getCustomerDoiPrefix());
        return bodyObject.toJson();
    }

    private String authorizationString() {
        return basicAuth(
            configWithSecretes.getDataCiteMdsClientUsername(),
            configWithSecretes.getDataCiteMdsClientPassword()
        );
    }

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + COLON + password).getBytes());
    }


    private URI requestTargetUri() {
        return attempt(this::buildUri).orElseThrow();
    }

    private URI buildUri() throws URISyntaxException {
        return new URI(HTTPS,
                null,
                host,
                port,
                SEPARATOR + DOIS_PATH,
                null,
                null
        );
    }

    private URI requestTargetUriToDoi(String id) {
        return attempt(() -> buildUriToDoi(id)).orElseThrow();
    }

    private URI buildUriToDoi(String id) throws URISyntaxException {
        return new URI(HTTPS,
                null,
                host,
                port,
                SEPARATOR + DOIS_PATH + SEPARATOR + id,
                null,
                null
        );
    }
}
