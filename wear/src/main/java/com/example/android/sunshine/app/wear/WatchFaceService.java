package com.example.android.sunshine.app.wear;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

/**
 * See <a hred="http://developer.android.com/training/wearables/watch-faces/service.html">Building a Watch Face Service</a>
 * See <a hred="http://catinean.com/2015/03/07/creating-a-watch-face-with-android-wear-api/">Creating a Watchface with Android Wear | PART 1</a>
 */
public class WatchFaceService extends CanvasWatchFaceService {

    // Logging Identifier for the class
    private static String LOG_TAG = WatchFaceService.class.getSimpleName();

    // Update rate in milliseconds for interactive mode. We update once a second since seconds are
    // displayed in interactive mode.
    private static final long INTERACTIVE_TICK_PERIOD_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine() {
        // Return the concrete implementation of the Engine
        return new WatchFaceEngine();
    }

    private class WatchFaceEngine extends CanvasWatchFaceService.Engine {
        // Handler that will post a runnable only if the watch is visible and not in ambient mode in order to start ticking
        private Handler timeTick;
        // Instance of a watch face
        private WatchFace digitalWatchFace;

        // Define your watch face style and other graphical elements.
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            // In defining the watch face style, you can customise how the UI elements such as the battery
            // indicator are drawn over the watch face or how the cards are behaving in both normal and ambient mode.
            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
                    // Specify that the first card peeked and shown on the watch will have a single
                    // line tail (i.e. it will have small height)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    // Watch enters in ambient mode, no peek card will be visible
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    // Background of the peek card should only be shown briefly, and only if the peek card
                    // represents an interruptive notification
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    // Set the UI time to false because we will already show the time on the watch by drawing it onto the canvas
                    .setShowSystemUiTime(false)
                    .build());

            timeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();
            // Initialize the watchface
            digitalWatchFace = WatchFace.newInstance(WatchFaceService.this);
        }

        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }
        }

        // Actual runnable posted by timeTick handler. It invalidates the watch and schedules
        // another run of itself on the handler with a delay of one second (since we want to tick every second) if necessary
        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    timeTick.postDelayed(this, INTERACTIVE_TICK_PERIOD_UPDATE_RATE_MS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        // Called when the watch becomes visible or not.
        @Override
        public void onVisibilityChanged(boolean visible) {
            //Must call super() first
            super.onVisibilityChanged(visible);
            startTimerIfNecessary();
        }

        // Lets draw the WatchFace
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            digitalWatchFace.draw(canvas, bounds);
        }

        // Called when the device enters or exits ambient mode. While on ambient mode, one should be considerate
        // to preserve battery consumption by providing a black and white display and not provide any animation such as displaying seconds.
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            // Battery performance optimizations
            digitalWatchFace.setAntiAlias(!inAmbientMode);
            // We set the color to gray in Ambien
            digitalWatchFace.setColor(inAmbientMode ? Color.GRAY : Color.WHITE);
            // Hide seconds in order to minimize the amount of animations (draws)
            digitalWatchFace.setShowSeconds(!isInAmbientMode());
            invalidate();
            startTimerIfNecessary();
        }

        // Callback is invoked every minute when the watch is in ambient mode. It is very important to consider that this callback is only
        // invoked while on ambient mode, as it's name is rather confusing suggesting that this callbacks every time.
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            // Above being said, usually, here we will have only to invalidate() the watch in order to trigger onDraw(). In order to keep track
            // of time outside ambient mode, we will have to provide our own mechanism.
            invalidate();
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            super.onDestroy();
        }
    }
}
