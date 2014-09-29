package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by seba on 26.08.2014.
 */
public class VerticalBarChart extends BarLineChartBase<BarDataSet> {

  /**
   * space indicator between the bars 0.1f == 10 %
   */
  private float mBarSpace = 0.15f;

  private RectF mBarRect = new RectF();

  /**
   * flag that enables or disables the highlighting arrow
   */
  private boolean mDrawHighlightArrow = false;

  private int mXLabelPaddingInDp = 10;

  private float mMaxXLabelWidth = Utils.convertDpToPixel(100);
  private TextPaint mTextPaint;

  public VerticalBarChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public VerticalBarChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VerticalBarChart(Context context) {
    super(context);
  }

  @Override
  protected void init() {

    super.init();

    mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mHighlightPaint.setStyle(Style.FILL);
    mHighlightPaint.setColor(Color.rgb(0, 0, 0));
    // set alpha after color
    mHighlightPaint.setAlpha(120);

    mXLabelPaint.setTextAlign(Align.RIGHT);

    mTextPaint = new TextPaint(mXLabelPaint);
  }

  @Override
  protected void drawValues() {

  }

  @Override
  protected void drawData() {
    ArrayList<BarDataSet> dataSets = mCurrentData.getDataSets();

    for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

      BarDataSet dataSet = dataSets.get(i);
      ArrayList<Entry> series = dataSet.getYVals();
      Paint paint = dataSet.getDrawingSpec().getBasicPaint();

      // do the drawing
      for (int j = 0; j < dataSet.getEntryCount(); j++) {

        // Set the color for the currently drawn value. If the index is out of bounds, reuse colors.
        prepareRect(series.get(j).getXIndex(), series.get(j).getVal(), mBarRect);
        transformRect(mBarRect);

        // avoid drawing outofbounds values
        if (isOffContentBottom(mBarRect.bottom))
          break;

        if (isOffContentTop(mBarRect.top)) {
          continue;
        }

        int originalColor = paint.getColor();

        if (dataSet.getDrawingSpec().hasMultipleColors()) {
          paint.setColor(dataSet.getDrawingSpec().getColor(j));
        } else {
          paint.setColor(dataSet.getDrawingSpec().getBasicPaint().getColor());
        }

        mDrawCanvas.drawRect(mBarRect, paint);

        paint.setColor(originalColor);
      }
    }
  }

  protected void drawXLabels() {

    if (!mDrawXLabels)
      return;

    float rightPadding = Utils.convertDpToPixel(mXLabelPaddingInDp);
    float maxTextWidth = mMaxXLabelWidth - rightPadding * 2;

    // pre allocate to save performance (dont allocate in loop)
    float[] position = new float[] {
        0f, 0f
    };

    for (int i = 0; i < mCurrentData.getXValCount(); i++) {

      position[1] = i;

      if (mXLabels.isCenterXLabelsEnabled()) {
        position[1] += 0.5f;
      }

      transformValueToPixel(position);

      if (position[1] >= mOffsetTop && position[1] <= getHeight() - mOffsetBottom) {
        String text = TextUtils.ellipsize(mCurrentData.getXLabels().get(i), mTextPaint, maxTextWidth, TruncateAt.END).toString();
        mDrawCanvas.drawText(text, mOffsetLeft - rightPadding, position[1], mXLabelPaint);
      }
    }
  }

  @Override
  protected void drawYLabels() {

  }

  /**
   * draws the horizontal grid
   */
  protected void drawHorizontalGrid() {

  }

  @Override
  protected void drawVerticalGrid() {

    if (!mDrawHorizontalGrid)
      return;

    // create a new path object only once and use reset() instead of
    // unnecessary allocations
    Path p = new Path();

    float[] position = new float[] {
        0f, 0f
    };

    //draw the horizontal grid
    for (int i = 0; i < mYLabels.mEntryCount; i++) {

      position[0] = mYLabels.mEntries[i];
      transformValueToPixel(position);

      if (position[0] >= mOffsetLeft && position[0] <= getWidth()) {

        mDrawCanvas.drawLine(position[0], mOffsetTop, position[0], getHeight()
            - mOffsetBottom, mGridPaint);
      }

      mDrawCanvas.drawPath(p, mGridPaint);
    }
  }

  @Override
  protected void drawAdditional() {
  }

  public Highlight getHighlightByTouchPoint(float x, float y) {

    // create an array of the touch-point
    float[] pts = new float[2];
    pts[0] = x;
    pts[1] = y;

    Matrix tmp = new Matrix();

    // invert all matrices to convert back to the original value
    mMatrixOffset.invert(tmp);
    tmp.mapPoints(pts);

    mMatrixTouch.invert(tmp);
    tmp.mapPoints(pts);

    mMatrixValueToPx.invert(tmp);
    tmp.mapPoints(pts);

    double xTouchVal = pts[0];
    double yTouchVal = pts[1];
    double base = Math.floor(yTouchVal);

    if (this instanceof VerticalBarChart && (yTouchVal < 0 || yTouchVal > mDeltaX + 1))
      return null;

    int xIndex = (int) base;
    int dataSetIndex; // index of the DataSet inside the ChartData // object

    ArrayList<SelInfo> valsAtIndex = getYValsAtIndex(xIndex);

    dataSetIndex = getClosestDataSetIndex(valsAtIndex, (float) yTouchVal);

    if (dataSetIndex == -1)
      return null;

    return new Highlight(xIndex, dataSetIndex);
  }

  @Override
  protected void drawHighlights() {

    if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight()) {

      // distance between highlight arrow and bar
      float offsetY = mDeltaY * 0.04f;

      for (int i = 0; i < mIndicesToHightlight.length; i++) {

        int index = mIndicesToHightlight[i].getXIndex();

        // check outofbounds
        if (index < mCurrentData.getYValCount() && index >= 0) {

          mHighlightPaint.setAlpha(120);

          float y = getYValueByDataSetIndex(index,
              mIndicesToHightlight[i].getDataSetIndex());

          RectF highlight = prepareRect(index, y, new RectF());
          transformRect(highlight);

          mDrawCanvas.drawRect(highlight, mHighlightPaint);

          if (mDrawHighlightArrow) {

            mHighlightPaint.setAlpha(200);

            Path arrow = new Path();
            arrow.moveTo(index + 0.5f, y + offsetY * 0.3f);
            arrow.lineTo(index + 0.2f, y + offsetY);
            arrow.lineTo(index + 0.8f, y + offsetY);

            transformPath(arrow);
            mDrawCanvas.drawPath(arrow, mHighlightPaint);
          }
        }
      }
    }
  }

  @Override
  protected BarDataSet createDataSet(ArrayList<Entry> approximated, String label) {
    return null;
  }

  private RectF prepareRect(int index, float value, RectF rectF) {

    float top = index + mBarSpace / 2f;
    float right = value >= 0 ? value : 0;
    float bottom = index + 1f - mBarSpace / 2f;
    float left = value <= 0 ? value : 0;
    rectF.set(left, top, right, bottom);

    return rectF;
  }

  @Override
  protected void calcMinMax(boolean fixedValues) {
    super.calcMinMax(fixedValues);

    if (!mStartAtZero && getYMin() >= 0f) {
      mYChartMin = getYMin();
      mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    // increase deltax by 1 because the bars have a width of 1
    mDeltaX++;
  }

  @Override
  protected void calculateOffsets() {

    float yleft = 0f;

    if (mDrawXLabels) {

      for (int i = 0; i < mCurrentData.getXValCount(); i++) {

        if (i % mXLabels.mXAxisLabelModulus == 0) {

          String label = mCurrentData.getXLabels().get(i);
          yleft = Math.max(yleft, Utils.calcTextWidth(mTextPaint, label)) + mXLabelPaddingInDp * 2;
        }
      }

      if (yleft > mMaxXLabelWidth) {
        yleft = mMaxXLabelWidth;
      }

      mOffsetLeft = Math.max(mOffsetLeft, yleft);
    }

    if (mDrawYLabels) {
      mAxisXLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q") * 2f;
      mOffsetTop += mAxisXLabelHeight;
    }

    float scaleX = ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaY);
    float scaleY = ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaX);

    Matrix val = new Matrix();
    mMatrixValueToPx.postTranslate(-mYChartMin, 0);
    val.postScale(scaleX, scaleY);

    mMatrixValueToPx.set(val);

    Matrix offset = new Matrix();
    offset.postTranslate(mOffsetLeft, mOffsetTop);

    mMatrixOffset.set(offset);
  }

  public void setDrawHighlightArrow(boolean enabled) {
    mDrawHighlightArrow = enabled;
  }
}
