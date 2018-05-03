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

    private static final String USERNAME = "author";

    private static final String PASSWORD = "password";

    private static final String LINKS_URL = "/links";

    private static final String SHORT_URL = "/short";

    private LinkRepository linkRepository;

    private UserRepository userRepository;

    private TagRepository tagRepository;

    private TestRestTemplate restTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    private BCryptPasswordEncoder passwordEncoder;

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
    public void shouldCreateLinkWithGeneratedShortenedAndReturnLink() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithoutShortened.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.exchange(LINKS_URL, HttpMethod.POST,
                entity, LinkGetterDto.class);

        LinkGetterDto savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(notNullValue()));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldCreateLinkWithExistShortenedAndReturnLink() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.exchange(LINKS_URL, HttpMethod.POST,
                entity, LinkGetterDto.class);

        LinkGetterDto savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is("short"));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldReturn400IfLinksAuthorNotExist() throws IOException {
        createUser();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithEmptyUser.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<String> responseEntity = restTemplate.exchange(LINKS_URL, HttpMethod.POST,
                entity, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is("User not found"));
    }

    @Test
    public void shouldReturn400IfShortenedNotUnique() throws Exception {
        createLink();

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);

        HttpEntity <LinkCreationDto> entity = new HttpEntity<>(link, getHeaders());

        ResponseEntity<String> responseEntity = restTemplate.exchange(LINKS_URL, HttpMethod.POST,
                entity, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is("Invalid shortened"));
    }

    @Test
    public void shouldReturnAllLinksByTagName() {
        getResponseLinkAndCheckIt("/links/tags/1");
    }

    @Test
    public void shouldReturnALlLinksByUserName() {
        getResponseLinkAndCheckIt("/links/users/author");
    }

    @Test
    public void shouldRedirectFromShortenedLinkToOriginalLink() {
        createLink();

        ResponseEntity<String> response = restTemplate.getForEntity(SHORT_URL, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("link"));
    }

    @Test
    public void shouldReturn404DuringRedirectionIfShortenedNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(SHORT_URL, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void shouldReturnLinkByShortenedLink() {
        Link link = createLink();

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.exchange("/links/short",
                HttpMethod.GET, entity, LinkGetterDto.class);

        LinkGetterDto response = responseEntity.getBody();
        checkAreLinksTheSame(response, link);
    }

    @Test
    public void shouldUpdateLinkAndReturnUpdatedLink() throws IOException {
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
    public void shouldReturn404IfShortenedLinkNotExistWhenLinkUpdating() throws IOException {
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

    private void getResponseLinkAndCheckIt(String url) {
        Link link = createLink();

        HttpEntity entity = new HttpEntity(getHeaders());

        ResponseEntity<LinkGetterDto[]> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, entity, LinkGetterDto[].class);

        assertThat(responseEntity.getBody(), is(notNullValue()));
        LinkGetterDto[] response = responseEntity.getBody();
        assertThat(response.length, is(1));
        checkAreLinksTheSame(response[0], link);

    }
}

