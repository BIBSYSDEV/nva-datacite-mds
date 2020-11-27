package no.unit.nva.datacite.handlers;

import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;

@JacocoGenerated
public final class AppEnv {

    public static final String DATACITE_MDS_HOST = "DATACITE_MDS_HOST";
    public static final String DATACITE_PORT = "DATACITE_PORT";
    public static final String CUSTOMER_SECRETS_SECRET_NAME = "CUSTOMER_SECRETS_SECRET_NAME";
    public static final String CUSTOMER_SECRETS_SECRET_KEY = "CUSTOMER_SECRETS_SECRET_KEY";
    private static final Environment ENVIRONMENT = new Environment();

    @JacocoGenerated
    private AppEnv() {
    }



    @JacocoGenerated
    public static String getDataCiteHost() {
        return getEnvValue(DATACITE_MDS_HOST);
    }

    @JacocoGenerated
    public static int getDataCitePort() {
        return Integer.parseInt(getEnvValue(DATACITE_PORT));
    }

    @JacocoGenerated
    private static String getEnvValue(final String name) {
        return ENVIRONMENT.readEnv(name);
    }

    @JacocoGenerated
    public static String getCustomerSecretsSecretName() {
        return getEnvValue(CUSTOMER_SECRETS_SECRET_NAME);
    }

    @JacocoGenerated
    public static String getCustomerSecretsSecretKey() {
        return getEnvValue(CUSTOMER_SECRETS_SECRET_KEY);
    }
}