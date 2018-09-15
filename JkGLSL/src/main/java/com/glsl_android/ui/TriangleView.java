package com.glsl_android.ui;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.glsl_android.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriangleView extends GLSurfaceView {


    private SceneRenderer mRenderer;

    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);//设置版本
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }


    private class SceneRenderer implements GLSurfaceView.Renderer {

        private Triangle mTriangle;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES30.glClearColor(0, 0, 0, 1);
            mTriangle = new Triangle();
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
           mTriangle.setViewSize(width, height);

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT| GLES30.GL_COLOR_BUFFER_BIT);
            mTriangle.drawSelf();
        }
    }



    private class Triangle {

        private float[] mProjMatrix = new float[16]; // 4x4 投影矩阵
        private float[] mVMatrix = new float[16]; //摄像机位置朝向总变换矩阵
        private float[] mMVPMatrix  = new float[16];; // 最后起作用的总变换矩阵
        private float[] mMMatrix = new float[16];


        private FloatBuffer mVertexBuffer; //顶点坐标数据缓冲
        private FloatBuffer mColorBuffer;  //顶点颜色数据缓冲

        private int mProgram ; //自定渲染管线程序id;
        private int mPositionHandle;
        private int mColorHandle;
        private int mUMVPMatrixHandle;

        private final  float ANGLE_SPAN = 0.13f;
        private float xAngle  = 0;

        private int vCount = 0;

        public Triangle() {
            //调用初始化顶点数据的initVertexData方法
            initVertexData();
            initShader();

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        xAngle += ANGLE_SPAN;
                        try {
                            Thread.sleep(10);
                        }catch (Exception e) {

                        }
                    }
                }
            }.start();
        }

        private void initVertexData() {  //初始化顶点数据的方法
            //顶点坐标数据的初始化
            vCount = 3;
            final float UNIT_SIZE = 0.2f;
            float vertices[] = new float[] {  //顶点坐标数组
                    -4 * UNIT_SIZE,              0,   0,
                     0            , -4 * UNIT_SIZE,   0,
                     4 * UNIT_SIZE,              0,   0,
            };

            ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
            vbb.order(ByteOrder.nativeOrder()); //设置字节顺序为本地操作系统顺序
            mVertexBuffer = vbb.asFloatBuffer();
            mVertexBuffer.put(vertices);
            mVertexBuffer.position(0);


            float colors[] = new float[] {  //顶点颜色数组 rgba
                    1,1,1,0, //白色
                    0,0,1,0, // 蓝色
                    0,1,0,0, //绿色
            };

            ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
            cbb.order(ByteOrder.nativeOrder());
            mColorBuffer = cbb.asFloatBuffer();
            mColorBuffer.put(colors);
            mColorBuffer.position(0); //设置缓冲区起始位置


        }


        private void initShader() {

            String vertexSource = "#version 300 es           \n" +
                    "uniform mat4 uMVPMatrix;     \n" +
                    "layout(location = 0) in vec3 aPosition;   \n" +
                    "layout(location = 1) in vec4 aColor;      \n" +
                    "out vec4 vColor;                          \n" +
                    "void main() {                             \n" +
                    "      gl_Position = uMVPMatrix * vec4(aPosition, 1); \n" +
                    "      vColor = aColor;                       \n" +
                    "}                                             \n";

            String fragmentSource = "#version 300 es        \n" +
                    "precision mediump float;               \n" +
                    "in vec4 vColor;                        \n" +
                    "out vec4 fragColor;                    \n" +
                    "void main(){                           \n" +
                    "    fragColor = vColor;                \n" +
                    "}                                     \n";

         mProgram = ShaderUtil.createProgram(vertexSource,  fragmentSource);

         mPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
         mColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
         mUMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        }


        public void setViewSize(int width, int height) {
            GLES30.glViewport(0,0, width, height);
            float ratio = (float) width / height;
            Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1,1, 1, 10 );
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, 2, 0, 0, 0, 0, 1 ,0);
        }

        public void drawSelf() {
                GLES30.glUseProgram(mProgram);
                Matrix.setRotateM(mMMatrix, 0, xAngle, 1, 0, 0);
//                Matrix.translateM(mMMatrix, 0, (float) Math.cos(xAngle ), (float) Math.sin(xAngle ), 0);
//            Matrix.rotateM(mMMatrix, 0, xAngle , 0, 0, 1);

                GLES30.glUniformMatrix4fv(mUMVPMatrixHandle, 1, false, getFinalMatrix(mMMatrix), 0);
                GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
                GLES30.glVertexAttribPointer(mColorHandle, 4, GLES30.GL_FLOAT, false, 4 * 4, mColorBuffer);
                GLES30.glEnableVertexAttribArray(mPositionHandle);
                GLES30.glEnableVertexAttribArray(mColorHandle);

                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);

        }

        public  float[] getFinalMatrix(float[] spec) {
            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix,  0, mMVPMatrix, 0);
           return mMVPMatrix;
        }


    }
}
