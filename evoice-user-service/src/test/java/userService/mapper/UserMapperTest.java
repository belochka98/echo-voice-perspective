package userService.mapper;

import apicore.rest.user.UserDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import userService.utils.EasyRandomParametersCustom;
import userservice.entity.User;
import userservice.mapper.UserMapper;

import java.util.stream.Collectors;

class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final EasyRandom easyRandom = new EasyRandom(new EasyRandomParametersCustom());

    @Test
    @DisplayName("Test of mapping User to UserDto (POSITIVE)")
    void apply() {
        final var user = easyRandom.nextObject(User.class);

        Assertions.assertDoesNotThrow(() -> userMapper.apply(user));
    }

    @Test
    @DisplayName("Test of mapping List<User> to List<UserDto> (POSITIVE)")
    void to() {
        final var users = easyRandom.objects(User.class, 15).collect(Collectors.toList());

        Assertions.assertDoesNotThrow(() -> userMapper.to(users));
    }

    @Test
    @DisplayName("Test of mapping UserDto to User (POSITIVE)")
    void testApply() {
        final var user = easyRandom.nextObject(UserDto.class);

        Assertions.assertDoesNotThrow(() -> userMapper.apply(user));
    }

    @Test
    @DisplayName("Test of mapping List<UserDto> to List<User> (POSITIVE)")
    void from() {
        final var users = easyRandom.objects(UserDto.class, 15).collect(Collectors.toList());

        Assertions.assertDoesNotThrow(() -> userMapper.from(users));
    }
}