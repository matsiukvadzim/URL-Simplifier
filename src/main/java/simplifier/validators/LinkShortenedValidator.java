package simplifier.validators;

import org.springframework.beans.factory.annotation.Autowired;
import simplifier.repositories.LinkRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LinkShortenedValidator implements ConstraintValidator<ValidShortenedLink, String> {

    private LinkRepository linkRepository;

    @Autowired
    public void setLinkRepository(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return linkRepository.findByShortenedLink(value)
                .map(link -> false)
                .orElse(true);
    }
}
