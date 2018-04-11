package simplifier.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;
import simplifier.repositories.LinkRepository;
import simplifier.repositories.TagRepository;
import simplifier.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LinkControllerIT {

    private LinkRepository linkRepository;

    private UserRepository userRepository;

    private TagRepository tagRepository;

    private TestRestTemplate restTemplate;

    private ObjectMapper mapper = new ObjectMapper();

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

    @Autowired
    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    private Link createLink() {
        Link link = new Link();
        link.setOriginalLink("link");
        link.setShortenedLink("short");
        link.setDescription("description");
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("1");
        tags.add(tag);
        tagRepository.saveAll(tags);
        link.setTags(tags);
        link.setAuthor(createUser());
        linkRepository.save(link);
        return link;
    }

    private User createUser() {
        User user = new User();
        user.setUsername("author");
        userRepository.save(user);
        return user;
    }

    @Test
    public void createWithGeneratedShortened() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithoutShortened.JSON"),
                LinkCreationDto.class);

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.postForEntity("/links",
                link, LinkGetterDto.class);

        LinkGetterDto savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(notNullValue()));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void createWithExistShortened() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.postForEntity("/links",
                link, LinkGetterDto.class);

        LinkGetterDto savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        String requiredMessage = "short";

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(requiredMessage));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void conflictIfUserNotExist() throws IOException {
        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithEmptyUser.JSON"),
                LinkCreationDto.class);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/links",
                link, String.class);

        String requiredMessage = "User not found";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is(requiredMessage));

    }

    @Test
    public void conflictIfShortenedNotUnique() throws Exception {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);
        restTemplate.postForEntity("/links", link, LinkGetterDto.class);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/links",
                link, String.class);

        String requiredMessage = "Invalid shortened";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is(requiredMessage));
    }

    @Test
    public void getLinksByTag() {
        Link link = createLink();
        LinkGetterDto[] response = restTemplate.getForObject("/links/tags/1",
                LinkGetterDto[].class);
        checkAreLinksTheSame(response, link);
    }

    @Test
    public void getLinksByUser() {
        Link link = createLink();
        LinkGetterDto[] response = restTemplate.getForObject("/links/users/author",
                LinkGetterDto[].class);
        checkAreLinksTheSame(response, link);
    }

    @Test
    public void redirect() {
        createLink();
        ResponseEntity<String> response = restTemplate.getForEntity("/short", String.class);
        String requiredMessage = "link";
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(requiredMessage));
    }

    @Test
    public void redirectIfShortenedNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity("/short", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    private void checkAreLinksTheSame(LinkGetterDto[] response, Link link) {
        assertThat(response.length, is(1));
        LinkGetterDto responseLink = response[0];
        assertThat(responseLink.getOriginalLink(), is(link.getOriginalLink()));
        assertThat(responseLink.getShortenedLink(), is(link.getShortenedLink()));
        assertThat(responseLink.getDescription(), is(link.getDescription()));
        assertThat(responseLink.getAuthor(), is(link.getAuthor().getUsername()));
        assertThat(responseLink.getClicks(), is(link.getClicks()));
        List<String> tags = link.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        assertThat(responseLink.getTags(), is(tags));
    }
}

