package simplifier.services;

import org.springframework.stereotype.Service;

@Service
public class LinkSimplifyServiceImpl implements LinkSimplifyService {

    public final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ-_";
    public final int BASE = ALPHABET.length();

    @Override
    public String encode(Integer linkId) {
        StringBuilder str = new StringBuilder();
        while (linkId > 0) {
            str.insert(0, ALPHABET.charAt(linkId % BASE));
            linkId = linkId / BASE;
        }
        return str.toString();
    }

    @Override
    public Integer decode(String shortLink) {
        int num = 0;
        for (int i = 0; i < shortLink.length(); i++) {
            num = num * BASE + ALPHABET.indexOf(shortLink.charAt(i));
        }
        return num;
    }
}
