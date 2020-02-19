package no.unit.nva.datacite;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigTest {

    public static final String DUMMY_DATACITE_CONFIGS_SECRET_ID = "secretId";
    public static final String DUMMY_NVA_HOST = "nvaHost";

    @Test
    public void testCorsHeaderNotSet() {
        final Config config = Config.getInstance();
        config.setCorsHeader(null);
        final String corsHeader = config.getCorsHeader();
        assertNull(corsHeader);
    }

    @Test
    public void testDataCiteConfigsNotSet() {
        final Config config = Config.getInstance();
        config.setDataCiteMdsConfigsSecretId(null);
        final String dataCiteMdsConfigsSecretId = config.getDataCiteMdsConfigsSecretId();
        assertNull(dataCiteMdsConfigsSecretId);
    }

    @Test
    public void testNvaHostNotSet() {
        final Config config = Config.getInstance();
        config.setNvaHost(null);
        final String nvaHost = config.getNvaHost();
        assertNull(nvaHost);
    }

    @Test(expected = RuntimeException.class)
    public void testCheckPropertiesNotSet() {
        final Config config = Config.getInstance();
        config.setDataCiteMdsConfigsSecretId(null);
        config.checkProperties();
        fail();
    }

    @Test
    public void testCheckPropertiesSet() {
        final Config instance = Config.getInstance();
        instance.setDataCiteMdsConfigsSecretId(DUMMY_DATACITE_CONFIGS_SECRET_ID);
        instance.setNvaHost(DUMMY_NVA_HOST);
        assertTrue(instance.checkProperties());
    }
}
