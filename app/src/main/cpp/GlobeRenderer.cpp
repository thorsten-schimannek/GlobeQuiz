//
// Created by schimannek on 2/12/18.
//

#include <utilities/RenderTarget.h>
#include "GlobeRenderer.h"

GlobeRenderer::GlobeRenderer(unsigned int layers) : m_layers {layers} {

    for(int i = 0; i < m_layers; i++) {

        m_cubemap_invalid.push_back(true);

        m_triangle_assets.push_back(std::vector<std::tuple<int, int, Color>> {});
        m_line_assets.push_back(std::vector<std::tuple<int, int, Color>> {});
        m_point_assets.push_back(std::vector<std::tuple<int, int, Color>> {});
    }
}

void GlobeRenderer::clear(unsigned int layer) {

   if(layer < m_layers) {

       m_triangle_assets[layer].clear();
       m_line_assets[layer].clear();
       m_point_assets[layer].clear();
       m_cubemap_invalid[layer] = true;
   }
}

void GlobeRenderer::setAssetManager(std::shared_ptr<AssetManager> assetManager){

    if(!m_asset_manager) m_asset_manager = std::move(assetManager);

    initialize();
}

void GlobeRenderer::initialize() {

    // Since the GL context might be lost, it can happen that e.g. Rectangle() generates a new
    // vertex buffer id that is identical to the old one. The destructor of the old
    // Rectangle object would then immediately delete this newly created vertex buffer.
    // For this reason we make sure that the destructor of the old object is called first.
    m_rectangle = nullptr;
    m_shader_program_rectangle = nullptr;
    m_shader_program_rectangle_relief = nullptr;
    m_color_picking_target = nullptr;
    m_cubemaps.clear();

    m_asset_manager->unloadAssets();

    m_rectangle = std::make_unique<Rectangle>(-1.f, -1.f, 1.f, 1.f, 1.f);

    m_shader_program_rectangle = std::make_unique<GlobeRectangleShaderProgram>(
            m_asset_manager->getContentString("shader/rectangle_vertex_shader.glsl"),
            m_asset_manager->getContentString("shader/rectangle_fragment_shader.glsl"));

    m_shader_program_rectangle_relief = std::make_unique<GlobeRectangleShaderProgramRelief>(
            m_asset_manager->getContentString("shader/rectangle_relief_vertex_shader.glsl"),
            m_asset_manager->getContentString("shader/rectangle_relief_fragment_shader.glsl"));

    m_color_picking_target = std::make_unique<RenderTarget>(m_width, m_height);

    for(int i = 0; i < m_layers; i++) m_cubemaps.push_back(std::make_unique<CubeMap>(m_cubemap_size));
    std::fill(m_cubemap_invalid.begin(), m_cubemap_invalid.end(), true);

    GLint max_size;
    glGetIntegerv(GL_MAX_TEXTURE_SIZE, &max_size);
    m_max_texture_size = static_cast<int>(max_size);

    if(m_relief_texture_filename == nullptr) {
        if (m_max_texture_size >= 8192) setReliefTexture("relief_high.png");
        else if (m_max_texture_size >= 4096) setReliefTexture("relief_medium.png");
        else setReliefTexture("relief_low.png");
    }
    else setReliefTexture(*m_relief_texture_filename);

    loadAssets();
}

void GlobeRenderer::loadAssets() {

    if(m_asset_manager) {

        m_asset_manager->reloadAssets();

        m_triangle_shader_id = m_asset_manager->loadShaderAsset(AssetShader::TRIANGLES,
                "shader/triangle_vertex_shader.glsl",
                "shader/triangle_fragment_shader.glsl");

        m_triangle_shader_relief_id = m_asset_manager->loadShaderAsset(AssetShader::TRIANGLES_RELIEF,
            "shader/triangle_vertex_shader.glsl",
            "shader/triangle_relief_fragment_shader.glsl");

        m_line_shader_id = m_asset_manager->loadShaderAsset(AssetShader::LINES,
                "shader/line_vertex_shader.glsl",
                "shader/line_fragment_shader.glsl");

        m_point_shader_id = m_asset_manager->loadShaderAsset(AssetShader::POINTS,
                "shader/point_vertex_shader.glsl",
                "shader/point_fragment_shader.glsl");

        processAssetStack();
    }
}

void GlobeRenderer::processAssetStack() {

    while (!m_asset_loading_stack.empty()) {

        auto asset = m_asset_loading_stack.top();

        showAsset(std::get<0>(asset), std::get<1>(asset), std::get<2>(asset), std::get<3>(asset));

        m_asset_loading_stack.pop();
    }
}

void GlobeRenderer::setReliefTexture(std::string filename) {

    m_relief_texture_filename = std::make_unique<std::string>(filename);

    if(m_asset_manager != nullptr) {
        m_relief_texture_id = m_asset_manager->loadTextureAsset(
                AssetTexture::TEXTURE_2D, filename);
    }
}

void GlobeRenderer::showReliefTexture() {

    m_show_relief_texture = true;
    if(m_cubemap_invalid.size() > 0) m_cubemap_invalid[0] = true;
}

void GlobeRenderer::hideReliefTexture() {

    m_show_relief_texture = false;
    if(m_cubemap_invalid.size() > 0) m_cubemap_invalid[0] = true;
}

void GlobeRenderer::showAsset(unsigned int layer, std::string file, Color color) {

    showAsset(layer, file, -1, color);
}

void GlobeRenderer::showAsset(unsigned int layer, std::string file, int id, Color color) {

    if(layer > m_layers - 1) return;

    if(m_asset_manager == nullptr) {
        // Put request on asset stack
        m_asset_loading_stack.push(std::make_tuple<>(layer, file, id, color));
    }
    else {

        int assetId = m_asset_manager->loadGeometryAsset(file);
        AssetGeometry::GeometryType type = m_asset_manager->getGeometryAssetType(assetId);

        switch(type) {

            case AssetGeometry::TRIANGLES: {
                m_triangle_assets[layer].push_back(std::make_tuple(assetId, id, color));
            } break;

            case AssetGeometry::LINES: {
                m_line_assets[layer].push_back(std::make_tuple(assetId, id, color));
            } break;

            case AssetGeometry::POINTS: {
                m_point_assets[layer].push_back(std::make_tuple(assetId, id, color));
            } break;

            case AssetGeometry::UNKNOWN:
                break;
        }

        m_cubemap_invalid[layer] = true;
    }
}

void GlobeRenderer::setDimensions(int width, int height) {

    m_width = width;
    m_height = height;

    glViewport(0, 0, width, height);

    float aspect = (float) width / (float) height;

    m_switch_zoom = getSwitchZoom();

    glm::mat4 view = glm::lookAt(glm::vec3(0.f, 0.f, -5.f),
                                 glm::vec3(0.f, 0.f, 0.f),
                                 glm::vec3(0.f, 1.f, 0.f));

    // fov = 2.f
    m_orthographic_view_projection_matrix = glm::ortho(-2.f * aspect, 2.f * aspect,
            -2.f, 2.f, .1f, 10.f) * view;

    m_color_picking_target = nullptr;
    m_color_picking_target = std::make_unique<RenderTarget>(m_width, m_height);

    return;
}

float GlobeRenderer::getSwitchZoom() {

    constexpr float height = 2.f;
    float aspect = static_cast<float>(m_width) / m_height;

    return std::max(height * std::sqrt(2.f), height * aspect * std::sqrt(2.f));
}

bool GlobeRenderer::globeCoversScreen(){

    constexpr float height = 2.f;
    float aspect = static_cast<float>(m_width) / m_height;

    float zoom = m_globe.getZoom();

    if(aspect < 1.) return height < zoom / std::sqrt(2.f);
    else return height * aspect < zoom / std::sqrt(2.f);
}

bool GlobeRenderer::isPatchVisible(int longi, int lati, const glm::mat4& rotation, float zoom){

    glm::vec4 corners[4];

    for(int i = 0; i < 2; i++){
        for(int j = 0; j < 2; j++){

            int index = i * 2 + j;
            float phi, theta;

            phi = (longi + i) * (30.f / 360.f) * 2.f * m_pi - m_pi;
            theta = (lati + j) * (30.f / 360.f) * 2.f * m_pi - m_pi / 2.f;

            glm::vec4 worldSpace {
                    -(std::cos(theta) * std::sin(phi)),
                    std::sin(theta),
                    std::cos(theta) * std::cos(phi),
                    1.f
            };

            corners[index] = rotation * worldSpace;
            corners[index].x = corners[index].x * zoom;
            corners[index].y = corners[index].y * zoom;
            corners[index] = m_orthographic_view_projection_matrix * corners[index];
        }
    }

    bool top = (corners[0].y > 1.0) && (corners[1].y > 1.0) && (corners[2].y > 1.0) && (corners[3].y > 1.0);
    bool bottom = (corners[0].y < -1.0) && (corners[1].y < -1.0) && (corners[2].y < -1.0) && (corners[3].y < -1.0);
    bool left = (corners[0].x > 1.0) && (corners[1].x > 1.0) && (corners[2].x > 1.0) && (corners[3].x > 1.0);
    bool right = (corners[0].x < -1.0) && (corners[1].x < -1.0) && (corners[2].x < -1.0) && (corners[3].x < -1.0);
    bool back = (corners[0].z < 0.f) && (corners[1].z < 0.f) && (corners[2].z < 0.f) && (corners[3].z < 0.f);

    return !(top || bottom || left || right || back);
}

void GlobeRenderer::drawFrame() {

    m_fps = 1000.f / (float) m_frame_timer.getElapsedTime();
    m_frame_timer.startTimer();

    if(m_asset_manager == nullptr) return;

    if(m_show_relief_texture) {
        glActiveTexture(GL_TEXTURE0);
        glUniform1i(m_asset_manager->getTriangleShaderRelief(m_triangle_shader_relief_id).getTextureUniform(), 0);
        glUniform1i(m_shader_program_rectangle_relief->getTextureUniformLocation(), 0);
        m_asset_manager->getTexture(m_relief_texture_id)->bindTexture(GL_TEXTURE_2D);
    }

    updateCubeMaps();
    drawGlobe();
}

void GlobeRenderer::updateCubeMaps() {

    if(m_cubemap_invalid[0]) {

        updateCubeMap(0, m_ocean_color, m_show_relief_texture);
        m_cubemap_invalid[0] = false;
    }

    for(int i = 1; i < m_layers; i++) {
        if(m_cubemap_invalid[i]) {

            updateCubeMap(i, Color(0.f, 0.f, 0.f, 0.f));
            m_cubemap_invalid[i] = false;
        }
    }
}

void GlobeRenderer::drawGlobe() {

    auto [xRotation, yRotation] = m_globe.getRotation();
    float zoom = m_globe.getZoom();

    glDisable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    if(globeCoversScreen()) drawGlobeDirectly(xRotation, yRotation, zoom);
    else drawGlobeFromCubeMap(xRotation, yRotation, zoom);

    glDisable(GL_BLEND);
    glEnable(GL_CULL_FACE);
}

void GlobeRenderer::drawGlobeFromCubeMap(float x_rotation, float y_rotation, float zoom) {

    Color& bg = m_background_color;
    glClearColor(bg.r, bg.g, bg.b, bg.a);

    auto rotation_matrix = glm::rotate(glm::radians(-x_rotation), glm::vec3(0.f,1.f,0.f));
    rotation_matrix = rotation_matrix * glm::rotate(glm::radians(-y_rotation), glm::vec3(1.f,0.f,0.f));

    m_shader_program_rectangle->useProgram();

    m_shader_program_rectangle->setMatrices(m_orthographic_view_projection_matrix, rotation_matrix);
    m_shader_program_rectangle->setColor(glm::vec4(bg.r, bg.g, bg.g, bg.a));
    m_shader_program_rectangle->setZoom(zoom);
    m_shader_program_rectangle->setDirectRendering(false);

    for(int i = 0; i < m_layers; i++) {

        m_shader_program_rectangle->setCubeMapTexture(m_cubemaps[i]->getTexture());
        m_rectangle->draw(m_shader_program_rectangle->getPositionAttributeLocation());
    }
}

void GlobeRenderer::drawGlobeDirectly(float x_rotation, float y_rotation, float zoom) {

    auto rotation_matrix = glm::rotate(glm::radians(y_rotation), glm::vec3(1.f,0.f,0.f));
    rotation_matrix = rotation_matrix * glm::rotate(glm::radians(x_rotation), glm::vec3(0.f,1.f,0.f));

    Color &ocean = m_ocean_color;
    if(m_show_relief_texture) {

        m_shader_program_rectangle_relief->useProgram();
        m_shader_program_rectangle_relief->setMatrices(m_orthographic_view_projection_matrix,
                                                glm::inverse(rotation_matrix));
        m_shader_program_rectangle_relief->setColor(glm::vec4(ocean.r, ocean.g, ocean.b, ocean.a));
        m_shader_program_rectangle_relief->setZoom(zoom);
        m_shader_program_rectangle_relief->setDirectRendering(true);
        m_rectangle->draw(m_shader_program_rectangle_relief->getPositionAttributeLocation());
    }
    else {

        m_shader_program_rectangle->useProgram();
        m_shader_program_rectangle->setMatrices(m_orthographic_view_projection_matrix,
                                                rotation_matrix);
        m_shader_program_rectangle->setColor(glm::vec4(ocean.r, ocean.g, ocean.b, ocean.a));
        m_shader_program_rectangle->setZoom(zoom);
        m_shader_program_rectangle->setDirectRendering(true);
        m_shader_program_rectangle->setCubeMapTexture(0);
        m_rectangle->draw(m_shader_program_rectangle->getPositionAttributeLocation());
    }

    glEnable(GL_CULL_FACE);

    for(int i = 0; i < m_layers; i++) {

        int triangleShaderId = m_show_relief_texture ? m_triangle_shader_relief_id : m_triangle_shader_id;
        AssetShader* triangleShader = m_asset_manager->getShader(triangleShaderId);

        triangleShader->useProgram();
        triangleShader->setMatrices(m_orthographic_view_projection_matrix, rotation_matrix);
        triangleShader->setZoom(zoom);
        triangleShader->setDirectRendering(true);

        glCullFace(GL_FRONT);
        drawVisiblePatches(rotation_matrix, zoom, *triangleShader, m_triangle_assets[i]);

        const AssetLineShader &lineShader = m_asset_manager->getLineShader(m_line_shader_id);

        lineShader.useProgram();
        lineShader.setMatrices(m_orthographic_view_projection_matrix, rotation_matrix);
        lineShader.setZoom(zoom);
        lineShader.setDirectRendering(true);
        lineShader.setThickness(.001f * (m_switch_zoom / zoom));

        glCullFace(GL_BACK);
        drawVisiblePatches(rotation_matrix, zoom, lineShader, m_line_assets[i]);

        const AssetPointShader &pointShader = m_asset_manager->getPointShader(m_point_shader_id);

        pointShader.useProgram();
        pointShader.setMatrices(m_orthographic_view_projection_matrix, rotation_matrix);
        pointShader.setZoom(zoom);
        pointShader.setDirectRendering(true);

        glCullFace(GL_FRONT);
        pointShader.setThickness(.015f * (m_switch_zoom / zoom));
        drawVisiblePatches(rotation_matrix, zoom, pointShader,
                m_point_assets[i], Color(0.f, 0.f, 0.f, 1.f));

        pointShader.setThickness(.01f * (m_switch_zoom / zoom));
        drawVisiblePatches(rotation_matrix, zoom, pointShader, m_point_assets[i]);
    }

    glDisable(GL_CULL_FACE);
}

void GlobeRenderer::updateCubeMap(unsigned int layer, Color background, bool relief) {

    CubeMap& cubemap = *m_cubemaps[layer];

    cubemap.bind();
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_CULL_FACE);

    for(int face = 0; face < 6; face++) {

        cubemap.switchToFace(face);

        Color& bg = background; // Just for readability
        glClearColor(bg.r, bg.g, bg.b, bg.a);
        glClear(GL_COLOR_BUFFER_BIT);
        glDepthMask(GL_FALSE);

        glm::mat4 projectionView = cubemap.getProjectionViewMatrix();
        auto identityMatrix = glm::mat4(1.0f);

        if(relief) {

            // This is a bit of a hack and should eventually be refactored

            glm::mat4 rectView = cubemap.getCamera().getViewMatrix();

            glm::mat4 projection;
            if(face == 0) projection = glm::diagonal4x4(glm::vec4(-1.f, +1.f, -1.f, 1.f));
            else if(face == 1) projection = glm::diagonal4x4(glm::vec4(-1.f, +1.f, -1.f, 1.f));
            else projection = identityMatrix;

            m_shader_program_rectangle_relief->useProgram();
            m_shader_program_rectangle_relief->setMatrices(
                    projection, cubemap.getCamera().getViewMatrix());
            m_shader_program_rectangle_relief->setColor(glm::vec4(bg.r, bg.g, bg.b, bg.a));
            m_shader_program_rectangle_relief->setZoom(1.f);
            m_shader_program_rectangle_relief->setDirectRendering(false);
            m_rectangle->draw(
                    m_shader_program_rectangle_relief->getPositionAttributeLocation());
        }

        int triangleShaderId = m_show_relief_texture ? m_triangle_shader_relief_id : m_triangle_shader_id;
        AssetShader* triangleShader = m_asset_manager->getShader(triangleShaderId);

        triangleShader->useProgram();
        triangleShader->setMatrices(projectionView, identityMatrix);
        triangleShader->setZoom(1.f);
        triangleShader->setDirectRendering(false);

        auto& assetTriangleList = m_triangle_assets[layer];
        drawAssets(*triangleShader, assetTriangleList);

        const AssetLineShader& lineShader
                = m_asset_manager->getLineShader(m_line_shader_id);

        lineShader.useProgram();
        lineShader.setMatrices(projectionView, identityMatrix);
        lineShader.setZoom(1.f);
        lineShader.setDirectRendering(false);

        auto& assetLineList = m_line_assets[layer];

        glBlendFunc(GL_ONE, GL_ZERO);
        lineShader.setThickness(.002f);
        drawAssets(lineShader, assetLineList, true);

        lineShader.setThickness(.001f);
        drawAssets(lineShader, assetLineList);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        const AssetPointShader& pointShader
                = m_asset_manager->getPointShader(m_point_shader_id);

        pointShader.useProgram();
        pointShader.setMatrices(projectionView, identityMatrix);
        pointShader.setZoom(1.f);
        pointShader.setDirectRendering(false);

        auto& assetPointList = m_point_assets[layer];

        pointShader.setThickness(.015f);
        drawAssets(pointShader, assetPointList, Color(0.f, 0.f, 0.f, 1.f));

        pointShader.setThickness(.01f);
        drawAssets(pointShader, assetPointList);
    }

    glEnable(GL_CULL_FACE);
    glDisable(GL_BLEND);
    cubemap.unbind();
}

std::tuple<float, float, float> GlobeRenderer::getWorldSpaceFromScreenCoordinates(float x, float y) {

    float rx = (2.f * x / m_width - 1.f);
    float ry = -(2.f * y / m_height - 1.f);

    auto inverseProjection = glm::inverse(m_orthographic_view_projection_matrix);
    glm::vec4 worldCoordinates = inverseProjection * glm::vec4(rx, ry, 0.f, 1.f);

    auto [xRotation, yRotation] = m_globe.getRotation();
    float zoom = m_globe.getZoom();

    worldCoordinates.x = worldCoordinates.x / zoom;
    worldCoordinates.y = worldCoordinates.y / zoom;

    float xylength = glm::length(glm::vec2(worldCoordinates.x, worldCoordinates.y));
    worldCoordinates.z = std::sqrt(1.f - xylength * xylength);

    auto rotationMatrix = glm::rotate(glm::radians(yRotation), glm::vec3(1.f,0.f,0.f));
    rotationMatrix = rotationMatrix * glm::rotate(glm::radians(xRotation), glm::vec3(0.f,1.f,0.f));
    auto inverseRotationMatrix = glm::inverse(rotationMatrix);

    worldCoordinates = inverseRotationMatrix * worldCoordinates;

    return std::tie(worldCoordinates.x, worldCoordinates.y, worldCoordinates.z);
}

std::tuple<int, int> GlobeRenderer::getGridRegionFromWorldSpacePoint(float x, float y, float z) {

    if(glm::length(glm::vec2(x, y)) < 1.f){

        float candidatePhi1, candidatePhi2, phi;

        float theta = std::asin(y);
        candidatePhi1 = std::asin(-x / std::cos(theta));

        if(candidatePhi1 < 0.f) candidatePhi2 = -m_pi - candidatePhi1;
        else candidatePhi2 = m_pi - candidatePhi1;

        float z1 = std::cos(candidatePhi1) * std::cos(theta);
        float z2 = std::cos(candidatePhi2) * std::cos(theta);

        if(std::abs(z1 - z) < std::abs(z2 - z)) phi = candidatePhi1;
        else phi = candidatePhi2;

        phi = glm::degrees(phi);
        theta = glm::degrees(theta);

        int lon = std::floor((180.f + phi) / 30.f);
        int lat = std::floor((90.f + theta) / 30.f);

        return std::tie(lon, lat);
    }

    return std::make_tuple<int, int>(-1, -1);
}

int GlobeRenderer::getAssetRegionFromPoint(std::string file, float x, float y) {

    constexpr int colorSteps = 32;

    auto [wsx, wsy, wsz] = getWorldSpaceFromScreenCoordinates(x, y);
    auto [longi, lati] = getGridRegionFromWorldSpacePoint(wsx, wsy, wsz);

    int assetId = m_asset_manager->loadGeometryAsset(file);

    if(m_asset_manager->getGeometryAssetType(assetId) != AssetGeometry::TRIANGLES)
        return -1;

    int numRegions = m_asset_manager->getGeometryAssetEntryNumber(assetId);

    if(numRegions > colorSteps * colorSteps * colorSteps) return -1;

    auto [xRotation, yRotation] = m_globe.getRotation();
    auto rotation_matrix = glm::rotate(glm::radians(yRotation), glm::vec3(1.f,0.f,0.f));
    rotation_matrix = rotation_matrix * glm::rotate(glm::radians(xRotation), glm::vec3(0.f,1.f,0.f));

    const AssetTriangleShader& triangleShader = m_asset_manager->getTriangleShader(m_triangle_shader_id);

    m_color_picking_target->bind();

    triangleShader.useProgram();
    triangleShader.setMatrices(m_orthographic_view_projection_matrix, rotation_matrix);
    triangleShader.setZoom(m_globe.getZoom());
    triangleShader.setDirectRendering(false);

    glEnable(GL_CULL_FACE);
    glDisable(GL_DEPTH_TEST);
    glCullFace(GL_FRONT);

    glClearColor(0.f, 0.f, 0.f, 0.f);
    glClear(GL_COLOR_BUFFER_BIT);

    for(int i = 0; i < numRegions; i++) {

        float r = static_cast<float>(i % colorSteps) / colorSteps;
        float g = static_cast<float>((i % (colorSteps * colorSteps)) / colorSteps) / colorSteps;
        float b = static_cast<float>((i % (colorSteps * colorSteps * colorSteps))
                / (colorSteps * colorSteps)) / colorSteps;

        triangleShader.setColor(glm::vec4 {r, g, b, 1.f});
        m_asset_manager->draw(m_triangle_shader_id, assetId, i, longi, lati);
    }

    unsigned char pixel[4];
    glReadPixels(std::round(x), m_height - std::round(y), 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, (void*)&pixel);

    m_color_picking_target->unbind();

    int index = std::round((pixel[0] * colorSteps) / 256.f);
    index += std::round((pixel[1] * colorSteps * colorSteps) / 256.f);
    index += std::round((pixel[2] * colorSteps * colorSteps * colorSteps) / 256.f);

    if(pixel[3] < 128) return -1;
    else return index;
}

void GlobeRenderer::drawVisiblePatches(const glm::mat4& rotation, float zoom, const AssetShader& shader,
                                       const std::vector<std::tuple<int, int, Color>>& assets) {

    for(int longi = 0; longi < 12; longi++)
        for (int lati = 0; lati < 6; lati++)
            if(isPatchVisible(longi, lati, rotation, zoom))
                drawAssets(shader, assets, longi, lati);
}

void GlobeRenderer::drawVisiblePatches(const glm::mat4& rotation, float zoom, const AssetShader& shader,
                                       const std::vector<std::tuple<int, int, Color>>& assets,
                                       Color force_color) {

    for(int longi = 0; longi < 12; longi++)
        for (int lati = 0; lati < 6; lati++)
            if(isPatchVisible(longi, lati, rotation, zoom))
                drawAssets(shader, assets, longi, lati, force_color);
}

void GlobeRenderer::drawAssets(const AssetShader& shader,
                               const std::vector<std::tuple<int, int, Color>>& assets, bool zero_alpha) {

    for(auto asset : assets) {

        Color color = std::get<2>(asset);
        if(zero_alpha) color.setAlpha(0.f);
        shader.setColor(color.getVector());

        m_asset_manager->draw(shader.getId(), std::get<0>(asset), std::get<1>(asset));
    }
}

void GlobeRenderer::drawAssets(const AssetShader& shader,
                               const std::vector<std::tuple<int, int, Color>>& assets, Color force_color) {

    for(auto asset : assets) {

        shader.setColor(force_color.getVector());
        m_asset_manager->draw(shader.getId(), std::get<0>(asset), std::get<1>(asset));
    }
}

void GlobeRenderer::drawAssets(const AssetShader& shader,
                               const std::vector<std::tuple<int, int, Color>>& assets, int longi, int lati) {

    for(auto asset : assets) {

        Color color = std::get<2>(asset);
        shader.setColor(color.getVector());

        m_asset_manager->draw(shader.getId(), std::get<0>(asset), std::get<1>(asset), longi, lati);
    }
}

void GlobeRenderer::drawAssets(const AssetShader& shader,
                               const std::vector<std::tuple<int, int, Color>>& assets,
                               int longi, int lati, Color force_color) {

    for(auto asset : assets) {
        shader.setColor(force_color.getVector());
        m_asset_manager->draw(shader.getId(), std::get<0>(asset), std::get<1>(asset), longi, lati);
    }
}
