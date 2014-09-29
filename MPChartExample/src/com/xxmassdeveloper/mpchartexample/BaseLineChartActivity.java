package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.BaseLineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.ChartData.LabelFormatter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BaseLineChartActivity extends DemoBase implements OnChartValueSelectedListener {

  private static float X_VALUES_DISTANCE_DP = 60;

  private float xValuesDistance;

  private int mPadding;
  private BaseLineChart mChart;
  private boolean mListVisible = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Utils.init(getResources());
    xValuesDistance = Utils.convertDpToPixel(X_VALUES_DISTANCE_DP);
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int maxValuesPerPage = (int)Math.floor(size.x / xValuesDistance);
    if (maxValuesPerPage % 2 != 0) {
      maxValuesPerPage--;
    }
    mPadding = maxValuesPerPage / 2;

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_new_design_chart);

    final Resources r = getResources();

    mChart = (BaseLineChart) findViewById(R.id.chart1);

    mChart.setValuePadding(mPadding);
    mChart.setUnit("$");
    mChart.setDrawUnitsInChart(true);
    mChart.setOffsets(0, 20, 0, 0);
    mChart.setStartAtZero(false);
    mChart.setHighlightEnabled(false);
    mChart.setHighlightIndicatorEnabled(false);
    mChart.setOnChartValueSelectedListener(this);
    mChart.setValuePaintColor(Color.parseColor("#83e5d7"));
    mChart.setDrawMarkerViews(false);
    mChart.setCircleSize(Utils.convertDpToPixel(1.5f));
    mChart.setMaxScaleY(1.0f);
    mChart.getXLabels().setPosition(XLabelPosition.BOTTOM);
    mChart.getPaint(Chart.PAINT_XLABEL).setColor(Color.parseColor("#c7c7cc"));
    mChart.getPaint(Chart.PAINT_XLABEL).setTextSize(Utils.convertDpToPixel(15));
    mChart.setDrawYLabels(false);
    mChart.setDrawGridBackground(false);
    mChart.setBackgroundColor(Color.parseColor("#ffffff"));
    mChart.setDrawVerticalGrid(false);
    mChart.setDrawHorizontalGrid(false);
    mChart.setDrawBorder(false);
    mChart.getYLabels().setPosition(YLabelPosition.RIGHT);
    mChart.setValueTextSize(Utils.convertDpToPixel(20));
    mChart.setDrawLegend(false);
    mChart.setPinchZoom(false);
    Paint selectionCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    selectionCirclePaint.setColor(Color.parseColor("#83e5d7"));
    mChart.setSelectionCirclePaint(selectionCirclePaint);

    setData(17, 100, 100000);

    mChart.zoom(mChart.getDataCurrent().getXValCount() / maxValuesPerPage, 1.0f, mChart.getWidth() / 2, mChart.getHeight() / 2);
    mChart.invalidate();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.neue, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
    case R.id.actionToggleSize: {
      final int initialHeight = mChart.getMeasuredHeight();
      final int targetHeight = mListVisible ? ((View) mChart.getParent()).getMeasuredHeight() : mChart.getMeasuredHeight() * 5 / 6;
      final int delta = initialHeight - targetHeight;

      Animation anim = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
          mChart.getLayoutParams().height = initialHeight - (int) (interpolatedTime * delta);
          mChart.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
          return true;
        }
      };
      anim.setDuration(500);
      mChart.startAnimation(anim);
      mListVisible = !mListVisible;
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

  private LineDataSet createSet(String name, int count, float range, float rangeOffset, boolean dashed) {
    Resources r = getResources();
    ArrayList<Entry> yVals = new ArrayList<Entry>();

    for (int i = 0; i < count; i++) {
      float mult = (range + 1);
      float val = rangeOffset + (float) (Math.random() * mult);
      yVals.add(new Entry(val, i));
    }

    // create a dataset and give it a type
    LineDataSet set = new LineDataSet(yVals, name);
    Paint paint = set.getDrawingSpec().getBasicPaint();
    paint.setColor(Color.parseColor("#c7c7cc"));
    paint.setStrokeWidth(Utils.convertDpToPixel(1));
    if (dashed) {
      paint.setPathEffect(new DashPathEffect(new float[] { 3, 3 }, 0));
    }
    Point size = new Point();
    getWindowManager().getDefaultDisplay().getSize(size);
    set.getDrawingSpec().getFillPaint().setShader(new LinearGradient(0, 0, 0, size.y, r.getColor(R.color.neue_gradient_start), r.getColor(R.color.neue_gradient_end), TileMode.CLAMP));
    set.getDrawingSpec().getDataPointInnerCirclePaint().setColor(Color.parseColor("#c7c7cc"));
    return set;
  }

  private void setData(int count, float range, float rangeOffset) {
    ArrayList<Long> xVals = new ArrayList<Long>();
    long ts = System.currentTimeMillis();

    for (int i = 0; i < count; i++) {
      xVals.add(ts);
      ts += TimeUnit.DAYS.toMillis(2);
    }

    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
    dataSets.add(createSet("Data 1", count, range, rangeOffset, false));

    // create a data object with the datasets
    ChartData<LineDataSet> data = new ChartData<LineDataSet>(xVals, dataSets, new LabelFormatter() {
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.US);

      @Override
      public String formatValue(long value) {
        return sdf.format(new Date(value)).toUpperCase();
      }
    }, mPadding);

    // set data
    mChart.setData(data);
  }
}
