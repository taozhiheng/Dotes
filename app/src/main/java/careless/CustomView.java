package careless;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class CustomView extends RelativeLayout {

    private ViewDragHelper mDragHelper;
    private int mLeftLimit;
    private int mRightLimit;

	public CustomView(Context context) {
		super(context);
		init();
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
    }

	private void init() {
		/**
		 * @params ViewGroup forParent 必须是一个ViewGroup
		 * @params float sensitivity 灵敏度
		 * @params Callback cb 回调
		 */
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        mRightLimit = point.x;
        mLeftLimit = 0;
		mDragHelper = ViewDragHelper.create(this, 1.2f, new ViewDragCallback());
	}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLeftLimit = getPaddingLeft();
        mRightLimit -= getPaddingRight();
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
		/**
		 * 尝试捕获子view，一定要返回true
		 * @param view 尝试捕获的view
		 * @param pointerId 指示器id？
		 * 这里可以决定哪个子view可以拖动
		 */
		@Override
		public boolean tryCaptureView(View view, int pointerId) {
//			return mCanDragView == view;
			return true;
		}
		
		/**
		 * 处理水平方向上的拖动
		 * @param child 被拖动到view
		 * @param left 移动到达的x轴的距离
		 * @param dx 建议的移动的x距离
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			System.out.println("left = " + left + ", dx = " + dx +", width = "+getWidth()+" screen:"+mRightLimit);

            if(mRightLimit - child.getWidth() < left && mRightLimit<getWidth() - getPaddingRight())
            {
                int x = left+child.getWidth()- mRightLimit;
                scrollBy(x, 0);
                mRightLimit += x;
                mLeftLimit += x;
            }

            if(mLeftLimit > left && mLeftLimit > getPaddingLeft())
            {
                int x = left - mLeftLimit;
                scrollBy(x, 0);
                mLeftLimit += x;
                mRightLimit += x;
            }
			// 两个if主要是为了让viewViewGroup里
			if(getPaddingLeft() > left) {
				return getPaddingLeft();
			}
			
			if(getWidth() - child.getWidth() < left) {
				return getWidth() - child.getWidth();
                //setRight(child.getWidth()+left+10);
                //System.out.println("left = " + (left-10) + ", dx = " + dx +", width = "+getWidth());

			}
			
			return left;
		}
		
		/**
		 *  处理竖直方向上的拖动
		 * @param child 被拖动到view
		 * @param top 移动到达的y轴的距离
		 * @param dy 建议的移动的y距离
		 */
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			// 两个if主要是为了让viewViewGroup里
			if(getPaddingTop() > top) {
				return getPaddingTop();
			}
			
			if(getHeight() - child.getHeight() < top) {
				return getHeight() - child.getHeight();
			}
			
			return top;
		}
		
		/**
		 * 当拖拽到状态改变时回调
		 * @params 新的状态
		 */
		@Override
		public void onViewDragStateChanged(int state) {
			switch (state) {
			case ViewDragHelper.STATE_DRAGGING:  // 正在被拖动
				break;
			case ViewDragHelper.STATE_IDLE:  // view没有被拖拽或者 正在进行fling/snap
				break;
			case ViewDragHelper.STATE_SETTLING: // fling完毕后被放置到一个位置
				break;
			}
			super.onViewDragStateChanged(state);
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_DOWN:
			mDragHelper.cancel(); // 相当于调用 processTouchEvent收到ACTION_CANCEL
			break;
		}
		/**
		 * 检查是否可以拦截touch事件
		 * 如果onInterceptTouchEvent可以return true 则这里return true
		 */
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/**
		 * 处理拦截到的事件
		 * 这个方法会在返回前分发事件
		 */
		mDragHelper.processTouchEvent(event);
		return true;
	}
}