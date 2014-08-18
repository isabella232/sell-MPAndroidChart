package com.github.mikephil.charting.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

/**
 * View that can be displayed when selecting values in the chart. Extend this
 * class to provide custom layouts for your markers.
 *
 * @author Philipp Jahoda
 */
public abstract class MarkerView extends RelativeLayout {

  /**
   * draw offset on the x-axis
   */
  private float mXOffset, mYOffset;
  private float mXPosition, mYPosition;
  private Animation mInAnimation;
  private Animation mOutAnimation;

  /**
   * Constructor. Sets up the MarkerView with a custom layout resource.
   *
   * @param context
   * @param layoutResource the layout resource to use for the MarkerView
   */
  public MarkerView(Context context, int layoutResource) {
    super(context);
    setupLayoutResource(layoutResource);
    super.setVisibility(View.GONE);
  }

  public void setInAnimation(Animation inAnimation) {
    mInAnimation = inAnimation;
  }

  public void setOutAnimation(Animation outAnimation) {
    mOutAnimation = outAnimation;
  }

  /**
   * Sets the layout resource for a custom MarkerView.
   *
   * @param layoutResource
   */
  private void setupLayoutResource(int layoutResource) {
    View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this, true);
    inflated.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
  }

  public void setVisibility(int visibility) {
    if (getVisibility() != visibility) {
      super.setVisibility(visibility);
      View child = getChildAt(0);
      if (visibility == View.VISIBLE) {
        if (mInAnimation != null) {
          child.startAnimation(mInAnimation);
        }
      } else if (visibility == View.GONE) {
        if (mOutAnimation != null) {
          child.startAnimation(mOutAnimation);
        }
      }
    }
  }

  /**
   * this method enables a specified custom MarkerView to update it's content
   * everytime the MarkerView is redrawn
   *
   * @param xIndex the index on the x-axis
   * @param value the actual selected value
   * @param dataSetIndex the index of the DataSet the selected value is in
   */
  public abstract void refreshContent(int xIndex, float value, int dataSetIndex);

  /**
   * Set the position offset of the MarkerView. By default, the top left edge
   * of the MarkerView is drawn directly where the selected value is at. In
   * order to change that, offsets in pixels can be defined. Default offset is
   * zero (0f) on both axes. For offsets dependent on the MarkerViews width
   * and height, use getMeasuredWidth() / getMeasuredHeight().
   *
   * @param x
   * @param y
   */
  public void setOffsets(float x, float y) {
    this.mXOffset = x;
    this.mYOffset = y;
  }

  public void setPosition(float x, float y) {
    mXPosition = x;
    mYPosition = y;
    View child = getChildAt(0);
    int posX = (int) (mXPosition + mXOffset);
    int posY = (int) (mYPosition + mYOffset);
    layout(posX, posY, posX + child.getMeasuredWidth(), posY + child.getMeasuredHeight());
  }
}
