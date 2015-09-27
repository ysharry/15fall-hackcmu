package org.hackcmu.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ysharry on 9/26/15.
 */
public class MapView extends View {

    private float duration = 1500;

    private Bitmap bmp;
    private Bitmap locator;
    private int[] x = {156,110,83,55,25,60,95,75,55};
    private int[] y = {14,90,200,240,288,273,258,290,322};
    private int[] dis = {0,5000,10000,12500,15000,17250,19500,21750,24000};
    private float xmultiplier = 4f;
    private float ymultiplier = 4f;
    private float xoffset = -54f;
    private float yoffset = 54f;
    private int a = x[0];
    private int b = y[0];
    private int i = 0;
    private int aXDiff;
    private int bYDiff;
    private long startTime;
    private boolean passedMidpoint = true;
    private boolean needToWait = true;

    public MapView(Context context, AttributeSet attrs){
        super(context,attrs);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        if(MainActivity.mCityPlans.getCurrentLevel() >= 1) {
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map_london);
        }
        locator = BitmapFactory.decodeResource(getResources(), R.drawable.ico_paw);

        int steps = MainActivity.mCityPlans.getCurrentSteps();
        startTime = getCurrentTime();

        while(i < 6 && steps >= dis[i]){
            i++;
        }
        i--;
        int exceed_amount = steps - dis[i];
        int xdiff, ydiff,interv;

        if(i < 6){
            if(i % 2 == 1) {
                passedMidpoint = false;
            }
            xdiff = x[i+1]-x[i];
            ydiff = y[i+1]-y[i];
            interv = dis[i+1]-dis[i];
            a = x[i] + xdiff * exceed_amount/interv;
            b = y[i] + ydiff * exceed_amount/interv;
        }




    }
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        int h = canvasHeight;
        int w = canvasWidth;

        if(canvasHeight / canvasWidth > 16/9) {
            w = canvasHeight * 9 / 16;
        } else {
            h = canvasWidth * 16 / 9;
        }

//        Log.d("Canvas", "height: " + h + ", width: " + w);



        Rect bg = new Rect(0,0,w,h);
        canvas.drawBitmap(bmp, null, bg, null);

        if(passedMidpoint) {
            aXDiff = a - x[i];
            bYDiff = b - y[i];
            float progress = (getCurrentTime() - 1000 - startTime) / duration;

            if(!needToWait) {
                progress = (getCurrentTime() - startTime) / duration;
            }

            if (progress < 0) progress = 0;

            float currentX = x[i] + aXDiff * progress;
            float currentY = y[i] + bYDiff * progress;
            //Log.d("Canvas", "Progress: " + progress + "Curr X: " + currentX + ", Curr Y: " + currentY);
            canvas.drawBitmap(locator, currentX * xmultiplier + xoffset, currentY * ymultiplier + yoffset, null);

            if (progress < 1) {
                invalidate();
            }
        } else {
            aXDiff = x[i] - x[i-1];
            bYDiff = y[i] - y[i-1];
            float progress = (getCurrentTime() - 1000 - startTime) / duration;
            if(progress < 0) progress = 0;
            float currentX = x[i-1] + aXDiff * progress;
            float currentY = y[i-1] + bYDiff * progress;
            canvas.drawBitmap(locator, currentX * xmultiplier + xoffset, currentY * ymultiplier + yoffset, null);
            if(progress >= 1){
                passedMidpoint = true;
                startTime = getCurrentTime();
                needToWait = false;
            }
            invalidate();
        }
    }

    private long getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        return cal.getTimeInMillis();
    }


}
