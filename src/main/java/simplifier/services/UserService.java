package simplifier.services;

import java.util.Optional;
import simplifier.model.User;

public interface UserService {

 Optional<User> saveUser(User user);
}
