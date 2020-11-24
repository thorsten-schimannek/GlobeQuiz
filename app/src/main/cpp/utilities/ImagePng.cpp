//
// Created by thorsten on 14.11.20.
//

#include "ImagePng.h"

ImagePng::ImagePng(const AssetFile &asset) {

    using namespace std::string_literals;

    int descriptor = asset.getDescriptor();
    off_t start = asset.getDataStart();

    FILE* file = fdopen(descriptor, "r");
    fseek(file, start, SEEK_SET);

    png_image image;
    memset(&image, 0, (sizeof image));
    image.version = PNG_IMAGE_VERSION;
    if(png_image_begin_read_from_stdio(&image, file) != 0) {

        switch(image.format) {
            case PNG_FORMAT_GRAY:
                m_format = GRAY;
                break;
            case PNG_FORMAT_RGB:
                m_format = RGB;
                break;
            case PNG_FORMAT_RGBA:
                m_format = RGBA;
                break;
            default:
                throw std::runtime_error(__FUNCTION__ + ": Unsupported png format."s);
        }

        m_width = image.width;
        m_height = image.height;

        m_buffer = std::make_unique<unsigned char[]>(PNG_IMAGE_SIZE(image));

        if(m_buffer != nullptr && png_image_finish_read(&image,
                NULL, m_buffer.get(), 0, NULL) != 0)
            return;
    }

    throw std::runtime_error(__FUNCTION__ + ": Error reading png."s);
}

ImagePng::~ImagePng() { }
