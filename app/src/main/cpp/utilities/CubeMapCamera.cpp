//
// Created by schimannek on 11.11.18.
//

#include "CubeMapCamera.h"

CubeMapCamera::CubeMapCamera(float center[3]): m_view_matrix(glm::mat4(1.f)) {

    m_center[0] = center[0];
    m_center[1] = center[1];
    m_center[2] = center[2];

    createProjectionMatrix();
}

CubeMapCamera::CubeMapCamera(float center_x, float center_y, float center_z)
    : m_view_matrix(glm::mat4(1.f)) {

    m_center[0] = center_x;
    m_center[1] = center_y;
    m_center[2] = center_z;

    createProjectionMatrix();
}

void CubeMapCamera::switchToFace(int faceIndex) {

    switch (faceIndex) {
        case 0:
            m_view_matrix = glm::lookAt(glm::vec3( 0.0f, 0.0f, 0.0f),
                                        glm::vec3( 1.0f, 0.0f, 0.0f),
                                        glm::vec3( 0.0f,-1.0f, 0.0f));
            break;
        case 1:
            m_view_matrix = glm::lookAt(glm::vec3( 0.0f, 0.0f, 0.0f),
                                        glm::vec3(-1.0f, 0.0f, 0.0f),
                                        glm::vec3( 0.0f,-1.0f, 0.0f));
            break;
        case 2:
            m_view_matrix = glm::lookAt(glm::vec3( 0.0f, 0.0f, 0.0f),
                                        glm::vec3( 0.0f, 1.0f, 0.0f),
                                        glm::vec3( 0.0f, 0.0f, 1.0f));
            break;
        case 3:
            m_view_matrix = glm::lookAt(glm::vec3( 0.0f, 0.0f, 0.0f),
                                        glm::vec3( 0.0f,-1.0f, 0.0f),
                                        glm::vec3( 0.0f, 0.0f,-1.0f));
            break;
        case 4:
            m_view_matrix = glm::lookAt(glm::vec3( 0.0f, 0.0f, 0.0f),
                                        glm::vec3( 0.0f, 0.0f, 1.0f),
                                        glm::vec3( 0.0f,-1.0f, 0.0f));
            break;
        case 5:
            m_view_matrix = glm::lookAt(glm::vec3( 0.0f, 0.0f, 0.0f),
                                        glm::vec3( 0.0f, 0.0f,-1.0f),
                                        glm::vec3( 0.0f,-1.0f, 0.0f));
            break;
    }
    updateViewMatrix();
}

void CubeMapCamera::getPosition(float *position) {

    position[0] = m_center[0];
    position[1] = m_center[1];
    position[2] = m_center[2];
}

void CubeMapCamera::getViewMatrix(glm::mat4 *view_matrix) {

    *view_matrix = m_view_matrix;
}

void CubeMapCamera::getProjectionMatrix(glm::mat4 *projection_matrix) {

    *projection_matrix = m_projection_matrix;
}

void CubeMapCamera::getProjectionViewMatrix(glm::mat4 *projection_view_matrix) {

    *projection_view_matrix = m_projection_view_matrix;
}

void CubeMapCamera::createProjectionMatrix() {

    m_projection_matrix = glm::perspective(m_FOV, m_ASPECT_RATIO, m_NEAR_PLANE, m_FAR_PLANE); //RH_NO
}

void CubeMapCamera::updateViewMatrix() {

    glm::mat4 view_matrix = m_view_matrix * glm::translate(glm::vec3(-m_center[0], -m_center[1], -m_center[2]));
    m_projection_view_matrix = m_projection_matrix * view_matrix;
}