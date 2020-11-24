//
// Created by schimannek on 2/13/18.
//

#ifndef GLOBE_SHADER_H
#define GLOBE_SHADER_H

#include <GLES2/gl2.h>

#include <cstring>
#include <tuple>
#include <string>

#include <glm/glm/glm.hpp>
#include <glm/glm/gtc/type_ptr.hpp>

#include "log.h"

class ShaderProgram {

public:

    ShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code);
    ~ShaderProgram();

    void useProgram() {

        glUseProgram(m_program);
    }

    void setMatrices(const glm::mat4& viewProjection, const glm::mat4& rotation);
    void setColor(const glm::vec4& color);
    void setZoom(GLfloat zoom);
    void setDirectRendering(bool direct);

protected:

    static constexpr auto UNIFORM_MATRIX = "u_ViewProjectionMatrix";
    static constexpr auto UNIFORM_ROTATION = "u_RotationMatrix";
    static constexpr auto UNIFORM_ZOOM = "u_Zoom";
    static constexpr auto UNIFORM_COLOR = "u_Color";
    static constexpr auto UNIFORM_DIRECT = "u_directRendering";

    int m_program {0};

    GLint m_uniform_view_projection;
    GLint m_uniform_rotation;
    GLint m_uniform_zoom;
    GLint m_uniform_direct;
    GLint m_uniform_color;

private:

    GLuint compileShader(GLuint shader_type, const char* shader_code, int code_length);
    GLuint linkProgram(GLuint vertex_shader_id, GLuint fragment_shader_id);
    bool validateProgram(GLuint program_id);
    GLuint buildProgram(const char* vertex_shader_code, const char* fragment_shader_code,
                        int vertex_shader_length, int fragment_shader_length);
};


#endif //GLOBE_SHADER_H
