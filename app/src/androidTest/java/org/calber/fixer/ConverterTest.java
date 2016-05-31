package org.calber.fixer;

import org.calber.fixer.models.Exchange;
import org.calber.fixer.models.Product;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by calber on 28/5/16.
 */

public class ConverterTest {

    @Test
    public void testConversionBase() throws Exception {

        FixerApi api = FixerApi.builder().withBase(FixerApi.GBP).withNetwork().withStaticProductApi().build();

        Exchange exchange = api.conversion().toBlocking().first();
        List<Product> plist = api.getProductsApi().getProducts();

        plist = api.convertPrices(plist,exchange,exchange.base);

        for(int c = 0 ; c < plist.size(); c++) {
            final float expected = api.getProductsApi().getProducts().get(c).unitprice.floatValue();
            final float actual = plist.get(c).unitprice.floatValue();
            assertEquals(expected, actual,0.001);
        }
    }

    @Test
    public void testConversion1() throws Exception {

        FixerApi api = FixerApi.builder().withBase(FixerApi.GBP).withNetwork().withStaticProductApi().build();

        Exchange exchange = api.conversion().toBlocking().first();
        List<Product> original = api.getProductsApi().getProducts();

        List<String> currencies = api.currencies().toBlocking().first();

        assertNotNull(currencies);

        String change = currencies.get(1);
        List<Product> plist = api.convertPrices(original, exchange, change);

        exchange = api.conversion().toBlocking().first();
        plist = api.convertPrices(plist,exchange,FixerApi.GBP);

        for(int c = 0 ; c < plist.size(); c++) {
            final float expected = original.get(c).unitprice.floatValue();
            final float actual = plist.get(c).unitprice.floatValue();
            assertEquals(expected, actual,0.001);
        }
    }
}

