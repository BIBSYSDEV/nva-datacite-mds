package no.unit.nva.datacite.config;

import static nva.commons.utils.JsonUtils.objectMapper;
import com.amazonaws.secretsmanager.caching.SecretCache;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import no.unit.nva.datacite.models.DataCiteMdsClientConfig;
import no.unit.nva.datacite.models.DataCiteMdsClientSecretConfig;

public class DataciteConfigurationFactory {
    public static final String ENVIRONMENT_NAME_DATACITE_MDS_CONFIGS = "DATACITE_MDS_CONFIGS";

    private final Map<String, DataCiteMdsClientSecretConfig> dataCiteMdsClientConfigsMap = new ConcurrentHashMap<>();
    private final SecretCache secretCache;

    // Replace SecretCache with AWSSecretsManagerClientBuilder.standard() , we dont reload configuration..
    public DataciteConfigurationFactory(SecretCache secretCache, String secretId) {
        this.secretCache = secretCache;
        loadSecretsFromSecretManager(secretId);
    }

    protected DataCiteMdsClientSecretConfig getCredentials(String customerId) {
        return dataCiteMdsClientConfigsMap.get(customerId);
    }


    public DataCiteMdsClientConfig getConfig(String customerId) {
        return dataCiteMdsClientConfigsMap.get(customerId);
    }

    private void loadSecretsFromSecretManager(String secretId) {
        try {
            String secretASJson =
                secretCache.getSecretString(secretId);
            var dataCiteMdsClientConfigs = objectMapper.readValue(secretASJson,
                DataCiteMdsClientSecretConfig[].class);
            if (dataCiteMdsClientConfigs != null) {
                for (DataCiteMdsClientSecretConfig dataCiteMdsClientSecretConfig : dataCiteMdsClientConfigs) {
                    dataCiteMdsClientConfigsMap.put(
                        dataCiteMdsClientSecretConfig.getInstitution(), dataCiteMdsClientSecretConfig);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not parse configuration from " + secretId);
        }
    }
}
