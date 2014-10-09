package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.MulticolorDrawingSpec;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Chart that draws bars.
 *
 * @author Philipp Jahoda
 */
public class BarChart extends BarLineChartBase<BarDataSet> {

  /**
   * space indicator between the bars 0.1f == 10 %
   */
  private float mBarSpace = 0.15f;

  /**
   * indicates the angle of the 3d effect
   */
  private float mSkew = 0.3f;

  /**
   * indicates how much the 3d effect goes back
   */
  private float mDepth = 0.3f;

  /**
   * flag the enables or disables 3d bars
   */
  private boolean m3DEnabled = false;

  /**
   * flag that enables or disables the highlighting arrow
   */
  private boolean mDrawHighlightArrow = false;

  public BarChart(Context context) {
    super(context);
  }

  public BarChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BarChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void init() {
    super.init();

    mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mHighlightPaint.setStyle(Paint.Style.FILL);
    mHighlightPaint.setColor(Color.rgb(0, 0, 0));
    // set alpha after color
    mHighlightPaint.setAlpha(120);
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
  protected void drawHighlights() {

    // if there are values to highlight and highlighnting is enabled, do it
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
          float left = index + mBarSpace / 2f;
          float right = index + 1f - mBarSpace / 2f;
          float top = y >= 0 ? y : 0;
          float bottom = y <= 0 ? y : 0;

          RectF highlight = new RectF(left, top, right, bottom);
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

  private RectF mBarRect = new RectF();

  @Override
  protected void drawData() {

    ArrayList<Path> topPaths = new ArrayList<Path>();
    ArrayList<Path> sidePaths = new ArrayList<Path>();

    ArrayList<BarDataSet> dataSets = mCurrentData.getDataSets();

    // preparations for 3D bars
    if (m3DEnabled) {

      float[] pts = new float[] {
          0f, 0f, 1f, 0f
      };

      // calculate the depth depending on scale

      transformValueToPixel(pts);

      pts[3] = pts[2] - pts[0];
      pts[2] = 0f;
      pts[1] = 0f;
      pts[0] = 0f;

      Matrix invert = new Matrix();

      mMatrixOffset.invert(invert);
      invert.mapPoints(pts);

      mMatrixTouch.invert(invert);
      invert.mapPoints(pts);

      mMatrixValueToPx.invert(invert);
      invert.mapPoints(pts);

      float depth = Math.abs(pts[3] - pts[1]) * mDepth;

      for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

        DataSet dataSet = dataSets.get(i);
        ArrayList<Entry> series = dataSet.getYVals();

        for (int j = 0; j < series.size(); j++) {

          float x = series.get(j).getXIndex();
          float y = series.get(j).getVal();
          float left = x + mBarSpace / 2f;
          float right = x + 1f - mBarSpace / 2f;
          float top = y >= 0 ? y : 0;

          // create the 3D effect paths for the top and side
          Path topPath = new Path();
          topPath.moveTo(left, top);
          topPath.lineTo(left + mSkew, top + depth);
          topPath.lineTo(right + mSkew, top + depth);
          topPath.lineTo(right, top);

          topPaths.add(topPath);

          Path sidePath = new Path();
          sidePath.moveTo(right, top);
          sidePath.lineTo(right + mSkew, top + depth);
          sidePath.lineTo(right + mSkew, depth);
          sidePath.lineTo(right, 0);

          sidePaths.add(sidePath);
        }
      }

      transformPaths(topPaths);
      transformPaths(sidePaths);
    }

    int cnt = 0;

    // 2D drawing
    for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {
      BarDataSet dataSet = dataSets.get(i);
      Paint paint = dataSet.getDrawingSpec().getBasicPaint();
      ArrayList<Entry> series = dataSet.getYVals();

      // do the drawing
      for (int j = 0; j < dataSet.getEntryCount(); j++) {

        int x = series.get(j).getXIndex();
        float y = series.get(j).getVal();
        float left = x + mBarSpace / 2f;
        float right = x + 1f - mBarSpace / 2f;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        mBarRect.set(left, top, right, bottom);

        transformRect(mBarRect);

        // avoid drawing outofbounds values
        if (isOffContentRight(mBarRect.left))
          break;

        if (isOffContentLeft(mBarRect.right)) {
          cnt++;
          continue;
        }

        int originalColor = paint.getColor();

        if (dataSet.getDrawingSpec().hasMultipleColors()) {
          paint.setColor(dataSet.getDrawingSpec().getColor(j));
        }

        mDrawCanvas.drawRect(mBarRect, paint);

        // 3D drawing
        if (m3DEnabled && dataSet.getDrawingSpec().hasMultipleColors()) {
          paint.setColor(dataSet.getDrawingSpec().getTopColor(j));
          mDrawCanvas.drawPath(topPaths.get(cnt), paint);

          paint.setColor(dataSet.getDrawingSpec().getSideColor(j));
          mDrawCanvas.drawPath(sidePaths.get(cnt), paint);
        }

        paint.setColor(originalColor);

        cnt++;
      }
    }
  }

  @Override
  protected void drawValues() {

    // if values are drawn
    if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

      ArrayList<BarDataSet> dataSets = mCurrentData.getDataSets();

      for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

        DataSet dataSet = dataSets.get(i);
        ArrayList<Entry> series = dataSet.getYVals();

        float[] valuePoints = generateTransformedValues(series, 0.5f);

        for (int j = 0; j < valuePoints.length; j += 2) {

          if (isOffContentRight(valuePoints[j]))
            break;

          if (isOffContentLeft(valuePoints[j]))
            continue;

          float val = series.get(j / 2).getVal();

          if (mDrawUnitInChart) {

            mDrawCanvas.drawText(mValueFormat.format(val) + mUnit, valuePoints[j],
                valuePoints[j + 1] - 12,
                mValuePaint);
          } else {

            mDrawCanvas.drawText(mValueFormat.format(val), valuePoints[j],
                valuePoints[j + 1] - 12,
                mValuePaint);
          }
        }
      }
    }
  }

  /**
   * sets the skew (default 0.3f), the skew indicates how much the 3D effect
   * of the chart is turned to the right
   *
   * @param skew
   */
  public void setSkew(float skew) {
    this.mSkew = skew;
  }

  /**
   * returns the skew value that indicates how much the 3D effect is turned to
   * the right
   *
   * @return
   */
  public float getSkew() {
    return mSkew;
  }

  /**
   * set the depth of the chart (default 0.3f), the depth indicates how much
   * the 3D effect of the chart goes back
   *
   * @param depth
   */
  public void setDepth(float depth) {
    this.mDepth = depth;
  }

  /**
   * returhs the depth, which indicates how much the 3D effect goes back
   *
   * @return
   */
  public float getDepth() {
    return mDepth;
  }

  /**
   * returns the space between bars in percent of the whole width of one value
   *
   * @return
   */
  public float getBarSpace() {
    return mBarSpace * 100f;
  }

  /**
   * sets the space between the bars in percent of the total bar width
   *
   * @param percent
   */
  public void setBarSpace(float percent) {
    mBarSpace = percent / 100f;
  }

  /**
   * if enabled, chart will be drawn in 3d
   *
   * @param enabled
   */
  public void set3DEnabled(boolean enabled) {
    this.m3DEnabled = enabled;
  }

  /**
   * returns true if 3d bars is enabled, false if not
   *
   * @return
   */
  public boolean is3DEnabled() {
    return m3DEnabled;
  }

  /**
   * set this to true to draw the highlightning arrow
   *
   * @param enabled
   */
  public void setDrawHighlightArrow(boolean enabled) {
    mDrawHighlightArrow = enabled;
  }

  /**
   * returns true if drawing the highlighting arrow is enabled, false if not
   *
   * @return
   */
  public boolean isDrawHighlightArrowEnabled() {
    return mDrawHighlightArrow;
  }

  @Override
  public void setPaint(Paint p, int which) {
    super.setPaint(p, which);

    switch (which) {
    case PAINT_HIGHLIGHT_BAR:
      mHighlightPaint = p;
      break;
    }
  }

  @Override
  public Paint getPaint(int which) {
    super.getPaint(which);

    switch (which) {
    case PAINT_HIGHLIGHT_BAR:
      return mHighlightPaint;
    }

    return null;
  }

  @Override
  protected BarDataSet createDataSet(ArrayList<Entry> approximated, String label) {
    return new BarDataSet(approximated, label);
  }

  @Override
  protected void drawAdditional() {
  }

  public void prepareLegend() {
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<Integer> colors = new ArrayList<Integer>();

    for (int i = 0; i < mOriginalData.getDataSetCount(); i++) {
      BarDataSet dataSet = mOriginalData.getDataSetByIndex(i);
      MulticolorDrawingSpec spec = dataSet.getDrawingSpec();
      if (spec.hasMultipleColors()) {
        int entriesCount = mOriginalData.getDataSetByIndex(i).getEntryCount();

        for (int j = 0; j < spec.getColorsCount() && j < entriesCount; j++) {
          if (j < spec.getColorsCount() - 1 && j < entriesCount - 1) {
            // if multiple colors are set for a DataSet, group them
            labels.add(null);
          } else {
            // add label to the last entry
            String label = mOriginalData.getDataSetByIndex(i).getLabel();
            labels.add(label);
          }

          colors.add(spec.getColor(j));
        }
      } else {
        labels.add(dataSet.getLabel());
        colors.add(spec.getBasicPaint().getColor());
      }
    }

    Legend l = new Legend(colors, labels);

    if (mLegend != null) {
      l.apply(mLegend);
    }

    mLegend = l;
  }
}
