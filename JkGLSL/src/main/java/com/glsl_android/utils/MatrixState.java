package com.glsl_android.utils;

import android.opengl.Matrix;

public class MatrixState {

    private static float[] mProjMatrix = new float[16];
    private static float[] mVMatrix = new float[16];
    private static float[] mMVPMatrix = new float[16];
    private static float[] currMatrix; // 当前变换矩阵

    static float[][] mStack = new float[10][16];
    static int stackTop = -1;


    //产生无任何变换的初始化矩阵
    public static void setInitStack() {
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    //将当前变换矩阵存入栈中
    public static void pushMatrix() {
        stackTop++;
        for(int i = 0; i < 16; i++) {
            mStack[stackTop][i]  = currMatrix[i];
        }
    }

    //从栈顶取出变换矩阵
    public static void popMatrix() {
        for(int i = 0; i < 16; i++) {
            currMatrix[i] = mStack[stackTop][i];
        }
        stackTop--;
    }

    public static void translate(float x, float y , float z) {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }


    public static void setCamera(float cx, float cy , float cz,  float tx, float ty, float tz, float upx, float upy, float upz) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
    }

    //设置透视投影
    public static void setProjectFrustum(float left , float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    public static void setProjectOrtho   (
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far
    ) {
        Matrix.orthoM(mProjMatrix, 0 , left, right, bottom, top, near, far);
    }

    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0,mVMatrix, 0, currMatrix,0);
        Matrix.multiplyMM(mMVPMatrix, 0 , mProjMatrix, 0 , mMVPMatrix, 0 );
        return mMVPMatrix;
    }

    public static float[] getMMatrix() {
        return currMatrix;
    }
}
