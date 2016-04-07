package kgk.beacon.map.osm;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class PointMarkerOverlayItem extends OverlayItem {

    public PointMarkerOverlayItem(Resources resources, Bitmap bitmap, double latitude, double longitude) {
        super("", "", new GeoPoint(latitude, longitude));
        this.setMarker(new BitmapDrawable(resources, bitmap));
    }
}
