package org.hackcmu.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.jar.Attributes;

/**
 * Created by ysharry on 9/26/15.
 */
public class MapView extends View {

    Bitmap bmp;
    public MapView(Context context, AttributeSet attrs){
        super(context,attrs);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
    }
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(bmp, 0, 0, null);
    }


}
