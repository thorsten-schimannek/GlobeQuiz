//
// Created by schimannek on 28.01.19.
//

#include "TriangleShaderProgram.h"

TriangleShaderProgram::TriangleShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code)
        : ShaderProgram(vertex_shader_code, fragment_shader_code){

    m_attribute_position = glGetAttribLocation(m_program, ATTRIBUTE_POSITION);
}

GLint TriangleShaderProgram::getPositionAttributeLocation() {

    return m_attribute_position;
}