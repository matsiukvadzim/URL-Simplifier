package simplifier.services;

import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;

public interface LinkService {

    LinkGetterDto saveLink(LinkCreationDto link);

    Iterable<LinkGetterDto> findAllLinks();

    Iterable<LinkGetterDto> getLinksByTag(String tagName);

    Iterable<LinkGetterDto> getLinksByUser(String username);
}
