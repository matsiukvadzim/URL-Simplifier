package simplifier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import simplifier.model.User;
import simplifier.repositories.UserRepository;

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
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User saveUser(User user) {
    user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }
}
