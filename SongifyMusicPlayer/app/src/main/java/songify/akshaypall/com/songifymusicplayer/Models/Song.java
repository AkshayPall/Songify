package songify.akshaypall.com.songifymusicplayer.Models;

/**
 * Created by Akshay on 2017-03-09.
 * This class is for the audio files to be read on the device for playback.
 * The constructor should only be called when the audio files are searched for, which should be
 * when the main activity is created and occasionally when it is resumed (to check if any new files
 * were added or older ones were deleted)
 */

public class Song {
    private long id;
    private String title;
    private String artists;

    public Song(long id, String title, String artists){
        this.id = id;
        this.title = title;
        this.artists = artists;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtists() {
        return artists;
    }
}
