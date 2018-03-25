package simplifier.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import simplifier.model.Link;
import simplifier.model.Tag;
import simplifier.model.User;
import simplifier.repositories.LinkRepository;
import simplifier.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LinkControllerIT {

    private LinkRepository linkRepository;

    private UserRepository userRepository;

    private TestRestTemplate restTemplate;

    private Link link = new Link();

    @Autowired
    public void setLinkRepository(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Before
    public void setUp() throws Exception {
        link.setOriginalLink("original link");
        link.setDescription("description");
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tag1.setName("tag1");
        tag2.setName("tag2");
        tags.add(tag1);
        tags.add(tag2);
        link.setTags(tags);
        User author = new User();
        author.setUsername("author");
        link.setAuthor(author);
        userRepository.save(author);
    }

    private ObjectNode initJsonObj(Link link) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode linkNode = mapper.createObjectNode();
        linkNode.put("originalLink", link.getOriginalLink());
        linkNode.put("shortenedLink", link.getShortenedLink());
        linkNode.put("description", link.getDescription());
        ArrayNode tagNode = mapper.createArrayNode();
        List<Tag> tags = link.getTags();
        for (Tag tag : tags) {
            tagNode.add(tag.getName());
        }
        linkNode.set("tags", tagNode);
        linkNode.put("author", link.getAuthor().getUsername());

        return linkNode;
    }

    @Test
    public void createWithGeneratedShortened() {

        ObjectNode linkNode = initJsonObj(link);

        ResponseEntity<Link> responseEntity = restTemplate.postForEntity("/link",
                linkNode, Link.class);

        Link savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(notNullValue()));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void createWithExistShortened() {

        link.setShortenedLink("short");
        ObjectNode linkNode = initJsonObj(link);

        ResponseEntity<Link> responseEntity = restTemplate.postForEntity("/link",
                linkNode, Link.class);

        Link savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        String requiredMessage = "short";

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(requiredMessage));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }
}

