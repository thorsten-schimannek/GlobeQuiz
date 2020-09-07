//
// Created by schimannek on 11.11.18.
//

#ifndef GLOBE_CUBEMAPCAMERA_H
#define GLOBE_CUBEMAPCAMERA_H

#define GLM_ENABLE_EXPERIMENTAL

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/constants.hpp>
#include <glm/gtx/transform.hpp>

class CubeMapCamera {

public:

    CubeMapCamera(float center[3]);
    CubeMapCamera(float center_x, float center_y, float center_z);

    void switchToFace(int faceIndex);

    void getPosition(float *position);
    void getViewMatrix(glm::mat4 *view_matrix);
    void getProjectionMatrix(glm::mat4 *projection_matrix);
    void getProjectionViewMatrix(glm::mat4 *projection_view_matrix);

    void createProjectionMatrix();
    void updateViewMatrix();

protected:

    float m_NEAR_PLANE = 0.1f;
    float m_FAR_PLANE = 3.0f;
    float m_FOV = glm::half_pi<float>();
    float m_ASPECT_RATIO = 1;

    float m_center[3];

    glm::mat4 m_projection_matrix;
    glm::mat4 m_view_matrix;
    glm::mat4 m_projection_view_matrix;
};


#endif //GLOBE_CUBEMAPCAMERA_H
