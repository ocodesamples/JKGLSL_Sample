package com.glslutil;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

public class ShaderUtil {

    private final static  String TAG = ShaderUtil.class.getName();
    private final static int[] mTempIntArray = new int[1];

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        checkError();
        GLES30.glCompileShader(shader);

        int[] compile = mTempIntArray;
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compile, 0);

        if(compile[0] == 0) {
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
        }
        checkError();
        return shader;

    }


    public static int assembleProgram(int vertexShader, int fragmentShader) {
        int program = GLES30.glCreateProgram();
        checkError();
        if(program == 0) {
            throw new RuntimeException("Cannot create GL Program:"+ GLES30.glGetError());
        }
        GLES30.glAttachShader(program, vertexShader);
        checkError();
        GLES30.glAttachShader(program, fragmentShader);
        checkError();
        GLES30.glLinkProgram(program);
        int[] mLinkStatus = mTempIntArray;
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, mLinkStatus, 0);
        if(mLinkStatus[0] != GLES30.GL_TRUE) {
            Log.e(TAG, "Could not link program");
            Log.e(TAG, GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program);
            program = 0;
        }

        return program;
    }


    public static void checkError() {
        int error = GLES20.glGetError();
        if(error == 0) {
            Log.e(TAG, "GL error:"+error);
        }
    }

}
