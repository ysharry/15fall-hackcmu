package org.hackcmu.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.jar.Attributes;

/**
 * Created by ysharry on 9/26/15.
 */
public class MapView extends View {

    private Bitmap bmp;
    private Bitmap locator;
    private int[] x = {156,91,25,95,55};
    private int[] y = {14,200,288,258,322};
    private int[] dis = {0,10000,5000,4500,4500};

    public MapView(Context context, AttributeSet attrs){
        super(context,attrs);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        locator = BitmapFactory.decodeByteArray(getResources(),R.drawable.);
    }
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(bmp, 0, 0, null);




    }


}
