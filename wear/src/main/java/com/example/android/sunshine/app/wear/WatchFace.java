package com.example.android.sunshine.app.wear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;

import com.example.android.sunshine.app.R;

/**
 * See <a hred="http://developer.android.com/training/wearables/watch-faces/service.html">Building a Watch Face Service</a>
 * See <a hred="http://catinean.com/2015/03/07/creating-a-watch-face-with-android-wear-api/">Creating a Watchface with Android Wear | PART 1</a>
 */
public class WatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d.%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ".%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";

    private final Paint timePaint;
    private final Paint datePaint;
    private final Time time;

    private boolean shouldShowSeconds = true;

    public static WatchFace newInstance(Context context) {
        Paint timePaint = new Paint();
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        // Will differentiate the drawing between interactive mode and ambient mode
        timePaint.setColor(Color.WHITE);
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        // Will differentiate the drawing between interactive mode and ambient mode
        datePaint.setColor(Color.WHITE);
        datePaint.setAntiAlias(true);

        return new WatchFace(timePaint, datePaint, new Time());
    }

    WatchFace(Paint timePaint, Paint datePaint, Time time) {
        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.time = time;
    }

    // Perform all the drawing operations on the canvas.
    public void draw(Canvas canvas, Rect bounds) {
        // Place the time to the current time
        time.setToNow();
        // Set the background color of canvas
        canvas.drawColor(Color.BLACK);

        String timeText = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute, time.second);
        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, timePaint, bounds);
        // Draw the time
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);

        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1), time.year);
        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, datePaint);
        // Draw the date
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, datePaint);
    }

    // Helper methods in order to compute the x offset of both time and date drawings.
    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    // Helper methods in order to compute the y offset of both time drawing.
    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    // Helper methods in order to compute the y offset of date drawing.
    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 10.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        timePaint.setColor(color);
        datePaint.setColor(color);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }
}
