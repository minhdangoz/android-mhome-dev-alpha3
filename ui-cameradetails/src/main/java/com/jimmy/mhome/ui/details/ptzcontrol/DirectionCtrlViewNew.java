package com.jimmy.mhome.ui.details.ptzcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jimmy.mhome.ui.details.R;

public class DirectionCtrlViewNew extends AbsDirectionCtrlView {
    
    private int padding = 0;
    private int imageSize = 0;


    public DirectionCtrlViewNew(Context context) {
        super(context);
    }

    public DirectionCtrlViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionCtrlViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DirectionCtrlViewNew(Context context, boolean z, float f) {
        super(context, z, f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        imageSize = Math.min(w, h);
        Log.i("-->", "onSizeChanged imageSize = " + imageSize);
        init(attr, defStyleAttr);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int measuredWidth;
        int measuredHeight;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 1073741824 || mode == Integer.MIN_VALUE) {
            measuredWidth = getPaddingRight() + getPaddingLeft() + size;
        } else {
            measuredWidth = getPaddingLeft() + mBitmapRockerDisable.getWidth() + getPaddingRight();
        }
        if (mode2 == 1073741824 || mode == Integer.MIN_VALUE) {
            measuredHeight = getPaddingBottom() + getPaddingTop() + size2;
        } else {
            measuredHeight = getPaddingTop() + mBitmapRockerDisable.getHeight() + getPaddingBottom();
        }
        int min = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(min, min);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (getVisibility() != View.VISIBLE) {
            return false;
        }
        if (!mIsDisabled) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            mDownTime = System.currentTimeMillis();
            float x = motionEvent.getX() - padding;
            float y = motionEvent.getY() - padding;
            float f = x - mRadius;
            float f2 = y - mRadius;
            if (Math.sqrt((f * f) + (f2 * f2)) <= mRadius) {
                if (supportCenterClick && x > mCenterEdge && x < mDiameter - mCenterEdge && y > mCenterEdge && y < mDiameter - mCenterEdge) {
                    Log.i("-->","onTouchEvent direction 0");
                    isPress = true;
                    direction = 0;
                    invalidate();
                } else if (x < mRadius && y > x && y < mDiameter - x) {
                    isPress = true;
                    direction = 1;
                    invalidate();
                } else if (y >= mRadius || x >= mRadius ? y < mDiameter - x : y < x) {
                    isPress = true;
                    direction = 3;
                    invalidate();
                } else if (x <= mRadius || y >= mRadius ? y < x : y > mDiameter - x) {
                    isPress = true;
                    direction = 2;
                    invalidate();
                } else if (y <= mRadius || x >= mRadius ? y > x : y > mDiameter - x) {
                    isPress = true;
                    direction = 4;
                    invalidate();
                }
                if (isPress && mOnDirectionCtrlListener != null) {
                    mOnDirectionCtrlListener.onActionDown();
                }
                if (isPress) {
                    performHapticFeedback(0, 2);
                    getHandler().post(directionRunnable);
                }
            }
        } else if (action == 1 || action == 3) {
            isPress = false;
            if (mOnDirectionCtrlListener != null) {
                OnDirectionCtrlListener onDirectionCtrlListener = mOnDirectionCtrlListener;
                if (System.currentTimeMillis() - mDownTime > 500) {
                    z = true;
                }
                onDirectionCtrlListener.onActionUp(z);
            }
            direction = -1;
            getHandler().removeCallbacks(directionRunnable);
            invalidate();
        }
        return true;
    }

    @Override
    public void init(AttributeSet attributeSet, int i, int i2) {
        super.init(attributeSet, i, i2);

        if (mBitmapRockerDisable == null) {
            mBitmapRockerDisable = BitmapUtils.decode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_bg_no_center : R.drawable.home_ptz_fullscreen_bg);
        }

        if (imageSize > 0) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapUtils.decode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_bg_no_center : R.drawable.home_ptz_fullscreen_bg, options);
            int i3 = (int) (options.outWidth * mScale);
            int i4 = (int) (options.outHeight * mScale);
            int i5 = imageSize;
            if (i5 < i4) {
                i4 = i5;
                i3 = i4;
            }
            mBgWidth = i3;
            padding = (imageSize - mBgWidth) / 2;
            mDiameter = mBgWidth;
            mRadius = mBgWidth / 2;
            mCenterEdge = (int) (mDiameter / 2.8d);

            Log.i("-->", "->>mCenterEdge = " + mCenterEdge);
            int i6 = padding;
            mRectFMain = new RectF(i6, i6, i3 + i6, i6 + i4);
        }
    }

    @Override
    public void delayInit() {
        Log.i("-->", "->>mOrientation should be 1 = " + mOrientation);

        bitmap = BitmapUtils.decode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_bg_no_center_new : R.drawable.home_ptz_fullscreen_bg);
        super.delayInit();
    }

    private Bitmap getBitmapByDirection(int i) {
        if (i == 1) {
            if (mPtzLeft == null) {
                mPtzLeft = BitmapUtils.scaleDecode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_left_new : R.drawable.home_ptz_fullscreen_left_press);
            }
            return mPtzLeft;
        } else if (i == 2) {
            if (mPtzRight == null) {
                mPtzRight = BitmapUtils.scaleDecode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_right_new : R.drawable.home_ptz_fullscreen_right_press);
            }
            return mPtzRight;
        } else if (i == 3) {
            if (mPtzTop == null) {
                mPtzTop = BitmapUtils.scaleDecode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_top_new : R.drawable.home_ptz_fullscreen_up_press);
            }
            return mPtzTop;
        } else if (i != 4) {
            return null;
        } else {
            if (mPtzBottom == null) {
                mPtzBottom = BitmapUtils.scaleDecode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_bottom_new : R.drawable.home_ptz_fullscreen_down_press);
            }
            return mPtzBottom;
        }
    }

    @Override
    protected void canvasOk(Canvas canvas) {
        Bitmap bitmapByDirection;
        try {
            if (mIsDisabled) {
                canvas.drawBitmap(bitmap, (Rect) null, mRectFMain, mPaint);
                if (isPress && (bitmapByDirection = getBitmapByDirection(direction)) != null) {
                    canvas.drawBitmap(bitmapByDirection, (Rect) null, mRectFMain, mPaint);
                }
            } else {

                if (mBitmapRockerDisable == null) {
                    mBitmapRockerDisable = BitmapUtils.scaleDecode(getResources(), mOrientation == 1 ? R.drawable.home_ptz_bg_no_center_new : R.drawable.home_ptz_fullscreen_bg);
                }
                canvas.drawBitmap(mBitmapRockerDisable, (Rect) null, mRectFMain, mPaint);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}