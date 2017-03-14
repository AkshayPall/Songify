package songify.akshaypall.com.songifymusicplayer.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akshay on 2017-03-09.
 * This class is for the audio files to be read on the device for playback.
 * The constructor should only be called when the audio files are searched for, which should be
 * when the main activity is created and occasionally when it is resumed (to check if any new files
 * were added or older ones were deleted)
 */

public class Song implements Parcelable {
    private long id;
    private String title;
    private String artists;

    public Song(long id, String title, String artists){
        this.id = id;
        this.title = title;
        this.artists = artists;
    }

    protected Song(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artists = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public long getId() {
        return id;
    }

    //TODO: Make get title and get artists shorten the return values if it is too long

    public String getTitle() {
        return title;
    }

    public String getArtists() {
        return artists;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artists);
    }
}
