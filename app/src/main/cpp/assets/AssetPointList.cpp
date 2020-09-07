//
// Created by thorsten on 23.05.20.
//

#include "AssetPointList.h"

AssetPointList::AssetPointList(std::string filename, std::weak_ptr<AssetManager> asset_manager)
        : AssetGeometry {AssetGeometry::POINTS}, m_filename {filename}, m_asset_manager {asset_manager} {

    loadData();
}

AssetPointList::~AssetPointList() {

    unload();
}

void AssetPointList::unload() {

    glDeleteBuffers(1, &m_vertex_buffer);
    glDeleteBuffers(1, &m_directions_vertex_buffer);
    glDeleteBuffers(1, &m_index_buffer);
}

void AssetPointList::reload() {

    loadData();
}

void AssetPointList::loadData() {

    auto assetManager = m_asset_manager.lock();
    std::unique_ptr<AssetFile> asset_file = assetManager->getFile(m_filename);

    int descriptor = asset_file->getDescriptor();
    off_t start = asset_file->getDataStart();

    FILE* file = fdopen(descriptor, "r");
    fseek(file, start, SEEK_SET);

    float phi, theta;
    fscanf(file, "%i\n", &m_num_entries);

    auto vertices = std::make_unique<GLfloat[]>(m_num_entries * 8);
    auto directions = std::make_unique<GLfloat[]>(m_num_entries * 8);
    auto indices = std::make_unique<GLushort[]>(m_num_entries * 6);

    for(int i = 0; i < m_num_entries; i++){

        m_offsets.push_back(sizeof(GLushort) * 6 * i);

        // Vertices are arranged as longitude, latitude
        fscanf(file, "%f,%f\n", &phi, &theta);

        int lon = std::floor((180.f + phi) / 30.f);
        int lat = std::floor((90.f + theta) / 30.f);
        m_grid_regions.push_back(std::make_tuple(lon, lat));

        vertices[8 * i + 0] = (GLfloat)phi;
        vertices[8 * i + 1] = (GLfloat)theta;
        vertices[8 * i + 2] = (GLfloat)phi;
        vertices[8 * i + 3] = (GLfloat)theta;
        vertices[8 * i + 4] = (GLfloat)phi;
        vertices[8 * i + 5] = (GLfloat)theta;
        vertices[8 * i + 6] = (GLfloat)phi;
        vertices[8 * i + 7] = (GLfloat)theta;

        indices[6 * i + 0] = 4 * i + 0;
        indices[6 * i + 1] = 4 * i + 1;
        indices[6 * i + 2] = 4 * i + 2;
        indices[6 * i + 3] = 4 * i + 2;
        indices[6 * i + 4] = 4 * i + 1;
        indices[6 * i + 5] = 4 * i + 3;

        directions[8 * i + 0] = (GLfloat)phi + .01f;
        directions[8 * i + 1] = (GLfloat)theta;
        directions[8 * i + 2] = (GLfloat)phi;
        directions[8 * i + 3] = (GLfloat)theta - .01f;
        directions[8 * i + 4] = (GLfloat)phi;
        directions[8 * i + 5] = (GLfloat)theta + .01f;
        directions[8 * i + 6] = (GLfloat)phi - .01f;
        directions[8 * i + 7] = (GLfloat)theta;
    }

    GLuint bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * m_num_entries * sizeof(GLushort), indices.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    m_index_buffer = bufferId;

    bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ARRAY_BUFFER, 8 * m_num_entries * sizeof(GLfloat), vertices.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    m_vertex_buffer = bufferId;

    bufferId = 0;
    glGenBuffers(1, &bufferId);
    glBindBuffer(GL_ARRAY_BUFFER, bufferId);
    glBufferData(GL_ARRAY_BUFFER, 8 * m_num_entries * sizeof(GLfloat), directions.get(), GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    m_directions_vertex_buffer = bufferId;

    fclose(file);
}

void AssetPointList::bindVertexArray(int shader_id) {

    auto assetManager = m_asset_manager.lock();
    int position_attribute_location = assetManager->getPointShader(shader_id).getPositionAttribute();
    int direction_attribute_location = assetManager->getPointShader(shader_id).getDirectionAttribute();

    glBindBuffer(GL_ARRAY_BUFFER, m_vertex_buffer);
    glVertexAttribPointer(position_attribute_location, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(position_attribute_location);

    glBindBuffer(GL_ARRAY_BUFFER, m_directions_vertex_buffer);
    glVertexAttribPointer(direction_attribute_location, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(direction_attribute_location);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_index_buffer);
}

void AssetPointList::draw(int shader_id) {

    bindVertexArray(shader_id);
    glDrawElements(GL_TRIANGLES, 6 * m_num_entries, GL_UNSIGNED_SHORT, 0);
}

void AssetPointList::draw(int shader_id, int region_id){

    if(region_id == -1) draw(shader_id);
    else{
        for(int lon = 0; lon < 12; lon++)
            for(int lat = 0; lat < 6; lat++)
                draw(shader_id, region_id, lon, lat);
    }
}

void AssetPointList::draw(int shader_id, int region_id,
                         int longitude_grid_n, int latitude_grid_n){

    bindVertexArray(shader_id);

    if(region_id == -1)
        for(int i = 0; i < m_num_entries; i++)
            drawEntry(i, longitude_grid_n, latitude_grid_n);
    else
        drawEntry(region_id, longitude_grid_n, latitude_grid_n);
}

void AssetPointList::drawEntry(int region_id, int longitude_grid_n, int latitude_grid_n) {

    auto [longi, lati] = m_grid_regions[region_id];
    if((longi == longitude_grid_n) && (lati == latitude_grid_n)) {

        int offset = m_offsets[region_id];
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, (void *) offset);
    }
}
