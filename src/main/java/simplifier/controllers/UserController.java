package simplifier.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simplifier.model.User;
import simplifier.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

  private UserService userService;

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.saveUser(user);
  }
}
