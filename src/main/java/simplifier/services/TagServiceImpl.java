package simplifier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simplifier.model.Tag;
import simplifier.repositories.TagRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> saveAllTags(List<Tag> tags) {
        return StreamSupport.stream(tagRepository.saveAll(tags).spliterator(),false)
                .collect(Collectors.toList());
    }
}
