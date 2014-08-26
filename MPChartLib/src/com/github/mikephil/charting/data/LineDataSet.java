package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.LineDrawingSpec;

import java.util.ArrayList;

public class LineDataSet extends DataSet {
  public LineDataSet(ArrayList<Entry> yVals, String label) {
    super(yVals, label);
  }

  @Override
  protected void initDrawingSpec() {
    mDrawingSpec = new LineDrawingSpec();
  }

  @Override
  public LineDrawingSpec getDrawingSpec() {
    return (LineDrawingSpec) mDrawingSpec;
  }
}
