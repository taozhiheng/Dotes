package util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by taozhiheng on 15-4-15.
 *
 */
public class AnimatorCreator {

    public static AnimatorSet getTranslateWithScaleAnimator(View view, float fromX, float fromY, float x, float y, float scale)
    {
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "x", fromX, x);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "y", fromY, y);
//        ObjectAnimator objectAnimatorSX = ObjectAnimator.ofFloat(view, "scaleX", 1, scale);
//        ObjectAnimator objectAnimatorSY = ObjectAnimator.ofFloat(view, "scaleY", 1, scale);
        ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1);
        objectAnimatorX.setDuration(1000);
        objectAnimatorY.setDuration(1000);
//        objectAnimatorSX.setDuration(1000);
//        objectAnimatorSY.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimatorX).with(objectAnimatorY).with(objectAnimatorA);
//        animatorSet.play(objectAnimatorSX).with(objectAnimatorSY).after(objectAnimatorY);
        return animatorSet;
    }

    public static Animator getAlphaAnimator(View view, float alpha)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, alpha);
        objectAnimator.setDuration(1000);
        return objectAnimator;
    }
}
