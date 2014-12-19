package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.charts.PieChart;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class PieChartAnimator {

  private PieChart mChart;

  private Scroller mScroller;

  private ValueAnimator mScrollAnimator;

  public PieChartAnimator(PieChart chart) {
    mChart = chart;

    mScroller = new Scroller(mChart.getContext());

    mScrollAnimator = ValueAnimator.ofFloat(0, 1);
    mScrollAnimator.setInterpolator(new DecelerateInterpolator());
    mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        doScroll();
      }
    });
  }

  public void scroll(float theta) {
    mChart.updateRotation(mChart.getChartAngle() - (int) theta / 4);
  }

  public void fling(float theta) {
    mScroller.fling(
        0, (int) mChart.getChartAngle(),
        0, (int) theta / 4,
        0, 0,
        Integer.MIN_VALUE, Integer.MAX_VALUE);
    mScrollAnimator.setDuration(mScroller.getDuration());
    mScrollAnimator.start();
  }

  public void smoothScroll(float startAngle, float endAngle, long duration) {
    ValueAnimator animator = ValueAnimator.ofFloat(startAngle, endAngle);
    animator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mChart.updateRotation((Float) animation.getAnimatedValue());
      }
    });
    animator.setDuration(duration);
    animator.start();
  }

  public void stop() {
    mScrollAnimator.cancel();
    mScroller.forceFinished(true);
  }

  private void doScroll() {
    if (!mScroller.isFinished()) {
      mScroller.computeScrollOffset();
      mChart.updateRotation(mScroller.getCurrY());
    } else {
      mScrollAnimator.cancel();
      mChart.centerOnHighlighted();
    }
  }

  public static float vectorToScalarScroll(float dx, float dy, float x, float y) {
    // get the length of the vector
    float l = (float) Math.sqrt(dx * dx + dy * dy);

    // decide if the scalar should be negative or positive by finding
    // the dot product of the vector perpendicular to (x,y).
    float crossX = -y;
    float crossY = x;

    float dot = (crossX * dx + crossY * dy);
    float sign = Math.signum(dot);

    return l * sign;
  }

}
