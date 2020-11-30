//
// Created by schimannek on 2/13/18.
//

#include "GlobeRectangleShaderProgramRelief.h"

GlobeRectangleShaderProgramRelief::GlobeRectangleShaderProgramRelief(std::string vertex_shader_code,
        std::string fragment_shader_code) : ShaderProgram(vertex_shader_code, fragment_shader_code){

    m_attribute_position = glGetAttribLocation(m_program, ATTRIBUTE_POSITION);
    m_texture_position = glGetUniformLocation(m_program, UNIFORM_TEXTURE);
}

GLint GlobeRectangleShaderProgramRelief::getTextureUniformLocation() const { return m_texture_position; }

GlobeRectangleShaderProgramRelief::~GlobeRectangleShaderProgramRelief() {}