package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Chart that draws lines, surfaces, circles, ...
 *
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase {

  /**
   * the radius of the circle-shaped value indicators
   */
  protected float mCircleSize = 4f;

  /**
   * the width of the drawn data lines
   */
  protected float mLineWidth = 1f;

  /**
   * the width of the highlighning line
   */
  protected float mHighlightWidth = 3f;

  /**
   * if true, the data will also be drawn filled
   */
  protected boolean mDrawFilled = false;

  /**
   * if true, drawing circles is enabled
   */
  protected boolean mDrawCircles = true;

  /**
   * paint for the filled are (if enabled) below the chart line
   */
  protected Paint mFilledPaint;

  /**
   * paint for the inner circle of the value indicators
   */
  protected Paint mCirclePaintInner;

  /**
   * flag for cubic curves instead of lines
   */
  protected boolean mDrawCubic = false;

  /**
   * Tells how many values should be treated as place holders
   */
  protected int mValuePadding = 0;

  public LineChart(Context context) {
    super(context);
  }

  public LineChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public LineChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void init() {
    super.init();

    mCircleSize = Utils.convertDpToPixel(mCircleSize);

    mFilledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mFilledPaint.setStyle(Paint.Style.FILL);

    mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
    mCirclePaintInner.setStyle(Paint.Style.FILL);
    mCirclePaintInner.setColor(Color.WHITE);

    mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mHighlightPaint.setStyle(Paint.Style.STROKE);
    mHighlightPaint.setStrokeWidth(2f);
    mHighlightPaint.setColor(Color.rgb(255, 187, 115));
  }

  @Override
  protected void drawHighlights() {

    // if there are values to highlight and highlighnting is enabled, do it
    if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight()) {

      for (int i = 0; i < mIndicesToHightlight.length; i++) {

        DataSet set = getDataSetByIndex(mIndicesToHightlight[i].getDataSetIndex());

        int xIndex = mIndicesToHightlight[i].getXIndex(); // get the
        // x-position
        float y = set.getYValForXIndex(xIndex); // get the y-position

        float[] pts = new float[] {
            xIndex, mYChartMax, xIndex, mYChartMin, 0, y, mDeltaX, y
        };

        transformValueToPixel(pts);
        // draw the highlight lines
        mDrawCanvas.drawLines(pts, mHighlightPaint);
      }
    }
  }

  /**
   * draws the given y values to the screen
   */
  @Override
  protected void drawData() {

    ArrayList<DataSet> dataSets;

    dataSets = mCurrentData.getDataSets();

    if (mDrawFilled) {
      float heightOffset = pixelYToValue(mOffsetBottom + mOffsetTop);
      for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

        DataSet dataSet = dataSets.get(i);
        ArrayList<Entry> entries = dataSet.getYVals();

        // if drawing filled is enabled
        if (entries.size() > 0) {
          Path filled = new Path();
          filled.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal());

          // create a new path
          for (int x = 1; x < entries.size(); x++) {

            filled.lineTo(entries.get(x).getXIndex(), entries.get(x).getVal());
          }

          // close up
          filled.lineTo(entries.get(entries.size() - 1).getXIndex(), mYChartMin - heightOffset);
          filled.lineTo(entries.get(0).getXIndex(), mYChartMin - heightOffset);
          filled.close();

          transformPath(filled);

          mDrawCanvas.drawPath(filled, mFilledPaint);
        }
      }
    }

    for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

      DataSet dataSet = dataSets.get(i);
      ArrayList<Entry> entries = dataSet.getYVals();

      float[] valuePoints = generateTransformedValues(entries, 0f);

      // Get the colors for the DataSet at the current index. If the index
      // is out of bounds, reuse DataSet colors.
      ArrayList<Integer> colors = mCt.getDataSetColors(i % mCt.getColors().size());

      Paint paint = mRenderPaint;

      if (mDrawCubic) {

        paint.setColor(colors.get(i % colors.size()));

        Path spline = new Path();

        spline.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal());

        // create a new path
        for (int x = 1; x < entries.size() - 3; x += 2) {

          // spline.rQuadTo(entries.get(x).getXIndex(),
          // entries.get(x).getVal(), entries.get(x+1).getXIndex(),
          // entries.get(x+1).getVal());

          spline.cubicTo(entries.get(x).getXIndex(), entries.get(x).getVal(), entries
              .get(x + 1).getXIndex(), entries.get(x + 1).getVal(), entries
              .get(x + 2).getXIndex(), entries.get(x + 2).getVal());
        }

        // spline.close();

        transformPath(spline);

        mDrawCanvas.drawPath(spline, paint);
      } else {

        for (int j = 0; j < valuePoints.length - 2; j += 2) {

          // Set the color for the currently drawn value. If the index
          // is
          // out of bounds, reuse colors.
          paint.setColor(colors.get(j % colors.size()));

          if (isOffContentRight(valuePoints[j]))
            break;

          // make sure the lines don't do shitty things outside bounds
          if (j != 0 && isOffContentLeft(valuePoints[j - 1])
              && isOffContentTop(valuePoints[j + 1])
              && isOffContentBottom(valuePoints[j + 1]))
            continue;

          mDrawCanvas.drawLine(valuePoints[j], valuePoints[j + 1], valuePoints[j + 2],
              valuePoints[j + 3], paint);
        }
      }
    }
  }

  /**
   * Calculates the middle point between two points and multiplies its
   * coordinates with the given smoothness _Mulitplier.
   *
   * @param p1 First point
   * @param p2 Second point
   * @param _Result Resulting point
   * @param mult Smoothness multiplier
   */
  private void calculatePointDiff(PointF p1, PointF p2, PointF _Result, float mult) {
    float diffX = p2.x - p1.x;
    float diffY = p2.y - p1.y;
    _Result.x = (p1.x + (diffX * mult));
    _Result.y = (p1.y + (diffY * mult));
  }

  @Override
  protected void drawValues() {

    // if values are drawn
    if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

      // make sure the values do not interfear with the circles
      int valOffset = (int) (mCircleSize * 1.7f);

      if (!mDrawCircles)
        valOffset = valOffset / 2;

      ArrayList<DataSet> dataSets = mCurrentData.getDataSets();

      final int padding = mValuePadding * 2;
      for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

        DataSet dataSet = dataSets.get(i);
        ArrayList<Entry> entries = dataSet.getYVals();

        float[] positions = generateTransformedValues(entries, 0f);

        for (int j = padding; j < positions.length - padding; j += 2) {

          if (isOffContentRight(positions[j]))
            break;

          if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
              || isOffContentBottom(positions[j + 1]))
            continue;

          float val = entries.get(j / 2).getVal();

          String label;
          if (mDrawValueXLabelsInChart) {
            label = mCurrentData.getXLabels().get(j / 2);
          } else {
            label = mDrawUnitInChart ? mFormatValue.format(val) + mUnit : mFormatValue.format(val);
          }

          float yPosition = positions[j + 1];
          if (j - 1 >= 0 && j + 3 < positions.length && positions[j - 1] < yPosition && positions[j + 3] < yPosition) {
            yPosition += valOffset + mValuePaint.getTextSize();
          } else {
            yPosition -= valOffset;
          }
          mDrawCanvas.drawText(label, positions[j],
              yPosition, mValuePaint);
        }
      }
    }
  }

  /**
   * draws the circle value indicators
   */
  @Override
  protected void drawAdditional() {
    // if drawing circles is enabled
    if (mDrawCircles) {

      ArrayList<DataSet> dataSets = mCurrentData.getDataSets();

      for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

        DataSet dataSet = dataSets.get(i);
        ArrayList<Entry> entries = dataSet.getYVals();

        // Get the colors for the DataSet at the current index. If the
        // index
        // is out of bounds, reuse DataSet colors.
        ArrayList<Integer> colors = mCt.getDataSetColors(i % mCt.getColors().size());

        float[] positions = generateTransformedValues(entries, 0f);

        final int padding = mValuePadding * 2;
        for (int j = padding; j < positions.length - padding; j += 2) {

          // Set the color for the currently drawn value. If the index
          // is
          // out of bounds, reuse colors.
          mRenderPaint.setColor(colors.get(j % colors.size()));

          if (isOffContentRight(positions[j]))
            break;

          // make sure the circles don't do shitty things outside
          // bounds
          if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
              || isOffContentBottom(positions[j + 1]))
            continue;

          mDrawCanvas.drawCircle(positions[j], positions[j + 1], mCircleSize,
              mRenderPaint);
          mDrawCanvas.drawCircle(positions[j], positions[j + 1], mCircleSize / 2,
              mCirclePaintInner);
        }
      }
    }
  }

  /**
   * set this to true to enable the drawing of circle indicators
   *
   * @param enabled
   */
  public void setDrawCircles(boolean enabled) {
    this.mDrawCircles = enabled;
  }

  /**
   * returns true if drawing circles is enabled, false if not
   *
   * @return
   */
  public boolean isDrawCirclesEnabled() {
    return mDrawCircles;
  }

  /**
   * sets the size (radius) of the circle shpaed value indicators, default
   * size = 4f
   *
   * @param size
   */
  public void setCircleSize(float size) {
    mCircleSize = Utils.convertDpToPixel(size);
  }

  /**
   * returns the circlesize
   *
   * @param size
   */
  public float getCircleSize(float size) {
    return Utils.convertPixelsToDp(mCircleSize);
  }

  /**
   * set if the chartdata should be drawn as a line or filled default = line /
   * default = false, disabling this will give up to 20% performance boost on
   * large datasets
   *
   * @param filled
   */
  public void setDrawFilled(boolean filled) {
    mDrawFilled = filled;
  }

  /**
   * returns true if filled drawing is enabled, false if not
   *
   * @return
   */
  public boolean isDrawFilledEnabled() {
    return mDrawFilled;
  }

  /**
   * set the line width of the chart (min = 0.5f, max = 10f); default 1f NOTE:
   * thinner line == better performance, thicker line == worse performance
   *
   * @param width
   */
  public void setLineWidth(float width) {

    if (width < 0.5f)
      width = 0.5f;
    if (width > 10.0f)
      width = 10.0f;
    mLineWidth = width;

    mRenderPaint.setStrokeWidth(width);
  }

  public void setValuePadding(int valuePadding) {
    mValuePadding = valuePadding;
  }

  public int getValuePadding() {
    return mValuePadding;
  }

  /**
   * returns the width of the drawn chart line
   *
   * @return
   */
  public float getLineWidth() {
    return mLineWidth;
  }

  /**
   * sets the color for the fill-paint
   *
   * @param color
   */
  public void setFillColor(int color) {
    mFilledPaint.setColor(color);
  }

  /**
   * set the width of the highlightning lines, default 3f
   *
   * @param width
   */
  public void setHighlightLineWidth(float width) {
    mHighlightWidth = width;
  }

  /**
   * returns the width of the highlightning line, default 3f
   *
   * @return
   */
  public float getHighlightLineWidth() {
    return mHighlightWidth;
  }

  /**
   * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
   *
   * @param lineLength the length of the line pieces
   * @param spaceLength the length of space inbetween the pieces
   * @param phase offset, in degrees (normally, use 0)
   */
  public void enableDashedLine(float lineLength, float spaceLength, float phase) {
    mRenderPaint.setPathEffect(new DashPathEffect(new float[] {
        lineLength, spaceLength
    }, phase));
  }

  /**
   * Disables the line to be drawn in dashed mode.
   */
  public void disableDashedLine() {
    mRenderPaint.setPathEffect(null);
  }

  /**
   * Returns true if the dashed-line effect is enabled, false if not.
   *
   * @return
   */
  public boolean isDashedLineEnabled() {
    return mRenderPaint.getPathEffect() == null ? false : true;
  }

  @Override
  public void setPaint(Paint p, int which) {
    switch (which) {
    case PAINT_CIRCLES_INNER:
      mCirclePaintInner = p;
      break;
    case PAINT_HIGHLIGHT_LINE:
      mHighlightPaint = p;
      break;
    case PAINT_FILLED:
      mFilledPaint = p;
      break;
    default:
      super.setPaint(p, which);
      break;
    }
  }

  @Override
  public Paint getPaint(int which) {
    switch (which) {
    case PAINT_CIRCLES_INNER:
      return mCirclePaintInner;
    case PAINT_HIGHLIGHT_LINE:
      return mHighlightPaint;
    case PAINT_FILLED:
      return mFilledPaint;
    default:
      return super.getPaint(which);
    }
  }
}
