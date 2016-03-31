package ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.taozhiheng.dotes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taozhiheng on 15-4-6.
 *
 */
public class LockView extends View {

    private Paint mPaint;
    private Path mPath;
    private Spot[] mSpots;
    private List<Spot> mSpotList;

    public LockView(Context context)
    {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(context.getResources().getColor(R.color.SpringGreen));
        mPath = new Path();
        mSpotList = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int left = getLeft();
        int width = getWidth();
        int top = getTop();
        int height = getHeight();
        int i = 0;
        int j = 0;
        for(; i < 3; i++)
            for(j=0 ; j < 3; j++)
                mSpots[i*3+j] = new Spot(left+width*(2*j+1)/6, top+height*(2*i+1)/10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)getX();
        int y = (int)getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                validRect(x, y);
                if(mSpots.length>0)
                    mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void validRect(int x, int y)
    {
        for (Spot spot : mSpots)
        {
            if(spot.includePoint(x, y)) {
                mSpotList.add(spot);
                break;
            }
        }
    }
}
