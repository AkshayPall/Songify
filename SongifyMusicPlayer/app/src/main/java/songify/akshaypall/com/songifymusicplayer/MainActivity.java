package songify.akshaypall.com.songifymusicplayer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import songify.akshaypall.com.songifymusicplayer.ViewPageTransformers.SlideInTransformer;

public class MainActivity extends AppCompatActivity implements SongListFragment.OnSongListFragmentListener{

    private static Song mCurrentSong;
    private ImageView mCurrentSongAlbumImage;
    private TextView mCurrentSongTitle;
    private TextView mCurrentSongArtists;
    private FloatingActionButton mMiniPlayerFab;

    /**
     * This is a static list of all the songs (as {@link Song} objects) on the device. It is to be
     * filled with data when MainActivity is instantiated and occasionally refreshed (in case new
     * songs are added and old songs are deleted)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                float yDelta = context.getResources().getDisplayMetrics().heightPixels
                        - playerStatePackage.getY();
                if (position == 1){
                    playerStatePackage.animate().translationY(yDelta);
                } else {
                    playerStatePackage.animate().translationY(-yDelta);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPressedSong(Song song) {
        updateCurrentSong(song);
    }

    @Override
    public void setupFirstTrack(Song song) {
        updateCurrentSong(song);
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
