//
// Created by thorsten on 09.05.20.
//

#ifndef GLOBEQUIZ_INTERPOLATOR_H
#define GLOBEQUIZ_INTERPOLATOR_H

#include <cmath>

#include "Timer.h"

class Interpolator {

public:

    const double epsilon = 0.0000001;

    enum Mode {
        CONSTANT,
        LINEAR
    };

    Interpolator() { m_current = 0; m_mode = CONSTANT; }
    Interpolator(double value) { m_current = value; m_mode = CONSTANT; }
    ~Interpolator() {}

    void interpolate(double start, double end, double duration) {

        m_start = start; m_end = end; m_duration = duration; m_mode = LINEAR;
        m_timer.startTimer();
    }

    Mode getMode() { return m_mode; }

    void setConstant(double value) { m_current = value; m_mode = CONSTANT; }

    double getValue() {

        double elapsed = m_timer.getElapsedTime() / 1000.;

        switch(m_mode) {

            case CONSTANT:
                break;

            case LINEAR:
                if(elapsed >= m_duration) {
                    m_current = m_end;
                    m_mode = CONSTANT;
                }
                else m_current = m_start + (m_end - m_start) * (elapsed / m_duration);
                break;
        }

        return m_current;
    }

private:

    Mode m_mode;
    Timer m_timer;

    double m_start; double m_current; double m_end;

    double m_duration;
};

#endif //GLOBEQUIZ_INTERPOLATOR_H
