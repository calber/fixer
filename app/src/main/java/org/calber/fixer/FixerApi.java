package org.calber.fixer;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by calber on 24/11/15.
 */
public class FixerApi {

    private static String ROOTURL = "http://api.fixer.io/";
    private Api api;
    private static final String ANDROID = "ANDROID";

    public Api getApi() {
        return api;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        FixerApi loramanApi = new FixerApi();

        public Builder withNetwork() {
            loramanApi.api = NetworkData();
            return this;
        }

        public Builder withStaticData(Context context, String name) {
            return this;
        }

        public FixerApi build() {
            return loramanApi;
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

    public rx.Observable<Exchange> convertWrapper(String from, String to) {
        return api.convert(String.format("%s,%s",from,to));
    }
}
