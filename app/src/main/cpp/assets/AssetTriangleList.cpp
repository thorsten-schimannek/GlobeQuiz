//
// Created by thorsten on 28.04.20.
//

#include "AssetTriangleList.h"

AssetTriangleList::AssetTriangleList(std::string filename, std::weak_ptr<AssetManager> asset_manager)
    : AssetGeometry {AssetGeometry::TRIANGLES}, m_filename {filename}, m_asset_manager {asset_manager}{

    loadData();
}

AssetTriangleList::~AssetTriangleList() {

    unload();
}

void AssetTriangleList::unload() {

    glDeleteBuffers(1, &m_vertex_buffer);
}

void AssetTriangleList::reload() {

    loadData();
}

void AssetTriangleList::loadData() {

    auto assetManager = m_asset_manager.lock();
    std::unique_ptr<AssetFile> countries_file = assetManager->getFile(m_filename);

    int descriptor = countries_file->getDescriptor();
    off_t start = countries_file->getDataStart();

    FILE* file = fdopen(descriptor, "r");
    fseek(file, start, SEEK_SET);

    int num_entries, num_vertices, ida, idb, region_vertices;
    float vx, vy;
    fscanf(file, "%i\n", &num_entries);
    fscanf(file, "%i\n", &num_vertices);

    m_num_entries = num_entries;
    m_lengths = std::make_unique<int[]>(72 * num_entries);
    m_offsets = std::make_unique<int[]>(72 * num_entries);

    m_num_vertices = num_vertices;

    auto vertices = std::make_unique<GLfloat[]>(num_vertices * 2);
    int current_vertex_pair = 0;

    for(int i = 0; i < num_entries; i++) {

        int current_entry = -1;
        int num_regions = -1;
        fscanf(file, "i%i\n", &current_entry);
        fscanf(file, "%i\n", &num_regions);

        for (int j = 0; j < num_regions; j++) {

            fscanf(file, "%i\n", &ida);
            fscanf(file, "%i\n", &idb);

            fscanf(file, "%i\n", &region_vertices);

            m_offsets[72 * i + 6 * ida + idb] = current_vertex_pair;
            m_lengths[72 * i + 6 * ida + idb] = region_vertices;

            for (int k = 0; k < region_vertices; k++) {

                // Vertices are arranged as longitude, latitude
                fscanf(file, "%f,%f\n", &vx, &vy);
                vertices[2 * current_vertex_pair + 0] = (GLfloat) vx;
                vertices[2 * current_vertex_pair + 1] = (GLfloat) vy;
                current_vertex_pair++;
            }
        }
    }

    fclose(file);

    glGenBuffers(1, &m_vertex_buffer);
    glBindBuffer(GL_ARRAY_BUFFER, m_vertex_buffer);
    glBufferData(GL_ARRAY_BUFFER, 2 * num_vertices * sizeof(GLfloat), vertices.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}

void AssetTriangleList::bindVertexArray(int shader_id){

    auto assetManager = m_asset_manager.lock();
    int position_attribute_location = assetManager->getTriangleShader(shader_id).getPositionAttribute();

    glBindBuffer(GL_ARRAY_BUFFER, m_vertex_buffer);
    glVertexAttribPointer(position_attribute_location, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(position_attribute_location);
}

void AssetTriangleList::draw(int shader_id) {

    bindVertexArray(shader_id);
    glDrawArrays(GL_TRIANGLES, 0, m_num_vertices);
}

void AssetTriangleList::draw(int shader_id, int region_id){

    if(region_id == -1) draw(shader_id);
    else{
        for(int lon = 0; lon < 12; lon++)
            for(int lat = 0; lat < 6; lat++)
                draw(shader_id, region_id, lon, lat);
    }
}

void AssetTriangleList::draw(int shader_id, int region_id,
                             int longitude_grid_n, int latitude_grid_n){

    if(region_id == -1) {

        bindVertexArray(shader_id);

        for(int i = 0; i < m_num_entries; i++) {
            if(m_lengths[72 * i + 6 * longitude_grid_n + latitude_grid_n] > 0)
                drawEntry(i, longitude_grid_n, latitude_grid_n);
        }
    }
    else if(m_lengths[72 * region_id + 6 * longitude_grid_n + latitude_grid_n] > 0) {

        bindVertexArray(shader_id);
        drawEntry(region_id, longitude_grid_n, latitude_grid_n);
    }
}

void AssetTriangleList::drawEntry(int region_id, int longitude_grid_n, int latitude_grid_n) {

    int offset = m_offsets[72 * region_id + 6 * longitude_grid_n + latitude_grid_n];
    int length = m_lengths[72 * region_id + 6 * longitude_grid_n + latitude_grid_n];
    glDrawArrays(GL_TRIANGLES, offset, length);
}
