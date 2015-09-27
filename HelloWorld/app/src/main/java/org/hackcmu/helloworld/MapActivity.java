package org.hackcmu.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ysharry on 9/26/15.
 */
public class MapActivity extends Activity {

    private Animation slideUp = null;
    private FrameLayout cloudFrame = null;
    private FrameLayout visitedLocation = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        cloudFrame = (FrameLayout)findViewById(R.id.cloud_frame);
        visitedLocation = (FrameLayout)findViewById(R.id.visited);
        AnimationSet set = new AnimationSet(true);
        // Using property animation
        Animation translateAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.7f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        Animation alphaAnim = new AlphaAnimation(0.6f,1f);
        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(translateAnim);
        animSet.addAnimation(alphaAnim);
        animSet.setInterpolator(new FastOutSlowInInterpolator());
        animSet.setDuration(1500);
        cloudFrame.startAnimation(animSet);

        Intent callingIntent = getIntent();
        int totalSteps = callingIntent.getIntExtra(getResources().getString(R.string.step_count_intent_label),0);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "karla_bold.ttf");
        TextView steps_left_text = (TextView) findViewById(R.id.steps_left);
        TextView prompts_text = (TextView) findViewById(R.id.prompts);
        steps_left_text.setTypeface(myTypeface);
        prompts_text.setTypeface(myTypeface);

        // Visited Location animation
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.twin);
        visitedLocation.startAnimation(myFadeInAnimation);

        int stepsLeft = MainActivity.mCityPlans.getStepsTilNextCity();
        steps_left_text.setText(String.valueOf(stepsLeft));

        int currentLevel = MainActivity.mCityPlans.getCurrentLevel();
        if(currentLevel >= 1) {
            ImageView norway_circle = (ImageView) findViewById(R.id.map_norway_circle);
            norway_circle.setVisibility(View.VISIBLE);
        }
        if(currentLevel >= 2) {
            ImageView london_circle = (ImageView) findViewById(R.id.map_london_circle);
            london_circle.setVisibility(View.VISIBLE);
        }


    }
}