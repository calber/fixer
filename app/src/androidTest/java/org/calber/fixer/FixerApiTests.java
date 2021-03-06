package org.calber.fixer;

import org.calber.fixer.models.Exchange;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FixerApiTests {


    @Test
    public void testApi1() throws Exception {
        FixerApi api = FixerApi.builder().withNetwork().build();
        Exchange exc1 = api.convert("GBP").toBlocking().first();
        assertNotNull(exc1.rates.get("GBP"));
    }

    @Test
    public void testApi2() throws Exception {
        FixerApi api = FixerApi.builder().withNetwork().build();
        Exchange exc1 = api.convert(api.base).toBlocking().first();
        assertNull(exc1.rates.get(api.base));
    }

    @Test
    public void testApi3() throws Exception {
        FixerApi api = FixerApi.builder().withBase("GBP").withNetwork().build();
        Exchange exc2 = api.convert("USD").toBlocking().first();
        assertNotNull(exc2);
    }

    @Test
    public void testApi4() throws Exception {
        FixerApi api = FixerApi.builder().withNetwork().build();
        List<String> exc2 = api.currencies().toBlocking().first();
        assertNotNull(exc2);
    }

    @Test
    public void testApi5() throws Exception {
        FixerApi api = FixerApi.builder().withBase("GBP").withNetwork().build();
        Exchange exc2 = api.conversion().toBlocking().first();
        assertNotNull(exc2);
    }


}
