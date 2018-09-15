//
// Created by linus.yang on 2018/8/29.
//

#include "GLPrimitiveView.h"

void GLPrimitiveView::init() {
  int vertexShader =  loadShader(GL_VERTEX_SHADER, mVertexShader);
  int fragmentShader = loadShader(GL_FRAGMENT_SHADER, mFragmentShader);
  mUserProgram = assembProgram(vertexShader, fragmentShader);
    ALOGE("mUserProgram: %d", mUserProgram);
}

void GLPrimitiveView::onChangerSize(bool isChanger, float x, float y, float width, float height) {

}

void GLPrimitiveView::onDraw() {

}
