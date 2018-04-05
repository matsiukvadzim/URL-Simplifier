package simplifier.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "tags")
    private List<Link> links = new ArrayList<>();

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public List<Link> getLinks() {
        return links;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(name);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag that = (Tag) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(name, that.getName());
        return eb.isEquals();
    }
}
