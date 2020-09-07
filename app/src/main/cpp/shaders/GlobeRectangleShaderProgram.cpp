//
// Created by schimannek on 2/13/18.
//

#include "GlobeRectangleShaderProgram.h"

GlobeRectangleShaderProgram::GlobeRectangleShaderProgram(std::string vertex_shader_code,
        std::string fragment_shader_code) : ShaderProgram(vertex_shader_code, fragment_shader_code){

    m_attribute_position = glGetAttribLocation(m_program, ATTRIBUTE_POSITION);

    m_uniform_cubemap = glGetUniformLocation(m_program, UNIFORM_CUBEMAP);
}

void GlobeRectangleShaderProgram::setCubeMapTexture(GLuint texture) {

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
    glUniform1i(m_uniform_cubemap, 0);
}

GlobeRectangleShaderProgram::~GlobeRectangleShaderProgram() {}