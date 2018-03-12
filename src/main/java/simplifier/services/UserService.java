package simplifier.services;

import simplifier.exceptions.NameNotUniqueException;
import simplifier.model.User;

public interface UserService {

  User saveUser(User user) throws NameNotUniqueException;
}
