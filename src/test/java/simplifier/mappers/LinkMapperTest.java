package simplifier.mappers;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.model.User;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class LinkMapperTest {

    private LinkMapper linkMapper = Mappers.getMapper(LinkMapper.class);

    @Test
    public void linkToLinkDto() {
        Link link = new Link();
        link.setOriginalLink("original");
        link.setShortenedLink("shortened");
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

        List<String> tagNames = link.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toList());

        LinkGetterDto linkDto = linkMapper.linkToLinkDto(link);

        assertThat(linkDto.getOriginalLink(), is(link.getOriginalLink()));
        assertThat(linkDto.getShortenedLink(), is(link.getShortenedLink()));
        assertThat(linkDto.getDescription(), is(link.getDescription()));
        assertThat(linkDto.getTags(), is(tagNames));
        assertThat(linkDto.getAuthor(), is(link.getAuthor().getUsername()));
        assertThat(link.getClicks(), is(0));

    }

    @Test
    public void linkDtoToLink() {
        LinkCreationDto linkDto = new LinkCreationDto();
        linkDto.setOriginalLink("original");
        linkDto.setShortenedLink("shortened");
        linkDto.setDescription("description");
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");
        linkDto.setTags(tags);
        linkDto.setAuthor("author");

        Link link = linkMapper.linkDtoToLink(linkDto);

        List<String> tagNames = link.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toList());

        assertThat(link.getOriginalLink(), is(linkDto.getOriginalLink()));
        assertThat(link.getShortenedLink(), is(linkDto.getShortenedLink()));
        assertThat(link.getDescription(), is(linkDto.getDescription()));
        assertThat(tagNames, is(tags));
        assertThat(link.getAuthor().getUsername(), is(linkDto.getAuthor()));
        assertThat(link.getClicks(), is(0));
    }

}
