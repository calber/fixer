package org.calber.fixer;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by calber on 20/4/16.
 */
public interface Api {

    @GET("latest")
    rx.Observable<Exchange> convert(@Query("symbols") String symbols);


    @GET("latest")
    rx.Observable<Exchange> convertWithBase(@Query("base") String base, @Query("symbols") String symbols);
}
