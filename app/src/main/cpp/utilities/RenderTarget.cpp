//
// Created by thorsten on 21.05.20.
//

#include "RenderTarget.h"

RenderTarget::RenderTarget(int width, int height) : m_width {width}, m_height {height} {

    GLuint fbo[1];

    glGenFramebuffers(1, fbo);
    m_fbo = fbo[0];

    glBindFramebuffer(GL_FRAMEBUFFER, m_fbo);

    GLuint texture[1];

    glGenTextures(1, texture);
    glBindTexture(GL_TEXTURE_2D, texture[0]);
    m_texture = texture[0];

    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_width, m_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, m_texture, 0);

    glBindTexture(GL_TEXTURE_2D, 0);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

RenderTarget::~RenderTarget() {

    if(m_texture != 0) glDeleteTextures(1, &m_texture);
    if(m_fbo != 0) glDeleteFramebuffers(1, &m_fbo);
}

void RenderTarget::bind() {

    glBindFramebuffer(GL_FRAMEBUFFER, m_fbo);
}

void RenderTarget::unbind() {

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}
