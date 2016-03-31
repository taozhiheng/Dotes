package drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.taozhiheng.dotes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taozhiheng on 15-3-21.
 *
 */
public class DragView extends View {

    public final static int DEFAULT_LINE_COLOR = Color.GREEN;
    public final static int DEFAULT_SPECIAL_SHAPE_COLOR = Color.RED;
    public final static int DEFAULT_SHAPE_COLOR = Color.BLUE;
    public final static int DEFAULT_TEXT_COLOR = Color.WHITE;

    private final static int LIMIT_PADDING = 2 * Shape.DEFAULT_RADIUS;
    private final static long ON_CLICK_TIME = 150;
    private final static long ON_LONG_CLICK_TIME = 500;

    public enum DragState
    {
        BEGIN, DOING, DONE
    }

    //线条颜色，图形颜色，文字颜色，图形大小
    private int lineColor = DEFAULT_LINE_COLOR;
    private int specialShapeColor = DEFAULT_SPECIAL_SHAPE_COLOR;
    private int shapeColor = DEFAULT_SHAPE_COLOR;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int shapeRadius = Shape.DEFAULT_RADIUS;

    //第一个为终点，第二个为起点，之后为中间点
    private List<Shape> mShapeList;

    //起点，终点锁定状态
    private boolean mStartShapeFixed;
    private boolean mStopShapeFixed;
    //完成状态
    private boolean mFinish;

    //线条路径，线条画笔，图形画笔，文字画笔
    private Path mLinePath;
    private Paint mLinePaint;
    private Paint mShapePaint;
    private Paint mTextPaint;

    //绘图的有效范围，动态移动
    private float mLeftLimit;
    private float mTopLimit;
    private float mRightLimit;
    private float mBottomLimit;

    //屏幕的有效范围
    private int mLeftBorder;
    private int mTopBorder;
    private int mRightBorder;
    private int mBottomBorder;

    //触摸位置坐标
    private float mLastX;
    private float mLastY;

    //拖动状态
    private DragState mDragState;
    //当前操作图形，图形索引
    private Shape mTargetShape;
    private int mTargetIndex;

    //手指按下时间
    private long mDownTime;

    //手势动作
    public final static int NONE = 0;
    public final static int DRAG = 1;     //拖动中
    public final static int ZOOM = 2;     //缩放中
//    public final static int BIGGER = 3;   //放大ing
//    public final static int SMALLER = 4;  //缩小ing

    private int mMode;       //当前的手势动作

    private Matrix mMatrix = new Matrix();

    private Animation mAnimation;
    //两点触摸的前后间距
    private float mBeforeDistance;
    private float mAfterDistance;
    //当前显示大小
    float mScale = 1;

    //点击，长按监听
    private OnShapeClickListener mOnShapeClickListener;
    private OnShapeLongClickListener mOnShapeLongClickListener;
    //文字可见性
    private boolean mTextVisible;

    public DragView(Context context)
    {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lineColor = context.getResources().getColor(R.color.SpringGreen);
        shapeColor = context.getResources().getColor(R.color.NavajoWhite);
        specialShapeColor = context.getResources().getColor(R.color.LightPink);
        init();
    }

    //初始化各种参数
    private void init()
    {
        mMode = NONE;
        mBeforeDistance = 0;
        mAfterDistance = 0;
        mFinish = false;
        mStartShapeFixed = false;
        mStopShapeFixed = false;
        mTextVisible = false;
        mDragState = DragState.DONE;
        mTargetIndex = -1;
        mLastX = -1;
        mLastY = -1;
        mDownTime = -1;
        mShapeList = new ArrayList<>();
        mLinePath = new Path();
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(10);
        mShapePaint = new Paint();
        mShapePaint.setAntiAlias(true);
        mShapePaint.setDither(true);
        mShapePaint.setColor(shapeColor);
        mShapePaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(26);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    //设定屏幕范围，绘图范围
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLeftBorder = shapeRadius;
        mTopBorder = shapeRadius;
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        mRightBorder = point.x - shapeRadius;
        mBottomBorder = point.y - 4 * shapeRadius;
        //add padding
        mLeftLimit = mLeftBorder - LIMIT_PADDING;
        mTopLimit = mTopBorder - LIMIT_PADDING;
        mRightLimit = mRightBorder + LIMIT_PADDING;
        mBottomLimit = mBottomBorder + LIMIT_PADDING;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(mMatrix);
        drawLines(canvas);
        drawShapes(canvas);
    }

    //绘线条
    private void drawLines(Canvas canvas)
    {
        mLinePath.reset();
        Shape shape;
        for(int i = 0; i < mShapeList.size(); i++) {
            shape = mShapeList.get(i);
            if(i == 1)
                mLinePath.moveTo(shape.getCenterX(), shape.getCenterY());
            else if(i > 1)
                mLinePath.lineTo(shape.getCenterX(), shape.getCenterY());
        }
        if(mFinish)
            mLinePath.lineTo(mShapeList.get(0).getCenterX(), mShapeList.get(0).getCenterY());
        canvas.drawPath(mLinePath, mLinePaint);
    }

    //绘图形（及文字）
    private void drawShapes(Canvas canvas)
    {
        mShapePaint.setColor(specialShapeColor);
        for(Shape shape : mShapeList) {
            if(shape.getIndex()>1)
                mShapePaint.setColor(shapeColor);
//            canvas.drawCircle(shape.getCenterX(), shape.getCenterY(), shape.getRadius(), mShapePaint);
            shape.drawShape(canvas, mShapePaint);
            if(mTextVisible && shape.getTitle()!= null) {
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float baseline = shape.getCenterY() - (fontMetrics.top + fontMetrics.bottom)/2;
                if(mTextPaint.measureText(shape.getTitle())<= shape.getWritableLength())
                    canvas.drawText(shape.getTitle(), shape.getCenterX(), baseline, mTextPaint);
                else
                    canvas.drawText("...", shape.getCenterX(), baseline, mTextPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent  event) {
        switch (event.getAction()&MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                mMode = DRAG;

                mDownTime = System.currentTimeMillis();
                mLastX = event.getX();
                mLastY = event.getY();
                if(isValidTouch(event.getX(), event.getY()))
                {
                    mDragState = DragState.BEGIN;
                    checkLongClick();
                }

                if(event.getPointerCount()==2) {
                    mBeforeDistance = calculateDistance(event);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (calculateDistance(event) > 10f) {
                    mMode = ZOOM;
                    mBeforeDistance = calculateDistance(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                receiveActionUp = true;
                if(mMode == DRAG) {
                    float dx = event.getX() - mLastX;
                    float dy = event.getY() - mLastY;
                    if (mDragState == DragState.DONE) {
                        doMove(dx, dy);
                    } else {
                        if (dx * dx + dy * dy > 5)
                            doDrag(dx, dy);
                    }
                    mLastX = event.getX();
                    mLastY = event.getY();
                }
                else if(mMode == ZOOM)
                {
                    if(calculateDistance(event)>10f)
                    {
                        mAfterDistance = calculateDistance(event);
                        float moveDistance = mAfterDistance - mBeforeDistance;
                        if(moveDistance == 0) {
                            break;
                        }
                        else if(Math.abs(moveDistance)>5f)
                        {
                            doZoom(moveDistance);
                            mBeforeDistance = mAfterDistance;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                receiveActionUp = true;
                if(mOnShapeClickListener != null
                        && mDragState == DragState.BEGIN
                        &&(System.currentTimeMillis() - mDownTime)<=ON_CLICK_TIME)
                    mOnShapeClickListener.onClick(mTargetShape, mTargetIndex);
                mDragState = DragState.DONE;
                mTargetShape = null;
                mTargetIndex = -1;
                mLastX = -1;
                mLastY = -1;

                mMode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                if(mAnimation != null)
                    clearAnimation();
                mScale = 1;
                mMatrix.setScale(mScale, mScale);
                invalidate();
                break;
        }
        return true;
    }

    //拖动某个图形
    private void doDrag(float dx, float dy)
    {
        if(mFinish)
            return;
        //若终点已确定，禁止拖动
        if(mTargetIndex == 0 && mStopShapeFixed)
            return;
        //若起点已确定，禁止拖动
        if(mTargetIndex == 1 && mStartShapeFixed)
            return;
        mDragState = DragState.DOING;


        float targetX = mTargetShape.getCenterX()+dx;
        float targetY = mTargetShape.getCenterY()+dy;
        //禁止移到起点，终点范围内
        if(mStartShapeFixed && mStopShapeFixed &&
                ((targetX-mLeftLimit)*(targetX-mLeftLimit)
                +(targetY-mBottomLimit)*(targetY-mBottomLimit)
                <=4*shapeRadius*shapeRadius
                ||(targetX-mRightLimit)*(targetX-mRightLimit)
                +(targetY-mTopLimit)*(targetY-mTopLimit)
                <=4*shapeRadius*shapeRadius))
            return;
        //横向拖动处理
        //若终点，起点已固定，不允许左右出界
        if(mStartShapeFixed && mStopShapeFixed && (targetX <= mLeftLimit || targetX >= mRightLimit))
            return;
        if(mTargetShape.getCenterX()>=mLeftBorder
                && mTargetShape.getCenterX()<=mRightBorder
                &&(targetX < mLeftBorder||targetX > mRightBorder))
        {
            //到达屏幕左边,所有shape右移mLeftBorder-targetX或到达屏幕右边所有shape左移targetX-mrRightBorder
            for(Shape shape : mShapeList)
                shape.setCenterX(shape.getCenterX()-dx);
            mLeftLimit -= dx;
            mRightLimit -= dx;
            targetX = mTargetShape.getCenterX();
        }
        mTargetShape.setCenterX(targetX);

        //纵向拖动处理
        //若终点，起点已固定，不允许上下出界
        if(mStartShapeFixed && mStopShapeFixed &&(targetY <= mTopLimit || targetY >= mBottomLimit))
            return;
        if(mTargetShape.getCenterY()>=mTopBorder
                && mTargetShape.getCenterY()<=mBottomBorder
                &&(targetY < mTopBorder||targetY > mBottomBorder))
        {
            //所有shape下移mTopBorder-targetY或所有shape上移targetY-mBottomBorder
            for(Shape shape : mShapeList)
                shape.setCenterY(shape.getCenterY() - dy);
            mTopLimit -= dy;
            mBottomLimit -= dy;
            targetY = mTargetShape.getCenterY();
        }
        mTargetShape.setCenterY(targetY);
        invalidate();
    }


    public void performMove(float dx, float dy)
    {
        doMove(dx, dy);
    }

    //移动整个view
    private void doMove(float dx, float dy)
    {
        //若终点，起点已固定，不允许移动出界
        if(mStartShapeFixed && mStopShapeFixed &&
                ((mLeftBorder<mLeftLimit+dx && mRightBorder<mRightLimit+dx)
                ||(mLeftBorder>mLeftLimit+dx && mRightBorder>mRightLimit+dx)
                ||(mTopBorder<mTopLimit+dy && mBottomBorder<mBottomLimit+dy)
                ||(mTopBorder>mTopLimit+dy && mBottomBorder>mBottomLimit+dy)))
            return;
        for(Shape shape : mShapeList) {
            shape.setCenterX(shape.getCenterX() + dx);
            shape.setCenterY(shape.getCenterY() + dy);
        }
        mLeftLimit += dx;
        mRightLimit += dx;
        mTopLimit += dy;
        mBottomLimit += dy;
        invalidate();
    }

    /**
     * 计算两点间的距离
     */
    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    //启动伸缩动画
    private void doZoom(float moveDistance)
    {
        if(moveDistance>0)
            mScale += 0.04;
        else
            mScale -= 0.04;
        mMatrix.setScale(mScale, mScale);
        invalidate();
//        mAnimation = new ScaleAnimation(origin, mScale, origin, mScale);
//        mAnimation.setDuration(200);
//        mAnimation.setFillAfter(true);
//        startAnimation(mAnimation);
    }

    //判断触摸是否在图形的有效区域
    private boolean isValidTouch(float x, float y)
    {
        Shape shape;
        for(int i = 0; i < mShapeList.size(); i++)
        {
            shape = mShapeList.get(i);
            if(  (x- shape.getCenterX())*(x- shape.getCenterX())
                +(y- shape.getCenterY())*(y- shape.getCenterY())
                <= shape.getRadius()* shape.getRadius())
            {
                mTargetShape = shape;
                mTargetIndex = i;
                return true;
            }
        }
        return false;
    }

    private boolean receiveActionUp;

    //启动线程检查是否发生了长按事件
    private void checkLongClick()
    {
        receiveActionUp = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!receiveActionUp && System.currentTimeMillis()<mDownTime+ON_LONG_CLICK_TIME)
                {
                    try {
                        Thread.sleep(50);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(!receiveActionUp)
                    mLongClickHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private Handler mLongClickHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(mOnShapeLongClickListener != null && msg.what == 0)
                mOnShapeLongClickListener.onLongClick(mTargetShape, mTargetIndex);
        }
    };


    //移除最后的图形
    public void removeShape()
    {
        mShapeList.remove(mShapeList.size()-1);
        invalidate();
    }

    //判断能否移除图形
    public boolean canRemoveShape()
    {
        return !mFinish && mShapeList.size()>2;
    }


    //添加一个图形
    public Shape addShape(Shape shape)
    {
        mShapeList.add(shape);
        invalidate();
        return shape;
    }

    //判断能否添加图形
    public boolean canAddShape(float x, float y)
    {
        if(!mFinish && mStartShapeFixed && mStopShapeFixed
                && x >= mLeftLimit- Shape.DEFAULT_RADIUS && x <= mRightLimit+ Shape.DEFAULT_RADIUS
                && y >= mTopLimit- Shape.DEFAULT_RADIUS && y <= mBottomLimit+ Shape.DEFAULT_RADIUS)
        {
            //起点区域内
            if((x-mLeftLimit)*(x-mLeftLimit)
              +(y-mBottomLimit)*(y-mBottomLimit)
              <=4* Shape.DEFAULT_RADIUS* Shape.DEFAULT_RADIUS) {
                return false;
            }
            //终点区域内
            else if((x-mRightLimit)*(x-mRightLimit)
                    +(y-mTopLimit)*(y-mTopLimit)
                    <=4* Shape.DEFAULT_RADIUS* Shape.DEFAULT_RADIUS)
            {
                mFinish = true;
                invalidate();
                return false;
            }
            else
            {

                return true;
            }
        }
        return false;
    }


    //设置线条宽度
    public void setLinesWidth(float width)
    {
        if(mFinish)
            return;
        mLinePaint.setStrokeWidth(width);
    }

    //设置线条颜色
    public void setLinesCorlor(int color)
    {
        if(mFinish)
            return;
        mLinePaint.setColor(color);
        lineColor = color;
    }

    public void setTextVisible(boolean visible)
    {
        if(mFinish)
            return;
        this.mTextVisible = visible;
    }

    public void setTextSize(float size)
    {
        if(mFinish)
            return;
        if(mTextVisible)
            mTextPaint.setTextSize(size);
    }

    public void setTextColor(int color)
    {
        if(mFinish)
            return;
        if(mTextVisible)
            mTextPaint.setColor(color);
        textColor = color;
    }

    public float getLeftLimit()
    {
        return mLeftLimit;
    }

    public float getTopLimit()
    {
        return mTopLimit;
    }

    public float getRightLimit()
    {
        return mRightLimit;
    }

    public float getBottomLimit()
    {
        return mBottomLimit;
    }

    public boolean fixStartShape()
    {
        if(!mStartShapeFixed) {
            Shape startShape = mShapeList.get(1);

            float dx = mLeftBorder - startShape.getCenterX();
            float dy = mBottomBorder - startShape.getCenterY();
            for(Shape shape : mShapeList) {
                shape.setCenterX(shape.getCenterX() + dx);
                shape.setCenterY(shape.getCenterY() + dy);
            }
            mLeftLimit = mLeftBorder - LIMIT_PADDING;
            mRightLimit += dx;
            mTopLimit += dy;
            mBottomLimit = mBottomBorder + LIMIT_PADDING;
            invalidate();
            this.mStartShapeFixed = true;
            return true;
        }
        return false;
    }

    public boolean fixStopShape()
    {
        if(!mStartShapeFixed)
            return false;
        if(!mStopShapeFixed) {
            Shape stopShape = mShapeList.get(0);

            float dx = mRightBorder - stopShape.getCenterX();
            float dy = mTopBorder - stopShape.getCenterY();
            for(Shape shape : mShapeList) {
                shape.setCenterX(shape.getCenterX() + dx);
                shape.setCenterY(shape.getCenterY() + dy);
            }
            mLeftLimit +=dx;
            mRightLimit = mRightBorder + LIMIT_PADDING;
            mTopLimit = mTopBorder - LIMIT_PADDING;
            mBottomLimit += dy;
            invalidate();
            this.mStopShapeFixed = true;
            return true;
        }
        return false;
    }

    public boolean complete()
    {
        if(!mFinish) {
            this.mFinish = true;
            invalidate();
            return true;
        }
        return  false;
    }

    public boolean getFinishState()
    {
        return mFinish;
    }

    public Shape getShape(int id)
    {
        return mShapeList.get(id);
    }

    public List<Shape> getShapeList()
    {
        return mShapeList;
    }

    public void initShapeList(List<Shape> list)
    {
        this.mShapeList.clear();
        mShapeList.addAll(list);
        invalidate();
    }

    public void setOnShapeClickListener(OnShapeClickListener onShapeClickListener)
    {
        this.mOnShapeClickListener = onShapeClickListener;
    }

    public void setOnShapeLongClickListener(OnShapeLongClickListener onShapeLongClickListener)
    {
        this.mOnShapeLongClickListener = onShapeLongClickListener;
    }



}
