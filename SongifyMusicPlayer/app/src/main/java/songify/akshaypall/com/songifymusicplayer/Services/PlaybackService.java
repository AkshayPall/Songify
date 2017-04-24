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
import android.util.Log;

import java.util.ArrayList;

import songify.akshaypall.com.songifymusicplayer.Models.Song;

/**
 * Created by Akshay on 2017-03-25.
 */

public class PlaybackService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    // Tags for Logging
    private static final String ERROR_TAG = "PlaybackService_Error";
    private static final String UPDATE_TAG = "PlayBackService_Update";

    // Fields to store across lifecycle of service
    private ArrayList<Song> mSongs;
    private int mSongPos;
    private MediaPlayer mPlayer;
    private final PlaybackBinder mBinder = new PlaybackBinder();
    private boolean isPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize fields
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

        // loop by default
        mPlayer.setLooping(true);

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
        // repeat! Loop!
        //TODO: only repeat if looping is on!
        Log.i(UPDATE_TAG, "Completed playback");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(true);
        isPlaying = true;
        mp.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Stop operation and unbind player from service
        mPlayer.stop();
        mPlayer.release();
        isPlaying = false;
        return false;
    }


    /**
     * Methods for music playback and state control
     */

    public void playSong(){
        // Reset the player in case it was already playing a song or paused in another song
        mPlayer.reset();

        // Check to prevent the case of the playSong method being invoked too early in the lifecycle
        // of the app, where the SongListFragment hasn't passed the list of songs to the service yet
        if (mSongPos < mSongs.size()){
            Song toPlay = mSongs.get(mSongPos);
            Uri songUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, toPlay.getmId());
            try {
                mPlayer.setDataSource(getApplicationContext(), songUri);
                mPlayer.prepareAsync();
                // now after the player is asynchronously readied, the onPrepared method is invoked
            } catch (Exception e){
                //TODO: add interface to make a snackbar pop up when playback error occurs
                Log.wtf(ERROR_TAG, "Could not play song. ");
            }
        } else {
            Log.wtf(ERROR_TAG, "index out of bounds for song in song ArrayList. Possible that " +
                                "song list is empty");
        }
    }

    public void changeStateSong(){
        // Play/Pause a song, this is called in place of playSong when the current/already selected
        // song is selected for playback
        if(mPlayer.isPlaying()){
            isPlaying = false;
            mPlayer.pause();
        } else {
            isPlaying = true;
            mPlayer.start();
        }
    }

    public void seekTo(int percentageOfTotalDuration){
        isPlaying = true;
        double pos = ((double)percentageOfTotalDuration)/100 * ((double)mPlayer.getDuration());
        Log.i(UPDATE_TAG, "Seeked to "+pos);
        mPlayer.seekTo(((int) pos));
        mPlayer.start();
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    // Take in an ArrayList of Songs to update the service with playback. This comes from the
    // MainActivity, which gets it from the SongListFragment once it has finished retrieving tracks
    // using the Content resolver
    public void updateSongList(ArrayList<Song> songs){
        Log.wtf(UPDATE_TAG, "updated songs list");
        // do not do mSongs = songs as that would maintain a reference to it, which may be unsafe
        // in case the fragment/activity from which is came from has restarted
        mSongs.clear();
        mSongs.addAll(songs);
    }

    public void setSong(Song song){
        int index = mSongs.indexOf(song);
        // Prevents the case of a song being selected that is not in the mSongs ArrayList (which may
        // occur if the updateSongList method is not invoked before setSong.
        if (index == -1){
            Log.wtf(ERROR_TAG, "song not in index!");
        } else {
            mSongPos = index;
        }
    }

    public int getSongDuration(){
        return mPlayer.getDuration()/1000;
    }

    public int getCurrentSongTimestamp(){
        return mPlayer.getCurrentPosition()/1000;
    }

    /** Binder for this Service **/
    public class PlaybackBinder extends Binder{
        public PlaybackService getService(){
            return PlaybackService.this;
        }
    }
}
