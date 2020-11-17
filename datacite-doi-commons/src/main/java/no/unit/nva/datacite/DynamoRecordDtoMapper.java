package no.unit.nva.datacite;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import no.unit.nva.publication.doi.dto.Contributor;
import no.unit.nva.publication.doi.dto.Publication;
import no.unit.nva.publication.doi.dto.PublicationType;
import no.unit.nva.transformer.dto.CreatorDto;
import no.unit.nva.transformer.dto.DynamoRecordDto;
import no.unit.nva.transformer.dto.IdentifierDto;
import no.unit.nva.transformer.dto.PublisherDto;
import no.unit.nva.transformer.dto.ResourceTypeDto;
import no.unit.nva.transformer.dto.TitleDto;

public final class DynamoRecordDtoMapper {

    private DynamoRecordDtoMapper() {
    }

    /**
     * Maps a Publication to DynamoRecordDto. For use in the nva doi partner data Transformer.
     *
     * @param publication   publication
     * @return  dynamoRecordDto
     */
    public static DynamoRecordDto fromPublication(Publication publication) {
        return new DynamoRecordDto.Builder()
            .withCreator(toCreatorDtoList(publication.getContributor()))
            .withIdentifier(toIdentifierDto(publication.getId()))
            .withPublicationYear(publication.getPublicationDate().getYear())
            .withPublisher(toPublisherDto(publication.getInstitutionOwner()))
            .withTitle(toTitleDto(publication.getMainTitle()))
            .withResourceType(toResourceTypeDto(publication.getType()))
            .build();
    }

    private static ResourceTypeDto toResourceTypeDto(PublicationType value) {
        return new ResourceTypeDto.Builder()
            .withValue(value.name())
            .build();
    }

    private static TitleDto toTitleDto(String value) {
        return new TitleDto.Builder()
            .withValue(value)
            .build();
    }

    private static PublisherDto toPublisherDto(URI value) {
        return new PublisherDto.Builder()
            .withValue(value.toString())
            .build();
    }

    private static IdentifierDto toIdentifierDto(URI value) {
        return new IdentifierDto.Builder()
            .withValue(value.toString())
            .build();
    }

    private static List<CreatorDto> toCreatorDtoList(List<Contributor> contributors) {
        return contributors.stream()
            .map(DynamoRecordDtoMapper::toCreatorDto)
            .collect(Collectors.toList());
    }

    private static CreatorDto toCreatorDto(Contributor contributor) {
        return new CreatorDto.Builder()
            .withCreatorName(contributor.getName())
            .build();
    }

}
