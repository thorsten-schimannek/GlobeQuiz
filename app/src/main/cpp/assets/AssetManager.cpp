//
// Created by thorsten on 05.05.20.
//

#include "AssetManager.h"

AssetManager::AssetManager(AAssetManager* assetManager) : m_asset_manager {assetManager} { }

std::unique_ptr<AssetBuffer> AssetManager::getBuffer(std::string filename) const {

    if(m_asset_manager != nullptr) {

        auto asset = AAssetManager_open(m_asset_manager, filename.c_str(), AASSET_MODE_BUFFER);

        if(asset) return std::make_unique<AssetBuffer>(asset);
        else LOG("getBuffer", "AAssetManager_open(%s) failed.", filename.c_str());
    }

    return nullptr;
}

std::unique_ptr<AssetFile> AssetManager::getFile(std::string filename) const {

    if(m_asset_manager != nullptr) {

        auto asset = AAssetManager_open(m_asset_manager, filename.c_str(), AASSET_MODE_UNKNOWN);

        if(asset) return std::make_unique<AssetFile>(asset);
        else LOG("getFile", "AAssetManager_open(%s) failed.", filename.c_str());
    }

    return nullptr;
}

std::string AssetManager::getContentString(std::string filename) const {

    auto buffer = getBuffer(filename);

    if(buffer) {

        std::string content{buffer->getBuffer(), (size_t) buffer->getLength()};
        return content;
    }
    else return std::string("");
}

AssetGeometry::GeometryType AssetManager::getGeometryType(std::string filename) {

    AssetGeometry::GeometryType type = AssetGeometry::UNKNOWN;

    if(filename.substr(filename.length() - 4, filename.length()) == ".jet") {

        std::string extension = filename.substr(filename.rfind(".", filename.length() - 5),
                                                filename.length());

        if (extension == ".triangles.jet") type = AssetGeometry::TRIANGLES;
        else if (extension == ".lines.jet") type = AssetGeometry::LINES;
        else if (extension == ".points.jet") type = AssetGeometry::POINTS;
    }

    return type;
}

int AssetManager::loadGeometryAsset(std::string filename) {

    if(m_geometry_asset_ids.count(filename) > 0) return m_geometry_asset_ids.at(filename);

    int newId  = m_geometry_assets.size();

    AssetGeometry::GeometryType type = getGeometryType(filename);

    switch(type) {

        case AssetGeometry::TRIANGLES:
            m_geometry_assets.push_back(std::make_unique<AssetTriangleList>(filename, shared_from_this()));
            break;

        case AssetGeometry::LINES:
            m_geometry_assets.push_back(std::make_unique<AssetLineList>(filename, shared_from_this()));
            break;

        case AssetGeometry::POINTS:
            m_geometry_assets.push_back(std::make_unique<AssetPointList>(filename, shared_from_this()));
            break;

        default:
            return -1;
    }

    m_geometry_asset_ids[filename] = newId;
    return newId;
}

int AssetManager::loadShaderAsset(AssetShader::ShaderType type, std::string vertex_shader_file,
                                  std::string fragment_shader_file) {

    if(m_shader_asset_ids.count(std::make_tuple(vertex_shader_file, fragment_shader_file)) > 0)
        return m_shader_asset_ids.at(std::make_tuple(vertex_shader_file, fragment_shader_file));

    std::string vertexShaderCode = getContentString(vertex_shader_file);
    std::string fragmentShaderCode = getContentString(fragment_shader_file);

    int newId {-1};

    switch(type) {

        case AssetShader::TRIANGLES: {

            newId = m_triangle_shader_assets.size();
            m_triangle_shader_assets.push_back(
                    std::make_unique<AssetTriangleShader>(newId, vertexShaderCode, fragmentShaderCode));
        } break;

        case AssetShader::LINES: {

            newId = m_line_shader_assets.size();
            m_line_shader_assets.push_back(
                    std::make_unique<AssetLineShader>(newId, vertexShaderCode, fragmentShaderCode));
        } break;

        case AssetShader::POINTS: {

            newId = m_point_shader_assets.size();
            m_point_shader_assets.push_back(
                    std::make_unique<AssetPointShader>(newId, vertexShaderCode, fragmentShaderCode));
        } break;

        case AssetShader::UNKNOWN: break;
    }

    if(newId != -1)
        m_shader_asset_ids[std::make_tuple(vertex_shader_file, fragment_shader_file)] = newId;

    return newId;
}

void AssetManager::unloadAssets() {

    for(auto& asset : m_geometry_assets) asset->unload();
    for(auto& asset : m_triangle_shader_assets) asset->unload();
    for(auto& asset : m_line_shader_assets) asset->unload();
    for(auto& asset : m_point_shader_assets) asset->unload();
}

void AssetManager::reloadAssets() {

    for(auto& asset : m_geometry_assets) asset->reload();
    for(auto& asset : m_triangle_shader_assets) asset->reload();
    for(auto& asset : m_line_shader_assets) asset->reload();
    for(auto& asset : m_point_shader_assets) asset->reload();
}

AssetGeometry::GeometryType AssetManager::getGeometryAssetType(int asset_id) {

    return m_geometry_assets[asset_id]->getType();
}

int AssetManager::getGeometryAssetEntryNumber(int asset_id) {

    return m_geometry_assets[asset_id]->numEntries();
}

const AssetTriangleShader& AssetManager::getTriangleShader(int shader_id) {

    return *m_triangle_shader_assets[shader_id];
}

const AssetLineShader& AssetManager::getLineShader(int shader_id) {

    return *m_line_shader_assets[shader_id];
}

const AssetPointShader& AssetManager::getPointShader(int shader_id) {

    return *m_point_shader_assets[shader_id];
}

void AssetManager::draw(int shader_id, int asset_id, int region_id,
                        int longitude_grid_n, int latitude_grid_n) {

    m_geometry_assets[asset_id]->draw(shader_id, region_id,
                                      longitude_grid_n, latitude_grid_n);
}

void AssetManager::draw(int shader_id, int asset_id, int region_id) {

    if(region_id == -1) m_geometry_assets[asset_id]->draw(shader_id);
    else m_geometry_assets[asset_id]->draw(shader_id, region_id);
}

void AssetManager::draw(int shader_id, int asset_id) {

    m_geometry_assets[asset_id]->draw(shader_id);
}
