package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.widget.TextView;

public class MyMarkerView extends MarkerView {

  private TextView tvContent;

  public MyMarkerView(Context context, int layoutResource) {
    super(context, layoutResource);

    tvContent = (TextView) findViewById(R.id.tvContent);
  }

  @Override
  public void refreshContent(int xIndex, float value, int dataSetIndex, ChartData data) {
    tvContent.setText(" " + Utils.formatNumber(value, 0, true));
  }
}
