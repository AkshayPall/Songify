package songify.akshaypall.com.songifymusicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.TextView;

import songify.akshaypall.com.songifymusicplayer.Utils.BlurBackground;

public class SongInfoActivity extends Activity {

    // Tags and Bundle Keys
    private static final String TAG = SongInfoActivity.class.getSimpleName();
    public static final String KEY_INFO_STRING = "KEY_INFO_STRING";

    // Fields to display data in buncle
    private String mData;
    private TextView mSongDataTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);

        Bitmap background = getIntent().getExtras().getParcelable(BlurBackground.KEY_BACKGROUND_BITMAP);
        getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), background));

        mSongDataTv = (TextView) findViewById(R.id.song_info_tv);
        mData = getIntent().getExtras().getString(KEY_INFO_STRING, "");
        mSongDataTv.setText(mData);
    }
}