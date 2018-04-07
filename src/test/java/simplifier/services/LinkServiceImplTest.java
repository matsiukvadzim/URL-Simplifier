package simplifier.services;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import simplifier.mappers.LinkMapper;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.model.User;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;
import simplifier.repositories.LinkRepository;
import simplifier.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private LinkMapper linkMapper = Mappers.getMapper(LinkMapper.class);

    @InjectMocks
    private LinkServiceImpl linkService;

    private Link link = new Link();

    private LinkCreationDto creationDto = new LinkCreationDto();

    private LinkGetterDto getterDto = new LinkGetterDto();

    @Before
    public void setUp() {
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
        author.setUsername("author");
        link.setAuthor(author);
        tag1.addLink(link);
        tag2.addLink(link);

        creationDto.setOriginalLink(link.getOriginalLink());
        creationDto.setDescription(link.getDescription());
        List<String> tagNames = new ArrayList<>();
        tagNames.add("tag1");
        tagNames.add("tag2");
        creationDto.setTags(tagNames);
        creationDto.setAuthor("author");

        getterDto.setOriginalLink(link.getOriginalLink());
        getterDto.setDescription(link.getDescription());
        getterDto.setTags(tagNames);
        getterDto.setAuthor("author");
    }

    @Test
    public void saveWithExistShortened() {

        link.setShortenedLink("shortened link");
        creationDto.setShortenedLink(link.getShortenedLink());
        getterDto.setShortenedLink(link.getShortenedLink());

        when(linkRepository.save(link)).thenReturn(link);
        when(tagService.saveTags(link.getTags())).thenReturn(link.getTags());
        when(userRepository.findByUsername(link.getAuthor().getUsername()))
                .thenReturn(Optional.of(link.getAuthor()));
        when(linkMapper.linkDtoToLink(creationDto)).thenReturn(link);
        when(linkMapper.linkToLinkDto(link)).thenReturn(getterDto);

        LinkGetterDto savedLink = linkService.saveLink(creationDto);

        assertThat(savedLink.getShortenedLink(), is(link.getShortenedLink()));
        verify(tagService).saveTags(link.getTags());
        verify(userRepository).findByUsername(link.getAuthor().getUsername());
        verify(linkRepository).save(link);
        verify(linkMapper).linkDtoToLink(creationDto);
        verify(linkMapper).linkToLinkDto(link);
        verify(tagService).addLinkToTags(link);
        verifyNoMoreInteractions(tagService, userRepository, linkRepository, linkMapper);

    }

    @Test
    public void saveWithGeneratedShortened() {

        when(linkRepository.save(link)).thenReturn(link);
        when(tagService.saveTags(link.getTags())).thenReturn(link.getTags());
        when(userRepository.findByUsername(link.getAuthor().getUsername()))
                .thenReturn(Optional.of(link.getAuthor()));
        when(linkMapper.linkDtoToLink(creationDto)).thenReturn(link);
        when(linkMapper.linkToLinkDto(link)).thenReturn(getterDto);

        String generatedLink = "generatedLink";
        getterDto.setShortenedLink(generatedLink);

        when(simplifyService.encode(link.getId())).thenReturn(generatedLink);

        LinkGetterDto savedLink = linkService.saveLink(creationDto);

        System.out.println();

        assertThat(savedLink.getShortenedLink(), is(generatedLink));
        verify(tagService).saveTags(link.getTags());
        verify(userRepository).findByUsername(link.getAuthor().getUsername());
        verify(linkRepository, times(2)).save(link);
        verify(simplifyService).encode(link.getId());
        verify(linkMapper).linkDtoToLink(creationDto);
        verify(linkMapper).linkToLinkDto(link);
        verify(tagService).addLinkToTags(link);
        verifyNoMoreInteractions(tagService, userRepository, linkRepository,
                simplifyService, linkMapper);
    }

    @Test
    public void getLinksByTag() {
        Tag tag = link.getTags().get(0);
        when(tagService.findByName(tag.getName())).thenReturn(Optional.of(tag));
        when(linkMapper.linksToLinkDtos(tag.getLinks())).thenReturn(Lists.newArrayList(getterDto));

        List<LinkGetterDto> linkList = Lists.newArrayList(linkService.getLinksByTag(tag.getName()));
        assertThat(linkList.size(), is(1));
        LinkGetterDto foundLink = linkList.get(0);
        assertThat(foundLink, is(getterDto));
        verify(tagService).findByName(tag.getName());
        verify(linkMapper).linksToLinkDtos(tag.getLinks());
        verifyNoMoreInteractions(tagService, linkMapper);
    }
}
