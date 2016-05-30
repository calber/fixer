package org.calber.fixer;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by calber on 28/5/16.
 */

public class ConverterTest {

    @Test
    public void testProductPriceConversion() throws Exception {

        FixerApi api = FixerApi.builder().withStaticProductApi().build();

        Exchange exchange = new Exchange();
        HashMap<String, Double> aMap = new HashMap<>();
        aMap.put("DOUBLE",2.0);
        exchange.rates = aMap;

        List<Product> plist = api.getProductsApi().getProducts();

        ProductPriceManager.convertPrices(plist,exchange,"DOUBLE");


        for(int c = 0 ; c < plist.size(); c++) {

            final float expected = api.getProductsApi().getProducts().get(c).unitprice.floatValue();
            final float actual = plist.get(c).unitprice.floatValue();

            assertEquals(expected * 2, actual,0.001);
        }

    }

}

