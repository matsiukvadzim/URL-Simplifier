package simplifier.repositories;

import org.springframework.data.repository.CrudRepository;
import simplifier.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Integer> {
}
