//
// Created by thorsten on 14.11.20.
//

#ifndef GLOBEQUIZ_IMAGE_H
#define GLOBEQUIZ_IMAGE_H

class Image {

public:

    enum Format {
        GRAY = 1,
        RGB = 2,
        RGBA = 3
    };

    virtual int getWidth() = 0;
    virtual int getHeight() = 0;
    virtual Format getFormat() = 0;
    virtual unsigned char* getBuffer() = 0;
};

#endif //GLOBEQUIZ_IMAGE_H
