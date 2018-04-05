package simplifier.services;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.repositories.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> saveTags(List<Tag> tags) {
        List<String> names = tags.stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toList());
        List<Tag> savedTags = tagRepository.findByNameIn(names);
        for (Tag tag : tags) {
            if (!savedTags.contains(tag)) {
                savedTags.add(tag);
            }
        }
        return Lists.newArrayList(tagRepository.saveAll(savedTags));
    }

    @Override
    public void addLinkToTags(Link link) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : link.getTags()) {
            tag.addLink(link);
            tags.add(tag);
        }
        tagRepository.saveAll(tags);
    }
}
