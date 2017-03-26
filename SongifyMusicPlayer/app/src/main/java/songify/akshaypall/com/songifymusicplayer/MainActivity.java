package songify.akshaypall.com.songifymusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.jar.Manifest;

import songify.akshaypall.com.songifymusicplayer.Models.Song;
import songify.akshaypall.com.songifymusicplayer.Services.PlaybackService;
import songify.akshaypall.com.songifymusicplayer.ViewPageTransformers.SlideInTransformer;

public class MainActivity extends AppCompatActivity implements SongListFragment.OnSongListFragmentListener{

    // TAGs for logging
    private static final String SERVICE_TAG = "PLAYBACK SERVICE";

    private static Song mCurrentSong;
    private ImageView mCurrentSongAlbumImage;
    private ArrayList<Song> mInQueueSongs;
    private TextView mCurrentSongTitle;
    private TextView mCurrentSongArtists;
    private FloatingActionButton mMiniPlayerFab;

    // Service and fields necessary for the music player
    private PlaybackService mPlaybackService;
    private Intent mPlayIntent;

    // Setup for the music player
    private ServiceConnection mPlaybackConnection;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInQueueSongs = new ArrayList<>();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up current song views
        mCurrentSongAlbumImage = (ImageView)findViewById(R.id.player_mini_current_song_album);
        mCurrentSongTitle = (TextView) findViewById(R.id.player_mini_current_song_title);
        mCurrentSongArtists = (TextView) findViewById(R.id.player_mini_current_song_artist);
        mMiniPlayerFab = (FloatingActionButton)findViewById(R.id.player_mini_state_fab);
        final LinearLayout playerStatePackage = (LinearLayout)findViewById(R.id.player_mini_package);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        final HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the adapter.
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mHomePagerAdapter);
        mViewPager.setPageTransformer(false, new SlideInTransformer());
        final Context context = this;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(mHomePagerAdapter.getPageTitle(position));
                float displayHeight = context.getResources().getDisplayMetrics().heightPixels;
                float yDelta = displayHeight - playerStatePackage.getHeight();
                if (position == 1){
                    playerStatePackage.animate().y(displayHeight);
                } else {
                    playerStatePackage.animate().y(yDelta);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // TODO: remove later, this is for test only
            stopService(mPlayIntent);
            mPlaybackService = null;
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
        updateCurrentSong(song);
        // update playback service
        if (mPlaybackService != null){
            Log.wtf(SERVICE_TAG, "pressed song, playing now");
            mPlaybackService.setSong(song);
            mPlaybackService.playSong();
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
            mPlaybackConnection = new ServiceConnection() {
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
                bindService(mPlayIntent, mPlaybackConnection, Context.BIND_AUTO_CREATE);
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

    private void updateCurrentSong(Song song) {
        mCurrentSong = song;
        //TODO: update album image
        mCurrentSongTitle.setText(song.getTitle());
        mCurrentSongArtists.setText(song.getArtists());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return SongListFragment.newInstance();
            //TODO: return the media player fragment if on page 2
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
                    return "Your Tracks";
                case 1:
                    return "What's Playing";
            }
            return null;
        }
    }
}
