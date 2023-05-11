package userService.mapper;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.history.AnnotationRevisionMetadata;
import org.springframework.data.history.Revision;
import userService.utils.EasyRandomParametersCustom;
import userservice.entity.User;
import userservice.entity.envers.RevisionEntityCustom;
import userservice.mapper.RevisionMapper;

import java.util.stream.Collectors;

class RevisionMapperTest {
    private final RevisionMapper revisionMapper = Mappers.getMapper(RevisionMapper.class);
    private final EasyRandom easyRandom = new EasyRandom(new EasyRandomParametersCustom());

    @Test
    @DisplayName("Test of mapping Revision to RevisionDto (POSITIVE)")
    @SuppressWarnings("unchecked")
    void applyRevision() {
        final var user = easyRandom.nextObject(User.class);
        final var revision = Revision.of(easyRandom.nextObject(AnnotationRevisionMetadata.class), user);

        Assertions.assertDoesNotThrow(() -> revisionMapper.apply(revision));
    }

    @Test
    @DisplayName("Test of mapping List<Revision> to List<RevisionDto> (POSITIVE)")
    @SuppressWarnings("rawtypes")
    void applyRevisions() {
        final var user = easyRandom.nextObject(User.class);
        final var revisions = easyRandom.objects(RevisionEntityCustom.class, 15).map(revisionEntity -> {
            final var metadata = new AnnotationRevisionMetadata<Long>(revisionEntity, RevisionNumber.class, RevisionTimestamp.class);
            return (Revision) Revision.of(metadata, user);
        }).collect(Collectors.toList());

        Assertions.assertDoesNotThrow(() -> revisionMapper.apply(revisions));
    }
}