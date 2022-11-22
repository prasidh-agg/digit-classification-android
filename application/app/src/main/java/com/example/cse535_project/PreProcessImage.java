package com.example.cse535_project;

import static com.example.cse535_project.PreProcessImage.resizeImage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import androidx.appcompat.app.AppCompatActivity;

public class PreProcessImage extends AppCompatActivity {
    public static Bitmap greyscale(Bitmap colorPhoto) {
        int imgSize = Math.min(colorPhoto.getWidth(), colorPhoto.getHeight());
        int[] pixels = new int[28 * 28];
        Bitmap resizedBitmap = resizeImage(colorPhoto, imgSize, imgSize);
        // Load bitmap pixels into the temporary pixels variable
        resizedBitmap.getPixels(pixels, 0, 28, 0, 0, 28, 28);
        float[] retPixels = createInputPixels(pixels);

        int[] previewPixels = createPixelsPreview(pixels, retPixels);
        Bitmap preview = Bitmap.createBitmap(previewPixels, 28, 28, Bitmap.Config.ARGB_8888);
        return preview;
    }

    public static Bitmap resizeImage(Bitmap source, int newHeight,
                              int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top
                + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        return Bitmap.createScaledBitmap(dest, 28, 28, false);
    }

    private static int[] createPixelsPreview(int[] pixels, float[] retPixels) {
        int[] again = new int[pixels.length];
        for (int a = 0; a < pixels.length; a++) {
            again[a] = ColorConverter.tfToPixel(retPixels[a]);
        }
        return again;
    }

    private static float[] createInputPixels(int[] pixels) {
        float[] normalized = ColorConverter.convertToTfFormat(pixels);
        return normalized;
    }
}
