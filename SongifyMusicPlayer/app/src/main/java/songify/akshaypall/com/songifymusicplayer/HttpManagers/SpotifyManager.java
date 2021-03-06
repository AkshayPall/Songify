package songify.akshaypall.com.songifymusicplayer.HttpManagers;

import android.app.Activity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import songify.akshaypall.com.songifymusicplayer.R;

/**
 * Created by Akshay on 2017-06-08.
 */

public class SpotifyManager {

    public static final String NULL_ACCESS_TOKEN = "";

    public static int LOGIN_REQUEST_CODE = 1;
    public static String ACCESS_TOKEN = NULL_ACCESS_TOKEN;

    /**
     * Used to get access token to make any calls to the Spotify Api
     * @param callingActivity
     */
    public static void authenticateSpotify (Activity callingActivity){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                callingActivity.getResources().getString(R.string.spotify_client_id),
                AuthenticationResponse.Type.TOKEN,
                callingActivity.getResources().getString(R.string.spotify_redirect_uri)
        );
//        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(
                callingActivity,
                LOGIN_REQUEST_CODE,
                request
        );
    }
}
