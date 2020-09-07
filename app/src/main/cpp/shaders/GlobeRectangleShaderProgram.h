//
// Created by schimannek on 2/13/18.
//

#ifndef GLOBE_RECTANGLESHADER_H
#define GLOBE_RECTANGLESHADER_H

#include <string>

#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "ShaderProgram.h"

#define ATTRIBUTE_POSITION "a_Position"

#define UNIFORM_CUBEMAP "u_CubeMap"

class GlobeRectangleShaderProgram : public ShaderProgram{

    public:
        GlobeRectangleShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code);
        ~GlobeRectangleShaderProgram();

        void setCubeMapTexture(GLuint texture);

        int getPositionAttributeLocation() {

            return m_attribute_position;
        }

    protected:

        GLint m_attribute_position;
        GLuint m_uniform_cubemap;
};


#endif //GLOBE_RECTANGLESHADER_H
