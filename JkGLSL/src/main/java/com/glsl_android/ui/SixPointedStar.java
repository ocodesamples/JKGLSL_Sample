package com.glsl_android.ui;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.glsl_android.utils.MatrixState;
import com.glsl_android.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class SixPointedStar extends GLSurfaceView{

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private StarRenderer mRenderer;

    private float mPreviousX;
    private float mPreviousY;

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                for(Star s : mRenderer.mStar) {
                    s.xAngle += dx * TOUCH_SCALE_FACTOR;
                    s.yAngle += dy * TOUCH_SCALE_FACTOR;
                }
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    class StarRenderer implements Renderer {

        Star[] mStar = new Star[6] ;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
            for(int i = 0; i < mStar.length; i++) {
                mStar[i] = new Star(1f, 0.5f, -0.3f * i);
            }
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES30.glViewport(0,0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);

            MatrixState.setCamera(0,0, 3f,
                                    0,0, 0,
                                       0f, 1.0f, 0.0f);


        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            for(Star s : mStar) {
                s.drawSelf();
            }
        }

    }


    class Star {
        int mProgram;
        int muMVPMatrixHandle;
        int maPositionHandle;
        int maColorHandle;

        FloatBuffer mVertexBuffer;
        FloatBuffer mColorBuffer;
        float[] mMMatrix = new float[16];

        int vCount;
        float xAngle = 0;
        float yAngle = 0;

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

        Star(float R, float r , float z) {
           initVertexData(R, r, z);
           initShader();
        }

        void initVertexData(float R , float r, float z) {
            List<Float> fList = new ArrayList<>();
            float tempAngle = 360 / 6;
            for(float angle = 0; angle < 360; angle += tempAngle) {
                fList.add(0f);
                fList.add(0f);
                fList.add(z);

                fList.add((float)(R * Math.cos(Math.toRadians(angle))));
                fList.add((float) (R * Math.sin(Math.toRadians(angle))));
                fList.add(z);

                fList.add((float) (r * Math.cos(Math.toRadians(angle + tempAngle / 2))));
                fList.add((float)(r * Math.sin(Math.toRadians(angle + tempAngle / 2))));
                fList.add(z);

                fList.add(0f);
                fList.add(0f);
                fList.add(z);

                fList.add((float) (r * Math.cos(Math.toRadians(angle+tempAngle/2))));
                fList.add((float) (r * Math.sin(Math.toRadians(angle+tempAngle/2))));
                fList.add(z);


                fList.add((float) (R * Math.cos(Math.toRadians(angle+tempAngle))));
                fList.add((float) (R * Math.sin(Math.toRadians(angle+tempAngle))));
                fList.add(z);
            }

            vCount = fList.size() / 3;
            float[] vertexArray = new float[fList.size()];
            for(int i = 0; i < vCount; i++) {
                vertexArray[i * 3] = fList.get(i * 3);
                vertexArray[i * 3 + 1] = fList.get(i * 3 + 1);
                vertexArray[i * 3 + 2] = fList.get(i * 3 + 2);
            }

            ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            mVertexBuffer = vbb.asFloatBuffer();
            mVertexBuffer.put(vertexArray);
            mVertexBuffer.position(0);


            float[] colorArray=new float[vCount*4];
            for(int i=0;i<vCount;i++)
            {
                if(i%3==0){
                    colorArray[i*4]=1;
                    colorArray[i*4+1]=1;
                    colorArray[i*4+2]=1;
                    colorArray[i*4+3]=0;
                }
                else{
                    colorArray[i*4]=0.45f;
                    colorArray[i*4+1]=0.75f;
                    colorArray[i*4+2]=0.75f;
                    colorArray[i*4+3]=0;
                }
            }
            ByteBuffer cbb=ByteBuffer.allocateDirect(colorArray.length*4);
            cbb.order(ByteOrder.nativeOrder());
            mColorBuffer=cbb.asFloatBuffer();
            mColorBuffer.put(colorArray);
            mColorBuffer.position(0);
        }

        void initShader() {
          mProgram =  ShaderUtil.createProgram(mVertexShader, mFragmentShader);
          maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
          maColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
          muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        }

        void drawSelf() {
            GLES30.glUseProgram(mProgram);
            Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
            Matrix.translateM(mMMatrix, 0,0,0,1);
            Matrix.rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
            Matrix.rotateM(mMMatrix, 0, xAngle, 1,0,0);
            GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
            GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
            GLES30.glVertexAttribPointer(maColorHandle, 4, GLES30.GL_FLOAT, false, 4 * 4, mColorBuffer);
            GLES30.glEnableVertexAttribArray(maPositionHandle);
            GLES30.glEnableVertexAttribArray(maColorHandle);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0 , vCount);
        }

    }

}
