package no.unit.nva.doi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Instant;

public class DoiUpdateDto {

    private final String doi;
    private final URI publicationId;
    private final Instant modifiedDate;

    /**
     * Constructor for DoiUpdateDto.
     *
     * @param doi   doi
     * @param publicationId publicationId
     * @param modifiedDate  modifiedDate
     */
    @JsonCreator
    public DoiUpdateDto(@JsonProperty("doi") String doi,
                        @JsonProperty("publicationId") URI publicationId,
                        @JsonProperty("modifiedDate") Instant modifiedDate) {
        this.doi = doi;
        this.publicationId = publicationId;
        this.modifiedDate = modifiedDate;
    }

    public String getDoi() {
        return doi;
    }

    public URI getPublicationId() {
        return publicationId;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public static class Builder {

        private String doi;
        private URI publicationId;
        private Instant modifiedDate;

        public Builder() {
        }

        public Builder withDoi(String doi) {
            this.doi = doi;
            return this;
        }

        public Builder withPublicationId(URI publicationId) {
            this.publicationId = publicationId;
            return this;
        }

        public Builder withModifiedDate(Instant modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public DoiUpdateDto build() {
            return new DoiUpdateDto(doi, publicationId, modifiedDate);
        }
    }
}
