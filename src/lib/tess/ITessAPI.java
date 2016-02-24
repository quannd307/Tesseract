package lib.tess;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * An interface represents common TessAPI classes/constants.
 */
interface ITessAPI {

    /**
     * Base class for all tesseract APIs. Specific classes can add ability to
     * work on different inputs or produce different outputs. This class is
     * mostly an interface layer on top of the Tesseract instance class to hide
     * the data types so that users of this class don't have to include any
     * other Tesseract headers.
     */
    public static class TessBaseAPI extends PointerType {
    
        public TessBaseAPI(Pointer address) {
            super(address);
        }

        public TessBaseAPI() {
            super();
        }
    };
    
}
