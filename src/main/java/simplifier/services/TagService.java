package simplifier.services;

import simplifier.model.Link;
import simplifier.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    List<Tag> saveTags(List<Tag> tags);

    void addLinkToTags(Link link);

    Optional<Tag> findByName(String name);
}
