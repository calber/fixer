package org.calber.fixer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by calber on 27/5/16.
 */
public class Exchange {

    @SerializedName("base")
    @Expose
    public String base;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("rates")
    @Expose
    public HashMap<String,Double> rates;

}
