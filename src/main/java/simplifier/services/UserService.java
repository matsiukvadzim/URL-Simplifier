package simplifier.services;

import simplifier.model.Link;
import simplifier.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> saveUser(User user);

    Optional<User> findByUsername(String username);

    void addLinkToUser(Link link);

    String login(User user);
}
