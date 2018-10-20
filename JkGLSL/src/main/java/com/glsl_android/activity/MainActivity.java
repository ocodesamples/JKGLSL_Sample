package com.glsl_android.activity;

import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.glsl_android.R;
import com.glsl_android.ui.PointsOrLines;
import com.glsl_android.ui.SixPointedStar;
import com.glsl_android.ui.TriangleView;


public class MainActivity extends AppCompatActivity {


    private GLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mGlSurfaceView = new PointsOrLines(this);
        mGlSurfaceView.requestFocus();
        mGlSurfaceView.setFocusable(true);
        setContentView(mGlSurfaceView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }
}
