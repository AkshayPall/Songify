package songify.akshaypall.com.songifymusicplayer.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<String> mAlbumImageUrls; // list[0] is large, list[1] is medium, list[2] is small
    private String mDataParsed;

    public Song(long id, String title, String artists, ArrayList<String> albumImageUrls){
        this.mId = id;
        this.mTitle = title;
        this.mArtists = artists;
        this.mAlbumImageUrls = albumImageUrls;
        this.mDataParsed = "";
    }

    private Song(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mArtists = in.readString();
        in.readStringList(mAlbumImageUrls);
        mDataParsed = in.readString();
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
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtists() {
        return mArtists;
    }

    public String getLargeAlbumImagePath() {
        if (mAlbumImageUrls == null || mAlbumImageUrls.size() < 3){
            return "";
        }
        return mAlbumImageUrls.get(0);
    }
    public String getMediumAlbumImagePath() {
        if (mAlbumImageUrls == null || mAlbumImageUrls.size() < 3){
            return "";
        }
        return mAlbumImageUrls.get(1);
    }
    public String getSmallAlbumImagePath() {
        if (mAlbumImageUrls == null || mAlbumImageUrls.size() < 3){
            return "";
        }
        return mAlbumImageUrls.get(2);
    }

    public String getDataParsed(){
        return mDataParsed;
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
        dest.writeStringList(mAlbumImageUrls);
        dest.writeString(mDataParsed);
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.mAlbumImageUrls = imageUrls;
    }

}
