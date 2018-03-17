package simplifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import simplifier.model.User;
import simplifier.repositories.UserRepository;
import simplifier.services.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Optional<User> savedUser = userService.saveUser(user);

        assertThat(savedUser, is(Optional.of(user)));

        verify(userRepository).save(user);
        verify(bCryptPasswordEncoder).encode(user.getPassword());
    }

    @Test
    public void doNotSaveIfUsernameDuplicate() {
        User existingUser  = new User();
        existingUser.setUsername("test");
        existingUser.setPassword("test");

        User invalidUser = new User();
        invalidUser.setUsername("test");
        invalidUser.setPassword("test");

        when(userRepository.findByUsername(invalidUser.getUsername())).thenReturn(existingUser);

        Optional<User> savedUser = userService.saveUser(invalidUser);

        assertThat(savedUser, is(Optional.empty()));

        verify(userRepository).findByUsername(invalidUser.getUsername());
        verify(bCryptPasswordEncoder, never()).encode(invalidUser.getPassword());
    }
}
