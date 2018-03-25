package simplifier.services;

import simplifier.model.Link;
import simplifier.model.Tag;

import java.util.List;

public interface TagService {

    List<Tag> saveOrUpdateTags(Link link);
}
