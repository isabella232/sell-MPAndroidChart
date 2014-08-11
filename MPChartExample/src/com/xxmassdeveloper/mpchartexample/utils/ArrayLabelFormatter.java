package com.xxmassdeveloper.mpchartexample.utils;

import com.github.mikephil.charting.data.ChartData.LabelFormatter;

import java.util.ArrayList;

public class ArrayLabelFormatter implements LabelFormatter {
  private final String[] mLabels;
  private final ArrayList<Long> mValues = new ArrayList<Long>();

  public ArrayLabelFormatter(String[] labels) {
    mLabels = labels;
    for (int i = 0; i < labels.length; ++i) {
      mValues.add((long) i);
    }
  }

  @Override
  public String formatValue(long value) {
    return mLabels[(int) value];
  }

  public ArrayList<Long> getValues() {
    return mValues;
  }
}
