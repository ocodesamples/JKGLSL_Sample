//
// Created by linus.yang on 2018/8/28.
//

#include "GLView.h"



void GLView::onCreate() {
    init();
}


void GLView::onLayout(bool isChanger, float x, float y, float width, float height) {
    onChangerSize(isChanger, x, y , width, height);
}

void GLView::onDrawFrame() {
    glClearColor(1.0f, 0.0f ,0.0f , 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    onDraw();
}


int GLView::loadShader(int type, const GLchar * shaderCode) {
    GLint shader = glCreateShader(type);

    if(shader == 0) {
        ALOGE("cannot create GL shader: %d", shader);
    }

    glShaderSource(shader, 1, &shaderCode, NULL);
    checkError();
    glCompileShader(shader);

    GLint compile;

    glGetShaderiv(shader, GL_COMPILE_STATUS, &compile);

    if(!compile) {
        GLint infoLen = 0;

        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);

        if(infoLen > 1) {
            char * infoLog = (char *)malloc(sizeof(char) * infoLen);
            glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
            ALOGE("Error compiling shader : \n %s \n", infoLog);
            free(infoLog);
        }

        glDeleteShader(shader);
    }
    return shader;
}

int GLView::assembProgram(int vertexShader, int fragmentShader) {
    GLint program = glCreateProgram();
    checkError();
    if(program == 0) {
        ALOGE("cannot create GL Program: %d", program);
    }

    glAttachShader(program, vertexShader);
    checkError();
    glAttachShader(program, fragmentShader);
    checkError();
    glLinkProgram(program);

    int linkState;
    glGetProgramiv(program, GL_LINK_STATUS, &linkState);
    if(!linkState) {
        ALOGE("Could not link program");
        GLint  infoLen  = 0;
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &infoLen);

        if(infoLen > 1) {
            char * infoLog = (char *) malloc(sizeof(char) * infoLen);
            glGetProgramInfoLog(program, infoLen, NULL, infoLog);
            ALOGE("Error link program: %s", infoLog);
            free(infoLog);
        }
        glDeleteProgram(program);
    }
    return program;
}

void GLView::checkError() {
    int error = glGetError();
    if(error != GL_NO_ERROR) {
        ALOGE("GL error: %d", error);
    }
}