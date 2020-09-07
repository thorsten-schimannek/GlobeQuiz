//
// Created by schimannek on 2/13/18.
//

#include "ShaderProgram.h"

ShaderProgram::ShaderProgram(std::string vertex_shader_code, std::string fragment_shader_code) {

    m_program = buildProgram(vertex_shader_code.c_str(), fragment_shader_code.c_str(),
                             vertex_shader_code.size(), fragment_shader_code.size());

    m_uniform_view_projection = glGetUniformLocation(m_program, UNIFORM_MATRIX);
    m_uniform_rotation = glGetUniformLocation(m_program, UNIFORM_ROTATION);
    m_uniform_zoom = glGetUniformLocation(m_program, UNIFORM_ZOOM);
    m_uniform_color = glGetUniformLocation(m_program, UNIFORM_COLOR);
    m_uniform_direct = glGetUniformLocation(m_program, UNIFORM_DIRECT);
}

ShaderProgram::~ShaderProgram() {

    if(m_program != 0) glDeleteProgram(m_program);
}

void ShaderProgram::setMatrices(const glm::mat4& viewProjection, const glm::mat4& rotation) {

    glUniformMatrix4fv(m_uniform_view_projection, 1, false, (const GLfloat*)glm::value_ptr(viewProjection));
    glUniformMatrix4fv(m_uniform_rotation, 1, false, (const GLfloat*)glm::value_ptr(rotation));
}

void ShaderProgram::setColor(const glm::vec4& color) {

    glUniform4fv(m_uniform_color, 1, glm::value_ptr(color));
}

void ShaderProgram::setZoom(GLfloat zoom) {

    glUniform1f(m_uniform_zoom, zoom);
}

void ShaderProgram::setDirectRendering(bool direct) {

    glUniform1i(m_uniform_direct, direct);
}


GLuint ShaderProgram::compileShader(GLuint shader_type, const char* shader_code, int code_length){

    GLuint shader_id = glCreateShader(shader_type);

    if(shader_id == 0){

        LOG("compileShader", "glCreateShader(%d) failed.", shader_type);

        return 0;
    }

    GLint shader_code_length[] = {code_length};

    glShaderSource(shader_id, 1, (const GLchar* const*) &shader_code, shader_code_length);
    glCompileShader(shader_id);

    GLint compile_status;
    glGetShaderiv(shader_id, GL_COMPILE_STATUS, &compile_status);

    if(compile_status == GL_FALSE){

        LOG("compileShader", "glCompileShader(%d) failed.", shader_id);

        GLint info_size = 0;
        glGetShaderiv(shader_id, GL_INFO_LOG_LENGTH, &info_size);

        GLchar* info_log = new GLchar[info_size];
        glGetShaderInfoLog(shader_id, info_size, NULL, info_log);

        LOG("compileShader", "Log: %s", info_log);
        delete[] info_log;

        glDeleteShader(shader_id);

        return 0;
    }

    return shader_id;
}

GLuint ShaderProgram::linkProgram(GLuint vertex_shader_id, GLuint fragment_shader_id){

    GLuint program_id = glCreateProgram();

    if(program_id == 0){

        LOG("linkProgram", "glCreateProgram() failed.");

        return 0;
    }

    glAttachShader(program_id, vertex_shader_id);
    glDeleteShader(vertex_shader_id);

    glAttachShader(program_id, fragment_shader_id);
    glDeleteShader(fragment_shader_id);

    glLinkProgram(program_id);

    GLint link_status;
    glGetProgramiv(program_id, GL_LINK_STATUS, &link_status);

    if(link_status == 0){

        LOG("linkProgram", "glLinkProgram(%d) failed.", program_id);
        glDeleteProgram(program_id);

        return 0;
    }

    return program_id;
}

bool ShaderProgram::validateProgram(GLuint program_id){

    glValidateProgram(program_id);

    GLint validate_status;
    glGetProgramiv(program_id, GL_VALIDATE_STATUS, &validate_status);

    if(validate_status == 0){

        GLsizei info_size;
        glGetProgramiv(program_id, GL_INFO_LOG_LENGTH, &info_size);

        GLchar* info_log = new GLchar[info_size];

        glGetProgramInfoLog(program_id, info_size, NULL, info_log);

        LOG("validateProgram", "Results of validating program: %d\nLog: %s", validate_status, info_log);

        delete[] info_log;
    }

    return validate_status != 0;
}

GLuint ShaderProgram::buildProgram(const char* vertex_shader_code, const char* fragment_shader_code,
                                   int vertex_shader_length, int fragment_shader_length){

    GLuint vertex_shader_id = compileShader(GL_VERTEX_SHADER, vertex_shader_code, vertex_shader_length);
    GLuint fragment_shader_id = compileShader(GL_FRAGMENT_SHADER, fragment_shader_code, fragment_shader_length);

    GLuint program_id = linkProgram(vertex_shader_id, fragment_shader_id);

    validateProgram(program_id);

    return program_id;
}