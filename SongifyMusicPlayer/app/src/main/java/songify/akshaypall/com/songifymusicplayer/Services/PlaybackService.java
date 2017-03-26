package songify.akshaypall.com.songifymusicplayer.Services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;

import songify.akshaypall.com.songifymusicplayer.Models.Song;

/**
 * Created by Akshay on 2017-03-25.
 */

public class PlaybackService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener{


    private static final String ERROR_TAG = "PlaybackService_Error";
    private ArrayList<Song> mSongs;
    private int mSongPos;
    private MediaPlayer mPlayer;
    private final PlaybackBinder mBinder = new PlaybackBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mSongPos = 0;
        mPlayer = new MediaPlayer();
        mSongs = new ArrayList<>();

        // initialize media player
        setupPlayer();
    }

    private void setupPlayer() {
        // to allow for the player to keep playing even when the device is put to sleep
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // set the interfaces
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        mPlayer.release();
        return false;
    }


    /** Methods for music playback and state control **/
    public void playSong(){
        mPlayer.reset();
        Song toPlay = mSongs.get(mSongPos);
        Uri songUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, toPlay.getId());
        try {
            mPlayer.setDataSource(getApplicationContext(), songUri);
            mPlayer.prepareAsync();
            // now after the player is asynchronously readied, the onPrepared method is invoked
        } catch (Exception e){
            //TODO: add interface to make a snackbar pop up when playback error occurs
            Log.wtf(ERROR_TAG, "Could not play song. ");
        }
    }

    public void updateSongList(ArrayList<Song> songs){
        mSongs.clear();
        mSongs.addAll(songs);
    }

    public void setSong(Song song){
        int index = mSongs.indexOf(song);
        if (index != -1){
            mSongPos = index;
        }
    }

    /** Binder for this Service **/
    public class PlaybackBinder extends Binder{
        public PlaybackService getService(){
            return PlaybackService.this;
        }
    }
}
