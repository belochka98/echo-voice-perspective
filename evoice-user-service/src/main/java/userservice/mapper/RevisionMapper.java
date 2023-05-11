package userservice.mapper;

import apicore.envers.RevisionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.history.Revision;
import userservice.entity.envers.RevisionEntityCustom;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, Instant.class, ZoneId.class, RevisionEntityCustom.class}, uses = RevisionMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
@SuppressWarnings("rawtypes")
public interface RevisionMapper extends Function<Revision, RevisionDto> {
    @Override
    @Mapping(target = "id", expression = "java(source.getRequiredRevisionNumber().longValue())")
    @Mapping(target = "operation", expression = "java(source.getMetadata().getRevisionType())")
    @Mapping(target = "date", expression = "java(LocalDateTime.ofInstant(source.getMetadata().getRequiredRevisionInstant(), ZoneId.systemDefault()))")
    @Mapping(target = "userName", expression = "java(((RevisionEntityCustom) source.getMetadata().getDelegate()).getUserName())")
    @Mapping(target = "object", expression = "java(source.getEntity())")
    RevisionDto apply(Revision source);

    List<RevisionDto> apply(List<Revision> source);
}
