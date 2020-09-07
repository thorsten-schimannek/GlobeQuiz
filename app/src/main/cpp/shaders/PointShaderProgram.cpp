//
// Created by thorsten on 23.05.20.
//

#include "PointShaderProgram.h"

PointShaderProgram::PointShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code)
        : ShaderProgram(vertex_shader_code, fragment_shader_code) {

    m_attribute_position = glGetAttribLocation(m_program, ATTRIBUTE_POSITION);
    m_attribute_direction = glGetAttribLocation(m_program, ATTRIBUTE_DIRECTION);

    m_uniform_thickness = glGetUniformLocation(m_program, UNIFORM_THICKNESS);
}

PointShaderProgram::~PointShaderProgram() {};

void PointShaderProgram::setThickness(GLfloat thickness) {

    glUniform1f(m_uniform_thickness, thickness);
}

GLint PointShaderProgram::getPositionAttributeLocation() {

    return m_attribute_position;
}

GLint PointShaderProgram::getDirectionAttributeLocation() {

    return m_attribute_direction;
}
