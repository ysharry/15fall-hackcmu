package org.hackcmu.helloworld;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

/**
 * Created by ysharry on 9/26/15.
 */
public class MapActivity extends Activity {

    private Animation slideUp = null;
    private FrameLayout cloudFrame = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        cloudFrame = (FrameLayout)findViewById(R.id.cloud_frame);
        AnimationSet set = new AnimationSet(true);
        // Using property animation
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(2000);
        cloudFrame.startAnimation(animation);
    }
}