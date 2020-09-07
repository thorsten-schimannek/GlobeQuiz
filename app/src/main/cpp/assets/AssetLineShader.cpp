//
// Created by thorsten on 16.05.20.
//

#include "AssetLineShader.h"

AssetLineShader::AssetLineShader(int id, std::string vertex_shader_code,
        std::string fragment_shader_code) :
        AssetShader {id, AssetShader::LINES, vertex_shader_code, fragment_shader_code} {

    reload();
}

void AssetLineShader::unload() {

    m_shader = nullptr;
}

void AssetLineShader::reload() {

    m_shader = std::make_unique<LineShaderProgram>(m_vertex_shader_code, m_fragment_shader_code);
}

void AssetLineShader::setThickness(GLfloat thickness) const {

    if(m_shader) {

        LineShaderProgram *lineShader = static_cast<LineShaderProgram*>(m_shader.get());

        lineShader->setThickness(thickness);
    }
}

GLint AssetLineShader::getPositionAttribute() const {

    if(m_shader) {

        LineShaderProgram *lineShader = static_cast<LineShaderProgram*>(m_shader.get());

        return lineShader->getPositionAttributeLocation();
    }

    return -1;
}

GLint AssetLineShader::getPreviousAttribute() const {

    if(m_shader) {

        LineShaderProgram *lineShader = static_cast<LineShaderProgram*>(m_shader.get());

        return lineShader->getPreviousAttributeLocation();
    }

    return -1;
}

GLint AssetLineShader::getNextAttribute() const {

    if(m_shader) {

        LineShaderProgram *lineShader = static_cast<LineShaderProgram*>(m_shader.get());

        return lineShader->getNextAttributeLocation();
    }

    return -1;
}

GLint AssetLineShader::getDirectionAttribute() const {

    if(m_shader) {

        LineShaderProgram *lineShader = static_cast<LineShaderProgram*>(m_shader.get());

        return lineShader->getDirectionAttributeLocation();
    }

    return -1;
}

