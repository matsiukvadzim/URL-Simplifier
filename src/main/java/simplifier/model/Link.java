package simplifier.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String originalLink;

    private String shortenedLink;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "link_tags",
            joinColumns = {@JoinColumn(name = "link_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne
    @JoinTable(name = "link_users",
            joinColumns = {@JoinColumn(name = "link_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private User author;

    private Integer clicks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void addClicks() {
        clicks++;
    }
}
