package org.calber.fixer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by calber on 27/5/16.
 */
public class Product implements Parcelable {
    public String name;
    public String unit;
    public Double unitprice;
    public int quantity;

    public Product(String name, String unit, Double unitprice) {
        this.name = name;
        this.unit = unit;
        this.unitprice = unitprice;
    }

    public Product(String name, String unit, Double unitprice, int quantity) {
        this.name = name;
        this.unit = unit;
        this.unitprice = unitprice;
        this.quantity = quantity;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.unit);
        dest.writeValue(this.unitprice);
        dest.writeInt(this.quantity);
    }

    protected Product(Parcel in) {
        this.name = in.readString();
        this.unit = in.readString();
        this.unitprice = (Double) in.readValue(Double.class.getClassLoader());
        this.quantity = in.readInt();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
