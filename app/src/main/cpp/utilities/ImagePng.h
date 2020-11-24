//
// Created by thorsten on 14.11.20.
//

#ifndef GLOBEQUIZ_IMAGEPNG_H
#define GLOBEQUIZ_IMAGEPNG_H

#include <string>
#include <memory>
#include<exception>
#include <cstring>
#include <libpng/png.h>

#include "Image.h"
#include "assets/AssetFile.h"

class ImagePng : public Image {

public:
    ImagePng(const AssetFile& asset);
    ~ImagePng();

    int getWidth() { return m_width; }
    int getHeight() { return m_height; }
    Format getFormat() { return m_format; }
    unsigned char* getBuffer() { return m_buffer.get(); }

protected:

    void validateFile(FILE* file, off_t start);

    int m_width;
    int m_height;
    Format m_format;
    std::unique_ptr<unsigned char[]> m_buffer;
};

#endif //GLOBEQUIZ_IMAGEPNG_H
