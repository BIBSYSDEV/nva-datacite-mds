package no.unit.nva.datacite;

import com.amazonaws.secretsmanager.caching.SecretCache;
import com.amazonaws.services.lambda.runtime.Context;

import com.google.gson.Gson;
import no.unit.nva.datacite.exception.InstitutionIdUnknownException;
import no.unit.nva.datacite.exception.MissingParametersException;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;

import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class DataCiteMdsCreateDoiHandler extends ApiGatewayHandler<CreateDoiRequest, CreateDoiResponse> {

    public static final String ERROR_MISSING_REQUEST_JSON_BODY =
            "Request JSON body not present";
    public static final String ERROR_MISSING_JSON_ATTRIBUTE_VALUE_DATACITE_XML =
            "JSON attribute 'dataciteXml' is mandatory";
    public static final String ERROR_MISSING_JSON_ATTRIBUTE_VALUE_URL =
            "JSON attribute 'url' is mandatory";
    public static final String ERROR_MISSING_JSON_ATTRIBUTE_VALUE_INSTITUTION_ID =
            "JSON attribute 'institutionId' is mandatory";
    public static final String ERROR_RETRIEVING_DATACITE_MDS_CLIENT_CONFIGS =
            "Error retrieving DataCite MDS client configs";
    public static final String ERROR_INSTITUTION_IS_NOT_SET_UP_AS_DATACITE_PROVIDER =
            "Provided Institution ID is not set up as a DataCite provider";
    public static final String ERROR_SETTING_DOI_URL_COULD_NOT_DELETE_METADATA =
            "Error setting DOI url, error deleting metadata";
    public static final String ERROR_SETTING_DOI_METADATA = "Error setting DOI metadata";
    public static final String ERROR_SETTING_DOI_URL = "Error setting DOI url";
    public static final String ERROR_DELETING_DOI_METADATA = "Error deleting DOI metadata";

    public static final String CHARACTER_PARENTHESES_START = "(";
    public static final String CHARACTER_PARENTHESES_STOP = ")";
    public static final String CHARACTER_WHITESPACE = " ";

    private final transient Map<String, DataCiteMdsClientConfig> dataCiteMdsClientConfigsMap =
            new ConcurrentHashMap<>();
    private transient SecretCache secretCache = new SecretCache();
    private transient DataCiteMdsConnection dataCiteMdsConnection;

    private  static final String ENVIRONMENT_NAME_DATACITE_MDS_CONFIGS = "DATACITE_MDS_CONFIGS";

    /**
     * Default constructor for DataCiteMdsCreateDoiHandler.
     */
    @JacocoGenerated
    public DataCiteMdsCreateDoiHandler() {
        this(new Environment(), null, null);
    }

    /**
     * Constructor for DataCiteMdsCreateDoiHandler.
     *
     * @param environment        environment
     * @param dataCiteMdsConnection dataCiteMdsConnection
     * @param secretCache secretCache
     */
    public DataCiteMdsCreateDoiHandler(Environment environment,
                                       DataCiteMdsConnection dataCiteMdsConnection,
                                       SecretCache secretCache) {
        super(CreateDoiRequest.class, environment);

        if (dataCiteMdsConnection != null) {
            this.dataCiteMdsConnection = dataCiteMdsConnection;
        }

        if (secretCache != null) {
            this.secretCache = secretCache;
        }

        setDataCiteMdsConfigsFromSecretsManager(environment);
    }

    private void checkParameters(CreateDoiRequest input) throws InstitutionIdUnknownException,
            MissingParametersException {
        if (Objects.isNull(input)) {
            throw new MissingParametersException(ERROR_MISSING_REQUEST_JSON_BODY);
        }
        if (StringUtils.isEmpty(input.getDataciteXml())) {
            throw new MissingParametersException(ERROR_MISSING_JSON_ATTRIBUTE_VALUE_DATACITE_XML);
        }
        if (StringUtils.isEmpty(input.getUrl())) {
            throw new MissingParametersException(ERROR_MISSING_JSON_ATTRIBUTE_VALUE_URL);
        }
        if (StringUtils.isEmpty(input.getInstitutionId())) {
            throw new MissingParametersException(ERROR_MISSING_JSON_ATTRIBUTE_VALUE_INSTITUTION_ID);
        }
        if (!dataCiteMdsClientConfigsMap.containsKey(input.getInstitutionId())) {
            throw new InstitutionIdUnknownException(ERROR_INSTITUTION_IS_NOT_SET_UP_AS_DATACITE_PROVIDER);
        }
    }

    private void setDataCiteMdsConfigsFromSecretsManager(Environment environment) {
        try {
            String secretASJson =
                    secretCache.getSecretString(environment.readEnv(ENVIRONMENT_NAME_DATACITE_MDS_CONFIGS));
            DataCiteMdsClientConfig[] dataCiteMdsClientConfigs = new Gson().fromJson(secretASJson,
                    DataCiteMdsClientConfig[].class);
            if (dataCiteMdsClientConfigs != null) {
                for (DataCiteMdsClientConfig dataCiteMdsClientConfig : dataCiteMdsClientConfigs) {
                    dataCiteMdsClientConfigsMap.put(dataCiteMdsClientConfig.getInstitution(), dataCiteMdsClientConfig);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(ERROR_RETRIEVING_DATACITE_MDS_CLIENT_CONFIGS);
        }
    }

    public static void log(String message) {
        System.out.println(message);
    }

    @Override
    protected CreateDoiResponse processInput(CreateDoiRequest input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {

        checkParameters(input);

        // Create DataCite connection for institution
        DataCiteMdsClientConfig dataCiteMdsClientConfig = dataCiteMdsClientConfigsMap.get(input.getInstitutionId());
        dataCiteMdsConnection.configure(dataCiteMdsClientConfig.getDataCiteMdsClientUrl(),
                dataCiteMdsClientConfig.getDataCiteMdsClientUsername(),
                dataCiteMdsClientConfig.getDataCiteMdsClientPassword());

        return createDoi(dataCiteMdsClientConfig, input.getUrl(), input.getDataciteXml());
    }

    @Override
    protected Integer getSuccessStatusCode(CreateDoiRequest input, CreateDoiResponse output) {
        return HttpStatus.SC_CREATED;
    }

    private CreateDoiResponse createDoi(DataCiteMdsClientConfig dataCiteMdsClientConfig,
                                        String url, String dataciteXml) {

        // Register DOI metadata and retrieve generated DOI
        String createdDoi;
        try (CloseableHttpResponse createMetadataResponse =
                     dataCiteMdsConnection.postMetadata(dataCiteMdsClientConfig.getInstitutionPrefix(), dataciteXml)) {
            if (createMetadataResponse.getStatusLine().getStatusCode() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException(ERROR_SETTING_DOI_METADATA + CHARACTER_WHITESPACE
                        + CHARACTER_PARENTHESES_START
                        + createMetadataResponse.getStatusLine().getStatusCode()
                        + CHARACTER_PARENTHESES_STOP);
            }
            String createMetadataResponseBody = EntityUtils.toString(createMetadataResponse.getEntity(),
                    StandardCharsets.UTF_8.name());
            createdDoi = StringUtils.substringBetween(createMetadataResponseBody, CHARACTER_PARENTHESES_START,
                    CHARACTER_PARENTHESES_STOP);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(ERROR_SETTING_DOI_METADATA);
        }

        // Set DOI URL (Landing page)
        try (CloseableHttpResponse createDoiResponse = dataCiteMdsConnection.postDoi(createdDoi, url)) {
            if (createDoiResponse.getStatusLine().getStatusCode() == Response.Status.CREATED.getStatusCode()) {
                return new CreateDoiResponse(createdDoi);
            } else {
                log(ERROR_SETTING_DOI_URL
                        + CHARACTER_WHITESPACE
                        + CHARACTER_PARENTHESES_START
                        + createDoiResponse.getStatusLine().getStatusCode()
                        + CHARACTER_PARENTHESES_STOP);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Registering DOI URL has failed, delete metadata
        try (CloseableHttpResponse deleteDoiMetadata = dataCiteMdsConnection.deleteMetadata(createdDoi)) {
            if (deleteDoiMetadata.getStatusLine().getStatusCode() == Response.Status.OK.getStatusCode()) {
                throw new RuntimeException(ERROR_SETTING_DOI_URL);
            } else {
                log(ERROR_DELETING_DOI_METADATA
                        + CHARACTER_WHITESPACE
                        + CHARACTER_PARENTHESES_START
                        + deleteDoiMetadata.getStatusLine().getStatusCode()
                        + CHARACTER_PARENTHESES_STOP);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        log(ERROR_SETTING_DOI_URL_COULD_NOT_DELETE_METADATA + CHARACTER_WHITESPACE
                + CHARACTER_PARENTHESES_START
                + createdDoi + CHARACTER_PARENTHESES_STOP);
        throw new RuntimeException(ERROR_SETTING_DOI_URL_COULD_NOT_DELETE_METADATA);
    }
}
