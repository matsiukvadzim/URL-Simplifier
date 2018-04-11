package simplifier.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.repositories.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private List<Tag> tags = new ArrayList<>();

    private Tag createTag() {
        Tag tag = new Tag();
        tag.setName("tag1");
        tags.add(tag);
        return tag;
    }
    @Test
    public void saveTag() {
        Tag tag = createTag();

        when(tagRepository.findByNameIn(singletonList(tag.getName()))).thenReturn(new ArrayList<>());
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> savedTags = tagService.saveTags(tags);

        Tag savedTag = savedTags.get(0);
        assertThat(savedTag, is(tag));
        verify(tagRepository).findByNameIn(singletonList(tag.getName()));
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void saveIfTagAlreadyExist() {
        Tag tag = createTag();

        when(tagRepository.findByNameIn(singletonList(tag.getName()))).thenReturn(tags);
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> savedTags = tagService.saveTags(tags);

        Tag savedTag = savedTags.get(0);
        assertThat(savedTag, is(tag));
        assertThat(savedTags.size(), is(1));
        verify(tagRepository).findByNameIn(singletonList(tag.getName()));
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void addLinkToTags() {
        Tag tag = createTag();
        Link link = new Link();
        link.setTags(tags);

        when(tagRepository.saveAll(tags)).thenReturn(tags);
        tagService.addLinkToTags(link);

        assertThat(tag.getLinks(), is(singletonList(link)));
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void findByName() {
        Tag tag = createTag();
        when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));

        Optional<Tag> foundTag = tagService.findByName(tag.getName());
        assertThat(foundTag.get(), is(tag));
        verify(tagRepository).findByName(tag.getName());
        verifyNoMoreInteractions(tagRepository);
    }
}
