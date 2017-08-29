package org.tianjyan.blurbg.BlurProcess;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class RSBlurProcess implements IBlurProcess {
    private Context context;

    public RSBlurProcess(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap blur(Bitmap original, float radius) {
        Bitmap bitmap = Bitmap.createScaledBitmap(original, original.getWidth()/2, original.getHeight()/2, false);

        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        return bitmap;
    }
}
