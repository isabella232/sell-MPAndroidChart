package com.github.mikephil.charting.utils;

import android.graphics.Paint;

public class DrawingSpec {
  protected Paint mBasicPaint = new Paint();

  public DrawingSpec() {
    mBasicPaint.setAntiAlias(true);
  }

  public Paint getBasicPaint() {
    return mBasicPaint;
  }

  public void setBasicPaint(Paint basicPaint) {
    mBasicPaint = basicPaint;
  }
}
