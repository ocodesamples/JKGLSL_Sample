package com.glsl_android;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.glslutil.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private boolean isModeAdd = true;
    private int speed = 1;
    private int x;
    private int y;
    private int z = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.gl_surface_view);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setRenderer(new GLRenderer());
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        findViewById(R.id.bt_mode).setOnClickListener((v)->{
            Button b = (Button) v;
            if(b.getText().equals("模式+")) {
                b.setText("模式-");
                isModeAdd = false;
            } else {
                b.setText("模式+");
                isModeAdd = true;
            }
        });

        findViewById(R.id.bt_x).setOnClickListener((v)->{
            if(isModeAdd) {
                x += speed;
            } else {
                x -= speed ;
                if(x < 0) x = 0;
            }

            System.out.println("x:"+x);
        });

        findViewById(R.id.bt_y).setOnClickListener((v)->{
            if(isModeAdd) {
                y += speed;
            } else {
                y -= speed ;
                if(y < 0) y = 0;
            }

            System.out.println("y"+y);
        });

        findViewById(R.id.bt_z).setOnClickListener((v)->{
            if(isModeAdd) {
                z += speed;
            } else {
                z -= speed ;
                if(z < 0) z = 0;
            }
            System.out.println("z:"+z);
        });

    }


    private class GLRenderer implements GLSurfaceView.Renderer {

       private String mVertexShader = "#version 300 es        \n"
               +        "  layout(location = 1)uniform mat4 uMatrix;  \n "
               +        "  layout(location = 0) in vec4 a_position;  \n"
               +        "  layout(location = 1) in vec3 a_color;    \n "
               +        "   out vec4 o_color;                      \n "
               +        "  void main()                           \n"
               +        " {                                          \n"
               +        "    gl_Position = uMatrix * a_position; \n "
               +        "     o_color =  vec4(a_color, 1.0);            \n  "
               +        " }                                          \n";

       private String mFragmentShader = "#version 300 es            \n "
               +        " precision mediump float;                  \n"
               +        " in vec4 o_color;                               \n "
               +       "  layout(location = 0) out vec4 o_fragColor;  \n "
               +       " void main()                                  \n"
               +       " {                                            \n"
               +       "    o_fragColor  = o_color;                   \n"
               +       " } ";

       private int mProgram = -1;

       private int VBO[] = new int[1];
       private int IBO[] = new int[1];
                                       // xyz               rgb
       private float[] vector = {-1.0f, -1.0f, 0.0f, 1.0f, 0.0f , 0.0f,
                                 0.0f, -1.0f, 1.0f,  0.0f, 1.0f , 0.0f,
                                 1.0f, -1.0f, 0.0f,  0.0f, 0.0f , 1.0f,
                                 0.0f, 1.0f,  0.0f,   1f,   1f,    1f};

       private  short indeces[] = {
               0, 3, 1,
               1, 3, 2,
               2, 3, 0,
               0, 1, 2
        };

       private float[] mMMatrix = new float[16];
       private float[] mViewMatrix = new float[16];
       private float[] mPMatrix = new float[16];
       private float[] mMVPMatrix = new float[16];

        private float scale = 0.0f;
       private FloatBuffer mVertexBuffer;
       private ShortBuffer mIndeceBuffer;
       private int gWordLocation;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mVertexBuffer = ByteBuffer.allocateDirect(vector.length * 4).order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mVertexBuffer.put(vector).position(0);
            mIndeceBuffer = ByteBuffer.allocateDirect(indeces.length * 2).order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            mIndeceBuffer.put(indeces).position(0);


          int  vertexShader = ShaderUtil.loadShader(GLES30.GL_VERTEX_SHADER, mVertexShader);
          int  fragmentShader = ShaderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, mFragmentShader);
          int program =  ShaderUtil.assembleProgram(vertexShader, fragmentShader);

          mProgram = program;

          //VBO
          GLES30.glGenBuffers(1,VBO, 0);
          GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0]);
          GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vector.length * 4, mVertexBuffer, GLES30.GL_STATIC_DRAW);
          GLES30.glUseProgram(mProgram);


          //VAO
           GLES30.glGenBuffers(1, IBO, 0);
           GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, IBO[0]);
           GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indeces.length * 2, mIndeceBuffer ,GLES30.GL_STATIC_DRAW);

            gWordLocation = GLES30.glGetUniformLocation(program, "uMatrix");

            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.setIdentityM(mViewMatrix, 0);
            Matrix.setIdentityM(mPMatrix, 0);



            GLES30.glFrontFace(GLES30.GL_CW);  //确定那个方向是前方
            GLES30.glCullFace(GLES30.GL_BACK); //剔除背面
            GLES30.glEnable(GLES30.GL_CULL_FACE); //开启剔除
            GLES30.glDisable(GLES30.GL_CULL_FACE); //取消剔除
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);



        }

        public void printMatrix(float matrxi[]) {
            System.out.println("1:"+ matrxi[0] + "   2 : " + matrxi[1]  +"  3  "+ matrxi[2] +"  4: "+ matrxi[3]);
            System.out.println("5:"+ matrxi[4] + "   6 : " + matrxi[5]  +"  7  "+ matrxi[6] +"  8: "+ matrxi[7]);
            System.out.println("9:"+ matrxi[8] + "   10 : " + matrxi[9]  +" 11  "+ matrxi[10] +"  12: "+ matrxi[11]);
            System.out.println("13:"+ matrxi[12] + "  14 : " + matrxi[13]  +"  15  "+ matrxi[14] +"  16: "+ matrxi[15]);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES30.glViewport(0, 0, width, height);
            float r = (float) width / height;
            Matrix.frustumM(mPMatrix, 0, -r,  r, -1,1 , 10, 100 );

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES30.glClearColor(0.4f, 0.1f, 0.7f, 1);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);

            scale += 0.0001;
            Matrix.setLookAtM(mViewMatrix, 0, x, y, z, x,y, 0,0, 1 , 0);
            Matrix.rotateM(mMMatrix, 0, scale , 0, 1, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mMMatrix, 0 );
            Matrix.multiplyMM(mMVPMatrix, 0 , mPMatrix, 0 , mMVPMatrix, 0);
            GLES30.glUniformMatrix4fv(gWordLocation, 1, false, mMVPMatrix, 0);
            GLES30.glEnableVertexAttribArray(0);
            GLES30.glEnableVertexAttribArray(1);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0]);
            GLES30.glVertexAttribPointer ( 0, 3, GLES30.GL_FLOAT, false, vector.length, 0 );
            GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, vector.length, 3 * 4);

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, IBO[0]);
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, indeces.length, GLES30.GL_UNSIGNED_SHORT, 0);

//            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
            GLES30.glDisableVertexAttribArray(0);
            GLES30.glDisableVertexAttribArray(1);
        }
    }
}
