package com.xxmassdeveloper.mpchartexample.fragments;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.Legend;
import com.xxmassdeveloper.mpchartexample.R;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LineChartFrag extends SimpleFragment {

  public static Fragment newInstance() {
    return new LineChartFrag();
  }

  private LineChart mChart;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.frag_simple_line, container, false);

    mChart = (LineChart) v.findViewById(R.id.lineChart1);

    mChart.setDrawCircles(false);

    mChart.setDescription("");
    mChart.setDrawFilled(false);
    mChart.setDrawYValues(false);
    mChart.setDrawCircles(false);
    mChart.setHighlightIndicatorEnabled(false);
    mChart.setDrawBorder(false);
    mChart.setDrawGridBackground(false);
    mChart.setDrawVerticalGrid(false);
    mChart.setDrawXLabels(false);
    mChart.setDrawYValues(false);
    mChart.setStartAtZero(false);

    mChart.setYRange(-1.2f, 1.2f, false);

    mChart.setData(getComplexity());

    Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

    mChart.setYLabelTypeface(tf);

    Legend l = mChart.getLegend();
    l.setTypeface(tf);

    return v;
  }
}
