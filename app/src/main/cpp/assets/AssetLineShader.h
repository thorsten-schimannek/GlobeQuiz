//
// Created by thorsten on 16.05.20.
//

#ifndef GLOBEQUIZ_ASSETLINESHADER_H
#define GLOBEQUIZ_ASSETLINESHADER_H

#include <string>
#include <memory>

#include <GLES2/gl2.h>

#include "assets/AssetShader.h"

#include "shaders/LineShaderProgram.h"

class AssetLineShader : public AssetShader {

public:
    AssetLineShader(int id, std::string vertex_shader_code, std::string fragment_shader_code);

    void reload();
    void unload();

    void setThickness(GLfloat thickness) const;

    GLint getPositionAttribute() const;
    GLint getPreviousAttribute() const;
    GLint getNextAttribute() const;
    GLint getDirectionAttribute() const;
};

#endif //GLOBEQUIZ_ASSETLINESHADER_H
