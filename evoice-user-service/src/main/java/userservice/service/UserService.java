package userservice.service;

import apicore.amqp.product.ProductDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.validation.annotation.Validated;
import userservice.entity.User;

import java.util.List;
import java.util.UUID;

@Validated
public interface UserService {
    List<ProductDto> getUserProducts(@NotNull UUID userId);

    User getUser(@NotNull UUID userId);

    List<User> getAllUsers();

    User saveUser(@NotNull User user);

    List<User> saveUsers(@NotEmpty List<User> users);

    void deleteUser(@NotNull UUID userId);

    Revisions<Long, User> getRevisions(@NotNull UUID userId);

    Revision<Long, User> getLastRevision(@NotNull UUID userId);
}
