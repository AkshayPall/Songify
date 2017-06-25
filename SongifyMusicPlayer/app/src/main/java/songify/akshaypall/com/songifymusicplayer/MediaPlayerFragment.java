package songify.akshaypall.com.songifymusicplayer;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import songify.akshaypall.com.songifymusicplayer.Models.Song;
import songify.akshaypall.com.songifymusicplayer.Utils.BlurBackground;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaPlayerFragment.PlayerFragmentListener} interface
 * to handle interaction events.
 */
public class MediaPlayerFragment extends Fragment implements View.OnClickListener {

    private static final String SEEK_TAG = "SEEKING_MEDIA_PLAYER";
    private static final String TAG = MediaPlayerFragment.class.getSimpleName();
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

    // To show the user the data parse of the current song playing
    private Button mSongParseInfoButton;
    private String mSongParseData;

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

        mSongParseInfoButton = (Button)v.findViewById(R.id.media_player_song_parse_button);
        mSongParseInfoButton.setOnClickListener(this);

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

    public void setParseDataButtonGone(){
        mSongParseInfoButton.setVisibility(View.GONE);
    }

    public void retrievedSongParseData (String msg) {
        Log.wtf("YOOO", msg);
        mSongParseData = msg;
        mSongParseInfoButton.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.media_player_song_parse_button:
                Bitmap blurBackground = BlurBackground.blur(((MainActivity)getActivity()).getRootView());
                Intent i =  new Intent(getActivity(), SongInfoActivity.class);
                i.putExtra(BlurBackground.KEY_BACKGROUND_BITMAP, blurBackground);
                i.putExtra(SongInfoActivity.KEY_INFO_STRING, mSongParseData);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            default:
                break;
        }
    }

    interface PlayerFragmentListener {
        void startingSeekPauseSong();
        void updateSeekPos(int percentageOfTotalDuration);
    }
}
