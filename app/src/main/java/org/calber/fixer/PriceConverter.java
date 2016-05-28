package org.calber.fixer;

import java.util.List;

/**
 * Created by calber on 28/5/16.
 */
public class PriceConverter {

    public static List<Product> ConvertPrices(List<Product> products, Exchange factor, String currency) {

        for (Product p: products)
            p.unitprice *= factor.rates.get(currency);

        return products;
    }
}
