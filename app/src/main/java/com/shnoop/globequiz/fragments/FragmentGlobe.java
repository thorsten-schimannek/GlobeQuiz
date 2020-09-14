package com.shnoop.globequiz.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shnoop.globequiz.R;
import com.shnoop.globequiz.RendererWrapper;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGlobe extends Fragment {

    public enum Mode {
        Idle,
        Quiz,
        Globe
    }

    public enum PressType {
        Show,
        Short,
        Long,
        None
    }

    private Mode m_mode;

    private float m_sensitivity = .3f;

    private TextView m_fpsCounter;

    private GLSurfaceView m_glSurfaceView;
    private boolean m_renderer_set = false;

    private ScaleGestureDetector m_scale_gesture_detector;
    private GestureDetectorCompat m_scroll_gesture_detector;

    private RendererWrapper m_renderer_wrapper;

    private Handler m_handler;
    private String m_picking_region_asset;

    private Timer m_timer;

    public FragmentGlobe() {
    }

    public void clearForeground() {

        m_glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                m_renderer_wrapper.clear(1);
            }});
    }

    public void setBackgroundColor(double[] color) {

        final double[] c = color;

        m_glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                m_renderer_wrapper.setBackgroundColor(c);
            }});
    }

    public void showAsset(String file, double[] color) {

        showAsset(1, file, -1, color);
    }

    public void showAsset(String file, int id, double[] color) {

        showAsset(1, file, id, color);
    }

    public void showAsset(int layer, String file, int id, double[] color) {

        final int f_layer = layer;
        final String f_file = file;
        final int f_id = id;
        final double[] f_color = color;

        m_glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                m_renderer_wrapper.show(f_layer, f_file, f_id, f_color);
            }});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_globe, container, false);

        m_fpsCounter = new TextView(getActivity());
        m_fpsCounter.setTextColor(Color.BLACK);
        m_fpsCounter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        m_fpsCounter.setGravity(Gravity.CENTER);

        FrameLayout frameLayout = view.findViewById(R.id.frame_layout);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP);

        m_fpsCounter.setLayoutParams(params);

        frameLayout.addView(m_fpsCounter);

        m_glSurfaceView = view.findViewById(R.id.glSurfaceView);

        if(!createContext()) return view;

        m_renderer_wrapper = new RendererWrapper(getActivity());
        m_glSurfaceView.setRenderer(m_renderer_wrapper);
        m_renderer_set = true;

        m_handler = new Handler();

        m_scale_gesture_detector = new ScaleGestureDetector(getActivity(), m_scale_gesture_listener);
        m_scroll_gesture_detector = new GestureDetectorCompat(getActivity(), m_scroll_gesture_listener);

        // it would be great to change mMinSpan (default 446) of m_scale_gesture_detector
        // but there is no setter function. Perhaps use reflection?

        m_glSurfaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event != null) {

                    m_scale_gesture_detector.onTouchEvent(event);
                    if(!m_scale_gesture_detector.isInProgress()){
                        m_scroll_gesture_detector.onTouchEvent(event);
                    }

                    return true;

                } else {
                    return false;
                }
            }
        });

        m_glSurfaceView.queueEvent(new Runnable() {
           @Override
           public void run() {
               m_renderer_wrapper.clear(0);
               m_renderer_wrapper.clear(1);
               m_renderer_wrapper.clear(2);

               m_renderer_wrapper.setRotation(0.f, 20.f);
               m_renderer_wrapper.setZoom(2.f);
               setBackgroundColor(new double[]{.0, .0, .0, 1.});

               m_renderer_wrapper.show(0, "countries.triangles.jet", -1, new double[]{1., .63, .47, 1.});
               m_renderer_wrapper.show(2, "land_boundaries.lines.jet", -1, new double[]{0.3, 0.3, 0.3, 1.});
               m_renderer_wrapper.show(2, "long_lat_lines.lines.jet", -1, new double[]{0.7, 0.7, 0.7, 1.});
           }
       });

        m_picking_region_asset = "countries.triangles.jet";

        m_mode = Mode.Idle;

        startSpinning();

        return view;
    }

    private void startSpinning() {

        if(m_timer == null) m_timer = new Timer();

        m_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final float lon = m_renderer_wrapper.getRotationLong();
                m_glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        m_renderer_wrapper.rotateTo(lon + 5.f, 20.f, .5f);
                    }
                });
            } }, 0, 500);
    }

    public void setMode(Mode mode) {

        m_mode = mode;

        if(mode == Mode.Idle) startSpinning();
        else {

            m_timer.cancel();
            m_timer = null;
        }
    }

    public Mode getMode() { return m_mode; }

    private boolean createContext() {

        // Check that OpenGL Es 2.0 is supported

        ActivityManager activityManager =
                (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();

        boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            m_glSurfaceView.setEGLContextClientVersion(2);

        } else {

            Toast.makeText(getActivity(), "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (m_renderer_set) {

            m_glSurfaceView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (m_renderer_set) {

            m_glSurfaceView.onResume();
        }
    }

    private final ScaleGestureDetector.OnScaleGestureListener m_scale_gesture_listener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private float lastSpan;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {

            if(m_mode != Mode.Idle) {

                lastSpan = scaleGestureDetector.getCurrentSpan();
            }

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            if(m_mode != Mode.Idle) {

                float span = scaleGestureDetector.getCurrentSpan();

                final float factor = Math.max(span / lastSpan, .1f);

                m_glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        m_renderer_wrapper.handleZoom(m_renderer_wrapper.getZoom() * factor);
                    }
                });

                lastSpan = span;
            }

            return true;
        }
    };

    private final GestureDetector.SimpleOnGestureListener m_scroll_gesture_listener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            m_handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    setSelectedRegion(-1, PressType.None);
                }
            }, 300);

            if(m_mode != Mode.Idle) {

                final float dx = distanceX * m_sensitivity;
                final float dy = distanceY * m_sensitivity;

                m_glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        m_renderer_wrapper.handleTouchDrag(dx, dy);
                    }
                });
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            m_handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    setSelectedRegion(-1, PressType.None);
                }
            }, 300);

            if(m_mode != Mode.Idle) {

                final float vx = m_sensitivity * velocityX;
                final float vy = m_sensitivity * velocityY;

                m_glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        m_renderer_wrapper.handleFling(vx, vy);
                    }
                });
            }

            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

            final float x = e.getX();
            final float y = e.getY();

            m_glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {

                    int region = m_renderer_wrapper.getRegionFromPoint(m_picking_region_asset, x, y);
                    setSelectedRegion(region, PressType.Show);
                }
            });
        }

        @Override
        public void onLongPress(MotionEvent e) {

            final float x = e.getX();
            final float y = e.getY();

            m_glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {

                    int region = m_renderer_wrapper.getRegionFromPoint(m_picking_region_asset, x, y);
                    setSelectedRegion(region, PressType.Long);
                }
            });
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            final float x = e.getX();
            final float y = e.getY();

            m_glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {

                    int region = m_renderer_wrapper.getRegionFromPoint(m_picking_region_asset, x, y);
                    setSelectedRegion(region, PressType.Short);
                }
            });

            m_handler.post(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent("globe_tapped");
                    getActivity().sendBroadcast(intent);
                }
            });

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            m_handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                setSelectedRegion(-1, PressType.None);
        }
            }, 300);

            m_handler.post(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent("globe_double_tapped");
                    getActivity().sendBroadcast(intent);
                }
            });

            return true;
        }
    };

    public void pickRegion(String region_asset) {

        m_picking_region_asset = region_asset;
    }

    public void zoomTo(float zoom) {

        final float f_zoom = zoom;

        m_glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {

                m_renderer_wrapper.zoomTo(f_zoom, .5f);
            }
        });
    }

    public void rotateTo(float longitude, float latitude) {

        final float f_longitude = longitude;
        final float f_latitude = latitude;

        m_glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {

                m_renderer_wrapper.rotateTo(f_longitude, f_latitude, 0.3f);
            }
        });
    }

    public void setSelectedRegion(int region, PressType type) {

        final int f_region = region;
        final int f_type = type.ordinal();

        m_handler.post(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent("region_picked");
                intent.putExtra("region", f_region);
                intent.putExtra("type", f_type);
                getActivity().sendBroadcast(intent);
            }
        });
    }
}
