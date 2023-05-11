package userservice.service.impl;

import apicore.amqp.product.ProductDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import userservice.entity.User;
import userservice.repository.UserRepository;
import userservice.service.UserService;
import userservice.service.amqp.producer.ProductAPI;

import java.util.List;
import java.util.UUID;

@Validated
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProductAPI productAPI;

    @Override
    public List<ProductDto> getUserProducts(@NotNull UUID userId) {
        return productAPI.getUserProducts(userId);
    }

    @Override
    public User getUser(@NotNull UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(@NotNull User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public List<User> saveUsers(@NotEmpty List<User> users) {
        return userRepository.saveAllAndFlush(users);
    }

    @Override
    public void deleteUser(@NotNull UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Revisions<Long, User> getRevisions(@NotNull UUID userId) {
        return userRepository.findRevisions(userId);
    }

    @Override
    public Revision<Long, User> getLastRevision(@NotNull UUID userId) {
        return userRepository.findLastChangeRevision(userId).orElse(null);
    }
}
