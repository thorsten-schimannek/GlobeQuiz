//
// Created by thorsten on 15.05.20.
//

#ifndef GLOBEQUIZ_ASSETGEOMETRY_H
#define GLOBEQUIZ_ASSETGEOMETRY_H

#include <GLES2/gl2.h>

class AssetGeometry {

public:

    enum GeometryType {
        TRIANGLES = 0,
        LINES = 1,
        POINTS = 2,
        UNKNOWN = 3
    };

    virtual void reload() = 0;
    virtual void unload() = 0;

    AssetGeometry(GeometryType type) : m_type {type} {};
    virtual ~AssetGeometry() {};

    GeometryType getType() { return m_type; }

    virtual void draw(int shader_id) = 0;
    virtual void draw(int shader_id, int region_id) = 0;
    virtual void draw(int shader_id, int region_id,
                      int longitude_grid_n, int latitude_grid_n) = 0;
    virtual int numEntries() = 0;
    virtual std::string getFileName() { return m_filename; }

protected:

    std::string m_filename;
    GeometryType  m_type;
};

#endif //GLOBEQUIZ_ASSETGEOMETRY_H
