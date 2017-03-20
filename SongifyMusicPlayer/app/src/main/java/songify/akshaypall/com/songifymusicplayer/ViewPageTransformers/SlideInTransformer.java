package songify.akshaypall.com.songifymusicplayer.ViewPageTransformers;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Akshay on 2017-03-20.
 */

public class SlideInTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.85f;

    @Override
    public void transformPage(View page, float position) {
        // When page is still in view
        if (position <= 1) {
            float scaleValue = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float verticalSpace = page.getHeight() * (1 - scaleValue) / 2;
            float horizontalSpace = page.getWidth() * (1 - scaleValue) / 2;
            if (position < 0) {
                // Going to left
                page.setTranslationX(horizontalSpace - verticalSpace / 2);
            } else {
                // Going to right
                page.setTranslationX(-horizontalSpace + verticalSpace / 2);
            }

            page.setScaleX(scaleValue);
            page.setScaleY(scaleValue);

        }
    }
}
