/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib.tess;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.tess.ITessAPI.TessBaseAPI;
import net.sourceforge.lept4j.Leptonica;
import net.sourceforge.lept4j.Pix;

public class CharacterRecognitor{
    private final String datapath = ".";
    String language = "eng";
    
    public String regconize(String path){
        
        try {
            File tiff = new File(path);
            return getUTF8Text(tiff);
        } catch (IOException ex) {
            Logger.getLogger(CharacterRecognitor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
    
    public String regconize(File file){
        
        try {
            return getUTF8Text(file);
        } catch (IOException ex) {
            Logger.getLogger(CharacterRecognitor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
       
    private String getUTF8Text(File tiff) throws IOException{
        TessBaseAPI handle = TessAPI.TessBaseAPICreate();        
        Leptonica leptInstance = Leptonica.INSTANCE;
        Pix pix = leptInstance.pixRead(tiff.getPath());
        TessAPI.TessBaseAPIInit3(handle, datapath, language);
        TessAPI.TessBaseAPISetImage2(handle, pix);
        Pointer utf8Text = TessAPI.TessBaseAPIGetUTF8Text(handle);
        String result = utf8Text.getString(0);
        TessAPI.TessDeleteText(utf8Text);

        //release Pix resource
        PointerByReference pRef = new PointerByReference();
        pRef.setValue(pix.getPointer());
        leptInstance.pixDestroy(pRef);
        
        TessAPI.TessBaseAPIDelete(handle);
        
        return result;        
    }      
    
}
