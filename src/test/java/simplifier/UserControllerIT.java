package simplifier;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import simplifier.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class UserControllerIT {

  private TestRestTemplate restTemplate = new TestRestTemplate();

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

   assertThat(HttpStatus.CREATED, is(responseEntity.getStatusCode()));
   assertTrue(!responseEntity.hasBody());
  }
}
