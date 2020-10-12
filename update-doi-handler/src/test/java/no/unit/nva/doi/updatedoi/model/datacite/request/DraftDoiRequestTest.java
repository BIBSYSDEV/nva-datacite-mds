package no.unit.nva.doi.updatedoi.model.datacite.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.doi.updatedoi.model.datacite.request.DraftDoiRequest.Attributes;
import no.unit.nva.doi.updatedoi.model.datacite.request.DraftDoiRequest.AttributesBuilder;
import no.unit.nva.doi.updatedoi.model.datacite.request.DraftDoiRequest.Data;
import no.unit.nva.doi.updatedoi.model.datacite.request.DraftDoiRequest.DataBuilder;
import nva.commons.utils.JsonUtils;
import org.junit.jupiter.api.Test;

class DraftDoiRequestTest {
    private static final ObjectMapper objectMapper = JsonUtils.objectMapper;
    private static final String EXAMPLE_INSTITUTION_PREFIX = "NO_IDEA_HOW_THIS_LOOKS_LIKE";
    private static final String EXAMPLE_ATTRIBUTE_PREFIX = EXAMPLE_INSTITUTION_PREFIX;

    @Test
    public void testAttributeBuilder() {

        var actual = DraftDoiRequest.AttributesBuilder.newBuilder()
            .withPrefix(EXAMPLE_ATTRIBUTE_PREFIX)
            .build();

        assertThat(actual.getPrefix(), is(equalTo(EXAMPLE_ATTRIBUTE_PREFIX)));
    }

    public void testDraftDoiRequestBuilder() {
        var actual = DraftDoiRequest.DataBuilder.newBuilder()
            .withAttributes(AttributesBuilder.newBuilder().withPrefix(EXAMPLE_ATTRIBUTE_PREFIX))
            .build();

        assertThat(actual.getData(), is(equalTo(new Data(new Attributes(EXAMPLE_ATTRIBUTE_PREFIX)))));
    }

    public void testSerializingDraftDoiRequest_Matches_DataciteExpectations() {
        var draftDoiRequest = DataBuilder.newBuilder()
            .withAttributes(AttributesBuilder.newBuilder().withPrefix(EXAMPLE_ATTRIBUTE_PREFIX))
            .build();

        assertThat(objectMapper.writeValueAsString(draftDoiRequest), is(equalTo(Uti))
    }

}