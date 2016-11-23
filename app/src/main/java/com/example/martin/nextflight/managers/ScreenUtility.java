package com.example.martin.nextflight.managers;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by joaquin on 23/11/16.
 */

public class ScreenUtility {

    private float dpWidth;

    public ScreenUtility (Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = activity.getResources().getDisplayMetrics().density;
        dpWidth = outMetrics.widthPixels / density;
    }

    public float getWidth(){
        return dpWidth;
    }
}
