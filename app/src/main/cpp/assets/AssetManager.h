//
// Created by schimannek on 28.01.19.
//

#ifndef GLOBE_ASSETMANAGER_H
#define GLOBE_ASSETMANAGER_H

#include <tuple>
#include <string>
#include <unordered_map>
#include <vector>
#include <memory>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include "assets/AssetBuffer.h"
#include "assets/AssetFile.h"
#include "assets/AssetGeometry.h"
#include "assets/AssetTriangleShader.h"
#include "assets/AssetTriangleList.h"
#include "assets/AssetLineShader.h"
#include "assets/AssetLineList.h"
#include "assets/AssetPointShader.h"
#include "assets/AssetPointList.h"

class AssetManager : public std::enable_shared_from_this<AssetManager>{

public:

        AssetManager(AAssetManager* assetManager);

        std::unique_ptr<AssetBuffer> getBuffer(std::string filename) const;
        std::unique_ptr<AssetFile> getFile(std::string filename) const;

        std::string getContentString(std::string filename) const;

        int loadGeometryAsset(std::string filename);
        int loadShaderAsset(AssetShader::ShaderType type, std::string vertex_shader_filename,
                std::string fragment_shader_filename);

        void unloadAssets();
        void reloadAssets();

        AssetGeometry::GeometryType  getGeometryAssetType(int asset_id);
        int getGeometryAssetEntryNumber(int asset_id);

        void draw(int shader_id, int asset_id, int region_id,
              int longitude_grid_n, int latitude_grid_n);
        void draw(int shader_id, int asset_id, int region_id);
        void draw(int shader_id, int asset_id);

        const AssetTriangleShader& getTriangleShader(int shader_id);
        const AssetLineShader& getLineShader(int shader_id);
        const AssetPointShader& getPointShader(int shader_id);

protected:

        AssetGeometry::GeometryType getGeometryType(std::string filename);

        AAssetManager* m_asset_manager = nullptr;

        // Store pointers to asset objects to avoid slicing
        std::vector<std::unique_ptr<AssetGeometry>> m_geometry_assets;
        std::vector<std::unique_ptr<AssetTriangleShader>> m_triangle_shader_assets;
        std::vector<std::unique_ptr<AssetLineShader>> m_line_shader_assets;
        std::vector<std::unique_ptr<AssetPointShader>> m_point_shader_assets;

        // Keep track of the loaded assets to avoid duplicates
        std::unordered_map<std::string, int> m_geometry_asset_ids;
        std::map<std::tuple<std::string, std::string>, int> m_shader_asset_ids;
};

#endif //GLOBE_ASSETMANAGER_H
