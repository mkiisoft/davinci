package com.davinci.android.data;

import com.davinci.android.model.Classes;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiCall {

    @GET("json/get/4JW5tHAP8")
    Observable<List<Classes>> getClasses();
}
