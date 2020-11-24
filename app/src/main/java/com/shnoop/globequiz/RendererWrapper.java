package com.shnoop.globequiz;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.widget.OverScroller;

import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by schimannek on 2/12/18.
 */

public class RendererWrapper implements GLSurfaceView.Renderer {

    class ShowAssetRequest {

        public ShowAssetRequest(int layer, String filename, int id, double[] color) {
           m_layer = layer; m_filename = filename; m_id = id; m_color = color;
        }

        public void show() { RendererWrapper.this.show(m_layer, m_filename, m_id, m_color); }

        private int m_layer;
        private String m_filename;
        private int m_id;
        private double[] m_color;
    }

    private final Context m_context;
    private  AssetManager m_asset_manager;
    private boolean m_surface_created = false;
    private Stack<ShowAssetRequest> m_show_requests;
    private String m_relief_texture;

    private OverScroller m_scroller;

    static {
        System.loadLibrary("globe-lib");
    }

    public RendererWrapper(Context context) {

        m_scroller = new OverScroller(context);
        createRenderer(3);

        m_show_requests = new Stack<>();
        m_context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        m_asset_manager = m_context.getAssets();
        loadAssets(m_asset_manager);

        m_surface_created = true;

        while(!m_show_requests.empty()) m_show_requests.pop().show();

        if(m_relief_texture != null) setReliefTexture(m_relief_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {

        setDimensions(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        if(!m_scroller.isFinished()) {

            m_scroller.computeScrollOffset();
            float rx = ((float) m_scroller.getCurrX()) / 100.f;
            float ry = ((float) m_scroller.getCurrY()) / 100.f;

            setRotation(rx, ry);
        }
        drawFrame();
    }

    public void show (int layer, String filename, int id, double[] color) {

        if(m_surface_created) showAsset(layer, filename, id, color);
        else m_show_requests.push(new ShowAssetRequest(layer, filename, id, color));
    }

    public void setRelief(String filename) {

        m_relief_texture = filename;
        if(m_surface_created) setReliefTexture(filename);
    }

    public void handleZoom(float factor) {

        setZoom(factor);
    }

    public void handleTouchDrag(float dx, float dy) {

        float zoom = getZoom();

        float x_rotation = getRotationLong() + dx / zoom;
        float y_rotation = getRotationLat() - dy / zoom;

        if(y_rotation < -90f) y_rotation = -90f;
        else if(y_rotation > 90f) y_rotation = 90f;

        if (x_rotation < -360f) {
            x_rotation = x_rotation + 360f;
        } else if (x_rotation > 360f) {
            x_rotation = x_rotation - 360f;
        }

        m_scroller.forceFinished(true);

        setRotation(x_rotation, y_rotation);
    }

    public void handleFling(float vx, float vy) {

        float rx = getRotationLong();
        float ry = getRotationLat();

        float zoom = getZoom();

        m_scroller.fling((int)(rx*100.f), (int)(ry*100.f), -(int)(vx*100.f / zoom), (int)(vy*100.f / zoom),
                -100*360*100, 100*360*100, -100*360*100, 100*360*100);
    }

    /**
     * A native method that is implemented by the 'globe-lib' native library,
     * which is packaged with this application.
     */
    private native void showAsset(int layer, String filename,
                                  int id, double[] color);
    private native void setReliefTexture(String filename);
    public native int getRegionFromPoint(String filename, float x, float y);
    public native void loadAssets(AssetManager asset_manager);
    public native void setDimensions(int width, int height);
    public native void createRenderer(int layers);
    public native void clear(int layer);
    public native void setBackgroundColor(double[] color);
    public native void drawFrame();
    public native void setZoom(float factor);
    public native float getZoom();
    public native void zoomTo(float factor, float duration);
    public native void setRotation(float rx, float ry);
    public native float getRotationLong();
    public native float getRotationLat();
    public native float getFPS();
    public native void rotateTo(float rx, float ry, float duration);
}
