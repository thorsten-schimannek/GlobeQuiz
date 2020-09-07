//
// Created by thorsten on 21.05.20.
//

#ifndef GLOBEQUIZ_RENDERTARGET_H
#define GLOBEQUIZ_RENDERTARGET_H

#include <GLES2/gl2.h>

#include "log.h"

class RenderTarget {

public:

    RenderTarget(int width, int height);
    ~RenderTarget();

    void initialize();

    void bind();
    void unbind();

    GLuint getTexture() { return m_texture; }

private:

    int m_width, m_height;

    GLuint m_texture {0};
    GLuint m_fbo {0};
};

#endif //GLOBEQUIZ_RENDERTARGET_H
