//
// Created by linus.yang on 2018/8/29.
//

#include "GLPrimitiveView.h"


void GLPrimitiveView::init() {
   EGLDisplay  display = eglGetCurrentDisplay();
    EGLSurface  surface = eglGetCurrentSurface(EGL_READ);


    int width;
    int height;

    eglQuerySurface(display, surface, EGL_WIDTH, &width);
    eglQuerySurface(display, surface, EGL_HEIGHT, &height);

    ALOGE("width: %d,  height: %d", width, height);
}

void GLPrimitiveView::onChangerSize(bool isChanger, float x, float y, float width, float height) {

}

void GLPrimitiveView::onDraw() {

}
