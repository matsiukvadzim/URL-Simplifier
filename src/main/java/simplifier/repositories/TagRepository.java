package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.Tag;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    List<Tag> findByNameIn(List<String> names);
}
