package songify.akshaypall.com.songifymusicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import songify.akshaypall.com.songifymusicplayer.Utils.BlurBackground;

public class SongInfoActivity extends Activity {

    private static final String TAG = SongInfoActivity.class.getSimpleName();
    public static final String KEY_INFO_STRING = "KEY_INFO_STRING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);

        Bitmap background = getIntent().getExtras().getParcelable(BlurBackground.KEY_BACKGROUND_BITMAP);
        getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), background));
    }
}