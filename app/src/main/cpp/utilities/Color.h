//
// Created by thorsten on 07.05.20.
//

#ifndef GLOBEQUIZ_COLOR_H
#define GLOBEQUIZ_COLOR_H

#include <glm/glm.hpp>

class Color {

public:

    Color(float cr, float cg, float cb, float ca) : r{cr}, g{cg}, b{cb}, a{ca} {}
    Color(float rgba[4]) : r{rgba[0]}, g{rgba[1]}, b{rgba[2]}, a{rgba[3]} {}
    Color(double rgba[4]) : r{(float)rgba[0]}, g{(float)rgba[1]}, b{(float)rgba[2]}, a{(float)rgba[3]} {}

    void setAlpha(float alpha) { a = alpha; }

    glm::vec4 getVector() { return glm::vec4 { r, g, b, a }; }

    float r, g, b, a;
};

#endif //GLOBEQUIZ_COLOR_H
