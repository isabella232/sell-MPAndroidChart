package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.MulticolorDrawingSpec;

import java.util.ArrayList;

public class BarDataSet extends DataSet {

  public BarDataSet(ArrayList<Entry> yVals, String label) {
    super(yVals, label);
  }

  @Override
  protected void initDrawingSpec() {
    mDrawingSpec = new MulticolorDrawingSpec();
  }

  @Override
  public MulticolorDrawingSpec getDrawingSpec() {
    return (MulticolorDrawingSpec) mDrawingSpec;
  }
}
