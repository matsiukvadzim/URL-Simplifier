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

import static java.util.Arrays.asList;
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

    private Tag initTag() {
        Tag tag = new Tag();
        tag.setName("tag1");
        tags.add(tag);
        return tag;
    }
    @Test
    public void saveTag() {
        Tag tag = initTag();

        when(tagRepository.findByNameIn(asList(tag.getName()))).thenReturn(new ArrayList<>());
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> savedTags = tagService.saveTags(tags);

        Tag savedTag = savedTags.get(0);
        assertThat(savedTag, is(tag));
        verify(tagRepository).findByNameIn(asList(tag.getName()));
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void saveIfTagAlreadyExist() {
        Tag tag = initTag();

        when(tagRepository.findByNameIn(asList(tag.getName()))).thenReturn(tags);
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> savedTags = tagService.saveTags(tags);

        Tag savedTag = savedTags.get(0);
        assertThat(savedTag, is(tag));
        assertThat(savedTags.size(), is(1));
        verify(tagRepository).findByNameIn(asList(tag.getName()));
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void addLinkToTags() {
        Tag tag = initTag();
        Link link = new Link();
        link.setTags(tags);

        when(tagRepository.saveAll(tags)).thenReturn(tags);
        tagService.addLinkToTags(link);

        assertThat(tag.getLinks(), is(asList(link)));
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }
}
