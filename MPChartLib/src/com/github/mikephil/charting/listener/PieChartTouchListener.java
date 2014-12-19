package com.github.mikephil.charting.listener;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PieChartAnimator;

import android.graphics.PointF;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class PieChartTouchListener extends SimpleOnGestureListener {

  private PieChart mChart;

  private Highlight mLastHighlight;

  public PieChartTouchListener(PieChart ctx) {
    this.mChart = ctx;
  }

  @Override
  public boolean onSingleTapUp(MotionEvent e) {

    float distance = mChart.distanceToCenter(e.getX(), e.getY());

    // check if a slice was touched
    if (distance < mChart.getRadius() / 2 || distance > mChart.getRadius()) {

      // if no slice was touched, highlight nothing
      mChart.highlightValues(null);
      mLastHighlight = null;
    } else {

      int index = mChart.getIndexForAngle(mChart.getAngleForPoint(e.getX(), e.getY()));
      int dataSetIndex = mChart.getDataSetIndexForIndex(index);

      Highlight h = new Highlight(index, dataSetIndex);

      if (h.equalTo(mLastHighlight)) {

        mChart.highlightValues(null);
        mLastHighlight = null;
      } else {

        mChart.highlightValues(new Highlight[] { h });
        mLastHighlight = h;
      }
    }
    return true;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    PointF p = findChartCenter();
    float theta = PieChartAnimator.vectorToScalarScroll(
        distanceX, distanceY,
        e2.getX() - p.x,
        e2.getY() - p.y);
    mChart.getAnimator().scroll(theta);
    return true;
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    PointF p = findChartCenter();
    float theta = PieChartAnimator.vectorToScalarScroll(
        velocityX, velocityY,
        e2.getX() - p.x,
        e2.getY() - p.y);
    mChart.getAnimator().fling(theta);
    return true;
  }

  @Override
  public boolean onDown(MotionEvent e) {
    mChart.getAnimator().stop();
    return true;
  }

  private PointF findChartCenter() {
    return mChart.getCenterCircleBox();
  }

}
