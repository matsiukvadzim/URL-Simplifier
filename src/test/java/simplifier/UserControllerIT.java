package simplifier;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import simplifier.model.User;
import simplifier.repositories.UserRepository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Test
    public void createUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");

        ResponseEntity<User> responseEntity = restTemplate.postForEntity("/user",
                user, User.class);

        User savedUser = userRepository.findByUsername("test");

        assertThat(savedUser, is(user));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), is(nullValue()));
    }

    @Test
    public void conflictIfUsernameNotUnique() {
        User firstUser = new User();
        firstUser.setUsername("test");
        firstUser.setPassword("test");

        userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setUsername("test");
        secondUser.setPassword("test");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/user",
                secondUser, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
    }
}
