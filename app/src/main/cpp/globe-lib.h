//
// Created by schimannek on 2/12/18.
//

#ifndef GLOBE_GLOBE_LIB_H
#define GLOBE_GLOBE_LIB_H

#include <jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <string>
#include <memory>

#include "Globe.h"
#include "GlobeRenderer.h"

extern "C" {
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_loadAssets(
        JNIEnv *env, jobject object, jobject asset_manager);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_showAsset(
        JNIEnv *env, jobject object, jint layer, jstring file,
        jint id, jdoubleArray color);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setReliefTexture(
        JNIEnv *env, jobject object, jstring file);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_showReliefTexture(
        JNIEnv *env, jobject object);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_hideReliefTexture(
        JNIEnv *env, jobject object);
JNIEXPORT jint JNICALL Java_com_shnoop_globequiz_RendererWrapper_getRegionFromPoint(
        JNIEnv *env, jobject object, jstring file, jfloat x, jfloat y);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setDimensions(
        JNIEnv *env, jobject object, jint width, jint height);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_createRenderer(
        JNIEnv *env, jobject object, jint layers);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_clear(
        JNIEnv *env, jobject object, jint layer);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_drawFrame(
        JNIEnv *env, jobject object);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setBackgroundColor(
        JNIEnv *env, jobject  object, jdoubleArray color);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setZoom(
        JNIEnv *env, jobject  object, jfloat factor);
JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getZoom(
        JNIEnv *env, jobject  object);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_zoomTo(
        JNIEnv *env, jobject  object, jfloat factor, jfloat duration);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_setRotation(
        JNIEnv *env, jobject object, jfloat rx, jfloat ry);
JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getRotationLong(
        JNIEnv *env, jobject  object);
JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getRotationLat(
        JNIEnv *env, jobject  object);
JNIEXPORT jfloat JNICALL Java_com_shnoop_globequiz_RendererWrapper_getFPS(
        JNIEnv *env, jobject  object);
JNIEXPORT void JNICALL Java_com_shnoop_globequiz_RendererWrapper_rotateTo(
        JNIEnv *env, jobject object, jfloat rx, jfloat ry, jfloat duration);
};

#endif //GLOBE_GLOBE_LIB_H
