package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    Tag findByName(String name);
}
