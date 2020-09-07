//
// Created by thorsten on 15.05.20.
//

#include "LineShaderProgram.h"

LineShaderProgram::LineShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code)
    : ShaderProgram(vertex_shader_code, fragment_shader_code) {

    m_attribute_position = glGetAttribLocation(m_program, ATTRIBUTE_POSITION);
    m_attribute_previous = glGetAttribLocation(m_program, ATTRIBUTE_PREVIOUS);
    m_attribute_next = glGetAttribLocation(m_program, ATTRIBUTE_NEXT);
    m_attribute_direction = glGetAttribLocation(m_program, ATTRIBUTE_DIRECTION);

    m_uniform_thickness = glGetUniformLocation(m_program, UNIFORM_THICKNESS);
}

LineShaderProgram::~LineShaderProgram() {};

void LineShaderProgram::setThickness(GLfloat thickness) {

    glUniform1f(m_uniform_thickness, thickness);
}

GLint LineShaderProgram::getPositionAttributeLocation() {

    return m_attribute_position;
}

GLint LineShaderProgram::getPreviousAttributeLocation() {

    return m_attribute_previous;
}

GLint LineShaderProgram::getNextAttributeLocation() {

    return m_attribute_next;
}

GLint LineShaderProgram::getDirectionAttributeLocation() {

    return m_attribute_direction;
}
