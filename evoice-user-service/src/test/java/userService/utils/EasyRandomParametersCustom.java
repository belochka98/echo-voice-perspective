package userService.utils;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.FieldPredicates;
import org.springframework.data.history.AnnotationRevisionMetadata;
import userservice.entity.User;
import userservice.entity.envers.RevisionEntityCustom;

public class EasyRandomParametersCustom extends org.jeasy.random.EasyRandomParameters {
    public EasyRandomParametersCustom() {
        this.setUserPredicates();
        this.setAnnotationRevisionMetadataPredicates();
    }

    private void setUserPredicates() {
        this.randomize(
                FieldPredicates.inClass(User.class).and(FieldPredicates.named("phone")),
                () -> "9999999999"
        );
    }

    private void setAnnotationRevisionMetadataPredicates() {
        this.randomize(
                AnnotationRevisionMetadata.class,
                () -> {
                    final var revisionEntity = new EasyRandom().nextObject(RevisionEntityCustom.class);
                    return new AnnotationRevisionMetadata<Long>(revisionEntity, RevisionNumber.class, RevisionTimestamp.class);
                }
        );
    }
}
