package tk.omgpi.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipFile;

/**
 * Utils for managing NMS.
 */
public class ReflectionUtils {
    /**
     * Lib with net.minecraft.server classes
     */
    public static OMGList<String> nmsclasses = classStartsWith("net.minecraft.server.v");
    /**
     * Lib with org.bukkit.craftbukkit classes
     */
    public static OMGList<String> cbclasses = classStartsWith("org.bukkit.craftbukkit.v");

    /**
     * Get classes in the package, or a class that starts with given prefix
     *
     * @param prefix Starts with to check
     * @return List of full classes names
     */
    public static OMGList<String> classStartsWith(String prefix) {
        prefix = prefix.replaceAll("\\.", "/");
        OMGList<String> ss = new OMGList<>();
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();
        for (URL url : urls)
            try {
                File jar = new File(url.toURI());
                if (!jar.isDirectory()) {
                    try {
                        String finalPrefix = prefix;
                        new ZipFile(jar).stream().filter(e -> e.getName().startsWith(finalPrefix) && e.getName().endsWith(".class")).forEach(e -> ss.add(e.getName().substring(0, e.getName().length() - 6).replaceAll("/", ".")));
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        return ss;
    }

    /**
     * Find a class in a lib
     *
     * @param lib  nmsclasses, cbclasses or use classStartsWith().
     * @param name Class name
     * @return First matching class
     */
    public static Class<?> getClazz(OMGList<String> lib, String name) {
        try {
            return Class.forName(lib.omgstream().filter(cn -> cn.endsWith("." + name)).findFirst().orElse(null));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get version of NMS package
     *
     * @return v1_X_RY
     */
    public static String version() {
        String verpart = nmsclasses.get(0).replaceAll("net\\.minecraft\\.server\\.", "");
        return verpart.substring(0, verpart.indexOf("."));
    }

    /**
     * Get version of NMS package as an int
     *
     * @return v1_ THIS _RY
     */
    public static int intVer() {
        return Integer.parseInt(version().split("_")[1]);
    }
}
