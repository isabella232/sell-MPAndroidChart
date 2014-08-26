package com.github.mikephil.charting.utils;

import android.graphics.Color;
import android.graphics.Paint;

public class LineDrawingSpec extends DrawingSpec {
  private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mDataPointInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public LineDrawingSpec() {
    mFillPaint.setStyle(Paint.Style.FILL);
    mDataPointInnerCirclePaint.setStyle(Paint.Style.FILL);
    mDataPointInnerCirclePaint.setColor(Color.WHITE);
  }

  public Paint getFillPaint() {
    return mFillPaint;
  }

  public Paint getDataPointInnerCirclePaint() {
    return mDataPointInnerCirclePaint;
  }
}
