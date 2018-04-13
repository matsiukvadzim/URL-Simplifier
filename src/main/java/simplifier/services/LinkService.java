package simplifier.services;

import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;

import java.util.Optional;

public interface LinkService {

    LinkGetterDto saveLink(LinkCreationDto link);

    Iterable<LinkGetterDto> findAllLinks();

    Iterable<LinkGetterDto> getLinksByTag(String tagName);

    Iterable<LinkGetterDto> getLinksByUser(String username);

    Optional<String> redirect(String shortenedLink);

    Optional<LinkGetterDto> findByShortenedLink(String shortened);
}
