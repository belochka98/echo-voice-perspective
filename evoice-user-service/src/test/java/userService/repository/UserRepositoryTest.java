package userService.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import userService.utils.EasyRandomParametersCustom;
import userservice.entity.User;
import userservice.repository.UserRepository;

@ActiveProfiles("test")
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    private final EasyRandom easyRandom = new EasyRandom(new EasyRandomParametersCustom());

    @Test
    @DisplayName("Test for storing invalid values in the database (NEGATIVE)")
    void testUserRepository() {
        final var user = easyRandom.nextObject(User.class);
        user.setPhone("999 999 999 999 999 999");

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(user));
    }
}