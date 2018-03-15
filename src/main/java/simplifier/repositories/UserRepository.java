package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);
}
