package ui;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.taozhiheng.dotes.R;

/**
 * Created by taozhiheng on 15-4-10.
 *
 */
public class AnimationCreator {

    public static Animation createRecordAnimation(Context context, int x, int y,
                                                  float scale, int pivotX, int pivotY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, x, 0, y);
        translateAnimation.setDuration(250);
        translateAnimation.setInterpolator(context, R.anim.item_interpolator);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, scale, 1, scale, pivotX, pivotY);
        scaleAnimation.setDuration(250);
        scaleAnimation.setInterpolator(context, R.anim.item_interpolator);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);
        return animationSet;
    }
}
