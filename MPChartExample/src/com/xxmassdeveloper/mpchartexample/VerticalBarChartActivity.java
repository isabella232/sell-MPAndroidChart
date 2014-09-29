package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.charts.BarLineChartBase.BorderStyle;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.VerticalBarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.ChartData.LabelFormatter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class VerticalBarChartActivity extends DemoBase {

  private VerticalBarChart mChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_verticalbarchart);

    Resources r = getResources();

    mChart = (VerticalBarChart) findViewById(R.id.chart1);

    // enable the drawing of values
    mChart.setDrawYValues(true);

    mChart.setDescription("");

    // if more than 60 entries are displayed in the chart, no values will be
    // drawn
    mChart.setMaxVisibleValueCount(60);

    // sets the number of digits for values inside the chart
    mChart.setValueDigits(2);

    // disable 3D
    //mChart.set3DEnabled(false);
    mChart.setYLabelCount(5);

    // scaling can now only be done on x- and y-axis separately
    mChart.setPinchZoom(false);

    //mChart.setUnit(" €");

    // change the position of the y-labels
    YLabels yLabels = mChart.getYLabels();
    yLabels.setPosition(YLabelPosition.LEFT);

    XLabels xLabels = mChart.getXLabels();
    xLabels.setCenterXLabelText(true);

    mChart.setBackgroundColor(r.getColor(R.color.neue_reports_fill));
    mChart.setDrawGridBackground(false);
    mChart.setGridColor(r.getColor(R.color.neue_reports_grid));
    mChart.getPaint(Chart.PAINT_YLABEL).setColor(r.getColor(R.color.neue_reports_text));
    mChart.getPaint(Chart.PAINT_XLABEL).setColor(r.getColor(R.color.neue_reports_text));
    mChart.setValuePaintColor(r.getColor(R.color.neue_reports_text));
    mChart.setDrawLegend(false);
    mChart.setDrawYLabels(false);
    mChart.setBorderStyles(new BorderStyle[] { BorderStyle.LEFT });

    Paint mBorderPaint = new Paint();
    mBorderPaint.setColor(Color.WHITE);
    mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(1));
    mBorderPaint.setStyle(Style.STROKE);

    mChart.setPaint(mBorderPaint, Chart.PAINT_BORDER);

    prepareData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.bar, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
    case R.id.actionToggleValues: {
      if (mChart.isDrawYValuesEnabled())
        mChart.setDrawYValues(false);
      else
        mChart.setDrawYValues(true);
      mChart.invalidate();
      break;
    }

    case R.id.actionToggleHighlight: {
      if (mChart.isHighlightEnabled())
        mChart.setHighlightEnabled(false);
      else
        mChart.setHighlightEnabled(true);
      mChart.invalidate();
      break;
    }
    case R.id.actionTogglePinch: {
      if (mChart.isPinchZoomEnabled())
        mChart.setPinchZoom(false);
      else
        mChart.setPinchZoom(true);

      mChart.invalidate();
      break;
    }
    case R.id.actionToggleStartzero: {
      if (mChart.isStartAtZeroEnabled())
        mChart.setStartAtZero(false);
      else
        mChart.setStartAtZero(true);

      mChart.invalidate();
      break;
    }
    case R.id.actionToggleAdjustXLegend: {
      XLabels xLabels = mChart.getXLabels();

      if (xLabels.isAdjustXLabelsEnabled())
        xLabels.setAdjustXLabels(false);
      else
        xLabels.setAdjustXLabels(true);

      mChart.invalidate();
      break;
    }
    case R.id.actionToggleFilter: {

      Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 25);

      if (!mChart.isFilteringEnabled()) {
        mChart.enableFiltering(a);
      } else {
        mChart.disableFiltering();
      }
      mChart.invalidate();
      break;
    }
    case R.id.actionSave: {
      if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
        Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
      } else Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();
      break;
    }
    }
    return true;
  }

  private void prepareData() {

    ArrayList<Long> xVals = new ArrayList<Long>();
    for (int i = 0; i < 4; i++) {
      xVals.add((long) i);
    }

    ArrayList<Entry> yVals1 = new ArrayList<Entry>();

    for (int i = 0; i < 4; i++) {
      float val = 50 + i%2 * 50;
      yVals1.add(new Entry(val, i));
    }

    BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
    set1.getDrawingSpec().getBasicPaint().setColor(getResources().getColor(R.color.neue_reports_barColor));

    ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
    dataSets.add(set1);

    ChartData<BarDataSet> data = new ChartData<BarDataSet>(xVals, dataSets, new XFormatter());

    mChart.setData(data);
    mChart.invalidate();
  }

  private class XFormatter implements LabelFormatter {

    private HashMap<Long, String> values;

    XFormatter() {
      values = new HashMap<Long, String>();
      values.put(0l, "Missed deadline");
      values.put(1l, "We were to expensive");
      values.put(2l, "Chosen a competitor");
      values.put(3l, "Other");
    }

    @Override
    public String formatValue(long value) {
      return values.get(value);
    }
  }
}
