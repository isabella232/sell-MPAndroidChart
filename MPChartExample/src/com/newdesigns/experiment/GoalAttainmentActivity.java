/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.newdesigns.experiment;

import com.xxmassdeveloper.mpchartexample.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class GoalAttainmentActivity extends Activity {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.new_reports_activity);
  }

  @Override
  protected void onResume() {
    super.onResume();

    final GoalAttainmentView goalAttainmentView = (GoalAttainmentView)findViewById(R.id.goalAttainment);
    goalAttainmentView.setLabel("AUG 2014");
    goalAttainmentView.setGoalAttainment(0);

    final float value = 75;

    final Animation a = new Animation() {
      @Override
      protected void applyTransformation(float interpolatedTime, Transformation t) {
        goalAttainmentView.setGoalAttainment(interpolatedTime * value);
      }

      @Override
      public boolean willChangeBounds() {
        return true;
      }
    };

    a.setDuration(500);
    Interpolator interpolator = new DecelerateInterpolator(2f);
    a.setInterpolator(interpolator);

    new Handler().postDelayed(new Runnable()
    {
      @Override
      public void run()
      {
        goalAttainmentView.startAnimation(a);
      }
    }, 500);


  }
}

