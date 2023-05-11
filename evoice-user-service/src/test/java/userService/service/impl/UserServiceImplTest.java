package userService.service.impl;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import userService.utils.EasyRandomParametersCustom;
import userservice.entity.User;
import userservice.repository.UserRepository;
import userservice.service.impl.UserServiceImpl;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private final EasyRandom easyRandom = new EasyRandom(new EasyRandomParametersCustom());

    @Test
    @DisplayName("Test getting user by ID (POSITIVE / NEGATIVE)")
    void testGetUserPositive() {
        final var user = easyRandom.nextObject(User.class);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(user.getId());

        Assertions.assertEquals(user, userService.getUser(user.getId()));
        Assertions.assertNull(userService.getUser(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Test getting all users (POSITIVE)")
    void testGetAllUsers() {
        final var users = easyRandom.objects(User.class, 15).collect(Collectors.toList());
        Mockito.doReturn(users).when(userRepository).findAll();

        Assertions.assertEquals(users, userService.getAllUsers());
    }

    @Test
    @DisplayName("Test saving of user (POSITIVE)")
    void testSaveUser() {
        final var user = easyRandom.nextObject(User.class);
        Mockito.doReturn(user).when(userRepository).saveAndFlush(user);

        Assertions.assertEquals(user, userService.saveUser(user));
    }

    @Test
    @DisplayName("Test saving multiple users (POSITIVE)")
    void testSaveUsers() {
        final var users = easyRandom.objects(User.class, 15).collect(Collectors.toList());
        Mockito.doReturn(users).when(userRepository).saveAllAndFlush(users);

        Assertions.assertEquals(users, userService.saveUsers(users));
    }

    @Test
    @DisplayName("Test removing of user (POSITIVE)")
    void testDeleteUser() {
        final var user = easyRandom.nextObject(User.class);
        Mockito.doNothing().when(userRepository).deleteById(user.getId());

        Assertions.assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
    }

    @Test
    @DisplayName("Test for getting a slice of changes by user ID (POSITIVE)")
    void testGetRevisions() {
        Assertions.assertDoesNotThrow(() -> userService.getRevisions(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Test to get last revision-change by user ID (POSITIVE)")
    void testGetLastRevision() {
        Assertions.assertDoesNotThrow(() -> userService.getLastRevision(UUID.randomUUID()));
    }
}