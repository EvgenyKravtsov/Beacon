package kgk.beacon.monitoring.presentation.routereport;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class RouteReportSeekBar extends SeekBar {

    private Drawable thumb;

    ////

    public RouteReportSeekBar(Context context) {
        super(context);
    }

    public RouteReportSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RouteReportSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ////

    public Drawable getSeekBarThumb() {
        return thumb;
    }

    ////

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        this.thumb = thumb;
    }
}
