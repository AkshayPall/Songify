package songify.akshaypall.com.songifymusicplayer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import songify.akshaypall.com.songifymusicplayer.Models.Song;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSongListFragmentListener}
 * interface.
 */
public class SongListFragment extends Fragment {

//    private static final String ARG_SONGS = "song_list";
    private static final int REQUEST_STORAGE_PERMISSION = 10;
    ArrayList<Song> mSongs;
    private OnSongListFragmentListener mListener;
    private MySongRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongListFragment() {
    }

    @SuppressWarnings("unused")
    public static SongListFragment newInstance() {
//        SongListFragment fragment = new SongListFragment();
//        Bundle args = new Bundle();
//        args.putParcelableArrayList(ARG_SONGS, songs);
//        fragment.setArguments(args);
        return new SongListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mSongs = getArguments().getParcelableArrayList(ARG_SONGS);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        // Get song data
        mSongs = new ArrayList<>();

        // Set the adapter
        RecyclerView mRecyclerView = (RecyclerView) view;
        mAdapter = new MySongRecyclerViewAdapter(mSongs, mListener, getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        int storagePermission = ContextCompat.checkSelfPermission(
                getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (PackageManager.PERMISSION_GRANTED == storagePermission){
            updateSongData();
        } else {
            //permission wasn't granted, request it
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION
            );
        }

        return view;
    }

    private void updateSongData(){
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );

        if (null != cursor && cursor.moveToFirst()){
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistsColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                mSongs.add(new Song(
                        cursor.getLong(idColumn),
                        cursor.getString(titleColumn),
                        cursor.getString(artistsColumn)
                ));
            } while (cursor.moveToNext());
        } else {
            if (getActivity().getCurrentFocus() != null){
                Snackbar.make(getActivity().getCurrentFocus(), R.string.no_songs_detected_snackbar_message,
                        Snackbar.LENGTH_LONG).show();
            }
        }

        if(null != cursor) {
            cursor.close();
        }

        //Log the song list, for debugging.
        for (Song song : mSongs){
            Log.i(song.getTitle(), song.getArtists()+" "+song.getId());
        }

        mAdapter.notifyDataSetChanged();
        if(mSongs.size() > 0){
            mListener.setupFirstTrack(mSongs.get(0));
        }
        mListener.updateSongList(mSongs);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateSongData();
                    break;
                } else {
                    Log.wtf("STORAGE PERMISSION", "denied");
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSongListFragmentListener) {
            mListener = (OnSongListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSongListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface is implemented by MainActivity to update the current song
     * field in itself and also in the MediaPlayerFragment when a song is pressed.
     */
    interface OnSongListFragmentListener {
        void onPressedSong(Song song);
        void setupFirstTrack(Song song);
        void updateSongList(ArrayList<Song> song);
    }
}
