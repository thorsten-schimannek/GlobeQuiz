//
// Created by schimannek on 2/13/18.
//

#ifndef GLOBE_RECTANGLESHADERRELIEF_H
#define GLOBE_RECTANGLESHADERRELIEF_H

#include <string>

#include <glm/glm/glm.hpp>
#include <glm/glm/gtc/type_ptr.hpp>

#include "ShaderProgram.h"

class GlobeRectangleShaderProgramRelief : public ShaderProgram{

    public:
        GlobeRectangleShaderProgramRelief(std::string vertex_shader_code, std::string fragment_shader_code);
        ~GlobeRectangleShaderProgramRelief();

        GLint getTextureUniformLocation() const;

        int getPositionAttributeLocation() {

            return m_attribute_position;
        }

    protected:

        static constexpr auto ATTRIBUTE_POSITION = "a_Position";
        static constexpr auto UNIFORM_TEXTURE = "u_Texture";

        GLint m_attribute_position;
        GLint m_texture_position;
};


#endif GLOBE_RECTANGLESHADERRELIEF_H
