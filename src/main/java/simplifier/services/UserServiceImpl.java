package simplifier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import simplifier.model.User;
import simplifier.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setbCryptPasswordEncoder(
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Optional<User> saveUser(User user) {
        User foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser != null) {
            return Optional.empty();
        }
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
