//
// Created by thorsten on 15.05.20.
//

#ifndef GLOBEQUIZ_ASSETTRIANGLESHADER_H
#define GLOBEQUIZ_ASSETTRIANGLESHADER_H

#include <string>
#include <memory>

#include <GLES2/gl2.h>

#include "assets/AssetShader.h"

#include "shaders/TriangleShaderProgram.h"

class AssetTriangleShader : public AssetShader {

public:
    AssetTriangleShader(int id, std::string vertex_shader_code, std::string fragment_shader_code);

    void reload();
    void unload();

    GLint getPositionAttribute() const;
};

#endif //GLOBEQUIZ_ASSETTRIANGLESHADER_H
