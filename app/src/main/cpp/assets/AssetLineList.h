//
// Created by thorsten on 15.05.20.
//

#ifndef GLOBEQUIZ_ASSETLINELIST_H
#define GLOBEQUIZ_ASSETLINELIST_H

#include <GLES2/gl2.h>

#include <string>
#include <memory>
#include <vector>
#include <tuple>

#include "log.h"
#include "assets/AssetFile.h"
#include "assets/AssetManager.h"

class AssetManager;

class AssetLineList : public AssetGeometry {

public:

    AssetLineList(std::string filename, std::weak_ptr<AssetManager> asset_manager);
    ~AssetLineList();

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
    GLuint m_next_vertex_buffer;
    GLuint m_previous_vertex_buffer;
    GLuint m_directions_vertex_buffer;
    GLuint m_index_buffer;

    // The following arrays store the offsets and lengths of the vertices for
    // the longitude/latitude cells of the entries in the vertex buffer.
    // Both arrays are of size 72 * m_num_entries.
    // For each entry the 72 values correspond to a 12*6 longitude/latitude grid;
    // This is a bit C-ish, but using std::map would lead to a 50% performance penalty.
    std::unique_ptr<int[]> m_lengths;
    std::unique_ptr<int[]> m_offsets;
};

#endif //GLOBEQUIZ_ASSETLINELIST_H
