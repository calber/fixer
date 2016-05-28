package org.calber.fixer;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by calber on 24/11/15.
 */
public class FixerApi {

    public final static String EUR = "EUR";
    private static String ROOTURL = "http://api.fixer.io/";
    private Api api;
    private static final String ANDROID = "ANDROID";

    public Api getApi() {
        return api;
    }
    protected String base = EUR;
    HashMap<String,Double> foreignExchangeRates = new HashMap<>();

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

        public Builder withStaticData(Context context, String name) {
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

    private static Api NetworkData() {
        Gson gson = buildGson();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(ROOTURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(Api.class);
    }

    public rx.Observable<Exchange> convert(String to) {
        return api.exchangeRatesOf(base,to);
    }

    public rx.Observable<Set<String>> currencies() {
        return api.exchangeRates(base)
                .map(exchange -> exchange.rates.keySet());
    }


    private interface Api {

        @GET("latest")
        rx.Observable<Exchange> exchangeRates(@Query("base") String base);

        @GET("latest")
        rx.Observable<Exchange> exchangeRatesOf(@Query("base") String base,@Query("symbols") String symbols);

        @GET("latest")
        rx.Observable<Exchange> exchangeRate(@Query("base") String base, @Query("symbols") String symbols);
    }

}
