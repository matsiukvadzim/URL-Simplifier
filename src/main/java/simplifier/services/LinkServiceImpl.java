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
import simplifier.repositories.UserRepository;

import java.util.Optional;

@Service
public class LinkServiceImpl implements LinkService {

    private LinkRepository linkRepository;

    private LinkSimplifyService simplifyService;

    private TagService tagService;

    private UserRepository userRepository;

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
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public LinkGetterDto saveLink(LinkCreationDto linkCreationDto) {
        Link link = linkMapper.linkDtoToLink(linkCreationDto);
        link.setTags(tagService.saveTags(link.getTags()));
        Optional<User> existingUser = userRepository.findByUsername(link.getAuthor().getUsername());
        link.setAuthor(existingUser.get());
        if (link.getShortenedLink() == null) {
            linkRepository.save(link);
            link.setShortenedLink(simplifyService.encode(link.getId()));
        }
        Link savedLink = linkRepository.save(link);
        tagService.addLinkToTags(savedLink);
        return linkMapper.linkToLinkDto(savedLink);

    }

    @Override
    public Iterable<LinkGetterDto> findAllLinks() {
        return linkMapper.linksToLinkDtos(linkRepository.findAll());
    }


}
