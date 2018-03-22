package simplifier.services;

import simplifier.model.Tag;

import java.util.List;

public interface TagService {

    List<Tag> saveAllTags(List<Tag> tags);
}
