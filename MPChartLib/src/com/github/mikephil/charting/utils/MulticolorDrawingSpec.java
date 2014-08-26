package com.github.mikephil.charting.utils;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;

public class MulticolorDrawingSpec extends DrawingSpec {
  private ArrayList<Integer> mColors = new ArrayList<Integer>();

  // 3D colors
  private ArrayList<Integer> mTopColors = new ArrayList<Integer>();
  private ArrayList<Integer> mSideColors = new ArrayList<Integer>();

  public boolean hasMultipleColors() {
    return !mColors.isEmpty();
  }

  public int getColor(int idx) {
    return mColors.get(idx % mColors.size());
  }

  public int getTopColor(int idx) {
    return mTopColors.get(idx % mTopColors.size());
  }

  public int getSideColor(int idx) {
    return mSideColors.get(idx % mSideColors.size());
  }

  public int getColorsCount() {
    return mColors.size();
  }

  public void setColors(int... colors) {
    mColors.clear();
    for (int color : colors) {
      mColors.add(color);
    }
    calculate3DColors();
  }

  public void setColors(Collection<Integer> colors) {
    mColors.clear();
    mColors.addAll(colors);
  }

  public static int[] fromResources(Context ctx, int... resId) {
    int[] ret = new int[resId.length];
    for (int i = 0; i < resId.length; ++i) {
      ret[i] = ctx.getResources().getColor(resId[i]);
    }
    return ret;
  }

  private void calculate3DColors() {
    mTopColors.clear();
    mSideColors.clear();

    float[] hsv = new float[3];

    for (int color : mColors) {
      // extract the color
      Color.colorToHSV(color, hsv); // convert to hsv

      // make brighter
      hsv[1] = hsv[1] - 0.1f; // less saturation
      hsv[2] = hsv[2] + 0.1f; // more brightness

      // assign
      mTopColors.add(Color.HSVToColor(hsv));

      // convert
      Color.colorToHSV(color, hsv);

      // make darker
      hsv[1] = hsv[1] + 0.1f; // more saturation
      hsv[2] = hsv[2] - 0.1f; // less brightness

      mSideColors.add(Color.HSVToColor(hsv));
    }
  }
}
