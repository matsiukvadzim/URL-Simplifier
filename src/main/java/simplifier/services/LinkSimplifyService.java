package simplifier.services;

public interface LinkSimplifyService {

    String encode(Integer linkId);

    Integer decode(String shortLink);
}
