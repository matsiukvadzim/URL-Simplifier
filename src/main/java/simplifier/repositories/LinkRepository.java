package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.Link;
import simplifier.model.User;

import java.util.List;


public interface LinkRepository extends CrudRepository<Link, Integer> {
}
