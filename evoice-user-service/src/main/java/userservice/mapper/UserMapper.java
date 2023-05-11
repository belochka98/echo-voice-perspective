package userservice.mapper;

import apicore.rest.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import userservice.entity.User;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends Function<User, UserDto> {
    @Override
    UserDto apply(User source);

    List<UserDto> to(List<User> source);

    User apply(UserDto user);

    List<User> from(List<UserDto> source);
}
