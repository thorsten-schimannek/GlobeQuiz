//
// Created by thorsten on 14.11.20.
//

#ifndef GLOBEQUIZ_TRIANGLESHADERPROGRAMRELIEF_H
#define GLOBEQUIZ_TRIANGLESHADERPROGRAMRELIEF_H

#include <string>

#include "TriangleShaderProgram.h"


class TriangleShaderProgramRelief : public TriangleShaderProgram {

public:
    TriangleShaderProgramRelief(std::string vertex_shader_code, std::string fragment_shader_code);
    ~TriangleShaderProgramRelief() {};

    GLint getTextureUniformLocation() const;
    void setTextureSampler(GLint sampler);

protected:
    static constexpr auto UNIFORM_TEXTURE = "u_Texture";

    GLint m_texture_position;
};

#endif //GLOBEQUIZ_TRIANGLESHADERPROGRAMRELIEF_H
