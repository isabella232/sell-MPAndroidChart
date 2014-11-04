package com.github.mikephil.charting.listener;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.Highlight;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PieChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

  Matrix matrix = new Matrix();
  Matrix savedMatrix = new Matrix();

  PointF mid = new PointF();

  // We can be in one of these 3 states
  private static final int NONE = 0;
  private static final int LONGPRESS = 4;

  private int mode = NONE;

  private PieChart mChart;

  private GestureDetector mGestureDetector;

  private boolean[] quadrantTouched;

  public PieChartTouchListener(PieChart ctx) {
    this.mChart = ctx;

    mGestureDetector = new GestureDetector(ctx.getContext(), this);
    quadrantTouched = new boolean[] { false, false, false, false, false };
  }

  @Override
  public boolean onTouch(View v, MotionEvent e) {
    if (mode == NONE) {
      if (mGestureDetector.onTouchEvent(e))
        return true;
    }

    float x = e.getX();
    float y = e.getY();

    mChart.stopRotationAnimation();

    switch (e.getAction()) {

    case MotionEvent.ACTION_DOWN:

      for (int i = 0; i < quadrantTouched.length; i++) {
        quadrantTouched[i] = false;
      }

      mChart.setStartAngle(x, y);
      mChart.updateRotation(x, y);
      mChart.invalidate();
      break;
    case MotionEvent.ACTION_MOVE:
      mChart.updateRotation(x, y);
      mChart.invalidate();
      break;
    case MotionEvent.ACTION_UP:
      mode = NONE;
      break;
    }

    quadrantTouched[getQuadrant(e.getX() - (mChart.getWidth() / 2), mChart.getHeight() - e.getY() - (mChart.getHeight() / 2))] = true;

    return true;
  }

  public Matrix getMatrix() {
    return matrix;
  }

  @Override
  public void onLongPress(MotionEvent e) {
    if (mode == NONE) {
      mode = LONGPRESS;
    }
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
    return true;
  }

  /**
   * reference to the last highlighted object
   */
  private Highlight mLastHighlight = null;

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
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

    int q1 = getQuadrant(e1.getX() - (mChart.getWidth() / 2), mChart.getHeight() - e1.getY() - (mChart.getHeight() / 2));
    int q2 = getQuadrant(e2.getX() - (mChart.getWidth() / 2), mChart.getHeight() - e2.getY() - (mChart.getHeight() / 2));

    if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math.abs(velocityY))
        || (q1 == 3 && q2 == 3)
        || (q1 == 1 && q2 == 3)
        || (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math.abs(velocityY))
        || ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
        || ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
        || (q1 == 2 && q2 == 4 && quadrantTouched[3])
        || (q1 == 4 && q2 == 2 && quadrantTouched[3])) {

      mChart.post(new FlingRunnable(-1 * (velocityX + velocityY)));
    } else {
      mChart.post(new FlingRunnable(velocityX + velocityY));
    }


    return true;
  }

  private static int getQuadrant(double x, double y) {
    if (x >= 0) {
      return y >= 0 ? 1 : 4;
    } else {
      return y >= 0 ? 2 : 3;
    }
  }

  /**
   * A {@link Runnable} for animating the the dialer's fling.
   */
  private class FlingRunnable implements Runnable {

    private float velocity;

    public FlingRunnable(float velocity) {
      this.velocity = velocity;
    }

    @Override
    public void run() {
      if (Math.abs(velocity) > 5) {
        mChart.updateRotation(velocity / 75);
        velocity /= 1.2F;
        mChart.post(this);
      }
    }
  }
}
