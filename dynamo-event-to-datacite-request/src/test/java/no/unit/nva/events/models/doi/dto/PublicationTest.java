package no.unit.nva.events.models.doi.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import no.unit.nva.events.models.doi.dto.Publication.PublicationBuilder;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

class PublicationTest {

    private static final String EXAMPLE_ID = "https://example.net/nva/publicationIdentifier";
    private static final String EXAMPLE_DOI_ID = "http://doi.org/11541.2/124530";
    private static final String EXAMPLE_INSTITUTION_OWNER = "???";
    private static final String EXAMPLE_TITLE = "The Matrix";
    private static final String EXAMPLE_CONTRIBUTOR_ID = "https://example.net/contributor/id/4000";
    private static final String EXAMPLE_CONTRIBUTOR_NAME = "Brinx";

    @Test
    public void testBuilder() {
        var publication = PublicationBuilder.newBuilder()
            .withId(URI.create(EXAMPLE_ID))
            .withDoi(URI.create(EXAMPLE_DOI_ID))
            .withInstitutionOwner(URI.create(EXAMPLE_INSTITUTION_OWNER))
            .withPublicationDate(new PublicationDate("1999", "07", "09"))
            .withType(PublicationType.BOOK_ANTHOLOGY)
            .withTitle(EXAMPLE_TITLE)
            .withContributor(List.of(new Contributor.Builder()
                .withId(URI.create(EXAMPLE_CONTRIBUTOR_ID))
                .withName(EXAMPLE_CONTRIBUTOR_NAME)
                .build()))
            .build();

        assertThat(publication.getId(), is(equalTo(URI.create(EXAMPLE_ID))));
        assertThat(publication.getDoi(), is(equalTo(URI.create(EXAMPLE_DOI_ID))));
        assertThat(publication.getInstitutionOwner(), is(equalTo(URI.create(EXAMPLE_INSTITUTION_OWNER))));
        assertThat(publication.getPublicationDate(), is(equalTo(
            new PublicationDate("1999", "07", "09"))));
        assertThat(publication.getMainTitle(), is(equalTo(EXAMPLE_TITLE)));
        assertThat(publication.getType(), is(equalTo(PublicationType.BOOK_ANTHOLOGY)));
        assertThat(publication.getContributor(),
            hasItem(new Contributor(URI.create(EXAMPLE_CONTRIBUTOR_ID), EXAMPLE_CONTRIBUTOR_NAME)));
    }
}