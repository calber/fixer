package org.calber.fixer;

/**
 * Created by calber on 27/5/16.
 */
public class Product {
    public String name;
    public String unit;
    public Double unitprice;
    public int quantity;

    public Product(String name, String unit, Double unitprice) {
        this.name = name;
        this.unit = unit;
        this.unitprice = unitprice;
    }
}
