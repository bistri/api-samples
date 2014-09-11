package com.bistri.api_demo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bistri.api.GLRenderer;

public class CustomGLRenderer
        extends GLRenderer                  // Should extands com.bistri.api.GLRenderer
{
    private static String TAG = "CustomGLRenderer";


    public CustomGLRenderer(Context context) {
        super( context );

        Log.w(TAG, "CustomGLRenderer instantiated");
    }

    // TODO : Do your own modifications.

}
