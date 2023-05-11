package userservice.controller;

import apicore.amqp.product.ProductDto;
import apicore.envers.RevisionDto;
import apicore.rest.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import userservice.mapper.RevisionMapper;
import userservice.mapper.UserMapper;
import userservice.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@SuppressWarnings("rawtypes")
@Validated
@RestController
@RequestMapping("/v1/api/users")
@CrossOrigin(methods = {GET, POST, DELETE})
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final RevisionMapper revisionMapperDefault;

    @GetMapping("/products/{userId}")
    public List<ProductDto> getProductsByUserId(@PathVariable UUID userId) {
        return userService.getUserProducts(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return userMapper.apply(userService.getUser(userId));
    }

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        return userMapper.to(userService.getAllUsers());
    }

    @PostMapping("/save")
    public UserDto saveUser(@RequestBody UserDto user) {
        return userMapper.apply(userService.saveUser(userMapper.apply(user)));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }


    @GetMapping("/revisions/all/{userId}")
    public List<RevisionDto> getAllRevisions(@PathVariable UUID userId) {
        return revisionMapperDefault.apply(userService.getRevisions(userId).stream().collect(Collectors.toList()));
    }

    @GetMapping("/revisions/last/{userId}")
    public RevisionDto getLastRevision(@PathVariable UUID userId) {
        return revisionMapperDefault.apply(userService.getLastRevision(userId));
    }
}
