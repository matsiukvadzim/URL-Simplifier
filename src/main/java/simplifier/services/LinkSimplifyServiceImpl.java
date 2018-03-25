package simplifier.services;

import org.springframework.stereotype.Service;

@Service
public class LinkSimplifyServiceImpl implements LinkSimplifyService {

    private final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ-_";
    private final int BASE = ALPHABET.length();

    @Override
    public String encode(Integer linkId) {
        StringBuilder str = new StringBuilder();
        while (linkId > 0) {
            str.insert(0, ALPHABET.charAt(linkId % BASE));
            linkId = linkId / BASE;
        }
        return str.toString();
    }
}
