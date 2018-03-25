package simplifier.services;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.repositories.TagRepository;

import java.util.ArrayList;
import java.util.List;


@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> saveOrUpdateTags(Link link) {
        List<Tag> tags = new ArrayList<>();

        for (Tag tag : link.getTags()) {
            Tag currentTag = tagRepository.findByName(tag.getName());
            if (currentTag != null) {
                currentTag.addLink(link);
                tags.add(currentTag);
            } else {
                tag.addLink(link);
                Tag savedTag = tagRepository.save(tag);
                tags.add(savedTag);
            }
        }
        return Lists.newArrayList(tagRepository.saveAll(tags));
    }
}
