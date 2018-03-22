package simplifier.services;

import simplifier.model.Link;
import simplifier.model.User;

import java.util.List;

public interface LinkService {

    Link saveLink(Link link);

    Iterable<Link> findAllLinks();
}
