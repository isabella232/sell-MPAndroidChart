package com.xxmassdeveloper.mpchartexample.listviewitems;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;

import android.content.Context;
import android.view.View;

/**
 * baseclass of the chart-listview items
 *
 * @author philipp
 */
public abstract class ChartItem<T extends DataSet> {

  protected static final int TYPE_BARCHART = 0;
  protected static final int TYPE_LINECHART = 1;
  protected static final int TYPE_PIECHART = 2;

  protected ChartData<T> mChartData;

  public ChartItem(ChartData<T> cd) {
    this.mChartData = cd;
  }

  public abstract int getItemType();

  public abstract View getView(int position, View convertView, Context c);
}
