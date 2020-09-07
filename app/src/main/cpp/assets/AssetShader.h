//
// Created by thorsten on 16.05.20.
//

#ifndef GLOBEQUIZ_ASSETSHADER_H
#define GLOBEQUIZ_ASSETSHADER_H

#include <string>

#include <glm/glm.hpp>

#include "shaders/ShaderProgram.h"

class AssetShader {

public:

    enum ShaderType {
        TRIANGLES = 0,
        LINES = 1,
        POINTS = 2,
        UNKNOWN = 3
    };

    virtual void reload() = 0;
    virtual void unload() = 0;

    AssetShader(int id, ShaderType type,  std::string vertex_shader_code,
            std::string fragment_shader_code) :
            m_id {id}, m_type {type},
            m_vertex_shader_code {vertex_shader_code},
            m_fragment_shader_code {fragment_shader_code}{};

    virtual ~AssetShader() {};

    int getId() const { return m_id; }

    void setMatrices(const glm::mat4& view_projection, const glm::mat4& rotation) const {

        m_shader->setMatrices(view_projection, rotation);
    }

    void setColor(const glm::vec4& color) const {

        m_shader->setColor(color);
    }

    void setZoom(GLfloat zoom) const {

        m_shader->setZoom(zoom);
    }

    void setDirectRendering(bool direct) const {

        m_shader->setDirectRendering(direct);
    }

    void useProgram() const {

        if(m_shader) m_shader->useProgram();
    }

protected:

    std::string m_vertex_shader_code;
    std::string m_fragment_shader_code;

    int m_id;
    ShaderType  m_type;

    std::unique_ptr<ShaderProgram> m_shader = nullptr;
};

#endif //GLOBEQUIZ_ASSETSHADER_H
