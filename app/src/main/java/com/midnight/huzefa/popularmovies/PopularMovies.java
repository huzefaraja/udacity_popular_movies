package com.midnight.huzefa.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

/**
 * Created by huzefa on 8/18/2016.
 */
public class PopularMovies {
    public static final String URL_BASE = "http://image.tmdb.org/t/p/";
    public static final String URL_SIZE_185 = "/w185/";
    public static final String URL_SIZE_342 = "/w342/";
    public static final String URL_SIZE_500 = "/w500/";
    public static final String URL_SIZE_780 = "/w780/";
    public static final String URL_API_KEY = "api_key";
    public final static String URL_FOR_POPULAR_MOVIES = "http://api.themoviedb.org/3/movie/popular?" + URL_API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;
    public final static String URL_FOR_TOP_RATED_MOVIES = "http://api.themoviedb.org/3/movie/top_rated?" + URL_API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;
    public final static String URL_FOR_GENRES = "http://api.themoviedb.org/3/genre/movie/list?" + URL_API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;
    private static PopularMovies mInstance;
    private static Context mContext;
    private static SharedPreferences mSharedPreferences;
    HashMap<Integer, String> genres;
    private RequestQueue mRequestQueue;

    private PopularMovies(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
        mSharedPreferences = getSharedPreferences();
    }


    public static synchronized PopularMovies getInstance(Context context) {
        if (mInstance == null)
            mInstance = new PopularMovies(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        }
        return mSharedPreferences;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
