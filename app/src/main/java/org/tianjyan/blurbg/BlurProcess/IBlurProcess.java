package org.tianjyan.blurbg.BlurProcess;

import android.graphics.Bitmap;

public interface IBlurProcess {
    Bitmap blur(Bitmap original, float radius);
}
