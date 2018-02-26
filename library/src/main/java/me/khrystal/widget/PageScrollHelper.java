package me.khrystal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/2/26
 * update time:
 * email: 723526676@qq.com
 */

public class PageScrollHelper {

    private int startPage = 0;
    private RecyclerView mRecyclerView = null;

    private int offsetY = 0;
    private int offsetX = 0;
    private int startY = 0;
    private int startX = 0;

    private InnerOnScrollListener mOnScrollListener = null;
    private InnerOnFlingListener mOnFlingListener = null;
    private InnerOnTouchListener mOnTouchListener = null;

    private ValueAnimator mAnimator = null;

    enum ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    private ORIENTATION mOrientation = ORIENTATION.HORIZONTAL;


    @SuppressLint("ClickableViewAccessibility")
    public void setUpRecyclerView(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            throw new IllegalArgumentException("recycleView must be not null");
        }
        mRecyclerView = recyclerView;
        mOnFlingListener = new InnerOnFlingListener();
        recyclerView.setOnFlingListener(mOnFlingListener);
        mOnScrollListener = new InnerOnScrollListener();
        recyclerView.setOnScrollListener(mOnScrollListener);
        mOnTouchListener = new InnerOnTouchListener();
        recyclerView.setOnTouchListener(mOnTouchListener);
        recyclerView.scrollToPosition(position);
        updateLayoutManager(position);
    }

    private void updateLayoutManager(int position) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                mOrientation = ORIENTATION.VERTICAL;
            } else if (layoutManager.canScrollHorizontally()) {
                mOrientation = ORIENTATION.HORIZONTAL;
            } else {
                mOrientation = ORIENTATION.NULL;
            }
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            startX = position * mRecyclerView.getWidth();
            startY = position * mRecyclerView.getHeight();
            offsetX = position * mRecyclerView.getWidth();
            offsetY = position * mRecyclerView.getHeight();
        }
    }

    class InnerOnFlingListener extends RecyclerView.OnFlingListener {

        @Override
        public boolean onFling(int velocityX, int velocityY) {
            if (mOrientation == ORIENTATION.NULL)
                return false;
            // get start page index
            int startPageIndex = getStartPageIndex();
            int endPoint = 0;
            int startPoint = 0;

            if (mOrientation == ORIENTATION.VERTICAL) {
                startPoint = offsetY;
                if (velocityY < 0) {
                    startPageIndex--;
                } else if (velocityY > 0) {
                    startPageIndex++;
                }
                endPoint = startPageIndex * mRecyclerView.getHeight();
            } else {
                startPoint = offsetX;
                if (velocityX < 0) {
                    startPageIndex--;
                } else if (velocityX > 0) {
                    startPageIndex++;
                }
                endPoint = startPageIndex * mRecyclerView.getWidth();
            }
            if (endPoint < 0) {
                endPoint = 0;
            }

            if (mAnimator == null) {
                mAnimator = ValueAnimator.ofInt(startPoint, endPoint);
                mAnimator.setDuration(300);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int nowPoint = (int) valueAnimator.getAnimatedValue();

                        if (mOrientation == ORIENTATION.VERTICAL) {
                            int dy = nowPoint - offsetY;
                            mRecyclerView.scrollBy(0, dy);
                        } else {
                            int dx = nowPoint - offsetX;
                            mRecyclerView.scrollBy(dx, 0);
                        }
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (null != mOnPageChangeListener) {
                            mOnPageChangeListener.onPageChange(getPageIndex());
                        }
                    }
                });
            } else {
                mAnimator.cancel();
                mAnimator.setIntValues(startPoint, endPoint);
            }

            mAnimator.start();
            return true;
        }
    }

    class InnerOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mOrientation != ORIENTATION.NULL) {
                boolean move;
                int vX = 0, vY = 0;
                if (mOrientation == ORIENTATION.VERTICAL) {
                    int absY = Math.abs(offsetY - startY);
                    move = absY > recyclerView.getHeight() / 2;
                    vY = 0;

                    if (move) {
                        vY = offsetY - startY < 0 ? -1000 : 1000;
                    }

                } else {
                    int absX = Math.abs(offsetX - startX);
                    move = absX > recyclerView.getWidth() / 2;
                    if (move) {
                        vX = offsetX - startX < 0 ? -1000 : 1000;
                    }

                }
                mOnFlingListener.onFling(vX, vY);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            offsetY += dy;
            offsetX += dx;
        }
    }

    public class InnerOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startY = offsetY;
                startX = offsetX;
            }
            return false;
        }

    }

    private int getPageIndex() {
        int p = 0;
        if (mOrientation == ORIENTATION.VERTICAL) {
            p = offsetY / mRecyclerView.getHeight();
        } else {
            p = offsetX / mRecyclerView.getWidth();

        }
        return p;
    }

    private int getStartPageIndex() {
        int p = 0;
        if (mOrientation == ORIENTATION.VERTICAL) {
            p = startY / mRecyclerView.getHeight();
        } else {
            p = startX / mRecyclerView.getWidth();
        }
        return p;
    }

    onPageChangeListener mOnPageChangeListener;

    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public interface onPageChangeListener {
        void onPageChange(int index);
    }

    public void resetOffsetData(int nowPage){
        if(nowPage > startPage){
            offsetX += (nowPage - startPage) * mRecyclerView.getWidth();
            startX = offsetY;
        }else{
            offsetX -= (startPage - nowPage) * mRecyclerView.getWidth();
            startX = offsetY;
        }
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }
}
