package kgk.beacon.map.osm;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

/**
 * Кастомная реализация объекта доступа к хранилищу тайлов для OSM карть
 */
public class XYTileSourceKGK extends XYTileSource {

    public XYTileSourceKGK(final String aName, final int aZoomMinLevel,
                           final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
                           final String[] aBaseUrl) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding, aBaseUrl);
    }

    @Override
    public String getTileURLString(final MapTile aTile) {
        return getBaseUrl() + "get?z=" + aTile.getZoomLevel() + "&x=" + aTile.getX() + "&y=" + aTile.getY();
    }
}
