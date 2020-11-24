//
// Created by thorsten on 23.05.20.
//

#ifndef GLOBEQUIZ_POINTSHADERPROGRAM_H
#define GLOBEQUIZ_POINTSHADERPROGRAM_H

#include <string>

#include <glm/glm/glm.hpp>
#include <glm/glm/gtc/type_ptr.hpp>

#include "ShaderProgram.h"

class PointShaderProgram : public ShaderProgram{

public:

    PointShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code);
    ~PointShaderProgram();

    void setThickness(GLfloat thickness);

    GLint getPositionAttributeLocation();
    GLint getDirectionAttributeLocation();

protected:

    static constexpr auto ATTRIBUTE_POSITION = "a_Position";
    static constexpr auto ATTRIBUTE_DIRECTION = "a_Direction";
    static constexpr auto UNIFORM_THICKNESS = "u_Thickness";

    // Positions - Vertex Shader
    GLint m_attribute_position;
    GLint m_attribute_direction;

    GLint m_uniform_thickness;
};

#endif //GLOBEQUIZ_POINTSHADERPROGRAM_H
