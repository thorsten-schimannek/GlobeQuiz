//
// Created by thorsten on 14.11.20.
//

#include "AssetTexture2d.h"

AssetTexture2d::AssetTexture2d(int id, std::string filename,
        std::weak_ptr<AssetManager> asset_manager)
    : AssetTexture(id, TEXTURE_2D),
    m_filename {filename}, m_asset_manager {asset_manager} {

    reload();
}

AssetTexture2d::~AssetTexture2d() noexcept { }

void AssetTexture2d::reload() {

    auto assetManager = m_asset_manager.lock();
    std::unique_ptr<AssetFile> asset_file = assetManager->getFile(m_filename);
    std::unique_ptr<Image> image = std::make_unique<ImagePng>(*asset_file);

    GLuint texture[1];

    glGenTextures(1, texture);
    glBindTexture(GL_TEXTURE_2D, texture[0]);
    m_texture = texture[0];

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    GLuint format;
    switch(image->getFormat()) {
        case Image::Format::GRAY:
            format = GL_ALPHA;
            break;
        case Image::Format::RGB:
            format = GL_RGB;
            break;
        case Image::Format::RGBA:
            format = GL_RGBA;
            break;
    }

    glTexImage2D(GL_TEXTURE_2D, 0, format, image->getWidth(), image->getHeight(),
            0,format, GL_UNSIGNED_BYTE, image->getBuffer());
}

void AssetTexture2d::unload() {

    GLuint textures[] = {m_texture};
    glDeleteTextures(1, textures);
}

void AssetTexture2d::bindTexture(GLenum target) {

    glBindTexture(target, m_texture);
}

GLuint AssetTexture2d::getTextureId() {

    return m_texture;
}