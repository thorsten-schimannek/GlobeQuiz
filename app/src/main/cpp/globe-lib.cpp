//
// Created by schimannek on 2/12/18.
//

#include "globe-lib.h"

static GlobeRenderer* renderer = nullptr;

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_loadAssets(
        JNIEnv *env, jobject object, jobject asset_manager){

    AAssetManager* assetManager = AAssetManager_fromJava(env, asset_manager);

    if(renderer) renderer->setAssetManager(std::make_shared<AssetManager>(assetManager));

    return;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_showAsset(
        JNIEnv *env, jobject object, jint layer, jstring file,
        jint id, jdoubleArray color) {

    // Convert filename to std::string
    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(file, &isCopy);
    std::string filename = std::string(convertedValue);
    (env)->ReleaseStringUTFChars(file, convertedValue);

    // Convert color to Color
    jdouble* floatColors = env->GetDoubleArrayElements(color, 0);
    Color col { floatColors };
    env->ReleaseDoubleArrayElements(color, floatColors, 0);

    if(renderer) renderer->showAsset(layer, filename, id, col);

    return;
}

extern "C" JNIEXPORT jint JNICALL Java_com_shnoop_globequiz_RendererWrapper_getRegionFromPoint(
        JNIEnv *env, jobject object, jstring file, jfloat x, jfloat y) {

    // Convert filename to std::string
    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(file, &isCopy);
    std::string filename = std::string(convertedValue);
    (env)->ReleaseStringUTFChars(file, convertedValue);

    if(renderer) return renderer->getAssetRegionFromPoint(filename, x, y);
    else return -1;

}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setDimensions(
        JNIEnv *env, jobject object, jint width, jint height){

    if(renderer) renderer->setDimensions(width, height);

    return;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_createRenderer(
        JNIEnv *env, jobject object, jint layers){

    if(!renderer) { renderer = new GlobeRenderer(layers); }

    return;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_clear(
        JNIEnv *env, jobject object, jint layer) {

    if(renderer) renderer->clear(layer);
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_drawFrame(
        JNIEnv *env, jobject object){

    if(renderer) renderer->drawFrame();

    return;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setBackgroundColor(
        JNIEnv *env, jobject  object, jdoubleArray color) {

    // Convert jdoubleArray color to Color
    jdouble* floatColors = env->GetDoubleArrayElements(color, 0);
    Color col { floatColors };
    env->ReleaseDoubleArrayElements(color, floatColors, 0);

    if(renderer) renderer->setBackgroundColor(col);
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setZoom(
        JNIEnv* env, jobject object, jfloat factor){

    if(renderer) renderer->getGlobe().setZoom(factor);

    return;
}

extern "C" JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getZoom(
        JNIEnv* env, jobject object){

    if(renderer) return renderer->getGlobe().getZoom();
    else return 2.0f;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_zoomTo(
        JNIEnv* env, jobject object, jfloat factor, jfloat duration){

    if(renderer) renderer->getGlobe().zoomTo(factor, duration);

    return;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setRotation(
        JNIEnv* env, jobject object, jfloat rx, jfloat ry){

    if(renderer) renderer->getGlobe().setRotation(rx, ry);

    return;
}

extern "C" JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getRotationLong(
        JNIEnv* env, jobject object){

    if(renderer) {

        auto [rotX, rotY] = renderer->getGlobe().getRotation();
        return rotX;
    }

    return 0.f;
}

extern "C" JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getRotationLat(
        JNIEnv* env, jobject object){

    if(renderer) {

        auto [rotX, rotY] = renderer->getGlobe().getRotation();
        return rotY;
    }

    return 0.f;
}

extern "C" JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getFPS(
        JNIEnv* env, jobject object){

    if(renderer) return renderer->getFPS();
    return 0.f;
}

extern "C" JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_rotateTo(
        JNIEnv* env, jobject object, jfloat rx, jfloat ry, jfloat duration){

    if(renderer) renderer->getGlobe().rotateTo(rx, ry, duration);

    return;
}

