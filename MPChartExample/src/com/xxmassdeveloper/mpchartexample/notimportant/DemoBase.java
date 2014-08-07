package com.xxmassdeveloper.mpchartexample.notimportant;

import com.xxmassdeveloper.mpchartexample.R;

import android.support.v4.app.FragmentActivity;

public abstract class DemoBase extends FragmentActivity {

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
  }
}
