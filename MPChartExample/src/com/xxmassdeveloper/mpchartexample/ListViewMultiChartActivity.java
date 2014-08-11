package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.xxmassdeveloper.mpchartexample.listviewitems.BarChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.PieChartItem;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.utils.ArrayLabelFormatter;

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
        list.add(new LineChartItem(generateData(i + 1), getApplicationContext()));
      } else if (i % 3 == 1) {
        list.add(new BarChartItem(generateData(i + 1), getApplicationContext()));
      } else if (i % 3 == 2) {
        list.add(new PieChartItem(generatePieChartData(i + 1), getApplicationContext()));
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

  /**
   * generates a random ChartData object with just one DataSet
   *
   * @return
   */
  private ChartData generateData(int cnt) {

    ArrayList<Entry> entries = new ArrayList<Entry>();

    for (int i = 0; i < 12; i++) {
      entries.add(new Entry((int) (Math.random() * 70) + 30, i));
    }

    ArrayLabelFormatter formatter = new ArrayLabelFormatter(new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" });

    DataSet d = new DataSet(entries, "New DataSet " + cnt);

    ChartData cd = new ChartData(formatter.getValues(), d, formatter);
    return cd;
  }

  private ChartData generatePieChartData(int cnt) {

    ArrayList<Entry> entries = new ArrayList<Entry>();

    for (int i = 0; i < 4; i++) {
      entries.add(new Entry((int) (Math.random() * 70) + 30, i));
    }

    DataSet d = new DataSet(entries, "New DataSet " + cnt);

    ArrayLabelFormatter formatter = new ArrayLabelFormatter(new String[] { "1st Quarter", "2nd Quarter", "3rd Quarter", "4th Quarter" });
    ChartData cd = new ChartData(formatter.getValues(), d, formatter);
    return cd;
  }
}
