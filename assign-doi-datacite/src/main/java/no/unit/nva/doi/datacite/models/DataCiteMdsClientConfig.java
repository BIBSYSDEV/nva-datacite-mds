package no.unit.nva.doi.datacite.models;

import static java.util.Objects.nonNull;
import no.unit.nva.doi.datacite.config.DataCiteConfigurationFactory;
import nva.commons.utils.JacocoGenerated;

/**
 * DAO for DataCite MDS Configuration for a associated NVA customer.
 *
 * @see DataCiteMdsClientSecretConfig
 * @see DataCiteConfigurationFactory
 */
public class DataCiteMdsClientConfig {

    protected String institution;
    protected String institutionPrefix;
    protected String dataCiteMdsClientUrl;

    @JacocoGenerated
    public DataCiteMdsClientConfig() {
    }

    /**
     * Construct a {@link DataCiteMdsClientConfig}.
     *
     * @param institution          customerId
     * @param institutionPrefix    customer's prefix for the NVA repository in the Registry Agency
     * @param dataCiteMdsClientUrl Hostname to MDS API environment
     */
    public DataCiteMdsClientConfig(String institution, String institutionPrefix, String dataCiteMdsClientUrl) {
        this.institution = institution;
        this.institutionPrefix = institutionPrefix;
        this.dataCiteMdsClientUrl = dataCiteMdsClientUrl;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getInstitutionPrefix() {
        return institutionPrefix;
    }

    public void setInstitutionPrefix(String institutionPrefix) {
        this.institutionPrefix = institutionPrefix;
    }

    /**
     * Is configuration fully configured with all required values.
     *
     * @return <code>true</code> if config is fully configured.
     */
    public boolean isFullyConfigured() {
        return nonNull(institution) && nonNull(institutionPrefix) && nonNull(dataCiteMdsClientUrl);
    }
}
