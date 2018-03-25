package simplifier.services;

import simplifier.model.Link;

public interface LinkService {

    Link saveLink(Link link);

    Iterable<Link> findAllLinks();
}
