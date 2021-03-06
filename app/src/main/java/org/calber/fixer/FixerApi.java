package org.calber.fixer;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.calber.fixer.models.Exchange;
import org.calber.fixer.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.schedulers.Schedulers;


/**
 * Created by calber on 24/11/15.
 *
 */

public class FixerApi {

    public final static String EUR = "EUR";
    public final static String GBP = "GBP";

    private static String FXROOTURL = "http://api.fixer.io/";
    private ExchangeApi api;
    private StaticProducts productsApi;

    public ExchangeApi getApi() {
        return api;
    }

    public StaticProducts getProductsApi() {
        return productsApi;
    }

    protected String base = EUR;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        FixerApi fixerApi = new FixerApi();

        public Builder withNetwork() {
            fixerApi.api = NetworkData();
            return this;
        }

        public Builder withBase(String base) {
            fixerApi.base = base;
            return this;
        }

        public Builder withStaticProductApi() {
            fixerApi.productsApi = new StaticProducts();
            return this;
        }

        public FixerApi build() {
            return fixerApi;
        }

    }

    @NonNull
    private static Gson buildGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }


    private static ExchangeApi NetworkData() {
        Gson gson = buildGson();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(FXROOTURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(ExchangeApi.class);
    }

    public rx.Observable<Exchange> convert(String to) {
        return api.exchangeRatesOf(base, to);
    }

    public rx.Observable<Exchange> conversion() {
        return api.exchangeRates(base)
                .subscribeOn(Schedulers.io());
    }


    public rx.Observable<List<String>> currencies() {
        return api.exchangeRates(base)
                .subscribeOn(Schedulers.io())
                .map(exchange -> {
                    List<String> result = new ArrayList<>(exchange.rates.size() +1);
                    result.add(base);
                    result.addAll(exchange.rates.keySet());
                    return result;
                });
    }


    static class StaticProducts {
        public List<Product> getProducts() {
            List<Product> products = new ArrayList<>(4);
            products.add(new Product("Peas", "bag", 0.95));
            products.add(new Product("Eggs", "dozen", 2.1));
            products.add(new Product("Milk", "bottle", 1.3));
            products.add(new Product("Beans", "can", 0.73));
            return products;
        }
    }

    public List<Product> convertPrices(List<Product> products, Exchange factor, String currency) {
        if(currency.equals(factor.base)) return products;

        for (Product p: products)
            p.unitprice *= factor.rates.get(currency);

        base = currency;
        return products;
    }

    public double shopTotal(List<Product> products) {
        double tot = 0f;
        for (Product p: products)
            tot += p.unitprice * p.quantity;
        return tot;
    }

    private interface ExchangeApi {

        @GET("latest")
        rx.Observable<Exchange> exchangeRates(@Query("base") String base);

        @GET("latest")
        rx.Observable<Exchange> exchangeRatesOf(@Query("base") String base, @Query("symbols") String symbols);

        @GET("latest")
        rx.Observable<Exchange> exchangeRate(@Query("base") String base, @Query("symbols") String symbols);
    }

}
