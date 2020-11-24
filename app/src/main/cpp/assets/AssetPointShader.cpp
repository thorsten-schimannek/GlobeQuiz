//
// Created by thorsten on 23.05.20.
//

#include "AssetPointShader.h"

AssetPointShader::AssetPointShader(int id, std::string vertex_shader_code,
                                 std::string fragment_shader_code) :
        AssetShader {id, AssetShader::POINTS, vertex_shader_code, fragment_shader_code} {

    m_shader = std::make_unique<PointShaderProgram>(m_vertex_shader_code, m_fragment_shader_code);
}

void AssetPointShader::unload() {

    m_shader = nullptr;
}

void AssetPointShader::reload() {

    m_shader = std::make_unique<PointShaderProgram>(m_vertex_shader_code, m_fragment_shader_code);
}

void AssetPointShader::setThickness(GLfloat thickness) const {

    if(m_shader) {

        PointShaderProgram *lineShader = static_cast<PointShaderProgram*>(m_shader.get());

        lineShader->setThickness(thickness);
    }
}

GLint AssetPointShader::getPositionAttribute() const {

    if(m_shader) {

        PointShaderProgram *lineShader = static_cast<PointShaderProgram*>(m_shader.get());

        return lineShader->getPositionAttributeLocation();
    }

    return -1;
}

GLint AssetPointShader::getDirectionAttribute() const {

    if(m_shader) {

        PointShaderProgram *lineShader = static_cast<PointShaderProgram*>(m_shader.get());

        return lineShader->getDirectionAttributeLocation();
    }

    return -1;
}

