package simplifier.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simplifier.model.Link;
import simplifier.services.LinkService;

@RestController
@RequestMapping("/link")
public class LinkController {

    private LinkService linkService;

    @Autowired
    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<?> createLink(@RequestBody Link link) {
        Link savedLink = linkService.saveLink(link);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    @GetMapping
    public Iterable<Link> findAllLinks() {
        return linkService.findAllLinks();
    }
}
