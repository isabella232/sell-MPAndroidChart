package com.github.mikephil.charting.utils;

import android.content.Context;
import android.graphics.Color;

public class MulticolorDrawingSpec extends DrawingSpec {
  private int[] mColors = new int[] { Color.BLACK };

  public int getColor(int idx) {
    return mColors[idx % mColors.length];
  }

  public int getColorsCount() {
    return mColors.length;
  }

  public void setColors(int[] colors) {
    mColors = colors;
  }

  public static int[] fromResources(Context ctx, int... resId) {
    int[] ret = new int[resId.length];
    for (int i = 0; i < resId.length; ++i) {
      ret[i] = ctx.getResources().getColor(resId[i]);
    }
    return ret;
  }
}
