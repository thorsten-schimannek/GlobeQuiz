//
// Created by thorsten on 15.05.20.
//

#include "AssetLineList.h"

AssetLineList::AssetLineList(std::string filename, std::weak_ptr<AssetManager> asset_manager)
        : AssetGeometry {AssetGeometry::LINES}, m_filename {filename}, m_asset_manager {asset_manager} {

    loadData();
}

AssetLineList::~AssetLineList() {

    unload();
}

void AssetLineList::unload() {

    glDeleteBuffers(1, &m_vertex_buffer);
    glDeleteBuffers(1, &m_previous_vertex_buffer);
    glDeleteBuffers(1, &m_next_vertex_buffer);
    glDeleteBuffers(1, &m_directions_vertex_buffer);
    glDeleteBuffers(1, &m_index_buffer);
}

void AssetLineList::reload() {

    loadData();
}

void AssetLineList::loadData() {

    auto assetManager = m_asset_manager.lock();
    std::unique_ptr<AssetFile> asset_file = assetManager->getFile(m_filename);

    int descriptor = asset_file->getDescriptor();
    off_t start = asset_file->getDataStart();

    FILE* file = fdopen(descriptor, "r");
    fseek(file, start, SEEK_SET);

    int num_entries, num_vertices, entry_vertices, ida, idb, region_vertices;
    float vx, vy;
    fscanf(file, "%i\n", &num_entries);
    fscanf(file, "%i\n", &num_vertices);

    m_num_entries = num_entries;
    m_lengths = std::make_unique<int[]>(72 * num_entries);
    m_offsets = std::make_unique<int[]>(72 * num_entries);

    auto vertices = std::make_unique<GLfloat[]>(num_vertices * 4);
    auto previous = std::make_unique<GLfloat[]>(num_vertices * 4);
    auto next = std::make_unique<GLfloat[]>(num_vertices * 4);
    auto directions = std::make_unique<GLfloat[]>(num_vertices * 2);
    auto indices = std::make_unique<GLushort[]>(num_vertices * 6);

    int current_vertex = 0, current_index = 0;

    for(int i = 0; i < num_entries; i++){

        int current_entry = -1;
        int num_regions = -1;

        fscanf(file, "i%i\n", &current_entry);
        fscanf(file, "%i\n", &num_regions);
        fscanf(file, "%i\n", &entry_vertices);

        for(int j = 0; j < num_regions; j++){

            fscanf(file, "%i\n", &ida);
            fscanf(file, "%i\n", &idb);
            fscanf(file, "%i\n", &region_vertices);

            m_offsets[72 * i + 6 * ida + idb] = sizeof(GLushort) * current_index;
            m_lengths[72 * i + 6 * ida + idb] = (region_vertices - 1) * 6;

            for(int k = 0; k < region_vertices; k++){

                // Vertices are arranged as longitude, latitude
                fscanf(file, "%f,%f\n", &vx, &vy);

                vertices[4 * current_vertex + 0] = (GLfloat)vx;
                vertices[4 * current_vertex + 1] = (GLfloat)vy;
                vertices[4 * current_vertex + 2] = (GLfloat)vx;
                vertices[4 * current_vertex + 3] = (GLfloat)vy;

                if(k > 0) {
                    previous[4 * current_vertex + 0] = vertices[4 * (current_vertex - 1) + 0];
                    previous[4 * current_vertex + 1] = vertices[4 * (current_vertex - 1) + 1];
                    previous[4 * current_vertex + 2] = vertices[4 * (current_vertex - 1) + 2];
                    previous[4 * current_vertex + 3] = vertices[4 * (current_vertex - 1) + 3];

                    next[4*(current_vertex - 1) + 0] = vertices[4 * current_vertex + 0];
                    next[4*(current_vertex - 1) + 1] = vertices[4 * current_vertex + 1];
                    next[4*(current_vertex - 1) + 2] = vertices[4 * current_vertex + 2];
                    next[4*(current_vertex - 1) + 3] = vertices[4 * current_vertex + 3];
                }
                else {
                    previous[4 * current_vertex + 0] = vertices[4 * current_vertex + 0];
                    previous[4 * current_vertex + 1] = vertices[4 * current_vertex + 1];
                    previous[4 * current_vertex + 2] = vertices[4 * current_vertex + 2];
                    previous[4 * current_vertex + 3] = vertices[4 * current_vertex + 3];
                }

                directions[2*current_vertex + 0] = 1;
                directions[2*current_vertex + 1] = -1;

                if(k < region_vertices - 1) {
                    indices[current_index + 0] = 2 * current_vertex + 0;
                    indices[current_index + 1] = 2 * current_vertex + 1;
                    indices[current_index + 2] = 2 * current_vertex + 2;
                    indices[current_index + 3] = 2 * current_vertex + 2;
                    indices[current_index + 4] = 2 * current_vertex + 1;
                    indices[current_index + 5] = 2 * current_vertex + 3;
                    current_index += 6;
                }

                current_vertex++;
            }

            next[4*(current_vertex - 1) + 0] = vertices[4*(current_vertex - 1) + 0];
            next[4*(current_vertex - 1) + 1] = vertices[4*(current_vertex - 1) + 1];
            next[4*(current_vertex - 1) + 2] = vertices[4*(current_vertex - 1) + 2];
            next[4*(current_vertex - 1) + 3] = vertices[4*(current_vertex - 1) + 3];
        }
    }

    m_num_indices = current_index;

    GLuint bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, m_num_indices * sizeof(GLushort), indices.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    m_index_buffer = bufferId;

    bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ARRAY_BUFFER, 4 * num_vertices * sizeof(GLfloat), vertices.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    m_vertex_buffer = bufferId;

    bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ARRAY_BUFFER, 4 * num_vertices * sizeof(GLfloat), previous.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    m_previous_vertex_buffer = bufferId;

    bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ARRAY_BUFFER, 4 * num_vertices * sizeof(GLfloat), next.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    m_next_vertex_buffer = bufferId;

    bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ARRAY_BUFFER, 2 * num_vertices * sizeof(GLfloat), directions.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    m_directions_vertex_buffer = bufferId;

    fclose(file);
}

void AssetLineList::bindVertexArray(int shader_id) {

    auto assetManager = m_asset_manager.lock();
    int position_attribute_location = assetManager->getLineShader(shader_id).getPositionAttribute();
    int previous_attribute_location = assetManager->getLineShader(shader_id).getPreviousAttribute();
    int next_attribute_location = assetManager->getLineShader(shader_id).getNextAttribute();
    int direction_attribute_location = assetManager->getLineShader(shader_id).getDirectionAttribute();

    glBindBuffer(GL_ARRAY_BUFFER, m_vertex_buffer);
    glVertexAttribPointer(position_attribute_location, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(position_attribute_location);

    glBindBuffer(GL_ARRAY_BUFFER, m_previous_vertex_buffer);
    glVertexAttribPointer(previous_attribute_location, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(previous_attribute_location);

    glBindBuffer(GL_ARRAY_BUFFER, m_next_vertex_buffer);
    glVertexAttribPointer(next_attribute_location, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(next_attribute_location);

    glBindBuffer(GL_ARRAY_BUFFER, m_directions_vertex_buffer);
    glVertexAttribPointer(direction_attribute_location, 1, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(direction_attribute_location);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_index_buffer);
}

void AssetLineList::draw(int shader_id) {

    bindVertexArray(shader_id);
    glDrawElements(GL_TRIANGLES, m_num_indices, GL_UNSIGNED_SHORT, 0);
}

void AssetLineList::draw(int shader_id, int region_id){

    if(region_id == -1) draw(shader_id);
    else{
        for(int lon = 0; lon < 12; lon++)
            for(int lat = 0; lat < 6; lat++)
                draw(shader_id, region_id, lon, lat);
    }
}

void AssetLineList::draw(int shader_id, int region_id,
                             int longitude_grid_n, int latitude_grid_n){

    bindVertexArray(shader_id);

    if(region_id == -1) {

        for(int i = 0; i < m_num_entries; i++) {
            if(m_lengths[72 * i + 6 * longitude_grid_n + latitude_grid_n] > 0)
                drawEntry(i, longitude_grid_n, latitude_grid_n);
        }
    }
    else if(m_lengths[72 * region_id + 6 * longitude_grid_n + latitude_grid_n] > 0)
        drawEntry(region_id, longitude_grid_n, latitude_grid_n);
}

void AssetLineList::drawEntry(int region_id, int longitude_grid_n, int latitude_grid_n) {

    int offset = m_offsets[72 * region_id + 6 * longitude_grid_n + latitude_grid_n];
    int length = m_lengths[72 * region_id + 6 * longitude_grid_n + latitude_grid_n];
    glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_SHORT, (void*)offset);
}
