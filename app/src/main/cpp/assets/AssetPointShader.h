//
// Created by thorsten on 23.05.20.
//

#ifndef GLOBEQUIZ_ASSETPOINTSHADER_H
#define GLOBEQUIZ_ASSETPOINTSHADER_H

#include <string>
#include <memory>

#include <GLES2/gl2.h>

#include "assets/AssetShader.h"

#include "shaders/PointShaderProgram.h"

class AssetPointShader : public AssetShader {

public:
    AssetPointShader(int id, std::string vertex_shader_code, std::string fragment_shader_code);

    void reload();
    void unload();

    void setThickness(GLfloat thickness) const;

    GLint getPositionAttribute() const;
    GLint getDirectionAttribute() const;
};
#endif //GLOBEQUIZ_ASSETPOINTSHADER_H
