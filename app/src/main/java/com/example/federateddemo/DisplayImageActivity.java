package com.example.federateddemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.federateddemo.databinding.ActivityDisplayImageBinding;
import com.example.federateddemo.ml.ModelMobilenet20epoch;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Arrays;

public class DisplayImageActivity extends AppCompatActivity {

    private static final String TAG = "DisplayImageActivity";
    ActivityDisplayImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        int fromActivity = intent.getIntExtra(Constants.FROM_ACTIVITY, Constants.PICK_IMAGE);
        Bitmap bitmap = null;
        if (fromActivity == Constants.CAPTURE_IMAGE) {

            Uri uri = Uri.parse(intent.getStringExtra(Constants.IMAGE_URI));

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                binding.image.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.e(TAG, "onCreate: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (fromActivity == Constants.PICK_IMAGE) {

            Uri selectedImage = Uri.parse(intent.getStringExtra(Constants.IMAGE_URI));
            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d(TAG, "onActivityResult: picturePath : " + picturePath);


            binding.imageName.setText(Paths.get(picturePath).getFileName().toString());
            bitmap = BitmapFactory.decodeFile(picturePath);
            binding.image.setImageBitmap(bitmap);
        }

        try {
            assert bitmap != null;
            infer(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void infer(Bitmap bitmap) throws IOException {


        Log.d(TAG, "infer: " + bitmap.getWidth() + " x " + bitmap.getHeight());
//        ModelMobilenet95 model = ModelMobilenet95.newInstance(this);
        ModelMobilenet20epoch modelMobilenet20epoch = ModelMobilenet20epoch.newInstance(this);

        // Creates inputs for reference.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                        .build();

        TensorImage tfImage = new TensorImage(DataType.FLOAT32);
        tfImage.load(bitmap);
        tfImage = imageProcessor.process(tfImage);

        Log.d(TAG, "infer: remaining " + tfImage.getBuffer().remaining());


//        TensorImage tfImage = TensorImage.fromBitmap(bitmap);


        ByteBuffer byteBuffer = ByteBuffer.allocate(224 * 224 * 3);
        byteBuffer.rewind();
        byteBuffer = tfImage.getBuffer();
        Log.d(TAG, "infer: " + tfImage.getBuffer().array().length);

        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(byteBuffer);


//        ModelMobilenet95.Outputs outputs = model.process(inputFeature0);
        ModelMobilenet20epoch.Outputs outputs = modelMobilenet20epoch.process(inputFeature0);

        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        Log.d(TAG, "infer: " + outputFeature0.toString());

        float[] data = outputFeature0.getFloatArray();

        binding.preds.setText(String.valueOf(data[0]));
        float pred = outputFeature0.getFloatArray()[0];


        if (pred > 1 - pred) {
            binding.result.setText(R.string.positive);
            binding.result.setTextColor(getResources().getColor(R.color.red));
        } else {
            binding.result.setText(R.string.negative);
            binding.result.setTextColor(getResources().getColor(R.color.green));
        }

        Log.d(TAG, "infer: outputdata " + Arrays.toString(data));


        // Releases model resources if no longer used.
//        model.close();
        modelMobilenet20epoch.close();
    }

}