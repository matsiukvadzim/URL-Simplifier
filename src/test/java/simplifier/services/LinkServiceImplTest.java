package simplifier.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.model.User;
import simplifier.repositories.LinkRepository;
import simplifier.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LinkServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkSimplifyService simplifyService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private LinkServiceImpl linkService;

    private Link link = new Link();

    @Before
    public void setUp() throws Exception {
        link.setOriginalLink("original link");
        link.setDescription("description");
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tag1.setName("tag1");
        tag2.setName("tag2");
        tags.add(tag1);
        tags.add(tag2);
        link.setTags(tags);
        User author = new User();
        author.setUsername("user");
        link.setAuthor(author);
    }

    @Test
    public void saveWithExistShortened() {

        link.setShortenedLink("shortened link");

        when(linkRepository.save(link)).thenReturn(link);
        when(tagService.saveOrUpdateTags(link)).thenReturn(link.getTags());
        when(userRepository.findByUsername(link.getAuthor().getUsername()))
                .thenReturn(link.getAuthor());
        when(linkRepository.findByShortenedLink(link.getShortenedLink()))
                .thenReturn(null);

        Link savedLink = linkService.saveLink(link);

        assertThat(savedLink, is(link));
        verify(tagService).saveOrUpdateTags(link);
        verify(userRepository).findByUsername(link.getAuthor().getUsername());
        verify(linkRepository).save(link);
        verify(linkRepository).findByShortenedLink(link.getShortenedLink());
        verifyNoMoreInteractions(tagService, userRepository, linkRepository);

    }

    @Test
    public void saveWithGeneratedShortened() {

        when(linkRepository.save(link)).thenReturn(link);

        when(tagService.saveOrUpdateTags(link)).thenReturn(link.getTags());
        when(userRepository.findByUsername(link.getAuthor().getUsername()))
                .thenReturn(link.getAuthor());
        String generatedLink = "generatedLink";
        when(simplifyService.encode(link.getId())).thenReturn(generatedLink);

        Link savedLink = linkService.saveLink(link);

        assertThat(savedLink.getShortenedLink(), is(generatedLink));
        verify(tagService).saveOrUpdateTags(link);
        verify(userRepository).findByUsername(link.getAuthor().getUsername());
        verify(linkRepository, times(2)).save(link);
        verify(simplifyService).encode(link.getId());
        verifyNoMoreInteractions(tagService, userRepository, linkRepository, simplifyService);
    }
}
