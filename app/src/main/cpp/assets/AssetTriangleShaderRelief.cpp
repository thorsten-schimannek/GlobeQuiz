//
// Created by thorsten on 14.11.20.
//

#include "AssetTriangleShaderRelief.h"

AssetTriangleShaderRelief::AssetTriangleShaderRelief(int id, std::string vertex_shader_code,
                                         std::string fragment_shader_code) :
        AssetShader {id, AssetShader::TRIANGLES_RELIEF, vertex_shader_code, fragment_shader_code} {

    m_shader = std::make_unique<TriangleShaderProgramRelief>(m_vertex_shader_code, m_fragment_shader_code);
}

void AssetTriangleShaderRelief::unload() {

    m_shader = nullptr;
}

void AssetTriangleShaderRelief::reload() {

    m_shader = std::make_unique<TriangleShaderProgramRelief>(m_vertex_shader_code, m_fragment_shader_code);
}

GLint AssetTriangleShaderRelief::getPositionAttribute() const {

    if(m_shader) {

        TriangleShaderProgramRelief *triangleShader
            = static_cast<TriangleShaderProgramRelief*>(m_shader.get());

        return triangleShader->getPositionAttributeLocation();
    }

    return -1;
}

GLint AssetTriangleShaderRelief::getTextureUniform() const {

    if(m_shader) {

        TriangleShaderProgramRelief *triangleShader
                = static_cast<TriangleShaderProgramRelief*>(m_shader.get());

        return triangleShader->getTextureUniformLocation();
    }

    return -1;
}
