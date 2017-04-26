package com.stardust.mi666.ocr;

import android.graphics.Bitmap;

/**
 * Created by Stardust on 2017/4/23.
 */

public interface OCR {

    void addChar(char ch, Bitmap bitmap);

    char detect(Bitmap bitmap);

}
