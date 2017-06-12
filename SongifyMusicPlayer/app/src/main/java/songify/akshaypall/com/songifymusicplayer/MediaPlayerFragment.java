package songify.akshaypall.com.songifymusicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import songify.akshaypall.com.songifymusicplayer.Models.Song;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaPlayerFragment.PlayerFragmentListener} interface
 * to handle interaction events.
 */
public class MediaPlayerFragment extends Fragment {

    private static final String SEEK_TAG = "SEEKING_MEDIA_PLAYER";
    private PlayerFragmentListener mListener;

    private TextView mSongTitle;
    private TextView mSongArtist;

    // Needed to hide the seekbar when the app is first created (before any song is selected, to
    // avoid unexpected user behaviour)
    private LinearLayout mSeekBarSet;

    // To update the current position/time and total time of a song every second
    private SeekBar mSeekBar;
    private TextView mTotalSongTime;
    private TextView mCurrSongTime;

    public MediaPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_media_player, container, false);


        // Resize the album image to have a 1:1 ratio
        ImageView albumImageView = (ImageView)v.findViewById(R.id.media_player_album_image);
        albumImageView.setMaxHeight(albumImageView.getWidth());

        // Initialize views for the current song
        mSongTitle = (TextView)v.findViewById(R.id.media_player_song_title);
        mSongArtist = (TextView)v.findViewById(R.id.media_player_song_artist);
        mSeekBarSet = (LinearLayout)v.findViewById(R.id.media_player_seek_set);

        // Initialize the SeekBar and its accompanying textviews
        mSeekBar = (SeekBar)v.findViewById(R.id.media_player_seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Only consider the scrub when it is from the user
                if (fromUser){
                    mListener.updateSeekPos(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mListener.startingSeekPauseSong();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing
            }
        });
        mCurrSongTime = (TextView)v.findViewById(R.id.media_player_current_time);
        mTotalSongTime = (TextView)v.findViewById(R.id.media_player_total_time);

        return v;
    }

    /**
     * UPDATE CURRENT SONG METHODS
     */

    public void updateSongData(Song song){
        if (mSeekBarSet != null){
            mSeekBarSet.setVisibility(View.VISIBLE);
        }
        if (mSongTitle != null && mSongArtist != null){
            mSongTitle.setText(song.getTitle());
            mSongArtist.setText(song.getArtists());
        }
    }

    public void updateTimeStamps (int currSeconds, int totalSeconds){
        // Update the seek bar with the right progress level
        if (mSeekBarSet != null){
            Log.i(SEEK_TAG, "Currently: " + currSeconds + ", Total: " + totalSeconds);
            if(totalSeconds != 0){
                double percent = (double)((100*currSeconds)/totalSeconds);
                Log.i(SEEK_TAG, "Percent for seek bar is " + (int)percent + " " + percent);
                mSeekBar.setProgress((int)percent);
            }

            // Update the textviews for current and end time!
            int curMin = currSeconds/60;
            int totalMin = totalSeconds/60;
            // Using Canadian Locale to control the format of the text in all regions
            // Also Canadian pride :)
            mCurrSongTime.setText(String.format(
                    Locale.CANADA,"%d:%02d", curMin, currSeconds-60*curMin));
            mTotalSongTime.setText(String.format(
                    Locale.CANADA,"%d:%02d", totalMin, totalSeconds-60*totalMin));
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerFragmentListener) {
            mListener = (PlayerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface PlayerFragmentListener {
        void startingSeekPauseSong();
        void updateSeekPos(int percentageOfTotalDuration);
    }
}
