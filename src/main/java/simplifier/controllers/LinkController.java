package simplifier.controllers;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simplifier.mappers.LinkMapper;
import simplifier.model.Link;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;
import simplifier.services.LinkService;

import javax.validation.Valid;

@RestController
@RequestMapping("/link")
public class LinkController {

    private LinkService linkService;

    private LinkMapper linkMapper = Mappers.getMapper(LinkMapper.class);

    @Autowired
    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<?> createLink(@Valid @RequestBody LinkCreationDto linkDto) {
        Link link = linkMapper.linkDtoToLink(linkDto);
        Link savedLink = linkService.saveLink(link);
        LinkGetterDto responseLinkDto = linkMapper.linkToLinkDto(savedLink);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseLinkDto);
    }

    @GetMapping
    public Iterable<LinkGetterDto> findAllLinks() {
        return linkMapper.linksToLinkDtos(linkService.findAllLinks());
    }


}
