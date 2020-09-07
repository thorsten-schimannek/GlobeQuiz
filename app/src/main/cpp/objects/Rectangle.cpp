//
// Created by schimannek on 2/12/18.
//

#include "Rectangle.h"

Rectangle::Rectangle(GLfloat x1, GLfloat y1, GLfloat x2, GLfloat y2, GLfloat z){

    auto vertices = std::make_unique<GLfloat[]>(6 * 3);

    vertices[3*0 + 0] = x1;
    vertices[3*0 + 1] = y1;
    vertices[3*0 + 2] = z;

    vertices[3*1 + 0] = x1;
    vertices[3*1 + 1] = y2;
    vertices[3*1 + 2] = z;

    vertices[3*2 + 0] = x2;
    vertices[3*2 + 1] = y2;
    vertices[3*2 + 2] = z;

    vertices[3*3 + 0] = x1;
    vertices[3*3 + 1] = y1;
    vertices[3*3 + 2] = z;

    vertices[3*4 + 0] = x2;
    vertices[3*4 + 1] = y2;
    vertices[3*4 + 2] = z;

    vertices[3*5 + 0] = x2;
    vertices[3*5 + 1] = y1;
    vertices[3*5 + 2] = z;

    glGenBuffers(1, &m_vertex_buffer);
    glBindBuffer(GL_ARRAY_BUFFER, m_vertex_buffer);
    glBufferData(GL_ARRAY_BUFFER, 3 * 6 * sizeof(GLfloat), vertices.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}

Rectangle::~Rectangle(){

    glDeleteBuffers(1, &m_vertex_buffer);
}

void Rectangle::draw(GLuint vertex_attribute_location) {

    glBindBuffer(GL_ARRAY_BUFFER, m_vertex_buffer);
    glVertexAttribPointer(vertex_attribute_location, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(vertex_attribute_location);

    glDrawArrays(GL_TRIANGLES, 0, 6);

    return;
}