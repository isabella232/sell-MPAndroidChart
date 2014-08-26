package com.xxmassdeveloper.mpchartexample;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

public class MyMarkerView extends MarkerView<LineDataSet> {

  private TextView tvContent;

  public MyMarkerView(Context context) {
    super(context, R.layout.custom_marker_view);

    tvContent = (TextView) findViewById(R.id.tvContent);
  }

  @Override
  public void onContentUpdate(int xIndex, float value, int dataSetIndex, ChartData<LineDataSet> data) {
    tvContent.setText(" " + Utils.formatNumber(value, 0, true));
  }

  @Override
  public void onFreeSpaceChanged(int left, int top, int right, int bottom) {
    float xAnchor = 0.5f, yAnchor = 1.0f;
    int halfWidth = getMeasuredWidth() / 2;
    int halfHeight = getMeasuredHeight() / 2;
    if (left < halfWidth) {
      xAnchor = 0f;
      yAnchor = 0.5f;
    } else if (right < halfWidth) {
      xAnchor = 1.0f;
      yAnchor = 0.5f;
    } else if (top < halfHeight) {
      xAnchor = 0.5f;
      yAnchor = 0f;
    }
    setAnchor(xAnchor, yAnchor);
    ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xAnchor, Animation.RELATIVE_TO_SELF, yAnchor);
    animation.setDuration(300);
    setInAnimation(animation);
  }
}
