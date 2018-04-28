package simplifier.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Optional;
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

    private BCryptPasswordEncoder passwordEncoder;

    private static final String USERNAME = "author";

    private static final String PASSWORD = "password";

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

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
        user.setUsername(USERNAME);
        user.setEncryptedPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
        return user;
    }

    private HttpHeaders getHeaders() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/users/login",
                user, String.class);
        String token = responseEntity.getBody();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        return httpHeaders;
    }

    @Test
    public void createWithGeneratedShortened() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithoutShortened.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.exchange("/links", HttpMethod.POST,
                entity, LinkGetterDto.class);

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

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.exchange("/links", HttpMethod.POST,
                entity, LinkGetterDto.class);

        LinkGetterDto savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        String requiredMessage = "short";

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(requiredMessage));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void conflictIfUserNotExist() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithEmptyUser.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<String> responseEntity = restTemplate.exchange("/links", HttpMethod.POST,
                entity, String.class);

        String requiredMessage = "User not found";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is(requiredMessage));
    }

    @Test
    public void conflictIfShortenedNotUnique() throws Exception {
        createLink();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<String> responseEntity = restTemplate.exchange("/links", HttpMethod.POST,
                entity, String.class);

        String requiredMessage = "Invalid shortened";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is(requiredMessage));
    }

    @Test
    public void getLinksByTag() {
        Link link = createLink();
        
        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<LinkGetterDto[]> responseEntity = restTemplate.exchange("/links/tags/1",
                HttpMethod.GET, entity, LinkGetterDto[].class);
        LinkGetterDto responseLink = getResponseLink(responseEntity);
        checkAreLinksTheSame(responseLink, link);
    }

    @Test
    public void getLinksByUser() {
        Link link = createLink();

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<LinkGetterDto[]> responseEntity = restTemplate.exchange("/links/users/author",
                HttpMethod.GET, entity, LinkGetterDto[].class);

        LinkGetterDto responseLink = getResponseLink(responseEntity);
        checkAreLinksTheSame(responseLink, link);
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

    @Test
    public void getLinkByShortened() {
        Link link = createLink();

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.exchange("/links/short",
                HttpMethod.GET, entity, LinkGetterDto.class);

        LinkGetterDto response = responseEntity.getBody();
        checkAreLinksTheSame(response, link);
    }

    @Test
    public void updateLink() throws IOException {
        createLink();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/UpdateLink.JSON"),
                LinkCreationDto.class);

        HttpEntity<LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());
        restTemplate.exchange("/links/short", HttpMethod.PUT, entity, LinkGetterDto.class);
        assertThat(linkRepository.findByShortenedLink("short"), is(Optional.empty()));
        Link updatedLink = linkRepository.findByShortenedLink(link.getShortenedLink()).get();
        assertThat(updatedLink, is(notNullValue()));
        assertThat(updatedLink.getOriginalLink(), is(link.getOriginalLink()));
        assertThat(updatedLink.getDescription(), is(link.getDescription()));
    }

    @Test
    public void NotFoundIfShortenedNotExistWhileUpdatingLink() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/UpdateLink.JSON"),
                LinkCreationDto.class);
        HttpEntity<LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/links/non-existing shortened", HttpMethod.PUT,
                entity,String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    private void checkAreLinksTheSame(LinkGetterDto responseLink, Link link) {
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

    private LinkGetterDto getResponseLink(ResponseEntity <LinkGetterDto[]> responseEntity) {
        assertThat(responseEntity.getBody(), is(notNullValue()));
        LinkGetterDto[] response = responseEntity.getBody();
        assertThat(response.length, is(1));
        return response[0];
    }
}

