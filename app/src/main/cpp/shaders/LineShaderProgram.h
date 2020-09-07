//
// Created by thorsten on 15.05.20.
//

#ifndef GLOBEQUIZ_LINESHADERPROGRAM_H
#define GLOBEQUIZ_LINESHADERPROGRAM_H

#include <string>

#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "ShaderProgram.h"

#define ATTRIBUTE_POSITION  "a_Position"
#define ATTRIBUTE_PREVIOUS  "a_Previous"
#define ATTRIBUTE_NEXT      "a_Next"
#define ATTRIBUTE_DIRECTION "a_Direction"

#define UNIFORM_THICKNESS   "u_Thickness"

class LineShaderProgram : public ShaderProgram{

public:

    LineShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code);
    ~LineShaderProgram();

    void setThickness(GLfloat thickness);

    GLint getPositionAttributeLocation();
    GLint getPreviousAttributeLocation();
    GLint getNextAttributeLocation();
    GLint getDirectionAttributeLocation();

protected:

    // Positions - Vertex Shader
    GLint m_attribute_position;
    GLint m_attribute_previous;
    GLint m_attribute_next;
    GLint m_attribute_direction;

    GLint m_uniform_thickness;
};

#endif //GLOBEQUIZ_LINESHADERPROGRAM_H
