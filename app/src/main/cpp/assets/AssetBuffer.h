//
// Created by thorsten on 29.04.20.
//

#ifndef GLOBEQUIZ_ASSETBUFFER_H
#define GLOBEQUIZ_ASSETBUFFER_H

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

class AssetBuffer {

public:
    AssetBuffer(AAsset* asset) : m_asset {asset}, m_buffer {(char *) AAsset_getBuffer(asset)},
        m_length {AAsset_getLength(asset)} {};

    ~AssetBuffer() {
        AAsset_close(m_asset);
    };

    char* getBuffer() const { return m_buffer; }
    off_t getLength() const { return m_length; }

protected:

    AAsset* m_asset;
    char* m_buffer;
    off_t m_length;
};

#endif //GLOBEQUIZ_ASSETBUFFER_H
