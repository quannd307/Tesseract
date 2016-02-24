package lib.tess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads native libraries from JAR or project folder.
 */
class LoadLibs {

    private static final String JNA_LIBRARY_PATH = "jna.library.path";
    static final String TESS4J_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), "tess4j").getPath();

    /**
     * Native library name.
     */
    static final String LIB_NAME = "libtesseract304";
    static final String LIB_NAME_NON_WIN = "tesseract";

    static {
        try {
            System.setProperty("jna.encoding", "UTF8");
            File targetTempFolder = extractTessResources(Platform.RESOURCE_PREFIX);
            if (targetTempFolder != null && targetTempFolder.exists()) {
                String userCustomizedPath = System.getProperty(JNA_LIBRARY_PATH);
                if (null == userCustomizedPath || userCustomizedPath.isEmpty()) {
                    System.setProperty(JNA_LIBRARY_PATH, targetTempFolder.getPath());
                } else {
                    System.setProperty(JNA_LIBRARY_PATH, userCustomizedPath + File.pathSeparator + targetTempFolder.getPath());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadLibs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads Tesseract library via JNA.
     *
     * @return TessAPI instance being loaded using
     * <code>Native.loadLibrary()</code>.
     */
    static TessAPI getTessAPIInstance() {
        return (TessAPI) Native.loadLibrary(getTesseractLibName(), TessAPI.class);
    }

    /**
     * Gets native library name.
     *
     * @return the name of the tesseract library to be loaded using the
     * <code>Native.register()</code>.
     */
    static String getTesseractLibName() {
        return Platform.isWindows() ? LIB_NAME : LIB_NAME_NON_WIN;
    }

    /**
     * Extracts tesseract resources to temp folder.
     *
     * @param resourceName name of file or directory
     * @return target path, which could be file or directory
     */
    static File extractTessResources(String resourceName) throws IOException {
        File targetPath = null;

        try {
            targetPath = new File(TESS4J_TEMP_DIR, resourceName);

            Enumeration<URL> resources = LoadLibs.class.getClassLoader().getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL resourceUrl = resources.nextElement();
                copyResources(resourceUrl, targetPath);
            }
        } catch (IOException e) {
            throw e;
        }

        return targetPath;
    }

    /**
     * Copies resources to target folder.
     *
     * @param resourceUrl
     * @param targetPath
     * @return
     */
    static void copyResources(URL resourceUrl, File targetPath) throws IOException {
        if (resourceUrl == null) {
            return;
        }

        URLConnection urlConnection = resourceUrl.openConnection();

        /**
         * Copy resources either from inside jar or from project folder.
         */
        if (urlConnection instanceof JarURLConnection) {
            copyJarResourceToPath((JarURLConnection) urlConnection, targetPath);
        } else {
            File file = new File(resourceUrl.getPath());
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, targetPath);
            } else {
                FileUtils.copyFile(file, targetPath);
            }
        }
    }

    /**
     * Copies resources from the jar file of the current thread and extract it
     * to the destination path.
     *
     * @param jarConnection
     * @param destPath destination file or directory
     */
    static void copyJarResourceToPath(JarURLConnection jarConnection, File destPath) throws IOException {
        try {
            JarFile jarFile = jarConnection.getJarFile();
            String jarConnectionEntryName = jarConnection.getEntryName();

            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName + "/")) {
                    String filename = jarEntryName.substring(jarConnectionEntryName.length());
                    File currentFile = new File(destPath, filename);

                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs();
                    } else {
                        currentFile.deleteOnExit();
                        InputStream is = jarFile.getInputStream(jarEntry);
                        OutputStream out = FileUtils.openOutputStream(currentFile);
                        IOUtils.copy(is, out);
                        is.close();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
