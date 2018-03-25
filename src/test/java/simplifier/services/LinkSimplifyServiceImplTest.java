package simplifier.services;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LinkSimplifyServiceImplTest {

    private LinkSimplifyServiceImpl simplifyService = new LinkSimplifyServiceImpl();

    @Test
    public void encode() {
        String encoded1 = simplifyService.encode(1312);
        String encoded2 = simplifyService.encode(43342141);
        String encoded3 = simplifyService.encode(41);

        assertThat(encoded1, is("xM"));
        assertThat(encoded2, is("8rMGX"));
        assertThat(encoded3, is("R"));
    }
}
