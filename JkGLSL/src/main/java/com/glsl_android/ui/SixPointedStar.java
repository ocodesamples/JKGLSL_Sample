package com.glsl_android.ui;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.glsl_android.utils.ShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SixPointedStar extends GLSurfaceView{

    private Renderer mRenderer;

    public SixPointedStar(Context context) {
        this(context, null);
    }

    public SixPointedStar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        mRenderer = new StarRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }



    class StarRenderer implements Renderer {

        Star mStar ;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mStar = new Star();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }


    class Star {
        int mProgram;
        int muMVPMatrixHandle;
        int maPositionHandle;
        int maColorHandle;

        String mVertexShader = "#version 300 es           \n" +
                "uniform mat4 uMVPMatrix;                \n" +
                "in vec3 aPosition;                      \n" +
                "in vec4 aColor;                         \n" +
                "out vec4 oColor;                         \n" +
                "void main(){                              \n" +
                " gl_Position = uMVPMatrix * vec4(aPosition, 1); \n"+
                  "oColor = aColor;                             \n" +
                "}";
        String mFragmentShader = "#version 300 es               \n" +
                "precision mediump float;                       \n" +
                "in vec4 oColor;                                \n" +
                "out vec4 fragColor;                             \n" +
                "void main() {                                  \n" +
                "  fragColor = oColor;                          \n" +
                "}                                              \n";

        Star() {
           initVertexData();
           initShader();
        }

        void initVertexData() {

        }

        void initShader() {
          mProgram =  ShaderUtil.createProgram(mVertexShader, mFragmentShader);
          maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
          maColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
          muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
          System.out.println(maPositionHandle+"==="+ maColorHandle+"=="+muMVPMatrixHandle);
        }


    }

}
