package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.Link;

public interface LinkRepository extends CrudRepository<Link, Integer> {

    Link findByShortenedLink(String s);
}
