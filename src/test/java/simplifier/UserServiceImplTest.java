package simplifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import simplifier.model.User;
import simplifier.repositories.UserRepository;
import simplifier.services.UserServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  public void saveUser() {
    User user = new User();

    when(userRepository.save(user)).thenReturn(user);

    User savedUser = userService.saveUser(user).get();

    assertThat(savedUser, is(user));

    verify(userRepository).save(user);
  }
}
