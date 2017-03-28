package songify.akshaypall.com.songifymusicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import songify.akshaypall.com.songifymusicplayer.Models.Song;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MediaPlayerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TextView mSongTitle;
    private TextView mSongArtist;
    private LinearLayout mSeekBarSet;

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

        return v;
    }

    /** UPDATE CURRENT SONG METHODS **/
    public void updateSongData(Song song){
        if (mSeekBarSet != null){
            mSeekBarSet.setVisibility(View.VISIBLE);
        }
        if (mSongTitle != null && mSongArtist != null){
            mSongTitle.setText(song.getmTitle());
            mSongArtist.setText(song.getmArtists());
            // TODO: update seek bar info as well
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Add seek start, finish commands
    }
}
