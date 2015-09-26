package org.hackcmu.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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
    private int[] x = {156,110,91,25,95,55};
    private int[] y = {14,90,200,288,258,322};
    private int[] dis = {0,5000,10000,15000,19500,24000};
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

    public MapView(Context context, AttributeSet attrs){
        super(context,attrs);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        locator = BitmapFactory.decodeResource(getResources(), R.drawable.ico_paw);

        int steps = 14000;
        startTime = getCurrentTime();

        while(i < 5 && steps >= dis[i]){
            i++;
        }
        i--;
        int exceed_amount = steps - dis[i];
        int xdiff, ydiff,interv;
        if(i < 4){
            xdiff = x[i+1]-x[i];
            ydiff = y[i+1]-y[i];
            interv = dis[i+1]-dis[i];
            a = x[i] + xdiff * exceed_amount/interv;
            b = y[i] + ydiff * exceed_amount/interv;
        }
        if(i % 2 == 1) i--;

        aXDiff = a - x[i];
        bYDiff = b - y[i];
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

        float progress = (getCurrentTime() - 1000 - startTime) / duration;

        if(progress < 0) progress = 0;

        float currentX = x[i] + aXDiff * progress;
        float currentY = y[i] + bYDiff * progress;
        Log.d("Canvas", "Progress: " + progress + "Curr X: " + currentX + ", Curr Y: " + currentY);
        canvas.drawBitmap(locator,currentX* xmultiplier + xoffset,currentY* ymultiplier + yoffset,null);

        if(progress < 1) {
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
