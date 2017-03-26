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
    private long mId;
    private String mTitle;
    private String mArtists;
    private String mAlbumImagePath;

    public Song(long id, String title, String artists, String albumImagePath){
        this.mId = id;
        this.mTitle = title;
        this.mArtists = artists;
        this.mAlbumImagePath = albumImagePath;
    }

    protected Song(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mArtists = in.readString();
        mAlbumImagePath = in.readString();
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

    public long getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmArtists() {
        return mArtists;
    }

    public String getAlbumImagePath() {
        return mAlbumImagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mArtists);
        dest.writeString(mAlbumImagePath);
    }
}
