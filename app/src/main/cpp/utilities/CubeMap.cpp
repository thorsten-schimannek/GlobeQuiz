//
// Created by thorsten on 05.05.20.
//

#include "CubeMap.h"

CubeMap::CubeMap(int size)
    : m_camera {0.f, 0.f, 0.f}, m_size {size}, m_fbo {0}, m_texture {0} {

    GLuint fbo[] {0};

    glGenFramebuffers(1, fbo);
    m_fbo = fbo[0];

    GLuint texture[] {0};

    glGenTextures(1, texture);
    glBindTexture(GL_TEXTURE_CUBE_MAP, texture[0]);
    m_texture = texture[0];

    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    for(int i = 0; i < 6; i++)
        glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,0,
                     GL_RGBA, m_size, m_size, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

    glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
}

CubeMap::~CubeMap() {

    if(m_texture != 0) glDeleteTextures(1, &m_texture);
    if(m_fbo !=0 ) glDeleteFramebuffers(1, &m_fbo);
}

void CubeMap::bind() {

    glBindFramebuffer(GL_FRAMEBUFFER, m_fbo);
    glGetIntegerv(GL_VIEWPORT, m_viewport);
    glViewport(0, 0, m_size, m_size);
}

void CubeMap::switchToFace(int face) {

    m_camera.switchToFace(face);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                           GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, m_texture, 0);
}

void CubeMap::unbind() {

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glViewport(m_viewport[0], m_viewport[1], m_viewport[2], m_viewport[3]);
}

glm::mat4 CubeMap::getProjectionViewMatrix() {

    glm::mat4 projectionView = m_camera.getProjectionViewMatrix();

    return projectionView;
}