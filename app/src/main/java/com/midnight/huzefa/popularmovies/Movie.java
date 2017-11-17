package com.midnight.huzefa.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by huzefa on 8/18/2016.
 */
public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
    String posterPath;
    boolean adult;
    String overview;
    Calendar releaseDate;
    int[] genreIds;
    long id;
    String originalTitle;
    String originalLanguageCode;
    String title;
    String backdropPath;
    double popularity;
    long voteCount;
    boolean video;
    double voteAverage;

    public Movie(JSONObject movieInJSON) {
        try {
            posterPath = movieInJSON.getString(MovieAttributes.POSTER_PATH);
            overview = movieInJSON.getString(MovieAttributes.OVERVIEW);
            originalTitle = movieInJSON.getString(MovieAttributes.ORIGINAL_TITLE);
            originalLanguageCode = movieInJSON.getString(MovieAttributes.ORIGINAL_LANGUAGE);
            title = movieInJSON.getString(MovieAttributes.TITLE);
            backdropPath = movieInJSON.getString(MovieAttributes.BACKDROP_PATH);
            releaseDate = Calendar.getInstance();
            String[] brokenDate = movieInJSON.getString(MovieAttributes.RELEASE_DATE).split("-");
            releaseDate.set(Integer.parseInt(brokenDate[0]),
                    Integer.parseInt(brokenDate[1]),
                    Integer.parseInt(brokenDate[2]));
            JSONArray array = movieInJSON.getJSONArray(MovieAttributes.GENRE_IDS);
            genreIds = new int[array.length()];
            for (int i = 0; i < array.length(); i++) {
                genreIds[i] = array.getInt(i);
            }
            id = movieInJSON.getLong(MovieAttributes.ID);
            voteCount = movieInJSON.getLong(MovieAttributes.VOTE_COUNT);
            popularity = movieInJSON.getDouble(MovieAttributes.POPULARITY);
            voteAverage = movieInJSON.getDouble(MovieAttributes.VOTE_AVERAGE);
            adult = movieInJSON.getBoolean(MovieAttributes.ADULT);
            video = movieInJSON.getBoolean(MovieAttributes.VIDEO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Movie(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        originalTitle = in.readString();
        originalLanguageCode = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        boolean[] booleans = new boolean[2];
        in.readBooleanArray(booleans);
        adult = booleans[0];
        video = booleans[1];
        releaseDate = Calendar.getInstance();
        int[] intDate = new int[3];
        in.readIntArray(intDate);
        releaseDate.set(intDate[0], intDate[1], intDate[2]);
        id = in.readLong();
        voteCount = in.readLong();
        genreIds = new int[in.readInt()];
        in.readIntArray(genreIds);
        popularity = in.readDouble();
        voteAverage = in.readDouble();
    }

    public String iNeedABiggerPoster() {
        return PopularMovies.URL_BASE + PopularMovies.URL_SIZE_780 + posterPath;
    }

    public String getPosterPath() {
        return PopularMovies.URL_BASE + PopularMovies.URL_SIZE_185 + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Calendar releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguageCode() {
        return originalLanguageCode;
    }

    public void setOriginalLanguageCode(String originalLanguageCode) {
        this.originalLanguageCode = originalLanguageCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return PopularMovies.URL_BASE + PopularMovies.URL_SIZE_500 + backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(originalTitle);
        parcel.writeString(originalLanguageCode);
        parcel.writeString(title);
        parcel.writeString(backdropPath);
        parcel.writeBooleanArray(new boolean[]{adult, video});
        parcel.writeIntArray(new int[]{releaseDate.get(Calendar.YEAR), releaseDate.get(Calendar.MONTH), releaseDate.get(Calendar.DATE)});
        parcel.writeLong(id);
        parcel.writeLong(voteCount);
        parcel.writeInt(genreIds.length);
        parcel.writeIntArray(genreIds);
        parcel.writeDouble(popularity);
        parcel.writeDouble(voteAverage);
    }

    static class MovieAttributes {
        public static final String POSTER_PATH = "poster_path";
        public static final String ADULT = "adult";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
        public static final String GENRE_IDS = "genre_ids";
        public static final String ID = "id";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String ORIGINAL_LANGUAGE = "original_language";
        public static final String TITLE = "title";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String POPULARITY = "popularity";
        public static final String VOTE_COUNT = "vote_count";
        public static final String VIDEO = "video";
        public static final String VOTE_AVERAGE = "vote_average";
    }


}
