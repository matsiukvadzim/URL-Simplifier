package simplifier.services;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simplifier.mappers.LinkMapper;
import simplifier.model.Link;
import simplifier.model.User;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;
import simplifier.repositories.LinkRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class LinkServiceImpl implements LinkService {

    private LinkRepository linkRepository;

    private LinkSimplifyService simplifyService;

    private TagService tagService;

    private UserService userService;

    private LinkMapper linkMapper = Mappers.getMapper(LinkMapper.class);

    @Autowired
    public void setLinkRepository(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Autowired
    public void setSimplifyService(LinkSimplifyService simplifyService) {
        this.simplifyService = simplifyService;
    }

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public LinkGetterDto saveLink(LinkCreationDto linkCreationDto) {
        Link link = linkMapper.linkDtoToLink(linkCreationDto);
        link.setTags(tagService.saveTags(link.getTags()));
        Optional<User> existingUser = userService.findByUsername(link.getAuthor().getUsername());
        link.setAuthor(existingUser.get());
        if (link.getShortenedLink() == null) {
            linkRepository.save(link);
            link.setShortenedLink(simplifyService.encode(link.getId()));
        }
        Link savedLink = linkRepository.save(link);
        tagService.addLinkToTags(savedLink);
        userService.addLinkToUser(link);
        return linkMapper.linkToLinkDto(savedLink);

    }

    @Override
    public Iterable<LinkGetterDto> findAllLinks() {
        return linkMapper.linksToLinkDtos(linkRepository.findAll());
    }

    @Override
    public Iterable<LinkGetterDto> getLinksByTag(String tagName) {
       return tagService.findByName(tagName)
               .map(tag -> linkMapper.linksToLinkDtos(tag.getLinks()))
               .orElse(Collections.emptyList());
    }

    @Override
    public Iterable<LinkGetterDto> getLinksByUser(String username) {
        return userService.findByUsername(username)
                .map(user -> linkMapper.linksToLinkDtos(user.getLinks()))
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<String> redirect(String shortenedLink) {
        return linkRepository.findByShortenedLink(shortenedLink)
                .map(this::updateClicks)
                .map(Link::getOriginalLink);
    }

    @Override
    public Optional<LinkGetterDto> findByShortenedLink(String shortened) {
        return linkRepository.findByShortenedLink(shortened)
                .map(link -> linkMapper.linkToLinkDto(link));
    }

    @Override
    public LinkGetterDto updateLink(String shortened, LinkCreationDto link) {
        Link updatedLink = linkMapper.linkDtoToLink(link);
        Link savedLink = linkRepository.findByShortenedLink(shortened).get();
        savedLink.setOriginalLink(link.getOriginalLink());
        if (link.getShortenedLink() == null) {
            savedLink.setShortenedLink(shortened);
        }
        else {
            savedLink.setShortenedLink(link.getShortenedLink());
        }
        savedLink.setDescription(link.getDescription());
        savedLink.setTags(tagService.saveTags(updatedLink.getTags()));
        tagService.addLinkToTags(savedLink);
        return linkMapper.linkToLinkDto(linkRepository.save(savedLink));
    }

    private Link updateClicks(Link link) {
        link.addClicks();
        linkRepository.save(link);
        return link;
    }
}
