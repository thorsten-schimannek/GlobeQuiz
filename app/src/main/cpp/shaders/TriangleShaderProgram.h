//
// Created by schimannek on 28.01.19.
//

#ifndef GLOBE_ORTHOGRAPHICSHADERPROGRAM_H
#define GLOBE_ORTHOGRAPHICSHADERPROGRAM_H

#include <string>

#include <glm/glm.hpp>

#include "ShaderProgram.h"

#define ATTRIBUTE_POSITION  "a_Position"

class TriangleShaderProgram : public ShaderProgram{

    public:

        TriangleShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code);
        ~TriangleShaderProgram() {};

        GLint getPositionAttributeLocation();

    protected:

        // Positions - Vertex Shader
        GLint m_attribute_position;
};

#endif //GLOBE_ORTHOGRAPHICSHADERPROGRAM_H
