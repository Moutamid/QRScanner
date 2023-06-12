package free.qr.code.scanner.generator.utils;

import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapIO {

    public static boolean write(Bitmap image, String type, OutputStream stream) throws IOException {

        // Default should be Bitmap for android
        return BmpUtil.save(image, stream);

    }

    public static boolean write(Bitmap image, String type, File file) throws IOException {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            return write(image, type, stream);
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
    }
}
