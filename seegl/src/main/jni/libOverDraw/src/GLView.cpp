//
// Created by linus.yang on 2018/8/28.
//

#include "GLView.h"
#include <android/log.h>



void GLView::onCreate() {

}


void GLView::onLayout(bool isChanger, float x, float y, float width, float height) {

}

void GLView::onDraw() {
    glClearColor(1.0f, 0.0f ,0.0f , 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}


int GLView::loadShader(int type, const GLchar * shaderCode) {
    GLint shader = glCreateShader(type);
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
    
}

void GLView::checkError() {
    int error = glGetError();
    if(error != GL_NO_ERROR) {
        ALOGE("GL error: %d", error);
    }
}