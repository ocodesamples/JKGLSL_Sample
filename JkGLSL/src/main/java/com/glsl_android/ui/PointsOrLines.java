package com.glsl_android.ui;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.glsl_android.utils.MatrixState;
import com.glsl_android.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PointsOrLines extends GLSurfaceView {

    public static final float UNIT_SIZE=0.9f;
    //绘制方式
    public static final int GL_POINTS = 0;
    public static final int GL_LINES = 1;
    public static final int GL_LINE_STRIP = 2;
    public static final int GL_LINE_LOOP = 3;

    public static int CURR_DRAW_MODE = 0;//当前绘制方式

    PLRenderer mRenderer;

    public PointsOrLines(Context context) {
        this (context, null);
    }

    public PointsOrLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        mRenderer = new PLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                CURR_DRAW_MODE ++;
                if(CURR_DRAW_MODE > 3) {
                    CURR_DRAW_MODE = 0;
                }
                System.out.println(CURR_DRAW_MODE+"==========sdsd");
                break;
        }
        return true;
    }

    private class PLRenderer implements GLSurfaceView.Renderer {

        PLs mPLs;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0,0,0, 1.0f);
            //创建点或线对象
            mPLs = new PLs();
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口的大小及位置
            GLES30.glViewport(0, 0, width, height);   
            //计算视口的宽高比
            float ratio = (float) width / height;
            // 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 20, 100);
            // 调用此方法产生摄像机矩阵
            MatrixState.setCamera(100f, 0f, 30, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //保护现场
            MatrixState.pushMatrix();
            //绘制原点或线
            MatrixState.pushMatrix();
            mPLs.drawSelf();
            MatrixState.popMatrix();
            //恢复现场
            MatrixState.popMatrix();
        }
    }


     class PLs {

        int mProgram;  //自定义渲染管线着色器id
        int muMVPMatrixHandle;   // 总变换矩阵引用
        int maPositionHandle; // 顶点位置索引属性
        int maColorHandle; // 顶点颜色属性引用
         int vCount;

        String mVertexShader = "#version 300 es                               \n" +
                "uniform mat4 uMVPMatrix;                                     \n" +
                "in vec3 aPosition;                                           \n" +
                "in vec4 aColor;                                              \n" +
                "out vec4 vColor;                                             \n" +
                "void main(){                                                 \n" +
                "   gl_Position = uMVPMatrix * vec4(aPosition, 1);            \n" +
                "   vColor = aColor;                                          \n" +
                "}                                                            \n" ;

        String mFragmentShader = "#version 300 es                             \n" +
                "precision mediump float;                                     \n" +
                "in vec4 vColor;                                              \n" +
                "out vec4 fragColor;                                          \n" +
                "void main(){                                                 \n" +
                "   fragColor = vColor;                                       \n" +
                "}                                                            \n";

        FloatBuffer mVertexBuffer;
        FloatBuffer mColorBuffer;

         PLs() {
             initVertexData();
             initShader();
         }


         void initVertexData() {
             vCount = 5;

             float vertices[] = new float[] {
                     0, 0, 0,
                     UNIT_SIZE, UNIT_SIZE, 0,
                     -UNIT_SIZE, UNIT_SIZE, 0,
                     -UNIT_SIZE, -UNIT_SIZE, 0,
                     UNIT_SIZE, -UNIT_SIZE,  0
             };
             ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
             vbb.order(ByteOrder.nativeOrder());
             mVertexBuffer = vbb.asFloatBuffer();
             mVertexBuffer.put(vertices);
             mVertexBuffer.position(0);

             float colors[] = new float[] {
                     1, 1, 0, 0,
                     1, 1, 1, 0,
                     0, 1, 0, 0,
                     1, 1, 1, 0,
                     1, 1, 0, 0
             };

             ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
             cbb.order(ByteOrder.nativeOrder());
             mColorBuffer = cbb.asFloatBuffer();
             mColorBuffer.put(colors);
             mColorBuffer.position(0);

         }

         void initShader() {
             mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
             muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
             maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
             maColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
         }


         void drawSelf() {
             //指定使用某套着色器程序
             GLES30.glUseProgram(mProgram);
             //将最终变换矩阵传入渲染管线
             GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                     MatrixState.getFinalMatrix(), 0);
             //将顶点位置数据送入渲染管线
             GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,
                     false, 3 * 4, mVertexBuffer);
             //将顶点颜色数据送入渲染管线
             GLES30.glVertexAttribPointer(maColorHandle, 4, GLES30.GL_FLOAT, false,
                     4 * 4, mColorBuffer);
             //启用顶点位置数据数组
             GLES30.glEnableVertexAttribArray(maPositionHandle);
             //启用顶点颜色数据数组
             GLES30.glEnableVertexAttribArray(maColorHandle);

             GLES30.glLineWidth(10);//设置线的宽度
             //绘制点或线
             switch (CURR_DRAW_MODE) {
                 case GL_POINTS:// GL_POINTS方式
                     GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);
                     break;
                 case GL_LINES:// GL_LINES方式
                     GLES30.glDrawArrays(GLES30.GL_LINES, 0, vCount);
                     break;
                 case GL_LINE_STRIP:// GL_LINE_STRIP方式
                     GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, vCount);
                     break;
                 case GL_LINE_LOOP:// GL_LINE_LOOP方式
                     GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, vCount);
                     break;
             }
         }
    }
}
