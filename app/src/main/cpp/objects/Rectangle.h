//
// Created by schimannek on 2/12/18.
//

#ifndef GLOBE_RECTANGLE_H
#define GLOBE_RECTANGLE_H

#include <memory>

#include <GLES2/gl2.h>

class Rectangle {

    public:

        Rectangle(GLfloat x1, GLfloat y1, GLfloat x2, GLfloat y2, GLfloat z);
        ~Rectangle();

        void draw(GLuint vertex_attribute_location);

        GLuint m_vertex_buffer = 0;
};


#endif //GLOBE_RECTANGLE_H
