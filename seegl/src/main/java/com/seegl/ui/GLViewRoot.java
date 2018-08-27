package com.seegl.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLViewRoot extends GLSurfaceView {

    public GLViewRoot(Context context) {
        this(context, null);
    }

    public GLViewRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        setRenderer(new GLNativeRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }
}
