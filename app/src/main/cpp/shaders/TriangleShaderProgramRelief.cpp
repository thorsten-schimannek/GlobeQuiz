//
// Created by thorsten on 14.11.20.
//

#include "TriangleShaderProgramRelief.h"

TriangleShaderProgramRelief::TriangleShaderProgramRelief(std::string vertex_shader_code,
        std::string fragment_shader_code) : TriangleShaderProgram(vertex_shader_code, fragment_shader_code) {

    m_texture_position = glGetUniformLocation(m_program, UNIFORM_TEXTURE);
    glUniform1i(m_texture_position, 0);
}

GLint TriangleShaderProgramRelief::getTextureUniformLocation() const { return m_texture_position; }

void TriangleShaderProgramRelief::setTextureSampler(GLint sampler) {

    glUniform1i(m_texture_position, sampler);
}