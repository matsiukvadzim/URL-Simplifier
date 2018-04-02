package simplifier.mappers;

import org.mapstruct.Mapper;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.model.User;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface LinkMapper {

    default LinkGetterDto linkToLinkDto(Link link) {
        LinkGetterDto linkDto = new LinkGetterDto();
        linkDto.setOriginalLink(link.getOriginalLink());
        linkDto.setShortenedLink(link.getShortenedLink());
        linkDto.setDescription(link.getDescription());
        List<String> tags = new ArrayList<>();
        if (link.getTags() != null) {
            for (Tag tag : link.getTags()) {
                tags.add(tag.getName());
            }
        }
        linkDto.setTags(tags);
        linkDto.setAuthor(link.getAuthor().getUsername());
        linkDto.setClicks(link.getClicks());

        return linkDto;
    }

    default Link linkDtoToLink(LinkCreationDto linkDto) {
        Link link = new Link();
        link.setOriginalLink(linkDto.getOriginalLink());
        link.setShortenedLink(linkDto.getShortenedLink());
        link.setDescription(linkDto.getDescription());
        List<Tag> tags = new ArrayList<>();
        if (linkDto.getTags() != null) {
            for (String tagName : linkDto.getTags()) {
                Tag tag = new Tag();
                tag.setName(tagName);
                tags.add(tag);
            }
        }
        link.setTags(tags);
        User author = new User();
        author.setUsername(linkDto.getAuthor());
        link.setAuthor(author);

        return link;
    }

    Iterable<LinkGetterDto> linksToLinkDtos(Iterable<Link> links);
}
