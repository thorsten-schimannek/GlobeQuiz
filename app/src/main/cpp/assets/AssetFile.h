//
// Created by thorsten on 29.04.20.
//

#ifndef GLOBEQUIZ_ASSETFILE_H
#define GLOBEQUIZ_ASSETFILE_H

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

class AssetFile {

public:
    AssetFile (AAsset* asset): m_asset {asset},
        m_file_descriptor {AAsset_openFileDescriptor(m_asset, &m_data_start, &m_data_length)} { }

    ~AssetFile(){
        AAsset_close(m_asset);
    }

    int getDescriptor() const { return m_file_descriptor; }
    off_t getDataStart() const { return m_data_start; }
    off_t getDataLength() const { return m_data_length; }

protected:
    AAsset* m_asset;
    int m_file_descriptor;
    off_t m_data_start;
    off_t m_data_length;
};

#endif //GLOBEQUIZ_ASSETFILE_H
