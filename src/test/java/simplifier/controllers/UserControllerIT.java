package simplifier.controllers;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import simplifier.model.User;
import simplifier.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerIT {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    public void createUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");

        ResponseEntity<User> responseEntity = restTemplate.postForEntity("/user",
                user, User.class);

        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);

        assertThat(users.size(), is(1));

        User savedUser = users.get(0);

        boolean passwordsMatch = passwordEncoder.matches(user.getPassword(), savedUser.getEncryptedPassword());

        assertThat(passwordsMatch, is(true));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), is(nullValue()));

    }

    @Test
    public void conflictIfUsernameNotUnique() {
        User existingUser  = new User();
        existingUser.setUsername("test");
        existingUser.setPassword("test");

        User savedUser = userRepository.save(existingUser);

        User invalidUser = new User();
        invalidUser.setUsername("test");
        invalidUser.setPassword("test");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/user",
                invalidUser, String.class);

        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);

        assertThat(users.size(), is(1));

        assertThat(userRepository.findByUsername(existingUser.getUsername()), is(savedUser));

        String requiredMessage = "Username already exists";

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(responseEntity.getBody(), is(requiredMessage));
    }
}
