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
import simplifier.model.User;
import simplifier.model.dto.LinkCreationDto;
import simplifier.model.dto.LinkGetterDto;
import simplifier.repositories.LinkRepository;
import simplifier.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
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

    ObjectMapper mapper = new ObjectMapper();

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

    @Test
    public void createWithGeneratedShortened() throws IOException {

        User user = new User();
        user.setUsername("author");
        userRepository.save(user);


        LinkCreationDto link = mapper.readValue(new File("src/test/resources/LinkWithoutShortened.JSON"),
                LinkCreationDto.class);

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.postForEntity("/link",
                link, LinkGetterDto.class);

        LinkGetterDto savedLink = responseEntity.getBody();

        List<Link> links = Lists.newArrayList(linkRepository.findAll());

        assertThat(links.size(), is(1));
        assertThat(savedLink.getShortenedLink(), is(notNullValue()));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void createWithExistShortened() throws IOException {

        User user = new User();
        user.setUsername("author");
        userRepository.save(user);

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);

        ResponseEntity<LinkGetterDto> responseEntity = restTemplate.postForEntity("/link",
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

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/link",
                link, String.class);

        String requiredMessage = "User not found";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is(requiredMessage));

    }

    @Test
    public void conflictIfShortenedNotUnique() throws Exception {
        User user = new User();
        user.setUsername("author");
        userRepository.save(user);

        LinkCreationDto link = mapper.readValue(new File("src/test/resources/validLink.JSON"),
                LinkCreationDto.class);
        restTemplate.postForEntity("/link", link, LinkGetterDto.class);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/link",
                link, String.class);

        String requiredMessage = "Invalid shortened";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(responseEntity.getBody(), is(requiredMessage));
    }
}

