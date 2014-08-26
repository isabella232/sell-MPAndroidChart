package com.xxmassdeveloper.mpchartexample.fragments;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.FileUtils;
import com.xxmassdeveloper.mpchartexample.utils.ArrayLabelFormatter;
import com.xxmassdeveloper.mpchartexample.utils.Colors;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {

  /**
   * generates some random data
   *
   * @return
   */
  protected ChartData generateData(int dataSets, float range, int count) {

    ArrayList<DataSet> sets = new ArrayList<DataSet>();

    for (int i = 0; i < dataSets; i++) {

      ArrayList<Entry> entries = new ArrayList<Entry>();

      for (int j = 0; j < count; j++) {
        entries.add(new Entry((float) (Math.random() * range) + range / 4, j));
      }

      DataSet ds = new DataSet(entries, getLabel(i));
      ds.getDataSetPaint().setColor(getResources().getColor(Colors.VORDIPLOM_COLORS[i % Colors.VORDIPLOM_COLORS.length]));
      sets.add(ds);
    }

    ChartData d = new ChartData(ChartData.generateXVals(0, count), sets);
    return d;
  }

  /**
   * generates less data (1 DataSet, 4 values)
   *
   * @return
   */
  protected ChartData generateLessData() {

    int count = 4;

    ArrayList<Entry> entries1 = new ArrayList<Entry>();
    ArrayList<String> xVals = new ArrayList<String>();

    xVals.add("Quarter 1");
    xVals.add("Quarter 2");
    xVals.add("Quarter 3");
    xVals.add("Quarter 4");

    ArrayLabelFormatter formatter = new ArrayLabelFormatter(new String[] { "Quarter 1", "Quarter 2", "Quarter 3", "Quarter 4" });

    for (int i = 0; i < count; i++) {
      xVals.add("entry" + (i + 1));

      entries1.add(new Entry((float) (Math.random() * 60) + 40, i));
    }

    DataSet ds1 = new DataSet(entries1, "Quarterly Revenues 2014");

    ChartData d = new ChartData(formatter.getValues(), ds1, formatter);
    return d;
  }

  protected ChartData getComplexity() {
    ArrayList<DataSet> sets = new ArrayList<DataSet>();

    // load DataSets from textfiles in assets folder
    sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "sine.txt"));
    sets.add(FileUtils.dataSetFromAssets(getActivity().getAssets(), "cosine.txt"));

    int i = 0;
    for (DataSet set : sets) {
      set.getDataSetPaint().setColor(getResources().getColor(Colors.VORDIPLOM_COLORS[i % Colors.VORDIPLOM_COLORS.length]));
      i++;
    }

    int max = Math.max(sets.get(0).getEntryCount(), sets.get(1).getEntryCount());

    ChartData d = new ChartData(ChartData.generateXVals(0, max), sets);
    return d;
  }

  private String[] mLabels = new String[] { "Company A", "Company B", "Company C", "Company D", "Company E", "Company F" };

  private String getLabel(int i) {
    return mLabels[i];
  }
}
