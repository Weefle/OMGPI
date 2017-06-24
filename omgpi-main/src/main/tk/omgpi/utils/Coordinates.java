package tk.omgpi.utils;

/**
 * Coordinates parsing utils
 */
public class Coordinates {
    /**
     * Turn string like 0, 0, 0, ... into double array.
     *
     * @param s List of doubles.
     * @param c Type of parsing to use.
     * @return Array of coordinates.
     */
    public static double[] parse(String s, CoordinateType c) {
        s = s.replaceAll(" ", "");
        String[] coords = s.split(",");
        double[] ds = new double[c == CoordinateType.AREA ? 6 : c == CoordinateType.ROTATION ? 5 : 3];
        for (int i = 0; i < Math.min(ds.length, coords.length); i++) {
            ds[i] = Double.parseDouble(coords[i]);
        }
        if (c == CoordinateType.AREA) {
            if (coords.length == 6) for (int i = 0; i < 3; i++) {
                if (ds[i] > ds[i + 3]) {
                    double d = ds[i];
                    ds[i] = ds[i + 3];
                    ds[i + 3] = d;
                }
            }
            else System.arraycopy(ds, 0, ds, 3, 3);
        }
        return ds;
    }

    /**
     * Types of coordinate parsing
     */
    public enum CoordinateType {
        /**
         * AREA will sort out minimum and maximum points.
         * AREA sort: [0-2] min point, [3-5] max point.
         */
        AREA,
        /**
         * POINT will just put x, y and z into an array.
         */
        POINT,
        /**
         * ROTATION does yaw and pitch (if present).
         */
        ROTATION
    }
}
