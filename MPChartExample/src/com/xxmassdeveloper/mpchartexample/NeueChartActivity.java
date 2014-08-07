package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NeueChartActivity extends DemoBase implements OnChartValueSelectedListener {

  private LineChart mChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_neuechart);

    ColorTemplate ct = new ColorTemplate();
    ct.addDataSetColors(new int[] {
        R.color.neue_line
    }, this);

    final Resources r = getResources();

    mChart = (LineChart) findViewById(R.id.chart1);
    mChart.setOffsets(0, 0, 0, 0);
    mChart.setStartAtZero(false);
    mChart.setHighlightEnabled(false);
    mChart.setHighlightIndicatorEnabled(false);
    mChart.setOnChartValueSelectedListener(this);
    mChart.setValuePaintColor(r.getColor(R.color.neue_text));
    mChart.setValueTypeface(Typeface.DEFAULT_BOLD);
    mChart.setColorTemplate(ct);
    mChart.setLineWidth(3f);
    mChart.setCircleSize(5f);
    mChart.setTouchEnabled(true);
    mChart.setDragEnabled(true);
    mChart.setPinchZoom(true);
    mChart.setDrawFilled(true);
    mChart.getPaint(Chart.PAINT_FILLED).setShader(new LinearGradient(0, 0, 0, mChart.getMeasuredHeight() / 2, r.getColor(R.color.neue_gradient_start), r.getColor(R.color.neue_gradient_end), TileMode.CLAMP));
    mChart.setDrawXLabels(false);
    mChart.setDrawYLabels(true);
    mChart.setDrawAxisLabelsInChart(true);
    mChart.setDrawGridBackground(false);
    mChart.setBackgroundColor(r.getColor(R.color.neue_fill));
    mChart.setDrawVerticalGrid(false);
    mChart.setGridColor(r.getColor(R.color.neue_grid));
    mChart.setDrawBorder(false);
    mChart.setDrawValueXLabelsInChart(true);
    mChart.getYLabels().setPosition(YLabelPosition.RIGHT);
    mChart.getPaint(Chart.PAINT_YLABEL).setColor(r.getColor(R.color.neue_text));

    MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
    mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
    mChart.setMarkerView(mv);

    mChart.setHighlightIndicatorEnabled(false);
    setData(30, 10, 30);
    mChart.setDrawLegend(false);
    mChart.invalidate();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.line, menu);
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
    case R.id.actionToggleFilled: {
      if (mChart.isDrawFilledEnabled())
        mChart.setDrawFilled(false);
      else
        mChart.setDrawFilled(true);
      mChart.invalidate();
      break;
    }
    case R.id.actionToggleCircles: {
      if (mChart.isDrawCirclesEnabled())
        mChart.setDrawCircles(false);
      else
        mChart.setDrawCircles(true);
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
    case R.id.actionTogglePinch: {
      if (mChart.isPinchZoomEnabled())
        mChart.setPinchZoom(false);
      else
        mChart.setPinchZoom(true);

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

      // the angle of filtering is 35°
      Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 35);

      if (!mChart.isFilteringEnabled()) {
        mChart.enableFiltering(a);
      } else {
        mChart.disableFiltering();
      }
      mChart.invalidate();
      break;
    }
    case R.id.actionDashedLine: {
      if (!mChart.isDashedLineEnabled()) {
        mChart.enableDashedLine(10f, 5f, 0f);
      } else {
        mChart.disableDashedLine();
      }
      mChart.invalidate();
      break;
    }
    case R.id.actionSave: {
      if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
        Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
      } else Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();

      //                 mChart.saveToGallery("title"+System.currentTimeMillis())
      break;
    }
    }
    return true;
  }

  @Override
  public void onValuesSelected(Entry[] values, Highlight[] highlights) {
    Log.i("VALS SELECTED",
        "Value: " + values[0].getVal() + ", xIndex: " + highlights[0].getXIndex()
            + ", DataSet index: " + highlights[0].getDataSetIndex());
  }

  @Override
  public void onNothingSelected() {
    // TODO Auto-generated method stub

  }

  private void setData(int count, float range, float rangeOffset) {

    ArrayList<String> xVals = new ArrayList<String>();
    long ts = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.US);

    for (int i = 0; i < count; i++) {
      xVals.add(sdf.format(new Date(ts)).toUpperCase());
      ts += TimeUnit.DAYS.toMillis(2);
    }

    ArrayList<Entry> yVals = new ArrayList<Entry>();

    for (int i = 0; i < count; i++) {
      float mult = (range + 1);
      float val = rangeOffset + (float) (Math.random() * mult);
      yVals.add(new Entry(val, i));
    }

    // create a dataset and give it a type
    DataSet set1 = new DataSet(yVals, "DataSet 1");

    ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    dataSets.add(set1); // add the datasets

    // create a data object with the datasets
    ChartData data = new ChartData(xVals, dataSets);

    // set data
    mChart.setData(data);
  }
}
