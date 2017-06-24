package tk.omgpi.utils;

import java.util.Collection;

public class Strings {
    public static String join(Collection c, String joiner) {
        if (c == null || c.isEmpty()) return "";
        String s = null;
        for (Object o : c) {
            if (s == null) s = o + "";
            else s += joiner + o;
        }
        return s;
    }
}
