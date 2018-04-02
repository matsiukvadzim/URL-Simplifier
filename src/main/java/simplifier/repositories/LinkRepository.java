package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.Link;

import java.util.Optional;

public interface LinkRepository extends CrudRepository<Link, Integer> {

    Optional<Link> findByShortenedLink(String s);
}
