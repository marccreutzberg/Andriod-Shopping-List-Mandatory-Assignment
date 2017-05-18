package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Marc Creutzberg on 11-04-2017.
 */

public class Product implements Parcelable {
    private int mData;
    private String name;
    private String quantity;

    public Product() {} //Empty constructor we will need later!

    public Product(String name, String quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }

    protected Product(Parcel in) {
        mData = in.readInt();
        name = in.readString();
        quantity = in.readString();
    }

    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public String toString() {
        return name+" - "+quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }
}
