package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class ScatterDataSet extends DataSet {
  /**
   * Creates a new DataSet object with the given values it represents. Also, a
   * label that describes the DataSet can be specified. The label can also be
   * used to retrieve the DataSet from a ChartData object.
   *
   * @param yVals
   * @param label
   */
  public ScatterDataSet(ArrayList<Entry> yVals, String label) {
    super(yVals, label);
  }
}
