package org.calber.fixer;

import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by calber on 28/5/16.
 */

public class ConverterTest {
    @Mock
    Exchange exchange;

    @Test
    public void testProduct() throws Exception {

        FixerApi api = FixerApi.builder().withStaticProductApi().build();

        exchange = new Exchange();
        HashMap<String, Double> aMap = new HashMap<>();
        aMap.put("DOUBLE",2.0);
        exchange.rates = aMap;


        List<Product> plist = api.getProductsApi().getProducts();

        PriceConverter.ConvertPrices(plist,exchange,"DOUBLE");


        for(int c = 0 ; c < plist.size(); c++) {

            final float expected = api.getProductsApi().getProducts().get(c).unitprice.floatValue();
            final float actual = plist.get(c).unitprice.floatValue();

            assertEquals(expected * 2, actual,0.001);
        }

    }

}

