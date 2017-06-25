package songify.akshaypall.com.songifymusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import songify.akshaypall.com.songifymusicplayer.HttpManagers.SongDataManager;
import songify.akshaypall.com.songifymusicplayer.HttpManagers.SpotifyManager;
import songify.akshaypall.com.songifymusicplayer.Models.Song;
import songify.akshaypall.com.songifymusicplayer.Services.PlaybackService;
import songify.akshaypall.com.songifymusicplayer.ViewPageTransformers.SlideInTransformer;

import static songify.akshaypall.com.songifymusicplayer.HttpManagers.SpotifyManager.LOGIN_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements
        SongListFragment.OnSongListFragmentListener, MediaPlayerFragment.PlayerFragmentListener{

    // TAGs for logging
    private static final String SERVICE_TAG = "PLAYBACK SERVICE";
    private static final String TAG = MainActivity.class.getSimpleName();

    private MediaPlayerFragment mMediaPlayer;
    private SongListFragment mSongDataFragment;
    private ImageView mCurrentSongAlbumImage;
    private ArrayList<Song> mInQueueSongs;
    private TextView mCurrentSongTitle;
    private TextView mCurrentSongArtists;
    private FloatingActionButton mMiniPlayerFab;

    // Service and fields necessary for the music player
    private PlaybackService mPlaybackService;
    private Intent mPlayIntent;

    // Data needed to move the mini player and play/pause fab into position once a song is selected
    private LinearLayout mPlayerStatePackage;
    private ViewPager.OnPageChangeListener mViewPageChanger;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log into and Authenticate Spotify if necessary
        SpotifyManager.authenticateSpotify(this);

        mInQueueSongs = new ArrayList<>();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.actionbar_list_title));
        setSupportActionBar(toolbar);

        // Set up current song views
        mCurrentSongAlbumImage = (ImageView)findViewById(R.id.player_mini_current_song_album);
        mCurrentSongTitle = (TextView) findViewById(R.id.player_mini_current_song_title);
        mCurrentSongArtists = (TextView) findViewById(R.id.player_mini_current_song_artist);
        mMiniPlayerFab = (FloatingActionButton)findViewById(R.id.player_mini_state_fab);
        mPlayerStatePackage = (LinearLayout)findViewById(R.id.player_mini_package);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        final HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the adapter.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(homePagerAdapter);
        viewPager.setPageTransformer(false, new SlideInTransformer());
        final Context context = this;
        // Update the toolbar with the appropriate title, based on which
        // fragment is in focus
        // Animate the mini player bar at the bottom of the activity. This means it only
        // is visible when looking at the song list fragment, as it is redundant to have it
        // when viewing the MediaPlayerFragment.
        // Only do this once a song has been selected (if view is visible)
        mViewPageChanger = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Update the toolbar with the appropriate title, based on which
                // fragment is in focus
                toolbar.setTitle(homePagerAdapter.getPageTitle(position));

                // Animate the mini player bar at the bottom of the activity. This means it only
                // is visible when looking at the song list fragment, as it is redundant to have it
                // when viewing the MediaPlayerFragment.
                // Only do this once a song has been selected (if view is visible)
                float displayHeight = context.getResources().getDisplayMetrics().heightPixels;
                if(mPlayerStatePackage.getVisibility() == View.VISIBLE){
                    float yDelta = displayHeight - mPlayerStatePackage.getHeight();
                    if (position == 1){
                        mPlayerStatePackage.animate().y(displayHeight);
                    } else {
                        mPlayerStatePackage.animate().y(yDelta);
                    }
                } if (mMiniPlayerFab.getVisibility() == View.INVISIBLE){
                    mMiniPlayerFab.setVisibility(View.VISIBLE);
                    float marginBot = getResources().
                            getDimensionPixelSize(R.dimen.player_mini_state_fab_margin);
                    float yDelta = displayHeight - mMiniPlayerFab.getHeight() - marginBot;
                    mMiniPlayerFab.animate().y(yDelta);
                }

                // Show the toolbar always if in the media player view
                if (position == 1){
                    toolbar.findFocus();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };

        viewPager.addOnPageChangeListener(mViewPageChanger);

        // Change state (Play/Pause) of current song in the PlaybackService
        mMiniPlayerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaybackService.isPlaying()){
                    mMiniPlayerFab.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                } else {
                    mMiniPlayerFab.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                }
                mPlaybackService.changeStateSong();
            }
        });

        // Hide mini player and play fab until a song is selected
        float height = context.getResources().getDisplayMetrics().heightPixels;
        mPlayerStatePackage.setY(height);
        mMiniPlayerFab.setY(height);
        mPlayerStatePackage.setVisibility(View.INVISIBLE);
        mMiniPlayerFab.setVisibility(View.INVISIBLE);

        // Runnable to constantly update the seekbar current song time TextView
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mPlaybackService != null && mPlaybackService.isPlaying()){
                    mMediaPlayer.updateTimeStamps(
                            mPlaybackService.getCurrentSongTimestamp(),
                            mPlaybackService.getSongDuration());
                }
                handler.postDelayed(this, 500);
            }
        };

        handler.post(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(mPlayIntent);
        mPlaybackService = null;
        super.onDestroy();
    }

    @Override
    public void onPressedSong(Song song) {
        // must be in song list frag so set position of player package to default
        if (mPlayerStatePackage.getVisibility() == View.INVISIBLE){
            mPlayerStatePackage.setVisibility(View.VISIBLE);
            mViewPageChanger.onPageSelected(0);
        }

        // update playback service
        if (mPlaybackService != null){
            Log.wtf(SERVICE_TAG, "pressed song, playing now");
            mMiniPlayerFab.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
            mPlaybackService.setSong(song);
            mPlaybackService.playSong();
            mMediaPlayer.updateSongData(song);
            mMediaPlayer.setParseDataButtonGone();
            SongDataManager.SongDataService service = SongDataManager.getService(this);
            Call<String> call = service.getSongParseData(song.getTitle(), song.getArtists());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.body() != null){
                        Log.d(SERVICE_TAG, "song parse data call success");
                        mMediaPlayer.retrievedSongParseData(response.body());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d(SERVICE_TAG, "song parse data call failed");
                    t.printStackTrace();
                }
            });
        }
        updateCurrentSong(song);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If logged into Spotify, retrieve an access code to make requests
        if (requestCode == LOGIN_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient
                    .getResponse(LOGIN_REQUEST_CODE, data);
            switch (response.getType()){
                // If received token
                case TOKEN:
                    SpotifyManager.ACCESS_TOKEN = response.getAccessToken();
                    Log.wtf(TAG, "Got access token");
                    mSongDataFragment.notifySongDataAdapter();
                    break;
                case ERROR:
                    SpotifyManager.ACCESS_TOKEN = SpotifyManager.NULL_ACCESS_TOKEN;
                    Log.wtf(TAG, "ERROR retrieving access token");
                    break;
                case EMPTY:
                    SpotifyManager.ACCESS_TOKEN = SpotifyManager.NULL_ACCESS_TOKEN;
                    Log.wtf(TAG, "EMPTY access token");
                    break;
                default:
                    SpotifyManager.ACCESS_TOKEN = SpotifyManager.NULL_ACCESS_TOKEN;
                    Log.wtf(TAG, "Could not retrieve access token");
                    break;
            }
        }
    }

    @Override
    public void setupFirstTrack(Song song) {
        updateCurrentSong(song);
    }

    @Override
    public void updateSongList(ArrayList<Song> song) {
        boolean firstLoad = mInQueueSongs.size() == 0;
        mInQueueSongs.clear();
        mInQueueSongs.addAll(song);
        // If it is the first time the song list was updated
        if (firstLoad){
            ServiceConnection playbackConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    PlaybackService.PlaybackBinder binder = (PlaybackService.PlaybackBinder) service;
                    mPlaybackService = binder.getService();
                    mPlaybackService.updateSongList(mInQueueSongs);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };

            // Bind the service to an intent and then start the service
            if (mPlayIntent == null){
                mPlayIntent = new Intent(this, PlaybackService.class);
                bindService(mPlayIntent, playbackConnection, Context.BIND_AUTO_CREATE);
                startService(mPlayIntent);
                Log.wtf(SERVICE_TAG, "Started playback service!");
            }
        }
        else if (mPlaybackService != null){
            Log.wtf(SERVICE_TAG, "Update song list!");
            mPlaybackService.updateSongList(song);
        } else {
            Log.wtf(SERVICE_TAG, "playback service is null, could not pass list");
        }
    }

    public View getRootView() {
        return findViewById(R.id.main_content).getRootView();
    }

    private void updateCurrentSong(Song song) {
        // Update the mini player views
        //TODO: update album image
        mCurrentSongTitle.setText(song.getTitle());
        mCurrentSongArtists.setText(song.getArtists());
    }

    /**
     * The following two methods update the seekbar in the MediaPlayerFragment with the
     * correct information based on the current song
     */

    @Override
    public void startingSeekPauseSong() {
        if(mPlaybackService.isPlaying()){
            mPlaybackService.changeStateSong();
        }
    }

    @Override
    public void updateSeekPos(int percentageOfTotalDuration) {
        mPlaybackService.seekTo(percentageOfTotalDuration);
        mMiniPlayerFab.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class HomePagerAdapter extends FragmentPagerAdapter {

        private HomePagerAdapter(FragmentManager fm) {
            super(fm);
            mMediaPlayer = new MediaPlayerFragment();
            mSongDataFragment = new SongListFragment();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0){
                return mSongDataFragment;
            } else {
                return mMediaPlayer;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.actionbar_list_title);
                case 1:
                    return getString(R.string.actionbar_player_title);
            }
            return null;
        }
    }
}
