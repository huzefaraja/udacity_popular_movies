package com.midnight.huzefa.popularmovies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private JSONObject obj;
    private PopularMovies POPULAR_MOVIES;
    private ArrayList<Movie> movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException ex) {
            Log.e(LOG_TAG, "getSupportActionBar() returned null");
        }
        POPULAR_MOVIES = PopularMovies.getInstance(getApplicationContext());
        Spinner mSpinnerOrderBy = findViewById(R.id.spinner_order_by);
        mSpinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        requestMovies(PopularMovies.URL_FOR_POPULAR_MOVIES);
                        POPULAR_MOVIES.getSharedPreferences().edit().putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popularity)).apply();
                        break;
                    case 1:
                        requestMovies(PopularMovies.URL_FOR_TOP_RATED_MOVIES);
                        POPULAR_MOVIES.getSharedPreferences().edit().putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_top_rated)).apply();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movies = new ArrayList<>();
            if (POPULAR_MOVIES.getSharedPreferences().getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default)).equals(getString(R.string.pref_sort_by_popularity))) {
                requestMovies(PopularMovies.URL_FOR_POPULAR_MOVIES);
                mSpinnerOrderBy.setSelection(0, true);
            } else {
                requestMovies(PopularMovies.URL_FOR_TOP_RATED_MOVIES);
                mSpinnerOrderBy.setSelection(1, true);
            }
        } else {
            movies = savedInstanceState.getParcelableArrayList("movies");
            setUpView();
        }
    }

    private void requestMovies(String url) {
        StringRequest s = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response != null) {
                                movies.clear();
                                obj = new JSONObject(response);
                                if (obj.has("results")) {
                                    JSONArray arr = obj.getJSONArray("results");
                                    for (int i = 0; i < arr.length(); i++) {
                                        movies.add(new Movie(arr.getJSONObject(i)));
                                    }
                                    setUpView();
                                } else { // does not run, response code 401 causes a VolleyError
                                    String message = "No results. ";
                                    if (obj.has("status_message")) {
                                        message += obj.getString("status_message");
                                    }
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e(LOG_TAG, "The server did not respond with any data.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Error in request: " + error);
                        try {
                            String errorData = new String(error.networkResponse.data);
                            Log.e(LOG_TAG, "Error data: " + errorData);
                            Log.e(LOG_TAG, "Let's see whether we have something here.");
                            JSONObject jsonError = new JSONObject(errorData);
                            if (jsonError.has("status_message")) {
                                Log.e(LOG_TAG, "Got something. Let me toast it.");
                                Toast.makeText(getApplicationContext(), jsonError.getString("status_message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                            Log.e(LOG_TAG, "Never mind. " + ex);
                        }
                    }
                });
        POPULAR_MOVIES.addToRequestQueue(s);
    }

    private void setUpView() {
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_main_activity);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter mAdapter = new RecyclerViewForMoviesAdapter(movies);
        mRecyclerView.setAdapter(mAdapter);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int numberOfColumns = 0;
        int widths = 185;
        while (widths < width) {
            numberOfColumns++;
            widths += 185;
        }
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movies);
        super.onSaveInstanceState(outState);
    }

    public class RecyclerViewForMoviesAdapter extends RecyclerView.Adapter<RecyclerViewForMoviesAdapter.ViewHolder> {
        View transitioningView = null;
        private ArrayList<Movie> mMovies;

        public RecyclerViewForMoviesAdapter(ArrayList<Movie> movies) {
            mMovies = movies;
        }

        @Override
        public RecyclerViewForMoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            return new ViewHolder(LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.recycler_view_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(getApplicationContext()).load(mMovies.get(position).getPosterPath()).into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final String LOG_TAG = ViewHolder.class.getSimpleName();
            public ImageView mImageView;

            public ViewHolder(final View v) {
                super(v);
                v.setOnClickListener(this);
                mImageView = v.findViewById(R.id.image_view_poster);
                mImageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Movie movie = movies.get(getAdapterPosition());
                ActivityOptionsCompat activityOptions = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (transitioningView != null) {
                        transitioningView.setTransitionName(null);
                    }
                    transitioningView = mImageView;
                    transitioningView.setTransitionName("image_transition");
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, transitioningView, transitioningView.getTransitionName());
                } else {
                    activityOptions = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
                }
                Intent intent = (new Intent(getApplicationContext(), MovieDetailsActivity.class).putExtra("movie", movie));
                startActivity(intent, activityOptions.toBundle());
            }
        }
    }
}
