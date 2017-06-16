package songify.akshaypall.com.songifymusicplayer.HttpManagers;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Akshay on 2017-03-09.
 * This class is to organize the web requests, specifically to pull insight on the current song
 * that is playing, which will then be displayed to the user
 */

public class SongDataManager {

    // Constants for querying parameters
    public static final String QUERY_ARTISTS = "artists";
    public static final String QUERY_SONG_TITLE = "song_title";
    public static final String QUERY_ACCESS_TOKEN = "access_token";

    public interface SongDataService{

        @GET("cover_art")
        Call<ArrayList<String>> getCoverArt(
                @Query(QUERY_SONG_TITLE) String title,
                @Query(QUERY_ARTISTS) String artists,
                @Query(QUERY_ACCESS_TOKEN) String accessToken);
    }
}
