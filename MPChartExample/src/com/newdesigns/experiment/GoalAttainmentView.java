package com.newdesigns.experiment;

import com.xxmassdeveloper.mpchartexample.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GoalAttainmentView extends View {

  private RectF mBounds;
  private Paint mArcPaint;
  private Paint mInternalCirclePaine;
  private Paint mGoalTextPaint;
  private Paint mLabelPaint;
  private Rect mTextBoundsRect;
  private int mGoalArcColor;
  private int mInnerCircleColor;
  private float mGoalArcWidth;
  private float mInnerCircleWidth;
  private float mGoalTextHeight;
  private float mLabelTextHeight;
  private float mLabelTextTopMargin;

  private float mOffset;

  private float mGoalAttainment = 0;
  private String mLabel;

  private float mGoalY;
  private float mLabelY;

  private float mInternalCircleCenterX;
  private float mInternalCircleCenterY;
  private float mInternalCircleCenterRadius;

  public GoalAttainmentView(Context context) {
    super(context);

    init();
  }

  public GoalAttainmentView(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.GoalAttainmentView,
        0, 0
    );

    try {

      mGoalArcColor = a.getColor(R.styleable.GoalAttainmentView_goalArcColor, 0xff000000);
      mInnerCircleColor = a.getColor(R.styleable.GoalAttainmentView_innerCircleColor, 0xff000000);
      mGoalArcWidth = a.getDimension(R.styleable.GoalAttainmentView_goalArcWidth, 0.0f);
      mInnerCircleWidth = a.getDimension(R.styleable.GoalAttainmentView_innerCircleWidth, 0.0f);
      mGoalTextHeight = a.getDimension(R.styleable.GoalAttainmentView_goalTextHeight, 0.0f);
      mLabelTextHeight = a.getDimension(R.styleable.GoalAttainmentView_labelTextHeight, 0.0f);
      mLabelTextTopMargin = a.getDimension(R.styleable.GoalAttainmentView_labelTextTopMargin, 0.0f);

    } finally {

      a.recycle();
    }

    init();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(widthMeasureSpec));
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    mBounds = new RectF(0, 0, w - mGoalArcWidth, h - mGoalArcWidth);
    mOffset = mGoalArcWidth / 2;
    mBounds.offset(mOffset, mOffset);

    mGoalTextPaint.getTextBounds("9", 0, 1, mTextBoundsRect);
    float goalTextHeight = mTextBoundsRect.height();
    mLabelPaint.getTextBounds("Q", 0, 1, mTextBoundsRect);
    float labelTextHeight = mTextBoundsRect.height();

    float overallHeight = goalTextHeight + mLabelTextTopMargin + labelTextHeight;
    mGoalY = mBounds.centerY() - (overallHeight / 2 - goalTextHeight);
    mLabelY = mGoalY + mLabelTextTopMargin + labelTextHeight;

    mInternalCircleCenterX = mBounds.centerX() + mOffset - mGoalArcWidth / 2;
    mInternalCircleCenterY = mBounds.centerY() + mOffset - mGoalArcWidth / 2;
    mInternalCircleCenterRadius = mBounds.height() / 2 - mOffset - mInnerCircleWidth / 2;
  }

  private void init() {

    mTextBoundsRect = new Rect();

    mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mArcPaint.setStyle(Style.STROKE);
    mArcPaint.setColor(mGoalArcColor);
    mArcPaint.setStrokeWidth(mGoalArcWidth);

    mInternalCirclePaine = new Paint(Paint.ANTI_ALIAS_FLAG);
    mInternalCirclePaine.setColor(mInnerCircleColor);
    mInternalCirclePaine.setStyle(Style.STROKE);
    mInternalCirclePaine.setStrokeWidth(mInnerCircleWidth);

    mGoalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mGoalTextPaint.setColor(mGoalArcColor);
    mGoalTextPaint.setTextSize(mGoalTextHeight);
    mGoalTextPaint.setTextAlign(Align.CENTER);

    mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLabelPaint.setColor(mInnerCircleColor);
    mLabelPaint.setTextSize(mLabelTextHeight);
    mLabelPaint.setTextAlign(Align.CENTER);
  }

  private int measureHeight(int widthMeasureSpec) {

    int result = 500;

    int specMode = MeasureSpec.getMode(widthMeasureSpec);
    int specSize = MeasureSpec.getSize(widthMeasureSpec);

    if (specMode == MeasureSpec.AT_MOST) {
      result = specSize;
    } else if (specMode == MeasureSpec.EXACTLY){
      result = specSize;
    }

    return result;
  }

  private int measureWidth(int widthMeasureSpec) {

    int result = 500;

    int specMode = MeasureSpec.getMode(widthMeasureSpec);
    int specSize = MeasureSpec.getSize(widthMeasureSpec);

    if (specMode == MeasureSpec.AT_MOST) {
      result = specSize;
    } else if (specMode == MeasureSpec.EXACTLY){
      result = specSize;
    }

    return result;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    String goalAttainmentText = String.format("%d%%", (int) mGoalAttainment);

    canvas.drawText(goalAttainmentText, mBounds.centerX(), mGoalY, mGoalTextPaint);
    canvas.drawText(mLabel, mBounds.centerX(), mLabelY, mLabelPaint);

    canvas.save();

    canvas.rotate(270, mBounds.centerX(), mBounds.centerY());

    canvas.drawCircle(mInternalCircleCenterX, mInternalCircleCenterY, mInternalCircleCenterRadius, mInternalCirclePaine);

    float sweepAngle = 360 * mGoalAttainment / 100;
    canvas.drawArc(mBounds, 0, sweepAngle, false, mArcPaint);

    canvas.restore();
  }

  public void setLabel(String label) {

    mLabel = label;
  }

  public void setGoalAttainment(float attainment) {

    mGoalAttainment = attainment;
    invalidate();
  }
}
