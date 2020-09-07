//
// Created by thorsten on 19.05.20.
//

#ifndef GLOBEQUIZ_TIMER_H
#define GLOBEQUIZ_TIMER_H

#include <chrono>

class Timer {

public:

    void startTimer() {

        m_start_time = std::chrono::steady_clock::now();
    }

    long long int getElapsedTime() {

        auto now = std::chrono::steady_clock::now();
        return std::chrono::duration_cast<std::chrono::milliseconds>(now - m_start_time).count();
    }

private:

    std::chrono::time_point<std::chrono::steady_clock> m_start_time;
};

#endif //GLOBEQUIZ_TIMER_H
