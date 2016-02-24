/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib.tess;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import lib.tess.ITessAPI.TessBaseAPI;
import net.sourceforge.lept4j.Boxa;
import net.sourceforge.lept4j.Pix;

class TessAPI implements Library, ITessAPI{

    static {
        Native.register(LoadLibs.getTesseractLibName());
    }

    /**
     * Gets the version identifier.
     *
     * @return the version identifier
     */
    public static native String TessVersion();

    /**
     * Deallocates the memory block occupied by text.
     *
     * @param text the pointer to text
     */
    public static native void TessDeleteText(Pointer text);

    /**
     * Deallocates the memory block occupied by text array.
     *
     * @param arr text array pointer reference
     */
    public static native void TessDeleteTextArray(PointerByReference arr);

    /**
     * Deallocates the memory block occupied by integer array.
     *
     * @param arr int array
     */
    public static native void TessDeleteIntArray(IntBuffer arr);


    /**
     * Creates an instance of the base class for all Tesseract APIs.
     *
     * @return the TesseractAPI instance
     */
    public static native TessBaseAPI TessBaseAPICreate();

    /**
     * Disposes the TesseractAPI instance.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIDelete(TessBaseAPI handle);

    /**
     * Set the name of the input file. Needed only for training and reading a
     * UNLV zone file, and for searchable PDF output.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input file
     */
    public static native void TessBaseAPISetInputName(TessBaseAPI handle, String name);

    /**
     * These functions are required for searchable PDF output. We need our hands
     * on the input file so that we can include it in the PDF without
     * transcoding. If that is not possible, we need the original image.
     * Finally, resolution metadata is stored in the PDF so we need that as
     * well.
     *
     * @param handle the TesseractAPI instance
     * @return input file name
     */
    public static native String TessBaseAPIGetInputName(TessBaseAPI handle);

    public static native void TessBaseAPISetInputImage(TessBaseAPI handle, Pix pix);

    public static native Pix TessBaseAPIGetInputImage(TessBaseAPI handle);

    public static native int TessBaseAPIGetSourceYResolution(TessBaseAPI handle);

    public static native String TessBaseAPIGetDatapath(TessBaseAPI handle);

    /**
     * Set the name of the bonus output files. Needed only for debugging.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the output file
     */
    public static native void TessBaseAPISetOutputName(TessBaseAPI handle, String name);

    /**
     * Set the value of an internal "parameter." Supply the name of the
     * parameter and the value as a string, just as you would in a config file.
     * Returns false if the name lookup failed. E.g.,
     * <code>SetVariable("tessedit_char_blacklist", "xyz");</code> to ignore x,
     * y and z. Or <code>SetVariable("classify_bln_numeric_mode", "1");</code>
     * to set numeric-only mode. <code>SetVariable</code> may be used before
     * <code>Init</code>, but settings will revert to defaults on
     * <code>End()</code>.<br>
     * <br>
     * Note: Must be called after <code>Init()</code>. Only works for non-init
     * variables (init variables should be passed to <code>Init()</code>).
     *
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input
     * @param value variable value
     * @return 1 on success
     */
    public static native int TessBaseAPISetVariable(TessBaseAPI handle, String name, String value);

    /**
     * Get the value of an internal int parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input
     * @param value pass the int buffer value
     * @return 1 on success
     */
    public static native int TessBaseAPIGetIntVariable(TessBaseAPI handle, String name, IntBuffer value);

    /**
     * Get the value of an internal bool parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @param value pass the int buffer value
     * @return 1 on success
     */
    public static native int TessBaseAPIGetBoolVariable(TessBaseAPI handle, String name, IntBuffer value);

    /**
     * Get the value of an internal double parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @param value pass the double buffer value
     * @return 1 on success
     */
    public static native int TessBaseAPIGetDoubleVariable(TessBaseAPI handle, String name, DoubleBuffer value);

    /**
     * Get the value of an internal string parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @return the string value
     */
    public static native String TessBaseAPIGetStringVariable(TessBaseAPI handle, String name);

    /**
     * Print Tesseract parameters to the given file.<br>
     * <br>
     * Note: Must not be the first method called after instance create.
     *
     * @param handle the TesseractAPI instance
     * @param filename name of the file where the variables will be persisted
     */
    public static native void TessBaseAPIPrintVariablesToFile(TessBaseAPI handle, String filename);

    /**
     * Instances are now mostly thread-safe and totally independent, but some
     * global parameters remain. Basically it is safe to use multiple
     * TessBaseAPIs in different threads in parallel, UNLESS you use
     * <code>SetVariable</code> on some of the Params in classify and textord.
     * If you do, then the effect will be to change it for all your
     * instances.<br>
     * <br>
     * Start tesseract. Returns zero on success and -1 on failure. NOTE that the
     * only members that may be called before <code>Init</code> are those listed
     * above here in the class definition.<br>
     * <br>
     * It is entirely safe (and eventually will be efficient too) to call
     * <code>Init</code> multiple times on the same instance to change language,
     * or just to reset the classifier. Languages may specify internally that
     * they want to be loaded with one or more other languages, so the <i>~</i>
     * sign is available to override that. E.g., if <code>hin</code> were set to
     * load <code>eng</code> by default, then <code>hin+~eng</code> would force
     * loading only <code>hin</code>. The number of loaded languages is limited
     * only by memory, with the caveat that loading additional languages will
     * impact both speed and accuracy, as there is more work to do to decide on
     * the applicable language, and there is more chance of hallucinating
     * incorrect words. WARNING: On changing languages, all Tesseract parameters
     * are reset back to their default values. (Which may vary between
     * languages.) If you have a rare need to set a Variable that controls
     * initialization for a second call to <code>Init</code> you should
     * explicitly call <code>End()</code> and then use <code>SetVariable</code>
     * before <code>Init</code>.<br>
     * This is only a very rare use case, since there are very few uses that
     * require any parameters to be set before <code>Init</code>.<br>
     * <br>
     * If <code>set_only_non_debug_params</code> is true, only params that do
     * not contain "debug" in the name will be set.
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @param oem ocr engine mode
     * @param configs pointer configuration
     * @param configs_size pointer configuration size
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit1(TessBaseAPI handle, String datapath, String language, int oem,
            PointerByReference configs, int configs_size);

    /**
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @param oem ocr engine mode
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit2(TessBaseAPI handle, String datapath, String language, int oem);

    /**
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit3(TessBaseAPI handle, String datapath, String language);

    /**
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @param oem ocr engine mode
     * @param configs pointer configuration
     * @param configs_size pointer configuration size
     * @param vars_vec
     * @param vars_values
     * @param vars_vec_size
     * @param set_only_non_debug_params
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit4(TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size, PointerByReference vars_vec, PointerByReference vars_values, NativeSize vars_vec_size, int set_only_non_debug_params);

    /**
     * Returns the languages string used in the last valid initialization. If
     * the last initialization specified "deu+hin" then that will be returned.
     * If <code>hin</code> loaded <code>eng</code> automatically as well, then
     * that will not be included in this list. To find the languages actually
     * loaded, use <code>GetLoadedLanguagesAsVector</code>. The returned string
     * should NOT be deleted.
     *
     * @param handle the TesseractAPI instance
     * @return languages as string
     */
    public static native String TessBaseAPIGetInitLanguagesAsString(TessBaseAPI handle);

    /**
     * Returns the loaded languages in the vector of STRINGs. Includes all
     * languages loaded by the last <code>Init</code>, including those loaded as
     * dependencies of other loaded languages.
     *
     * @param handle the TesseractAPI instance
     * @return loaded languages as vector
     */
    public static native PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(TessBaseAPI handle);

    /**
     * Returns the available languages in the vector of STRINGs.
     *
     * @param handle the TesseractAPI instance
     * @return available languages as vector
     */
    public static native PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(TessBaseAPI handle);

    /**
     * Init only the lang model component of Tesseract. The only functions that
     * work after this init are <code>SetVariable</code> and
     * <code>IsValidWord</code>. WARNING: temporary! This function will be
     * removed from here and placed in a separate API at some future time.
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to eng. The language may be a
     * string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;] indicating that
     * multiple languages are to be loaded. E.g., hin+eng will load Hindi and
     * English.
     * @return api init language mode
     */
    public static native int TessBaseAPIInitLangMod(TessBaseAPI handle, String datapath, String language);

    /**
     * Init only for page layout analysis. Use only for calls to
     * <code>SetImage</code> and <code>AnalysePage</code>. Calls that attempt
     * recognition will generate an error.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIInitForAnalysePage(TessBaseAPI handle);

    /**
     * Read a "config" file containing a set of param, value pairs. Searches the
     * standard places: <code>tessdata/configs</code>,
     * <code>tessdata/tessconfigs</code> and also accepts a relative or absolute
     * path name. Note: only non-init params will be set (init params are set by
     * <code>Init()</code>).
     *
     *
     * @param handle the TesseractAPI instance
     * @param filename relative or absolute path for the "config" file
     * containing a set of param and value pairs
     * @param init_only
     */
    public static native void TessBaseAPIReadConfigFile(TessBaseAPI handle, String filename, int init_only);

    /**
     * Set the current page segmentation mode. Defaults to
     * <code>PSM_SINGLE_BLOCK</code>. The mode is stored as an IntParam so it
     * can also be modified by <code>ReadConfigFile</code> or
     * <code>SetVariable("tessedit_pageseg_mode", mode as string)</code>.
     *
     * @param handle the TesseractAPI instance
     * @param mode tesseract page segment mode
     */
    public static native void TessBaseAPISetPageSegMode(TessBaseAPI handle, int mode);

    /**
     * Return the current page segmentation mode.
     *
     * @param handle the TesseractAPI instance
     * @return page segment mode value
     */
    public static native int TessBaseAPIGetPageSegMode(TessBaseAPI handle);

    /**
     * Recognize a rectangle from an image and return the result as a string.
     * May be called many times for a single <code>Init</code>. Currently has no
     * error checking. Greyscale of 8 and color of 24 or 32 bits per pixel may
     * be given. Palette color images will not work properly and must be
     * converted to 24 bit. Binary images of 1 bit per pixel may also be given
     * but they must be byte packed with the MSB of the first byte being the
     * first pixel, and a 1 represents WHITE. For binary images set
     * bytes_per_pixel=0. The recognized text is returned as a char* which is
     * coded as UTF8 and must be freed with the delete [] operator.<br>
     * <br>
     * Note that <code>TesseractRect</code> is the simplified convenience
     * interface. For advanced uses, use <code>SetImage</code>, (optionally)
     * <code>SetRectangle</code>, <code>Recognize</code>, and one or more of the
     * <code>Get*Text</code> functions below.
     *
     * @param handle the TesseractAPI instance
     * @param imagedata image byte buffer
     * @param bytes_per_pixel bytes per pixel
     * @param bytes_per_line bytes per line
     * @param left image left
     * @param top image top
     * @param width image width
     * @param height image height
     * @return the pointer to recognized text
     */
    public static native Pointer TessBaseAPIRect(TessBaseAPI handle, ByteBuffer imagedata,
            int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height);

    /**
     * Call between pages or documents etc to free up memory and forget adaptive
     * data.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIClearAdaptiveClassifier(TessBaseAPI handle);

    /**
     * Provide an image for Tesseract to recognize. Format is as
     * <code>TesseractRect</code> above. Does not copy the image buffer, or take
     * ownership. The source image may be destroyed after <code>Recognize</code> is called,
     * either explicitly or implicitly via one of the <code>Get*Text</code>
     * functions. <code>SetImage</code> clears all recognition results, and sets
     * the rectangle to the full image, so it may be followed immediately by a
     * <code>GetUTF8Text</code>, and it will automatically perform recognition.
     *
     * @param handle the TesseractAPI instance
     * @param imagedata image byte buffer
     * @param width image width
     * @param height image height
     * @param bytes_per_pixel bytes per pixel
     * @param bytes_per_line bytes per line
     */
    public static native void TessBaseAPISetImage(TessBaseAPI handle, ByteBuffer imagedata, int width,
            int height, int bytes_per_pixel, int bytes_per_line);

    /**
     * Provide an image for Tesseract to recognize. As with
     * <code>SetImage</code> above, Tesseract doesn't take a copy or ownership
     * or <code>pixDestroy</code> the image, so it must persist until after
     * <code>Recognize</code>. <code>Pix</code> vs raw, which to use? Use
     * <code>Pix</code> where possible. A future version of Tesseract may choose
     * to use <code>Pix</code> as its internal representation and discard
     * <code>IMAGE</code> altogether. Because of that, an implementation that
     * sources and targets <code>Pix</code> may end up with less copies than an
     * implementation that does not.
     *
     * @param handle the TesseractAPI instance
     * @param pix
     */
    public static native void TessBaseAPISetImage2(TessBaseAPI handle, Pix pix);

    /**
     * Set the resolution of the source image in pixels per inch so font size
     * information can be calculated in results. Call this after
     * <code>SetImage()</code>.
     *
     * @param handle the TesseractAPI instance
     * @param ppi source resolution value
     */
    public static native void TessBaseAPISetSourceResolution(TessBaseAPI handle, int ppi);

    /**
     * Restrict recognition to a sub-rectangle of the image. Call after
     * <code>SetImage</code>. Each <code>SetRectangle</code> clears the
     * recognition results so multiple rectangles can be recognized with the
     * same image.
     *
     * @param handle the TesseractAPI instance
     * @param left value
     * @param top value
     * @param width value
     * @param height value
     */
    public static native void TessBaseAPISetRectangle(TessBaseAPI handle, int left, int top, int width,
            int height);

    /**
     * ONLY available after <code>SetImage</code> if you have Leptonica
     * installed. Get a copy of the internal thresholded image from Tesseract.
     *
     * @param handle the TesseractAPI instance
     * @return internal thresholded image
     */
    public static native Pix TessBaseAPIGetThresholdedImage(TessBaseAPI handle);

    /**
     * Get the result of page layout analysis as a Leptonica-style
     * <code>Boxa</code>, <code>Pixa</code> pair, in reading order. Can be
     * called before or after <code>Recognize</code>.
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetRegions(TessBaseAPI handle, PointerByReference pixa);

    /**
     * Get the textlines as a Leptonica-style <code>Boxa</code>,
     * <code>Pixa</code> pair, in reading order. Can be called before or after
     * <code>Recognize</code>. If <code>blockids</code> is not <code>NULL</code>, the
     * block-id of each line is also returned as an array of one element per
     * line. delete [] after use. If <code>paraids</code> is not
     * <code>NULL</code>, the paragraph-id of each line within its block is also
     * returned as an array of one element per line. delete [] after use.<br>
     * Helper method to extract from the thresholded image (most common usage).
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @param blockids
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetTextlines(TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids);

    /**
     * Get the textlines as a Leptonica-style <code>Boxa</code>,
     * <code>Pixa</code> pair, in reading order. Can be called before or after
     * <code>Recognize</code>. If <code>blockids</code> is not <code>NULL</code>, the
     * block-id of each line is also returned as an array of one element per
     * line. delete [] after use. If <code>paraids</code> is not
     * <code>NULL</code>, the paragraph-id of each line within its block is also
     * returned as an array of one element per line. delete [] after use.
     *
     * @param handle the TesseractAPI instance
     * @param raw_image
     * @param raw_padding
     * @param pixa array of Pix
     * @param blockids
     * @param paraids
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetTextlines1(TessBaseAPI handle, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids);

    /**
     * Get textlines and strips of image regions as a Leptonica-style
     * <code>Boxa</code>, <code>Pixa</code> pair, in reading order. Enables
     * downstream handling of non-rectangular regions. Can be called before or
     * after <code>Recognize</code>. If <code>blockids</code> is not NULL, the block-id of
     * each line is also returned as an array of one element per line. delete []
     * after use.
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @param blockids
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetStrips(TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids);

    /**
     * Get the words as a Leptonica-style <code>Boxa</code>, <code>Pixa</code>
     * pair, in reading order. Can be called before or after
     * <code>Recognize</code>.
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetWords(TessBaseAPI handle, PointerByReference pixa);

    /**
     * Gets the individual connected (text) components (created after pages
     * segmentation step, but before recognition) as a Leptonica-style
     * <code>Boxa</code>, <code>Pixa</code> pair, in reading order. Can be
     * called before or after <code>Recognize</code>.
     *
     * @param handle the TesseractAPI instance
     * @param cc array of Pix
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetConnectedComponents(TessBaseAPI handle, PointerByReference cc);

    /**
     * Get the given level kind of components (block, textline, word etc.) as a
     * Leptonica-style <code>Boxa</code>, <code>Pixa</code> pair, in reading
     * order. Can be called before or after <code>Recognize</code>. If <code>blockids</code>
     * is not <code>NULL</code>, the block-id of each component is also returned
     * as an array of one element per component. delete [] after use. If
     * <code>text_only</code> is true, then only text components are returned.
     * Helper function to get binary images with no padding (most common usage).
     *
     * @param handle the TesseractAPI instance
     * @param level PageIteratorLevel
     * @param text_only
     * @param pixa array of Pix
     * @param blockids
     * @return array of Box
     */
    public static native Boxa TessBaseAPIGetComponentImages(TessBaseAPI handle, int level, int text_only, PointerByReference pixa, PointerByReference blockids);

    /**
     * Get the given level kind of components (block, textline, word etc.) as a
     * Leptonica-style <code>Boxa</code>, <code>Pixa</code> pair, in reading
     * order. Can be called before or after <code>Recognize</code>. If <code>blockids</code>
     * is not <code>NULL</code>, the block-id of each component is also returned
     * as an array of one element per component. delete [] after use. If
     * <code>paraids</code> is not <code>NULL</code>, the paragraph-id of each
     * component with its block is also returned as an array of one element per
     * component. delete [] after use. If <code>raw_image</code> is true, then
     * portions of the original image are extracted instead of the thresholded
     * image and padded with raw_padding. If <code>text_only</code> is true,
     * then only text components are returned.
     *
     * @param handle the TesseractAPI instance
     * @param level PageIteratorLevel
     * @param text_only
     * @param raw_image
     * @param raw_padding
     * @param pixa array of Pix
     * @param blockids
     * @param paraids
     * @return
     */
    public static native Boxa TessBaseAPIGetComponentImages1(TessBaseAPI handle, int level, int text_only, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids);

    /**
     * @param handle the TesseractAPI instance
     * @return Scale factor from original image.
     */
    public static native int TessBaseAPIGetThresholdedImageScaleFactor(TessBaseAPI handle);

    /**
     * Dump the internal binary image to a PGM file.
     *
     * @param handle the TesseractAPI instance
     * @param filename pgm file name
     */
    public static native void TessBaseAPIDumpPGM(TessBaseAPI handle, String filename);

    /**
     * The recognized text is returned as a char* which is coded as UTF-8 and
     * must be freed with the delete [] operator.
     *
     * @param handle the TesseractAPI instance
     * @return the pointer to output text
     */
    public static native Pointer TessBaseAPIGetUTF8Text(TessBaseAPI handle);

    /**
     * Make a HTML-formatted string with hOCR markup from the internal data
     * structures. page_number is 0-based but will appear in the output as
     * 1-based.
     *
     * @param handle the TesseractAPI instance
     * @param page_number page number
     * @return the pointer to hOCR text
     */
    public static native Pointer TessBaseAPIGetHOCRText(TessBaseAPI handle, int page_number);

    /**
     * The recognized text is returned as a char* which is coded as a UTF8 box
     * file and must be freed with the delete [] operator. page_number is a
     * 0-base page index that will appear in the box file.
     *
     * @param handle the TesseractAPI instance
     * @param page_number number of the page
     * @return the pointer to box text
     */
    public static native Pointer TessBaseAPIGetBoxText(TessBaseAPI handle, int page_number);

    /**
     * The recognized text is returned as a char* which is coded as UNLV format
     * Latin-1 with specific reject and suspect codes and must be freed with the
     * delete [] operator.
     *
     * @param handle the TesseractAPI instance
     * @return the pointer to UNLV text
     */
    public static native Pointer TessBaseAPIGetUNLVText(TessBaseAPI handle);

    /**
     * Returns the average word confidence for Tesseract page result.
     *
     * @param handle the TesseractAPI instance
     * @return the (average) confidence value between 0 and 100.
     */
    public static native int TessBaseAPIMeanTextConf(TessBaseAPI handle);

    /**
     * Returns an array of all word confidences, terminated by -1. The calling
     * function must delete [] after use. The number of confidences should
     * correspond to the number of space-delimited words in
     * <code>GetUTF8Text</code>.
     *
     * @param handle the TesseractAPI instance
     * @return all word confidences (between 0 and 100) in an array, terminated
     * by -1
     */
    public static native IntByReference TessBaseAPIAllWordConfidences(TessBaseAPI handle);

    /**
     * Applies the given word to the adaptive classifier if possible. The word
     * must be SPACE-DELIMITED UTF-8 - l i k e t h i s , so it can tell the
     * boundaries of the graphemes. Assumes that
     * <code>SetImage</code>/<code>SetRectangle</code> have been used to set the
     * image to the given word. The mode arg should be
     * <code>PSM_SINGLE_WORD</code> or <code>PSM_CIRCLE_WORD</code>, as that
     * will be used to control layout analysis. The currently set PageSegMode is
     * preserved.
     *
     * @param handle the TesseractAPI instance
     * @param mode tesseract page segment mode
     * @param wordstr The word must be SPACE-DELIMITED UTF-8 - l i k e t h i s ,
     * so it can tell the boundaries of the graphemes.
     * @return false if adaption was not possible for some reason.
     */
    public static native int TessBaseAPIAdaptToWordStr(TessBaseAPI handle, int mode, String wordstr);

    /**
     * Free up recognition results and any stored image data, without actually
     * freeing any recognition data that would be time-consuming to reload.
     * Afterwards, you must call <code>SetImage</code> or
     * <code>TesseractRect</code> before doing any <code>Recognize</code> or
     * <code>Get*</code> operation.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIClear(TessBaseAPI handle);

    /**
     * Close down tesseract and free up all memory. <code>End()</code> is
     * equivalent to destructing and reconstructing your TessBaseAPI. Once
     * <code>End()</code> has been used, none of the other API functions may be
     * used other than <code>Init</code> and anything declared above it in the
     * class definition.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIEnd(TessBaseAPI handle);

    /**
     * Check whether a word is valid according to Tesseract's language model.
     *
     * @param handle the TesseractAPI instance
     * @param word word value
     * @return 0 if the word is invalid, non-zero if valid
     */
    public static native int TessBaseAPIIsValidWord(TessBaseAPI handle, String word);

    /**
     * Gets text direction.
     *
     * @param handle the TesseractAPI instance
     * @param out_offset offset
     * @param out_slope slope
     * @return TRUE if text direction is valid
     */
    public static native int TessBaseAPIGetTextDirection(TessBaseAPI handle, IntBuffer out_offset,
            FloatBuffer out_slope);

    /**
     * Clear any library-level memory caches. There are a variety of
     * expensive-to-load constant data structures (mostly language dictionaries)
     * that are cached globally -- surviving the <code>Init()</code> and
     * <code>End()</code> of individual TessBaseAPI's. This function allows the
     * clearing of these caches.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIClearPersistentCache(TessBaseAPI handle);   
}
