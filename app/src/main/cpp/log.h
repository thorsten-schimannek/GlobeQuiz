//
// Created by schimannek on 2/13/18.
//

#ifndef GLOBE_LOG_H
#define GLOBE_LOG_H

#include <android/log.h>

#define LOG(tag, ...) __android_log_print(ANDROID_LOG_INFO, tag, __VA_ARGS__)

#endif //GLOBE_LOG_H
