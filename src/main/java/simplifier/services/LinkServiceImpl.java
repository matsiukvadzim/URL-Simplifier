package simplifier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simplifier.model.Link;
import simplifier.model.User;
import simplifier.repositories.LinkRepository;
import simplifier.repositories.UserRepository;

@Service
public class LinkServiceImpl implements LinkService {

    private LinkRepository linkRepository;

    private LinkSimplifyService simplifyService;

    private TagService tagService;

    private UserRepository userRepository;

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
    public Link saveLink(Link link) {
        link.setTags(tagService.saveOrUpdateTags(link));
        User existingUser = userRepository.findByUsername(link.getAuthor().getUsername());
        link.setAuthor(existingUser);
        if (link.getShortenedLink() == null || !checkIsShortenedUnique(link)) {
            linkRepository.save(link);
            link.setShortenedLink(simplifyService.encode(link.getId()));
        }
        return linkRepository.save(link);
    }

    @Override
    public Iterable<Link> findAllLinks() {
        return linkRepository.findAll();
    }

    private boolean checkIsShortenedUnique(Link link) {
        Link existingLink = linkRepository.findByShortenedLink(link.getShortenedLink());
        if (existingLink == null) {
            return true;
        }
        return false;
    }

}
