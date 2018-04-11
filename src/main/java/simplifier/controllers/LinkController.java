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
public class LinkController {

    private LinkService linkService;

    @Autowired
    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/links")
    public ResponseEntity<?> createLink(@Valid @RequestBody LinkCreationDto linkDto) {
        LinkGetterDto savedLink = linkService.saveLink(linkDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    @GetMapping("/links")
    public Iterable<LinkGetterDto> findAllLinks() {
        return linkService.findAllLinks();
    }

    @GetMapping(value = "/links/tags/{name}")
    public Iterable<LinkGetterDto> getLinksByTag(@PathVariable String name) {
        return linkService.getLinksByTag(name);
    }

    @GetMapping(value = "/links/users/{username}")
    public Iterable<LinkGetterDto> getLinksByUser(@PathVariable String username) {
        return linkService.getLinksByUser(username);
    }

    @GetMapping("{shortened}")
    public ResponseEntity<?> redirect(@PathVariable String shortened) {
        return linkService.redirect(shortened)
                .map(original -> ResponseEntity.status(HttpStatus.OK).body(original))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
