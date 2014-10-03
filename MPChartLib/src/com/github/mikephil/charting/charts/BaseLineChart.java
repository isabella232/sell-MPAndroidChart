package com.github.mikephil.charting.charts;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BaseLineChart extends LineChart {

  private Integer mValueToHighlight = null;
  private Paint mSelectionCirclePaint;
  private Paint mYearXLabelTextPaint;
  private float mSelectionCircleSize = Utils.convertDpToPixel(10f);
  private float mSelectionRingWidth = 2f;
  private float mSelectionInternalCircleSize = mSelectionCircleSize - mSelectionRingWidth;
  private Paint mSelectionInternalCirclePaint;

  private DateFormat mYearFormatter = new SimpleDateFormat("yyyy");

  float mXLabelHeight;
  float mXYearLabelHeight;
  float mInterline = Utils.convertDpToPixel(5);
  float mTopMargin = Utils.convertDpToPixel(15);
  float mBottomMargin = Utils.convertDpToPixel(10);

  public BaseLineChart(Context context) {
    super(context);
  }

  public BaseLineChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BaseLineChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void init() {
    super.init();
    mSelectionInternalCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mSelectionInternalCirclePaint.setColor(Color.parseColor("#ffffff"));
    mListener = new NewLineTouchListener(this, mMatrixTouch);
    mYearXLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mYearXLabelTextPaint.setTextAlign(Align.CENTER);
  }

  public class NewLineTouchListener extends BarLineChartTouchListener {

    public NewLineTouchListener(BarLineChartBase chart, Matrix start) {
      super(chart, start);
    }

    @Override
    public void onFlingScroll() {
      markMiddlePoint();
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      boolean returnValue = super.onScroll(e1, e2, distanceX, distanceY);
      markMiddlePoint();
      return returnValue;
    }
  }

  @Override
  public void drawValues() {

    int index = getHighlight();
    int internalIndex = index * 2 + mValuePadding * 2;

    int valOffset = (int) (mSelectionCircleSize * 1.7f);

    if (!mDrawCircles)
      valOffset = valOffset / 2;

    ArrayList<LineDataSet> dataSets = mCurrentData.getDataSets();

    for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

      DataSet dataSet = dataSets.get(i);
      ArrayList<Entry> entries = dataSet.getYVals();

      float[] positions = generateTransformedValues(entries, 0f);
      float position = positions[internalIndex];

      float val = entries.get(index + mValuePadding).getVal();

      String label;
      if (mDrawValueXLabelsInChart) {
        label = mCurrentData.getXLabels().get(internalIndex / 2);
      } else {
        label = mDrawUnitInChart ? mUnit + mFormatValue.format(val) : mFormatValue.format(val);
      }

      position += Utils.calcTextWidth(mValuePaint, label) / 2 - mSelectionCircleSize;

      if (isOffContentRight(position)) {
        break;
      }

      float yPosition = positions[internalIndex + 1];
      if ((internalIndex - 1 >= 0 && internalIndex + 3 < positions.length &&
          position < yPosition && positions[internalIndex + 3] < yPosition &&
          yPosition + mOffsetBottom < getHeight() - mOffsetBottom) ||
          yPosition - (valOffset + Utils.calcTextHeight(mValuePaint, label)) < 0) {

        yPosition += valOffset + mValuePaint.getTextSize();
      } else {
        yPosition -= valOffset;
      }

      mDrawCanvas.drawText(label, position, yPosition, mValuePaint);
    }
  }

  @Override
  protected void drawXLabels() {

    float yPos = getHeight() - mBottomMargin - mXYearLabelHeight - mInterline;
    float yearHeight = getHeight() - mBottomMargin;

    // pre allocate to save performance (dont allocate in loop)
    float[] position = new float[] {
        0f, 0f
    };

    int labelPaintColor = mXLabelPaint.getColor();

    for (int i = mValuePadding; i < mCurrentData.getXValCount() - mValuePadding; i++) {
        position[0] = i;

        // center the text
        if (mXLabels.isCenterXLabelsEnabled())
          position[0] += 0.5f;

        transformValueToPixel(position);

        int valueToHighlight = getHighlight();
        if (i == valueToHighlight + mValuePadding) {
          mXLabelPaint.setColor(mSelectionCirclePaint.getColor());

          Date date = new Date(mCurrentData.getXVals().get(i));
          mDrawCanvas.drawText(mYearFormatter.format(date), position[0],
              yearHeight, mYearXLabelTextPaint);
        }

        mDrawCanvas.drawText(mCurrentData.getXLabels().get(i), position[0],
            yPos,
            mXLabelPaint);

        if (i == valueToHighlight + mValuePadding) {
          mXLabelPaint.setColor(labelPaintColor);
        }
    }
  }

  public void highlightValue(int index) {
    mValueToHighlight = index - mValuePadding;
  }

  public void setValueTextSize(float textSize) {
    mValuePaint.setTextSize(textSize);
  }

  private void markMiddlePoint() {

    // create an array of the touch-point
    float[] pts = new float[2];
    pts[0] = getWidth() / 2;
    pts[1] = 0;

    Matrix tmp = new Matrix();

    // invert all matrixes to convert back to the original value
    mMatrixOffset.invert(tmp);
    tmp.mapPoints(pts);

    mMatrixTouch.invert(tmp);
    tmp.mapPoints(pts);

    mMatrixValueToPx.invert(tmp);
    tmp.mapPoints(pts);

    double xTouchVal = pts[0];
    double base = Math.floor(xTouchVal);
    int xIndex = (int) base;

    if (xIndex == 0 || xIndex == mValuePadding - 1) {
      highlightValue(xIndex);
    }

    if (xTouchVal - base > 0.5) {
      xIndex = (int) base + 1;
    }

    float mHighlightFocusDelta = Utils.convertDpToPixel(15);
    float deltaInValue = pixelWidthToValue(mHighlightFocusDelta);

    if (Math.abs(xTouchVal - xIndex) > deltaInValue) {
      return;
    }

    highlightValue(xIndex);
  }

  @Override
  protected int getClosestDataSetIndex(ArrayList<SelInfo> valsAtIndex, float val) {
    return valsAtIndex.get(0).dataSetIndex;
  }

  /**
   * draws the given y values to the screen
   */
  @Override
  protected void drawData() {

    ArrayList<LineDataSet> dataSets = mCurrentData.getDataSets();

    for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

      DataSet dataSet = dataSets.get(i);
      ArrayList<Entry> entries = dataSet.getYVals();

      float[] valuePoints = generateTransformedValues(entries, 0f);

      Paint paint = mCurrentData.getDataSetByIndex(i).getDrawingSpec().getBasicPaint();

      for (int j = mValuePadding * 2; j < valuePoints.length - (mValuePadding * 2) - 2; j += 2) {

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

  public void setSelectionCirclePaint(Paint paint) {
    mSelectionCirclePaint = paint;
    mYearXLabelTextPaint.setColor(mSelectionCirclePaint.getColor());
  }

  @Override
  protected void drawAdditional() {
    super.drawAdditional();

    if (mDrawCircles) {

      Entry toHighlight = getDataCurrent().getDataSetByIndex(0).getEntryForXIndex(getHighlight() + mValuePadding);
      float[] entry = new float[] {toHighlight.getXIndex(), toHighlight.getVal()};
      transformValueToPixel(entry);
      mDrawCanvas.drawCircle(entry[0], entry[1], mSelectionCircleSize, mSelectionCirclePaint);
      mDrawCanvas.drawCircle(entry[0], entry[1], mSelectionInternalCircleSize, mSelectionInternalCirclePaint);
    }
  }

  private Integer getHighlight() {

    if (getDataCurrent() == null) {
      mValueToHighlight = 0;
    } else if (mValueToHighlight == null) {
      mValueToHighlight = getDataCurrent().getXValCount() - mValuePadding * 2 - 1;
    } else if (mValueToHighlight < 0) {
      mValueToHighlight = 0;
    } else if (mValueToHighlight + mValuePadding >= getDataCurrent().getXValCount()) {
      mValueToHighlight = getDataCurrent().getXValCount() - mValuePadding - 1;
    }

    return mValueToHighlight;
  }

  @Override
  protected void calculateOffsets() {

    Log.i(LOG_TAG, "Offsets calculated.");

    mAxisYLabelWidth = Utils.calcTextWidth(mYLabelPaint, ((int) (mYChartMin >= 0 ? mDeltaY : -mDeltaY)) + mUnit) + mAxisYLabelPadding;

    mAxisYLabelHeight = Utils.calcTextHeight(mYLabelPaint, "Q");
    mAxisXLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q") * 2f;

    mYearXLabelTextPaint.setTextSize(mXLabelPaint.getTextSize() * 0.6f);

    mXLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q");
    mXYearLabelHeight = Utils.calcTextHeight(mYearXLabelTextPaint, "2");

    mOffsetBottom = mTopMargin + mXYearLabelHeight + mInterline + mXLabelHeight + mBottomMargin + mSelectionCircleSize / 2;

    prepareContentRect();

    float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
    float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

    Matrix val = new Matrix();
    val.postTranslate(0, -mYChartMin);
    val.postScale(scaleX, -scaleY);

    mMatrixValueToPx.set(val);

    Matrix offset = new Matrix();
    offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

    mMatrixOffset.set(offset);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    refreshTouch(mMatrixTouch);
    centerViewPort(getHighlight() + mValuePadding, getHeight() / 2);
  }

  public void setSelectionRingWidth(float selectionRingWidth) {
    mSelectionRingWidth = selectionRingWidth;
    mSelectionCircleSize = mSelectionInternalCircleSize + mSelectionRingWidth;
  }

  public void setSelectionRingRadius(float radius) {

    mSelectionInternalCircleSize = radius;
    mSelectionCircleSize = mSelectionInternalCircleSize + mSelectionRingWidth;
  }

  @Override
  public void highlightValues(Highlight[] highs) {
    if (highs != null && highs.length > 0) {
      highlightValue(highs[0].getXIndex());
      invalidate();
    }
    super.highlightValues(highs);
  }
}
