package songify.akshaypall.com.songifymusicplayer.HttpManagers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import songify.akshaypall.com.songifymusicplayer.R;

/**
 * Created by Akshay on 2017-03-09.
 * This class is to organize the web requests, specifically to pull insight on the current song
 * that is playing, which will then be displayed to the user
 */

public class SongDataManager {

    // Constants for querying parameters
    private static final String QUERY_ARTISTS = "artists";
    private static final String QUERY_SONG_TITLE = "song_title";
    private static final String QUERY_ACCESS_TOKEN = "access_token";

    public interface SongDataService{

        @GET("cover_art")
        Call<ArrayList<String>> getCoverArt(
                @Query(QUERY_SONG_TITLE) String title,
                @Query(QUERY_ARTISTS) String artists,
                @Query(QUERY_ACCESS_TOKEN) String accessToken);

        @GET("song_parse")
        Call<String> getSongParseData(
                @Query(QUERY_SONG_TITLE) String title,
                @Query(QUERY_ARTISTS) String artists);
    }

    public static SongDataService getService(Context context) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.base_url_web_service))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return adapter.create(SongDataManager.SongDataService.class);
    }
}
