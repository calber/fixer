package org.calber.fixer;

import android.app.Application;
import android.test.ApplicationTestCase;

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    public void testApi() throws Exception {
        FixerApi api = FixerApi.builder().withNetwork().build();

        Exchange exc1 = api.getApi().convert("USD,GBP").toBlocking().first();

        assertNotNull(exc1.rates.get("USD"));
        assertNotNull(exc1.rates.get("GBP"));
    }

    public void testBase() throws Exception {
        FixerApi api = FixerApi.builder().withNetwork().build();

        Exchange exc2 = api.getApi().convertWithBase("USD","EUR").toBlocking().first();
        assertNotNull(exc2);

    }
}
