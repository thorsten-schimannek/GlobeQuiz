//
// Created by thorsten on 09.05.20.
//

#include "Globe.h"

Globe::Globe() : m_zoom { 1. } {
}

Globe::~Globe() {
}

void Globe::setZoom(float zoom) { m_zoom.setConstant(zoom); }

void Globe::setRotation(float longitude, float latitude) {
    m_longitude.setConstant(longitude);
    m_latitude.setConstant(latitude);
}

void Globe::rotateTo(float longitude, float latitude, float duration) {

    m_longitude.interpolate(m_longitude.getValue(), longitude, duration);
    m_latitude.interpolate(m_latitude.getValue(), latitude, duration);
}

void Globe::zoomTo(float zoom, float duration) {

    m_zoom.interpolate(m_zoom.getValue(), zoom, duration);
}

float Globe::getZoom() { return m_zoom.getValue(); }

std::tuple<float, float> Globe::getRotation() {

    float longitude = m_longitude.getValue();
    float latitude = m_latitude.getValue();

    if(latitude > 90.f) {
        latitude = 90.f;
        m_latitude.setConstant(90.f);
    }
    else if(latitude < -90.f) {
        latitude = -90.f;
        m_latitude.setConstant(-90.f);
    }

    if(longitude > 180.f) {
        longitude = -360.f + std::fmod(longitude, 360.f);
        m_longitude.setConstant(longitude);
    }
    else if(longitude < -180.f) {
        longitude = 360.f + std::fmod(longitude, -360.f);
        m_longitude.setConstant(longitude);
    }

    return std::tie(longitude, latitude);
}