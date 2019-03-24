package com.davinci.android.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.davinci.android.model.Classes;
import com.davinci.android.util.Constants;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Generator implements Constants {

    public static ApiCall initClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(ApiCall.class);
    }

    public static RxSharedPreferences initPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return RxSharedPreferences.create(preferences);
    }

    public static void saveClasses(Context context, String value) {
        initPreferences(context).getString(CLASSES, "[]").set(value);
    }

    static List<Classes> getClasses(Context context) {
        return new Gson().fromJson(initPreferences(context)
                .getString(CLASSES, "[]").get(),
                new TypeToken<List<Classes>>(){}.getType());
    }

    static String getSavedClasses(Context context) {
        return initPreferences(context).getString(CLASSES).get();
    }

    public static boolean darkMode(Context context) {
        return initPreferences(context).getBoolean(MODE).get();
    }

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}