package simplifier.model.dto;

import simplifier.validators.ValidAuthor;
import simplifier.validators.ValidShortenedLink;

import java.util.List;

public class LinkCreationDto {

    private String originalLink;

    @ValidShortenedLink
    private String shortenedLink;

    private String description;

    private List<String> tags;

    @ValidAuthor
    private String author;

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getShortenedLink() {
        return shortenedLink;
    }

    public void setShortenedLink(String shortenedLink) {
        this.shortenedLink = shortenedLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
