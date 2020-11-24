//
// Created by thorsten on 14.11.20.
//

#ifndef GLOBEQUIZ_ASSETTEXTURE_H
#define GLOBEQUIZ_ASSETTEXTURE_H

#include "utilities/Image.h"

class AssetTexture {

public:

    enum TextureType {
        TEXTURE_2D = 0,
    };

    virtual void reload() = 0;
    virtual void unload() = 0;
    virtual void bindTexture(GLenum target) = 0;
    virtual GLuint getTextureId() = 0;

    AssetTexture(int id, TextureType type) :
            m_id {id}, m_type {type} {}

    virtual ~AssetTexture() {};

    int getId() const { return m_id; }
    TextureType getType() const { return m_type; }

protected:

    int m_id;
    TextureType m_type;
};


#endif //GLOBEQUIZ_ASSETTEXTURE_H
