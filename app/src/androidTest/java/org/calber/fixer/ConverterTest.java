package org.calber.fixer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by calber on 28/5/16.
 */

public class ConverterTest {

    @Test
    public void testProduct() throws Exception {

        Exchange exchange = new Exchange();
        HashMap<String, Double> aMap = new HashMap<>();
        aMap.put("EUR",1.2);
        exchange.rates = aMap;

        List<Product> plist = new ArrayList<>();
        final Product product = new Product();
        product.unitprice = 1d;
        plist.add(product);


        PriceConverter.ConvertPrices(plist,exchange,"EUR");
        assertEquals(1.2, plist.get(0).unitprice.floatValue(),0.001);

    }
}

