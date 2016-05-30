package org.calber.fixer;

import java.util.List;

/**
 * Created by calber on 28/5/16.
 */
public class ProductPriceManager {

    public static List<Product> convertPrices(List<Product> products, Exchange factor, String currency) {

        if(factor.rates.isEmpty()) return products;

        for (Product p: products)
            p.unitprice *= factor.rates.get(currency);

        return products;
    }

    public static double shopTotal(List<Product> products) {
        double tot = 0f;

        for (Product p: products)
            tot += p.unitprice * p.quantity;

        return tot;
    }
}
