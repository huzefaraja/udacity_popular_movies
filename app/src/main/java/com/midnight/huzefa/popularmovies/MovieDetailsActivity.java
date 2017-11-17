package com.midnight.huzefa.popularmovies;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import static com.midnight.huzefa.popularmovies.PopularMovies.URL_FOR_GENRES;

public class MovieDetailsActivity extends AppCompatActivity {

    private final static String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    Movie movie;
    PopularMovies POPULAR_MOVIES;
    ImageView mImageViewBackdrop;
    ImageView mImageViewBigPoster;
    TextView mTextViewOverview;
    TextView mTextViewReleaseDate;
    TextView mTextViewRating;
    LinearLayout mLinearLayoutGenres;
    View[] animatedViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        POPULAR_MOVIES = PopularMovies.getInstance(getApplicationContext());
        mImageViewBackdrop = (ImageView) findViewById(R.id.image_view_backdrop);
        mImageViewBigPoster = (ImageView) findViewById(R.id.image_view_big_poster);
        mTextViewOverview = (TextView) findViewById(R.id.text_view_overview);
        mLinearLayoutGenres = (LinearLayout) findViewById(R.id.linear_layout_genres);
        mTextViewReleaseDate = (TextView) findViewById(R.id.text_view_release_date);
        mTextViewRating = (TextView) findViewById(R.id.text_view_rating);


        // setting initial positions
        // removed conditional check for API 14, it's the minimum now


        animatedViews = new View[]{
                mImageViewBackdrop,
                mLinearLayoutGenres,
                mTextViewOverview,
                mTextViewReleaseDate,
                mTextViewRating
        };
        int i;
        for (i = 0; i < 3; i++) {
            if (i % 2 == 0) {
                animatedViews[i].setRotationY(90f);
                animatedViews[i].setPivotX(0);
            } else {
                animatedViews[i].setRotationY(-90f);
                animatedViews[i].setPivotX(animatedViews[i].getWidth());
            }
        }
        animatedViews[i].setRotationX(-90f);
        animatedViews[i].setPivotY(0);
        i++;
        animatedViews[i].setRotationX(90f);
        animatedViews[i].setPivotY(animatedViews[i].getHeight());


        if (getMovie()) {
            Picasso.with(getApplicationContext()).load(movie.getPosterPath()).into(mImageViewBigPoster);
            getBackdrop();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        final RequestCreator load = Picasso.with(getApplicationContext()).load(movie.iNeedABiggerPoster()).noFade();
        Callback c = new Callback() {
            @Override
            public void onSuccess() {
                load.into(mImageViewBigPoster);
            }

            @Override
            public void onError() {

            }
        };
        load.fetch(c);
    }

    private void getDetails() {
        try {
            getSupportActionBar().setTitle(movie.getTitle());
            int sixDP = (int) (6 * getResources().getDisplayMetrics().density + 0.5f);
            int sixteenDP = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
            mTextViewOverview.setText(movie.getOverview());
            mTextViewOverview.setPadding(sixteenDP, sixteenDP, sixteenDP, sixteenDP);
            DateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String rating = "Rating: " + movie.getVoteAverage() + " / 10.0";
            mTextViewRating.setText(rating);
            mTextViewRating.setPadding(sixDP, sixDP, sixDP, sixDP);
            mTextViewReleaseDate.setText(String.format("Out: %s", simpleDateFormat.format(movie.getReleaseDate().getTime())));
            mTextViewReleaseDate.setPadding(sixDP, sixDP, sixDP, sixDP);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error in getDetails: " + ex);
            ex.printStackTrace();
        }
    }

    private void getGenres() {
        if (POPULAR_MOVIES.genres == null) {
            populateGenres();
        } else {
            for (int id : movie.genreIds) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText(POPULAR_MOVIES.genres.get(id));
                textView.setTextColor(Color.argb(255, 11, 20, 21));
                textView.setBackgroundColor(Color.argb(16, 0, 0, 0));
                int sixDP = (int) (6 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setPadding(sixDP, sixDP, sixDP, sixDP);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int threeDP = sixDP / 2;
                layoutParams.setMargins(threeDP, sixDP, threeDP, sixDP);
                textView.setLayoutParams(layoutParams);
                mLinearLayoutGenres.addView(textView);

            }
        }
    }


    private void populateGenres() {
        POPULAR_MOVIES.addToRequestQueue(new JsonObjectRequest(URL_FOR_GENRES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            POPULAR_MOVIES.genres = new HashMap<Integer, String>();
                            JSONArray array = response.getJSONArray("genres");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                POPULAR_MOVIES.genres.put(object.getInt("id"), object.getString("name"));
                            }
                            getGenres();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    private void getBackdrop() {
        Picasso.with(getApplicationContext()).load(movie.getBackdropPath()).into(mImageViewBackdrop, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                getGenres();
                getDetails();

                // just moving things
                // removed conditional check for API 14, it's the minimum now
                int i;
                for (i = 0; i < 3; i++) {
                    animatedViews[i].animate().rotationY(0f).setStartDelay(75 + 250 * i).start();
                }
                animatedViews[i].animate().rotationX(0f).setStartDelay(75 + 250 * i).start();
                i++;
                animatedViews[i].animate().rotationX(0f).setStartDelay(75 + 250 * i).start();


            }

            @Override
            public void onError() {
                super.onError();
                getGenres();
                getDetails();
            }
        });
    }


    private boolean getMovie() {
        if (getIntent() != null && getIntent().hasExtra("movie")) {
            movie = getIntent().getParcelableExtra("movie");
            return true;
        }
        return false;
    }

}
