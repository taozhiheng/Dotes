package util;

import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by taozhiheng on 15-4-16.
 */
public class AnimationCreator {

    public static Animation getTranslateWithScaleAnimation(float x, float y, float scale)
    {
        Log.d("drag", "x:"+x+" y:"+y+" scale:"+scale);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, x, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, scale, 1, scale, Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5F);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);

        translateAnimation.setDuration(1500);
        scaleAnimation.setDuration(1500);
        alphaAnimation.setDuration(1500);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);
        //animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        return animationSet;
    }

}
