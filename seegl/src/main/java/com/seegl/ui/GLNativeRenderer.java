package com.seegl.ui;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLNativeRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame();
    }


    private native void onSurfaceCreated();
    private native void onSurfaceChanged(int width, int height);
    private native void onDrawFrame();
}
