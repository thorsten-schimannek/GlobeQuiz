//
// Created by thorsten on 05.05.20.
//

#ifndef GLOBEQUIZ_CUBEMAP_H
#define GLOBEQUIZ_CUBEMAP_H

#include <GLES2/gl2.h>

#include "CubeMapCamera.h"

class CubeMap {

public:

    CubeMap(int size);
    ~CubeMap();

    void bind();
    void switchToFace(int face);
    void unbind();
    glm::mat4 getProjectionViewMatrix();

    GLuint getTexture() { return m_texture; }

protected:

    int m_size;
    CubeMapCamera m_camera;

    GLuint m_texture;
    GLuint m_fbo;
    GLint m_viewport[4];
};

#endif //GLOBEQUIZ_CUBEMAP_H
