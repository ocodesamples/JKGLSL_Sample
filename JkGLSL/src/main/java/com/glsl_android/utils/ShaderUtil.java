package com.glsl_android.utils;

import android.opengl.GLES10;
import android.opengl.GLES30;
import android.util.Log;

public class ShaderUtil {

    private static int[] TEMP = new int[1];

    public static int loadShader(int shaderType, String source) {
        int shader = GLES30.glCreateShader(shaderType);  //创建一个shader引用

        if(shader != 0) { //如果shader引用不等于0， 则创建成功
            GLES30.glShaderSource(shader, source); //设置shader的代码
            GLES30.glCompileShader(shader);  //编译shader

            int[] compiled = TEMP;
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0); //查询shader编译状态

            if(compiled[0] != GLES30.GL_TRUE) {  //状态等于0则失败
                Log.e("ES30_ERROR", "could not compile shader" + shaderType + ":");
                Log.e("ES30_ERROR", GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if(vertexShader == 0) {
            return 0;
        }

        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if(fragmentShader == 0) {
            return 0;
        }

        int program = GLES30.glCreateProgram();
        if(program != 0) {
            GLES30.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES30.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");

            GLES30.glLinkProgram(program);

            int[] linkStatus = TEMP;

            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);

            if(linkStatus[0] != GLES30.GL_TRUE) {
                Log.e("ES30_ERROR", "Could not link program");
                Log.e("ES30_ERROR", GLES30.glGetProgramInfoLog(program));
                program = 0;
            }
        }
        return program;
    }


    public static void checkGlError(String op){
        int error;
        while((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("ES30_ERROR", op + " : glError " + error);
            throw new RuntimeException(op+": glError" + error);
        }
    }
}
