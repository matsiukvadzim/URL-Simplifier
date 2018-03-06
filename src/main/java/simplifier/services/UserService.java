package simplifier.services;

import simplifier.model.User;

public interface UserService {

  User findByUsername(String username);

  User saveUser(User user);
}
