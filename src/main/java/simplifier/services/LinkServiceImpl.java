package simplifier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simplifier.model.Link;
import simplifier.model.User;
import simplifier.repositories.LinkRepository;

@Service
public class LinkServiceImpl implements LinkService {

    private LinkRepository linkRepository;

    private LinkSimplifyService simplifyService;

    private TagService tagService;

    private UserService userService;

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
    public Link saveLink(Link link) {
        tagService.saveAllTags(link.getTags());
        User existingUser = userService.findByUsername(link.getAuthor().getUsername());
        link.setAuthor(existingUser);
        linkRepository.save(link);
        link.setShortenedLink(simplifyService.encode(link.getId()));
        return linkRepository.save(link);
    }

    @Override
    public Iterable<Link> findAllLinks() {
        return linkRepository.findAll();
    }

}
