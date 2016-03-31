package com.example.taozhiheng.slidingmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by taozhiheng on 15-2-10.
 * copy from others
 */
public class SlidingMenu extends HorizontalScrollView
{
    private final static String TAG = "touch";
    //屏幕宽度
    private int mScreenWidth;
    private int mMenuRightPadding;
    //菜单的宽度
    private int mMenuWidth;
    private int mHalfMenuWidth;
    private boolean isOpen;
    private boolean once;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    public SlidingMenu(Context context)
    {
        this(context, null, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    // 获得屏幕高度
    private int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mScreenWidth = this.getScreenWidth(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SlidingMenu, defStyle, 0);
//        mMenuRightPadding = a.getDimensionPixelSize(R.styleable.SlidingMenu_rightPadding,
//                (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, 50f,
//                        getResources().getDisplayMetrics()));
        mMenuRightPadding = mScreenWidth/3;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!once)
        {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);

            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mHalfMenuWidth = mMenuWidth / 2;
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;
            //自己添加的
//            mContent.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(isOpen)
//                        closeMenu();
//                }
//            });
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed)
        {
            // 将菜单隐藏
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onTouchEvent:move");
                if(getScrollX() < 0 || getScrollX() > mMenuWidth) {
                    Log.e(TAG, "scrollX:"+getScrollX());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent:up");
                int scrollX = getScrollX();
                if (scrollX > mHalfMenuWidth)
                {
//                    this.smoothScrollTo(mMenuWidth, 0);
//                    isOpen = false;
                    closeMenu();
                } else
                {
//                    this.smoothScrollTo(0, 0);
//                    isOpen = true;
                    openMenu();
                }
                return true;

        }
        Log.e("touch", "super:"+super.onTouchEvent(ev));
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if(scrollX<0||scrollX>mMenuWidth)
            return;
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    /**
     * 关键部分
     * */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if(l<0||l>mMenuWidth)
            return;
        float scale = l * 1.0f / mMenuWidth;
        float leftScale = 1 - 0.3f * scale;
        float rightScale = 0.8f + scale * 0.2f;

        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);

        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);

    }

    //打开菜单
    private void openMenu()
    {
            this.smoothScrollTo(0, 0);
            isOpen = true;
    }

    //关闭菜单
    private void closeMenu()
    {
            this.smoothScrollTo(mMenuWidth, 0);
            isOpen = false;
    }

    //切换菜单状态
    public void toggle()
    {
        if (isOpen)
        {
            closeMenu();
        } else
        {
            openMenu();
        }
    }
}
