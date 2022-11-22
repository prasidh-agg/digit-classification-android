package com.example.cse535_project;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DigitsDetector {

     // Name of the file in the assets folder
    private static final String MODEL_PATH = "mnist2.tflite";

    private Interpreter tflite;

    // Output array [batch_size, 10]
    private float[][] mnistOutput = null;

    // Specify the output size
    private static final int NUMBER_LENGTH = 10;

    // Specify the input size
    private int width = 0;
    private int height = 0;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 1;

    // Number of bytes to hold a float (32 bits / float) / (8 bits / byte) = 4 bytes / float
    private static final int BYTE_SIZE_OF_FLOAT = 4;

    public DigitsDetector(Context context) {
    try {
        // Define the TensorFlow Lite Interpreter with the model
        tflite = new Interpreter(loadModelFile(context.getAssets()));
        int[] shape = tflite.getInputTensor(0).shape();
        width = shape[1];
        height = shape[2];
        mnistOutput = new float[DIM_BATCH_SIZE][NUMBER_LENGTH];
        } catch (IOException e) {
        Log.e("TAG", "IOException loading the tflite file");
    }
  }
  /**
   * Load the model file from the assets folder
   */
  public int detectDigit(Bitmap bitmap) {
      Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, width, height, true);
      Bitmap finalImage = PreProcessImage.greyscale(resizedImage);
      ByteBuffer byteBuffer = convertBitmapToByteBuffer(finalImage);

      tflite.run(byteBuffer, mnistOutput);

      float[] result = mnistOutput[0];
      return maxIdx(result);
  }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                BYTE_SIZE_OF_FLOAT * DIM_BATCH_SIZE * width * height * DIM_PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            // Set 0 for white and 255 for black pixels
            int channel = pixel & 0xff;
            byteBuffer.putFloat(0xff - channel);
        }

        return byteBuffer;
    }
    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
      AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
      FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
      FileChannel fileChannel = inputStream.getChannel();
      long startOffset = fileDescriptor.getStartOffset();
      long declaredLength = fileDescriptor.getDeclaredLength();
      return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
  }

    public int maxIdx(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}