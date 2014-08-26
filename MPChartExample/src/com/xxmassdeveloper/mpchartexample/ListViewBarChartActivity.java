package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.utils.ArrayLabelFormatter;
import com.xxmassdeveloper.mpchartexample.utils.Colors;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
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
public class ListViewBarChartActivity extends DemoBase {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_listview_chart);

    ListView lv = (ListView) findViewById(R.id.listView1);

    ArrayList<ChartData> list = new ArrayList<ChartData>();

    // 20 items
    for (int i = 0; i < 20; i++) {
      list.add(generateData(i + 1));
    }

    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
    lv.setAdapter(cda);
  }

  private class ChartDataAdapter extends ArrayAdapter<ChartData> {

    private Typeface mTf;

    public ChartDataAdapter(Context context, List<ChartData> objects) {
      super(context, 0, objects);
      mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      ChartData c = getItem(position);

      ViewHolder holder = null;

      if (convertView == null) {

        holder = new ViewHolder();

        convertView = LayoutInflater.from(getContext()).inflate(
            R.layout.list_item_barchart, null);
        holder.chart = (BarChart) convertView.findViewById(R.id.chart);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      // apply styling
      holder.chart.setYLabelCount(5);
      holder.chart.setBarSpace(20f);
      holder.chart.setYLabelTypeface(mTf);
      holder.chart.setXLabelTypeface(mTf);
      holder.chart.setValueTypeface(mTf);
      holder.chart.setDescription("");
      holder.chart.setDrawVerticalGrid(false);
      holder.chart.setDrawGridBackground(false);

      XLabels xl = holder.chart.getXLabels();
      xl.setCenterXLabelText(true);
      xl.setPosition(XLabelPosition.BOTTOM);

      // set data
      holder.chart.setData(c);

      // do not forget to refresh the chart
      holder.chart.invalidate();

      return convertView;
    }

    private class ViewHolder {

      BarChart chart;
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
    d.getDataSetPaint().setColor(getResources().getColor(Colors.VORDIPLOM_COLORS[0]));
    ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    dataSets.add(d);

    ChartData cd = new ChartData(formatter.getValues(), dataSets, formatter);
    return cd;
  }
}
