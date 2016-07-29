package kgk.beacon.util;

public class YandexMapUtils {

    public static double[] getGeoFromTile(int x, int y, int zoom) {
        double a, c1, c2, c3, c4, g, z, mercX, mercY;
        a = 6378137;
        c1 = 0.00335655146887969;
        c2 = 0.00000657187271079536;
        c3 = 0.00000001764564338702;
        c4 = 0.00000000005328478445;
        mercX = (x * 256 * 2 ^ (23 - zoom)) / 53.5865938 - 20037508.342789;
        mercY = 20037508.342789 - (y * 256 * 2 ^ (23 - zoom)) / 53.5865938;

        g = Math.PI / 2 - 2 * Math.atan(1 / Math.exp(mercY / a));
        z = g + c1 * Math.sin(2 * g) + c2 * Math.sin(4 * g) + c3
                * Math.sin(6 * g) + c4 * Math.sin(8 * g);

        return new double[] { mercX / a * 180 / Math.PI, z * 180 / Math.PI };
    }

    public static double[] geoToMercator(double[] g) {
        double d = g[0] * Math.PI / 180, m = g[1] * Math.PI / 180,
                l = 6378137, k = 0.0818191908426,
                f = k * Math.sin(m);
        double h = Math.tan(Math.PI / 4 + m / 2), j = Math.pow(
                Math.tan(Math.PI / 4 + Math.asin(f) / 2), k), i = h / j;
        // return new DoublePoint(Math.round(l * d), Math.round(l *
        // Math.log(i)));
        return new double[] { l * d, l * Math.log(i) };
    }

    public static double[] mercatorToTiles(double[] e) {
        double d = Math.round((20037508.342789 + e[0]) * 53.5865938), f = Math
                .round((20037508.342789 - e[1]) * 53.5865938);
        d = boundaryRestrict(d, 0, 2147483647);
        f = boundaryRestrict(f, 0, 2147483647);
        return new double[] { d, f };
    }

    public static double[] mercatorToGeo(double[] e) {
        double j = Math.PI, f = j / 2, i = 6378137, n = 0.003356551468879694, k = 0.00000657187271079536, h = 1.764564338702e-8, m = 5.328478445e-11;
        double g = f - 2 * Math.atan(1 / Math.exp(e[1] / i));
        double l = g + n * Math.sin(2 * g) + k * Math.sin(4 * g) + h
                * Math.sin(6 * g) + m * Math.sin(8 * g);
        double d = e[0] / i;
        return new double[] { d * 180 / Math.PI, l * 180 / Math.PI };
    }

    public static long[] getTile(double[] h, int i) {
        long e = 8;
        long j = toScale(i), g = (long) h[0] >> j, f = (long) h[1] >> j;
        return new long[] { g >> e, f >> e };
    }

    ////

    private static double boundaryRestrict(double f, double e, double d) {
        return Math.max(Math.min(f, d), e);
    }

    private static int toScale(int i) {
        return 23 - i;
    }
}
