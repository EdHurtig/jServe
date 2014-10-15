package jServe.Core;

import java.util.ArrayList;
import java.util.HashMap;

public class MIME {
    private ArrayList<String> types;

    public MIME(ArrayList<String> types) {
        this.setTypes(types);

    }

    private static HashMap<String, ArrayList<String>> mimes = new HashMap<String, ArrayList<String>>();

    public static MIME getMIME(String filename) {
        String extention = filename.substring(filename.lastIndexOf('.'));

        MIME mime = new MIME(mimes.get(extention));
        return mime;
    }

    public static void registerMIME(String extention, ArrayList<String> types) {
        mimes.put(extention, types);
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

}
