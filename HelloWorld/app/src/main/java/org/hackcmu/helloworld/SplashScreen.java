package org.hackcmu.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Billy on 9/26/2015.
 */
public class SplashScreen  extends Activity {

    int count = 0;
    private LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ll = (LinearLayout) findViewById(R.id.splash);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipImg();
            }
        });
    }

    private void flipImg() {
        if (count == 0) {
            ll.setBackground(getResources().getDrawable(R.drawable.splash_02));
        } else if(count == 1) {
            ll.setBackground(getResources().getDrawable(R.drawable.splash_03));
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        count++;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
