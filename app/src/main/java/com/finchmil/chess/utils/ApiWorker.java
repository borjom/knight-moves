package com.finchmil.chess.utils;

import com.finchmil.chess.models.LevelModel;
import com.finchmil.chess.models.LevelsConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vgrigoryev on 23.09.2016.
 */

public class ApiWorker {

    public static final long TIMEOUT = 60000;

    private static ApiWorker instance;
    private Api api;

    private ApiWorker() {
    }

    public static ApiWorker getInstance() {
        if (instance == null) {
            instance = new ApiWorker();
            init();
        }

        return instance;
    }

    private static void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(ApiWorker.TIMEOUT, TimeUnit.MILLISECONDS);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://finch.fm/")
                .build();

        instance.api = retrofit.create(Api.class);
    }

    public Observable<LevelModel> getLevelModel(String address) {
        return api
                .getLevelModel(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LevelsConfig> getLevelConfig() {
        return api
                .getLevelConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private interface Api {
        @GET
        Observable<LevelModel> getLevelModel(
                @Url String url
        );

        @GET("game/chess_config.json")
        Observable<LevelsConfig> getLevelConfig();
    }
}
