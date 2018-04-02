package simplifier.validators;

import org.springframework.beans.factory.annotation.Autowired;
import simplifier.repositories.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LinkAuthorValidator implements ConstraintValidator<ValidAuthor, String> {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userRepository.findByUsername(value)
                .map(user -> true)
                .orElse(false);
    }
}
