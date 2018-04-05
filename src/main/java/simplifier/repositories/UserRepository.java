package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
