package simplifier.services;

import simplifier.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> saveUser(User user);

    User findByUsername(String username);
}
