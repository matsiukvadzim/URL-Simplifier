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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LinkServiceImplTest {

    @Mock
    private UserService userService;

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
        when(userService.findByUsername(link.getAuthor().getUsername()))
                .thenReturn(Optional.of(link.getAuthor()));
        when(linkMapper.linkDtoToLink(creationDto)).thenReturn(link);
        when(linkMapper.linkToLinkDto(link)).thenReturn(getterDto);

        LinkGetterDto savedLink = linkService.saveLink(creationDto);

        assertThat(savedLink.getShortenedLink(), is(link.getShortenedLink()));
        verify(tagService).saveTags(link.getTags());
        verify(userService).findByUsername(link.getAuthor().getUsername());
        verify(userService).addLinkToUser(link);
        verify(linkRepository).save(link);
        verify(linkMapper).linkDtoToLink(creationDto);
        verify(linkMapper).linkToLinkDto(link);
        verify(tagService).addLinkToTags(link);
        verifyNoMoreInteractions(tagService, userService, linkRepository, linkMapper);

    }

    @Test
    public void saveWithGeneratedShortened() {

        when(linkRepository.save(link)).thenReturn(link);
        when(tagService.saveTags(link.getTags())).thenReturn(link.getTags());
        when(userService.findByUsername(link.getAuthor().getUsername()))
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
        verify(userService).findByUsername(link.getAuthor().getUsername());
        verify(userService).addLinkToUser(link);
        verify(linkRepository, times(2)).save(link);
        verify(simplifyService).encode(link.getId());
        verify(linkMapper).linkDtoToLink(creationDto);
        verify(linkMapper).linkToLinkDto(link);
        verify(tagService).addLinkToTags(link);
        verifyNoMoreInteractions(tagService, userService, linkRepository,
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

    @Test
    public void getLinksByUser() {
        User author = link.getAuthor();
        when(userService.findByUsername(author.getUsername())).thenReturn(Optional.of(author));
        when(linkMapper.linksToLinkDtos(author.getLinks())).thenReturn(Lists.newArrayList(getterDto));

        List<LinkGetterDto> linkList = Lists.newArrayList(linkService.getLinksByUser(author.getUsername()));
        assertThat(linkList.size(), is(1));
        LinkGetterDto foundLink = linkList.get(0);
        assertThat(foundLink, is(getterDto));
        verify(userService).findByUsername(author.getUsername());
        verify(linkMapper).linksToLinkDtos(author.getLinks());
        verifyNoMoreInteractions(userService, linkMapper);
    }

    @Test
    public void redirect() {
        link.setShortenedLink("short");
        when(linkRepository.findByShortenedLink(link.getShortenedLink())).thenReturn(Optional.of(link));
        when(linkRepository.save(link)).thenReturn(link);
        Optional<String> original = linkService.redirect(link.getShortenedLink());
        assertThat(original.get(), is(link.getOriginalLink()));
        verify(linkRepository).findByShortenedLink(link.getShortenedLink());
        verify(linkRepository).save(link);
        verifyNoMoreInteractions(linkRepository);
    }

    @Test
    public void findByShortenedLink() {
        link.setShortenedLink("short");
        getterDto.setShortenedLink(link.getShortenedLink());
        when(linkRepository.findByShortenedLink(link.getShortenedLink())).thenReturn(Optional.of(link));
        when(linkMapper.linkToLinkDto(link)).thenReturn(getterDto);

        Optional<LinkGetterDto> linkGetterDto = linkService.findByShortenedLink(link.getShortenedLink());

        assertThat(linkGetterDto.get().getShortenedLink(), is(link.getShortenedLink()));
        verify(linkRepository).findByShortenedLink(link.getShortenedLink());
        verify(linkMapper).linkToLinkDto(link);
        verifyNoMoreInteractions(linkRepository, linkMapper);
    }

    @Test
    public void updateLink() {
        String shortLink = "short";
        String updatedOriginal = "updatedLink";
        String updatedShort = "updatedShort";
        String updatedDescription = "updatedDescription";

        link.setShortenedLink(shortLink);
        when(linkMapper.linkDtoToLink(creationDto)).thenReturn(link);
        when(linkRepository.findByShortenedLink(shortLink)).thenReturn(Optional.of(link));
        when(tagService.saveTags(link.getTags())).thenReturn(link.getTags());
        when(linkRepository.save(link)).thenReturn(link);
        when(linkMapper.linkToLinkDto(link)).thenReturn(getterDto);
        creationDto.setOriginalLink(updatedOriginal);
        creationDto.setShortenedLink(updatedShort);
        creationDto.setDescription(updatedDescription);
        getterDto.setOriginalLink(updatedOriginal);
        getterDto.setShortenedLink(updatedShort);
        getterDto.setDescription(updatedDescription);

        LinkGetterDto updatedLink = linkService.updateLink(shortLink, creationDto);

        assertThat(updatedLink.getOriginalLink(), is(creationDto.getOriginalLink()));
        assertThat(updatedLink.getShortenedLink(), is(creationDto.getShortenedLink()));
        assertThat(updatedLink.getDescription(), is(creationDto.getDescription()));
        verify(linkMapper).linkDtoToLink(creationDto);
        verify(linkRepository).findByShortenedLink(shortLink);
        verify(tagService).saveTags(link.getTags());
        verify(linkRepository).save(link);
        verify(linkMapper).linkToLinkDto(link);
        verify(tagService).addLinkToTags(link);
        verifyNoMoreInteractions(linkMapper, linkMapper, tagService);
    }

    @Test
    public void updateLinkWithoutChangingShortened() {
        String shortLink = "short";
        String updatedOriginal = "updatedLink";
        String updatedDescription = "updatedDescription";

        link.setShortenedLink(shortLink);
        when(linkMapper.linkDtoToLink(creationDto)).thenReturn(link);
        when(linkRepository.findByShortenedLink(shortLink)).thenReturn(Optional.of(link));
        when(tagService.saveTags(link.getTags())).thenReturn(link.getTags());
        when(linkRepository.save(link)).thenReturn(link);
        when(linkMapper.linkToLinkDto(link)).thenReturn(getterDto);
        creationDto.setOriginalLink(updatedOriginal);
        creationDto.setDescription(updatedDescription);
        getterDto.setOriginalLink(updatedOriginal);
        getterDto.setShortenedLink(shortLink);
        getterDto.setDescription(updatedDescription);

        LinkGetterDto updatedLink = linkService.updateLink(shortLink, creationDto);

        assertThat(updatedLink.getOriginalLink(), is(creationDto.getOriginalLink()));
        assertThat(updatedLink.getShortenedLink(), is(link.getShortenedLink()));
        assertThat(updatedLink.getDescription(), is(creationDto.getDescription()));
        verify(linkMapper).linkDtoToLink(creationDto);
        verify(linkRepository).findByShortenedLink(shortLink);
        verify(tagService).saveTags(link.getTags());
        verify(linkRepository).save(link);
        verify(linkMapper).linkToLinkDto(link);
        verify(tagService).addLinkToTags(link);
        verifyNoMoreInteractions(linkMapper, linkMapper, tagService);
    }
}
