package com.jusfoun.customcommit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RatingBarView extends LinearLayout {

    public interface OnRatingListener {
        void onRating(Object bindObject, int RatingScore);
    }

    private boolean mClickable = true;
    private OnRatingListener onRatingListener;
    private Object bindObject;
    private float starImageSize;
    private int starCount;
    private Drawable starEmptyDrawable;
    private Drawable starFillDrawable;
    private int mStarCount;
    private float downX;

    public void setClickable(boolean clickable) {
        this.mClickable = clickable;
    }

    public RatingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatingBarView);
        starImageSize = ta.getDimension(R.styleable.RatingBarView_starImageSize, 20);
        starCount = ta.getInteger(R.styleable.RatingBarView_starCount, 5);
        starEmptyDrawable = ta.getDrawable(R.styleable.RatingBarView_starEmpty);
        starFillDrawable = ta.getDrawable(R.styleable.RatingBarView_starFill);
        ta.recycle();

        for (int i = 0; i < starCount; ++i) {
            ImageView imageView = getStarImageView(context, attrs);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickable) {
                        mStarCount = indexOfChild(v) + 1;
                        setStar(mStarCount, false);
                        if (onRatingListener != null) {
                            onRatingListener.onRating(bindObject, mStarCount);
                        }
                    }
                }
            });
            addView(imageView);
        }
    }

    private ImageView getStarImageView(Context context, AttributeSet attrs) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams para = new ViewGroup.LayoutParams(Math.round(starImageSize), Math.round(starImageSize));
        imageView.setLayoutParams(para);
        // TODO:you can change gap between two stars use the padding
        imageView.setPadding(0, 0, 40, 0);
        imageView.setImageDrawable(starEmptyDrawable);
        imageView.setMaxWidth(10);
        imageView.setMaxHeight(10);
        return imageView;
    }

    public void setStar(int starCount, boolean animation) {
        starCount = starCount > this.starCount ? this.starCount : starCount;
        starCount = starCount < 0 ? 0 : starCount;
        for (int i = 0; i < starCount; ++i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starFillDrawable);
            if (animation) {
                ScaleAnimation sa = new ScaleAnimation(0, 0, 1, 1);
                getChildAt(i).startAnimation(sa);
            }
        }
        for (int i = this.starCount - 1; i >= starCount; --i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
        }
    }

    public int getStarCount() {
        return mStarCount;
    }

    public void setStarFillDrawable(Drawable starFillDrawable) {
        this.starFillDrawable = starFillDrawable;
    }

    public void setStarEmptyDrawable(Drawable starEmptyDrawable) {
        this.starEmptyDrawable = starEmptyDrawable;
    }

    public void setStarCount(int startCount) {
        this.starCount = starCount;
    }

    public void setStarImageSize(float starImageSize) {
        this.starImageSize = starImageSize;
    }

    public void setBindObject(Object bindObject) {
        this.bindObject = bindObject;
    }

    public void setOnRatingListener(OnRatingListener onRatingListener) {
        this.onRatingListener = onRatingListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - downX) > 20){
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                return false;
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                return false;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                int index = checkPoint(event.getRawX(),event.getRawY());
                if (index >= 0){
                    setStar(index,false);
                }
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断move事件是否发生在某个view中，如果在，返回view在容器中的索引
     * @param x 事件的X坐标
     * @param y 事件的Y坐标
     * @return view在容器中的索引
     */
    private int checkPoint(float x, float y){
        int index = -1;
        for (int count = 0; count < getChildCount(); count++){
            View view = getChildAt(count);
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            int viewX = location[0];
            int viewY = location[1];
            if(( x > viewX && x < (viewX + view.getWidth())) &&
                    ( y > viewY && y < (viewY + view.getHeight()))){
                return count + 1;
            }
        }
        return index;
    }
}
