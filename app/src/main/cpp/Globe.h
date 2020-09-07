//
// Created by thorsten on 09.05.20.
//

#ifndef GLOBEQUIZ_GLOBE_H
#define GLOBEQUIZ_GLOBE_H

#include <cmath>
#include <tuple>

#include "utilities/Interpolator.h"

class Globe {

public:

    Globe();
    ~Globe();

    void setZoom(float zoom);
    void zoomTo(float zoom, float duration);
    void setRotation(float longitude, float latitude);
    void rotateTo(float longitude, float latitude, float duration);

    float getZoom();
    std::tuple<float, float> getRotation();

protected:

    Interpolator m_zoom;
    Interpolator m_longitude;
    Interpolator m_latitude;
};

#endif //GLOBEQUIZ_GLOBE_H
