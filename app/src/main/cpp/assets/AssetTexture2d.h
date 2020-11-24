//
// Created by thorsten on 14.11.20.
//

#ifndef GLOBEQUIZ_ASSETTEXTURE2D_H
#define GLOBEQUIZ_ASSETTEXTURE2D_H

#include <memory>

#include "AssetManager.h"
#include "AssetTexture.h"

class AssetTexture2d : public AssetTexture {

public:

    AssetTexture2d(int id, std::string filename, std::weak_ptr<AssetManager> asset_manager);
    ~AssetTexture2d();

    void reload();
    void unload();

    GLuint getTextureId();

    void bindTexture(GLenum target);

protected:

    std::string m_filename;
    std::weak_ptr<AssetManager> m_asset_manager;

    GLuint m_texture;
};

#endif //GLOBEQUIZ_ASSETTEXTURE2D_H
