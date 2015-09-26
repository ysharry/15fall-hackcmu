package org.hackcmu.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by ysharry on 9/26/15.
 */
public class MapView extends View {

    private Bitmap bmp;
    private Bitmap locator;
    private int[] x = {156,91,25,95,55};
    private int[] y = {14,200,288,258,322};
    private int[] dis = {0,10000,15000,19500,24000};
    private float xmultiplier = 4f;
    private float ymultiplier = 4f;
    private float xoffset = -54f;
    private float yoffset = 54f;

    public MapView(Context context, AttributeSet attrs){
        super(context,attrs);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        locator = BitmapFactory.decodeResource(getResources(), R.drawable.ico_paw);
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

        Log.d("Canvas","height: " + h + ", width: " + w);

        Rect bg = new Rect(0,0,w,h);
        canvas.drawBitmap(bmp, null, bg, null);

        /*int i = 0;
        while(i < 5 && steps >= dis[i]){
            i++;
        }
        i--;
        int exceed_amount = steps - dis[i];
        int xdiff, ydiff,a,b,interv;
        if(i < 4){
            xdiff = x[i+1]-x[i];
            ydiff = y[i+1]-y[i];
            interv = dis[i+1]-dis[i];
            a = x[i] + xdiff * exceed_amount/interv;
            b = y[i] + ydiff * exceed_amount/interv;
        }*/


        canvas.drawBitmap(locator,x[0]* xmultiplier + xoffset,y[0]* ymultiplier + yoffset,null);

    }


}
