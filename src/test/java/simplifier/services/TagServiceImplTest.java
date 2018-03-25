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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    public void saveNewTag() {
        Link link = new Link();
        Tag tag1 = new Tag();        ;
        tag1.setName("tag1");
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        link.setTags(tags);

        when(tagRepository.findByName(tag1.getName())).thenReturn(null);
        when(tagRepository.save(tag1)).thenReturn(tag1);
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> savedTags = tagService.saveOrUpdateTags(link);

        Tag savedTag = savedTags.get(0);
        assertThat(savedTag, is(tag1));
        assertThat(savedTag.getLinks().size(),is(1));
        verify(tagRepository).findByName(tag1.getName());
        verify(tagRepository).save(tag1);
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void updateIfTagAlreadyExist() {
        Link link = new Link();
        Tag tag1 = new Tag();        ;
        tag1.setName("tag1");
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        link.setTags(tags);

        when(tagRepository.findByName(tag1.getName())).thenReturn(tag1);
        when(tagRepository.saveAll(tags)).thenReturn(tags);

        List<Tag> savedTags = tagService.saveOrUpdateTags(link);

        Tag savedTag = savedTags.get(0);

        assertThat(savedTag, is(tag1));
        assertThat(savedTag.getLinks().size(), is(1));
        verify(tagRepository).findByName(tag1.getName());
        verify(tagRepository).saveAll(tags);
        verifyNoMoreInteractions(tagRepository);

    }
}
