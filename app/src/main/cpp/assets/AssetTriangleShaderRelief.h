//
// Created by thorsten on 14.11.20.
//

#ifndef GLOBEQUIZ_ASSETTRIANGLESHADERRELIEF_H
#define GLOBEQUIZ_ASSETTRIANGLESHADERRELIEF_H

#include <string>
#include <memory>

#include <GLES2/gl2.h>

#include "assets/AssetShader.h"

#include "shaders/TriangleShaderProgramRelief.h"

class AssetTriangleShaderRelief : public AssetShader {

public:
    AssetTriangleShaderRelief(int id, std::string vertex_shader_code, std::string fragment_shader_code);

    void reload();
    void unload();

    GLint getPositionAttribute() const;
    GLint getTextureUniform() const;
};

#endif //GLOBEQUIZ_ASSETTRIANGLESHADERRELIEF_H
