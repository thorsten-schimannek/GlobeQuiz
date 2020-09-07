//
// Created by thorsten on 15.05.20.
//

#include "AssetTriangleShader.h"

AssetTriangleShader::AssetTriangleShader(int id, std::string vertex_shader_code,
        std::string fragment_shader_code) :
        AssetShader {id, AssetShader::TRIANGLES, vertex_shader_code, fragment_shader_code} {

    reload();
}

void AssetTriangleShader::unload() {

    m_shader = nullptr;
}

void AssetTriangleShader::reload() {

    m_shader = std::make_unique<TriangleShaderProgram>(m_vertex_shader_code, m_fragment_shader_code);
}

GLint AssetTriangleShader::getPositionAttribute() const {

    if(m_shader) {

        TriangleShaderProgram *triangleShader = static_cast<TriangleShaderProgram*>(m_shader.get());

        return triangleShader->getPositionAttributeLocation();
    }

    return -1;
}
