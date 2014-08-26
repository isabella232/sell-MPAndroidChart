package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.MulticolorDrawingSpec;
import com.xxmassdeveloper.mpchartexample.listviewitems.BarChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.PieChartItem;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.utils.ArrayLabelFormatter;
import com.xxmassdeveloper.mpchartexample.utils.Colors;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 *
 * @author Philipp Jahoda
 */
public class ListViewMultiChartActivity extends DemoBase {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_listview_chart);

    ListView lv = (ListView) findViewById(R.id.listView1);

    ArrayList<ChartItem> list = new ArrayList<ChartItem>();

    // 30 items
    for (int i = 0; i < 30; i++) {

      if (i % 3 == 0) {
        list.add(new LineChartItem(generateLineChartData(i, i + 1), getApplicationContext()));
      } else if (i % 3 == 1) {
        list.add(new BarChartItem(generateBarChartData(i, i + 1), getApplicationContext()));
      } else if (i % 3 == 2) {
        list.add(new PieChartItem(generatePieChartData(i, i + 1), getApplicationContext()));
      }
    }

    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
    lv.setAdapter(cda);
  }

  /**
   * adapter that supports 3 different item types
   */
  private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

    public ChartDataAdapter(Context context, List<ChartItem> objects) {
      super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      return getItem(position).getView(position, convertView, getContext());
    }

    @Override
    public int getItemViewType(int position) {
      // return the views type
      return getItem(position).getItemType();
    }

    @Override
    public int getViewTypeCount() {
      return 3; // we have 3 different item-types
    }
  }

  private ChartData<LineDataSet> generateLineChartData(int idx, int cnt) {

    ArrayList<Entry> entries = new ArrayList<Entry>();

    for (int i = 0; i < 12; i++) {
      entries.add(new Entry((int) (Math.random() * 70) + 30, i));
    }

    ArrayLabelFormatter formatter = new ArrayLabelFormatter(new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" });

    LineDataSet d = new LineDataSet(entries, "New DataSet " + cnt);
    d.getDrawingSpec().getBasicPaint().setColor(getResources().getColor(Colors.FRESH_COLORS[idx % Colors.FRESH_COLORS.length]));

    ChartData<LineDataSet> cd = new ChartData<LineDataSet>(formatter.getValues(), d, formatter);
    return cd;
  }

  private ChartData<BarDataSet> generateBarChartData(int idx, int cnt) {

    ArrayList<Entry> entries = new ArrayList<Entry>();

    for (int i = 0; i < 12; i++) {
      entries.add(new Entry((int) (Math.random() * 70) + 30, i));
    }

    ArrayLabelFormatter formatter = new ArrayLabelFormatter(new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" });

    BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
    d.getDrawingSpec().setColors(MulticolorDrawingSpec.fromResources(this, Colors.FRESH_COLORS));

    ChartData<BarDataSet> cd = new ChartData<BarDataSet>(formatter.getValues(), d, formatter);
    return cd;
  }

  private ChartData<PieDataSet> generatePieChartData(int idx, int cnt) {

    ArrayList<Entry> entries = new ArrayList<Entry>();

    for (int i = 0; i < 4; i++) {
      entries.add(new Entry((int) (Math.random() * 70) + 30, i));
    }

    PieDataSet d = new PieDataSet(entries, "New DataSet " + cnt);
    d.getDrawingSpec().setColors(MulticolorDrawingSpec.fromResources(this, Colors.FRESH_COLORS));

    ArrayLabelFormatter formatter = new ArrayLabelFormatter(new String[] { "1st Quarter", "2nd Quarter", "3rd Quarter", "4th Quarter" });
    ChartData<PieDataSet> cd = new ChartData<PieDataSet>(formatter.getValues(), d, formatter);
    return cd;
  }
}
