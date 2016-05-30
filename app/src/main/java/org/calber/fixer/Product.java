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

    @Override
    public String toString() {
        return String.format("%s (%.2f %s)",name,unitprice,unit);
    }

    @Override
    public boolean equals(Object o) {
        Product other = (Product) o;
        return other.name.equals(name);
    }
}
