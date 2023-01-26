package com.jimmy.mhome.ui.details.ptzcontrol;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.jimmy.mhome.ui.details.R;

public abstract class AbsDirectionCtrlView extends View {
    
    protected AttributeSet attr;
    protected Bitmap bitmap;
    protected int defStyleAttr;
    protected int direction;
    protected Runnable directionRunnable;
    protected boolean isPress;
    protected int mBgWidth;
    protected Bitmap mBitmapRockerDisable;
    protected int mCenterEdge;
    protected int mDiameter;
    protected long mDownTime;
    protected boolean mIsDisabled;
    protected OnDirectionCtrlListener mOnDirectionCtrlListener;
    protected int mOrientation;
    protected Paint mPaint;
    protected Bitmap mPtzBottom;
    protected Bitmap mPtzCenter;
    protected Bitmap mPtzLeft;
    protected Bitmap mPtzRight;
    protected Bitmap mPtzTop;
    protected int mRadius;
    protected RectF mRectFMain;
    public float mScale;
    protected boolean supportCenterClick;
    protected float threshold;

    public interface OnDirectionCtrlListener {
        void onActionDown();

        void onActionUp(boolean z);

        void onCenterClick();

        void onClickPTZDirection(int i);
    }

    protected abstract void canvasOk(Canvas canvas);

    public AbsDirectionCtrlView(Context context) {
        super(context);
        isPress = false;
        direction = -1;
        mIsDisabled = true;
        supportCenterClick = false;
        mScale = 1.0f;
        threshold = 0.001f;
        mDownTime = 0L;
        mRectFMain = new RectF();
        directionRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPress) {
                    onDirection(direction);
                    getHandler().postDelayed(directionRunnable, 100L);
                }
            }
        };
        init(null, 0);
    }

    public AbsDirectionCtrlView(Context context, boolean z, float f) {
        super(context);
        isPress = false;
        direction = -1;
        mIsDisabled = true;
        supportCenterClick = false;
        mScale = 1.0f;
        threshold = 0.001f;
        mDownTime = 0L;
        mRectFMain = new RectF();
        directionRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPress) {
                    AbsDirectionCtrlView absDirectionCtrlView = AbsDirectionCtrlView.this;
                    absDirectionCtrlView.onDirection(absDirectionCtrlView.direction);
                    getHandler().postDelayed(directionRunnable, 100L);
                }
            }
        };
        mScale = f;
        supportCenterClick = z;
        init(null, 0);
    }

    public AbsDirectionCtrlView(Context context, boolean z, float f, boolean z2) {
        super(context);
        isPress = false;
        direction = -1;
        int i = 1;
        mIsDisabled = true;
        supportCenterClick = false;
        mScale = 1.0f;
        threshold = 0.001f;
        mDownTime = 0L;
        mRectFMain = new RectF();
        directionRunnable = new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                if (isPress) {
                    AbsDirectionCtrlView absDirectionCtrlView = AbsDirectionCtrlView.this;
                    absDirectionCtrlView.onDirection(absDirectionCtrlView.direction);
                    getHandler().postDelayed(directionRunnable, 100L);
                }
            }
        };
        mScale = f;
        supportCenterClick = z;
        init(null, 0, !z2 ? 2 : i);
    }

    public AbsDirectionCtrlView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        isPress = false;
        direction = -1;
        mIsDisabled = true;
        supportCenterClick = false;
        mScale = 1.0f;
        threshold = 0.001f;
        mDownTime = 0L;
        mRectFMain = new RectF();
        directionRunnable = new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                if (isPress) {
                    AbsDirectionCtrlView absDirectionCtrlView = AbsDirectionCtrlView.this;
                    absDirectionCtrlView.onDirection(absDirectionCtrlView.direction);
                    getHandler().postDelayed(directionRunnable, 100L);
                }
            }
        };
        init(attributeSet, 0);
    }

    public AbsDirectionCtrlView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        isPress = false;
        direction = -1;
        mIsDisabled = true;
        supportCenterClick = false;
        mScale = 1.0f;
        threshold = 0.001f;
        mDownTime = 0L;
        mRectFMain = new RectF();
        directionRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPress) {
                    AbsDirectionCtrlView absDirectionCtrlView = AbsDirectionCtrlView.this;
                    absDirectionCtrlView.onDirection(absDirectionCtrlView.direction);
                    getHandler().postDelayed(directionRunnable, 100L);
                }
            }
        };
        init(attributeSet, i);
    }

    public void myConfigurationChanged(Configuration configuration) {
        init(attr, defStyleAttr, configuration.orientation);
    }

    public void myConfigurationChanged(int i) {
        init(attr, defStyleAttr, i);
    }

    public void setSupportCenterClick(boolean z) {
        supportCenterClick = z;
    }

    public void init(AttributeSet attributeSet, int i) {
        init(attributeSet, i, 1);
    }

    public void init(AttributeSet attributeSet, int i, int orientation) {
        TypedArray obtainStyledAttributes;
        attr = attributeSet;
        defStyleAttr = i;
        if (Build.VERSION.SDK_INT >= 29) {
            setForceDarkAllowed(false);
        }
        //TODO:
        if (!(attributeSet == null || (obtainStyledAttributes =
                getContext().obtainStyledAttributes(attributeSet, new int[]{R.attr.view_scale})) == null)) {
            mScale = obtainStyledAttributes.getFloat(0, 1.0f);
            obtainStyledAttributes.recycle();
        }
        mOrientation = orientation;
        setBackgroundColor(0);
    }

    public void delayInit() {
        Paint paint = new Paint();
        mPaint = paint;
        paint.setColor(-7829368);
        mPaint.setAntiAlias(true);
    }

    public void onDirection(int i) {
        OnDirectionCtrlListener onDirectionCtrlListener = mOnDirectionCtrlListener;
        if (onDirectionCtrlListener == null) {
            return;
        }
        if (i == 0) {
            onDirectionCtrlListener.onClickPTZDirection(0);
        } else if (i == 1) {
            onDirectionCtrlListener.onClickPTZDirection(1);
        } else if (i == 2) {
            onDirectionCtrlListener.onClickPTZDirection(2);
        } else if (i == 3) {
            onDirectionCtrlListener.onClickPTZDirection(3);
        } else if (i == 4) {
            onDirectionCtrlListener.onClickPTZDirection(4);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size2 = MeasureSpec.getSize(i2);
        if (mode != 1073741824) {
            size = (int) (getPaddingLeft() + mRectFMain.width() + getPaddingRight());
        }
        if (mode2 != 1073741824) {
            size2 = (int) (getPaddingTop() + mRectFMain.height() + getPaddingBottom());
        }
        setMeasuredDimension(size, size2);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mPaint == null) {
            delayInit();
        }
        canvasOk(canvas);
    }

    public void setDisable(boolean z) {
        mIsDisabled = z;
        invalidate();
    }

    public int getRocherWidth() {
        return mBgWidth;
    }

    public void setOnDirectionCtrlListener(OnDirectionCtrlListener onDirectionCtrlListener) {
        mOnDirectionCtrlListener = onDirectionCtrlListener;
    }
}