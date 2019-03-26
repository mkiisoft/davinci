package com.davinci.android.data;

import com.davinci.android.model.Classes;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiCall {

    @GET("json/get/{endpoint}")
    Observable<List<Classes>> getClasses(@Path("endpoint") String endpoint);
}
