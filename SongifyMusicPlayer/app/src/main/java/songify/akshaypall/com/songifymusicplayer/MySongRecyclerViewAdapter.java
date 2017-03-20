package songify.akshaypall.com.songifymusicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import songify.akshaypall.com.songifymusicplayer.Models.Song;
import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a
 * {@link songify.akshaypall.com.songifymusicplayer.Models.Song} and makes a call to the specified
 * {@link songify.akshaypall.com.songifymusicplayer.SongListFragment.OnSongListFragmentListener}.
 */
class MySongRecyclerViewAdapter extends
        RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Song> mSongs;
    private final SongListFragment.OnSongListFragmentListener mListener;
    private Context mContext;

    MySongRecyclerViewAdapter(ArrayList<Song> songs,
                                     SongListFragment.OnSongListFragmentListener listener,
                                     Context context) {
        mSongs = songs;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Song song = mSongs.get(position);
        Log.i("SONG", song.getTitle()+" "+song.getArtists());
        holder.mTitle.setText(song.getTitle());
        holder.mArtists.setText(song.getArtists());
        //TODO: set album image

        Log.wtf("Current Song", "--View-- "+song.getTitle()+holder.mTitle.getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onPressedSong(song);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("SONG SIZE", ""+mSongs.size());
        return mSongs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAlbumImage;
        private final TextView mArtists;
        private final TextView mTitle;

        private ViewHolder(View view) {
            super(view);
            mAlbumImage = (ImageView) view.findViewById(R.id.song_album_image);
            mArtists = (TextView) view.findViewById(R.id.song_artists);
            mTitle = (TextView) view.findViewById(R.id.song_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
