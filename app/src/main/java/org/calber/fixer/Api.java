package org.calber.fixer;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @deprecated
 * Created by calber on 20/4/16.
 */
public interface Api {

    @GET("latest")
    rx.Observable<Exchange> exchangeRates(@Query("base") String base);

    @GET("latest")
    rx.Observable<Exchange> exchangeRatesOf(@Query("base") String base,@Query("symbols") String symbols);

    @GET("latest")
    rx.Observable<Exchange> exchangeRate(@Query("base") String base, @Query("symbols") String symbols);
}
