package simplifier.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;
import simplifier.services.LinkService;

import javax.validation.Valid;

@RestController
@RequestMapping("/link")
public class LinkController {

    private LinkService linkService;

    @Autowired
    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<?> createLink(@Valid @RequestBody LinkCreationDto linkDto) {
        LinkGetterDto savedLink = linkService.saveLink(linkDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    @GetMapping
    public Iterable<LinkGetterDto> findAllLinks() {
        return linkService.findAllLinks();
    }


}
