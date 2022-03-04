package com.example.federateddemo.Helpers;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Handles loading the trained multiple signature model stored as a directory under Android assets.
 */
public class ModelLoader {

    private AssetManager assetManager;
    private String directoryName;

    /**
     * Create a loader for a transfer learning model under given directory.
     *
     * @param directoryName path to model directory in assets tree.
     */
    public ModelLoader(Context context, String directoryName) {
        this.directoryName = directoryName;
        this.assetManager = context.getAssets();
    }

    protected MappedByteBuffer loadMappedFile(String filePath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(this.directoryName + "/" + filePath);

        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(MapMode.READ_ONLY, startOffset, declaredLength);
    }
}