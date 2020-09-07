//
// Created by thorsten on 23.05.20.
//

#ifndef GLOBEQUIZ_ASSETPOINTLIST_H
#define GLOBEQUIZ_ASSETPOINTLIST_H

#include <GLES2/gl2.h>

#include <string>
#include <memory>
#include <vector>
#include <tuple>

#include "log.h"
#include "assets/AssetFile.h"
#include "assets/AssetManager.h"

class AssetManager;

class AssetPointList : public AssetGeometry {

public:

    AssetPointList(std::string filename, std::weak_ptr<AssetManager> asset_manager);
    ~AssetPointList();

    void reload();
    void unload();

    void draw(int shader_id);
    void draw(int shader_id, int region_id);
    void draw(int shader_id, int region_id, int longitude_grid_n, int latitude_grid_n);

    int numEntries() { return m_num_entries; }

protected:

    void bindVertexArray(int shader_id);
    void drawEntry(int region_id, int longitude_grid_n, int latitude_grid_n);
    void loadData();

    std::string m_filename;
    std::weak_ptr<AssetManager> m_asset_manager;

    // performance test
    int m_num_indices;
    int m_num_entries;

    GLuint m_vertex_buffer;
    GLuint m_index_buffer;
    GLuint m_directions_vertex_buffer;

    std::vector<int> m_offsets;
    std::vector<std::tuple<int, int>> m_grid_regions;
};

#endif //GLOBEQUIZ_ASSETPOINTLIST_H
