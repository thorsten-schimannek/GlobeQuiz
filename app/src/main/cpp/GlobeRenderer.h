//
// Created by schimannek on 2/12/18.
//

#ifndef GLOBE_GLOBERENDERER_H
#define GLOBE_GLOBERENDERER_H

#include <GLES2/gl2.h>

#define GLM_ENABLE_EXPERIMENTAL

#include <memory>
#include <map>
#include <tuple>
#include <vector>
#include <stack>

#include <glm/glm/glm.hpp>
#include <glm/glm/gtc/matrix_transform.hpp>
#include <glm/glm/gtx/matrix_operation.hpp>
#include <glm/glm/gtx/transform.hpp>

#include <assets/AssetManager.h>
#include <utilities/CubeMapCamera.h>
#include <objects/Rectangle.h>
#include <shaders/GlobeRectangleShaderProgram.h>
#include <shaders/GlobeRectangleShaderProgramRelief.h>
#include <utilities/RenderTarget.h>
#include <utilities/CubeMap.h>
#include <utilities/Color.h>
#include <utilities/Timer.h>
#include <Globe.h>

class GlobeRenderer {

public:

    GlobeRenderer(unsigned int layers = 1);

    Globe& getGlobe() { return m_globe; }

    void setDimensions(int width, int height);
    void setAssetManager(std::shared_ptr<AssetManager> assetManager);
    void drawFrame();

    void setBackgroundColor(Color color) { m_background_color = color; }

    void clear(unsigned int layer);

    void showAsset(unsigned int layer, std::string file, Color color);
    void showAsset(unsigned int layer, std::string file, int id, Color color);
    void setReliefTexture(std::string filename);
    void hideReliefTexture();

    int getAssetRegionFromPoint(std::string file, float x, float y);

    float getFPS() { return m_fps; }

protected:

    void initialize();
    void loadAssets();
    void processAssetStack();

    float getSwitchZoom();
    bool globeCoversScreen();
    bool isPatchVisible(int longi, int lati, const glm::mat4& rotation, float zoom);

    void drawGlobe();
    void drawGlobeFromCubeMap(float x_rotation, float y_rotation, float zoom);
    void drawGlobeDirectly(float x_rotation, float y_rotation, float zoom);

    void drawVisiblePatches(const glm::mat4& rotation, float zoom, const AssetShader& shader,
                            const std::vector<std::tuple<int, int, Color>>& assets);
    void drawVisiblePatches(const glm::mat4& rotation, float zoom, const AssetShader& shader,
                            const std::vector<std::tuple<int, int, Color>>& assets,
                            Color force_color);

    void drawAssets(const AssetShader& shader,
            const std::vector<std::tuple<int, int, Color>>& assets, bool zero_alpha = false);
    void drawAssets(const AssetShader& shader,
                    const std::vector<std::tuple<int, int, Color>>& assets, Color force_color);
    void drawAssets(const AssetShader& shader,
            const std::vector<std::tuple<int, int, Color>>& assets, int longi, int lati);
    void drawAssets(const AssetShader& shader,
                    const std::vector<std::tuple<int, int, Color>>& assets,
                    int longi, int lati, Color force_color);

    void updateCubeMaps();
    void updateCubeMap(unsigned int layer, Color background, bool relief = false);

    std::tuple<int, int> getGridRegionFromWorldSpacePoint(float x, float y, float z);
    std::tuple<float, float, float> getWorldSpaceFromScreenCoordinates(float x, float y);

    static constexpr double m_pi = 3.14159265358979323846f;

    Color m_background_color {1.f, 1.f, 1.f, 1.f};
    Color m_ocean_color {0.40f, 0.62f, 0.74f, 1.f};

    static constexpr int m_cubemap_size {2048};

    unsigned int m_layers {1};
    std::vector<bool> m_cubemap_invalid;
    std::vector<std::unique_ptr<CubeMap>> m_cubemaps;

    std::shared_ptr<AssetManager> m_asset_manager {nullptr};

    std::unique_ptr<RenderTarget> m_color_picking_target {nullptr};

    int m_width, m_height;

    glm::mat4 m_orthographic_view_projection_matrix;

    Globe m_globe;
    Timer m_frame_timer;

    float m_switch_zoom {1.f};

    float m_fps;

    bool m_relief_texture_set {false};

    int m_triangle_shader_id;
    int m_triangle_shader_relief_id;
    int m_line_shader_id;
    int m_point_shader_id;
    int m_relief_texture_id;

    std::vector<std::vector<std::tuple<int, int, Color>>> m_triangle_assets;
    std::vector<std::vector<std::tuple<int, int, Color>>> m_line_assets;
    std::vector<std::vector<std::tuple<int, int, Color>>> m_point_assets;

    std::stack<std::tuple<int, std::string, int, Color>> m_asset_loading_stack;

    std::string m_relief_texture_filename;

    std::unique_ptr<Rectangle> m_rectangle;
    std::unique_ptr<GlobeRectangleShaderProgram> m_shader_program_rectangle;
    std::unique_ptr<GlobeRectangleShaderProgramRelief> m_shader_program_rectangle_relief;
};


#endif //GLOBE_GLOBERENDERER_H
