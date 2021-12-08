package com.example.federateddemo;

import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.federateddemo.databinding.ActivityQuizBinding;
import com.example.federateddemo.ml.Ann80;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private final ArrayList<String> questions = new ArrayList<>();
    private final ArrayList<String> options = new ArrayList<>();
    private final HashMap<String, Integer> optionsValueMap = new HashMap<>();
    private final ArrayList<Float> scores = new ArrayList<>();
    private ActivityQuizBinding mBinding;
    private int currentQuestionIndex = 0;
    private TypedArray questionsData;
    private boolean updated = false;
    private final View.OnClickListener buttonClickListener = view -> {
        Button button = (Button) view;
        try {
            Log.d(TAG, "onClick: map male:" + optionsValueMap.get("Male"));

            if (!updated) {
                Log.d(TAG, "NOT UPDATED: ");
            } else {
                Log.d(TAG, "NOTUPDATED: ");
            }
            String key = button.getText().toString().toLowerCase();

            Log.d(TAG, "key : " + key);

            scores.add((float) Float.valueOf(optionsValueMap.get(key)));
            currentQuestionIndex++;
            updateOptions();
            configureView();
        } catch (NullPointerException e) {
            Toast.makeText(QuizActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.questionsLayout.setVisibility(View.VISIBLE);
        mBinding.scoreLayout.setVisibility(View.GONE);
        mBinding.ageEdittext.setVisibility(View.VISIBLE);
        mBinding.optionsGroup.setVisibility(View.GONE);

        Map<String, String> map = ResourceUtils.getHashMapResource(QuizActivity.this, R.xml.map);
        assert map != null;
        Log.d(TAG, "onCreate: map : " + map.toString());

        questionsData = getResources().obtainTypedArray(R.array.questions);

        addQuestions();
        updateOptions();

        mBinding.next.setOnClickListener(v -> {
            int age = Integer.parseInt((mBinding.ageEdittext.getText().toString()));
            if (age < 0 || age > 150) {
                mBinding.ageEdittext.setText("");
                mBinding.ageEdittext.setError("Please enter valid age");
            } else {
                float x_min = 5, x_max = 72;
                float score = (age - x_min) / (x_max - x_min);


                scores.add((float) score);
                currentQuestionIndex++;
                updateOptions();
                configureView();
            }
        });

        mBinding.option1.setOnClickListener(buttonClickListener);
        mBinding.option2.setOnClickListener(buttonClickListener);
        mBinding.option3.setOnClickListener(buttonClickListener);
        mBinding.option4.setOnClickListener(buttonClickListener);
        mBinding.option5.setOnClickListener(buttonClickListener);

        mBinding.returnBack.setOnClickListener(view -> finish());


    }

    private void addQuestions() {
        for (int i = 0; i < questionsData.length(); i += 2) {
            Log.d(TAG, "addQuestions: " + questionsData.getString(i));
            questions.add(questionsData.getString(i));
        }
    }


    private void updateOptions() {
        options.clear();
        if (currentQuestionIndex != 0 && currentQuestionIndex < questions.size()) {
            List<String> temp = Arrays.asList(getResources().getStringArray(questionsData.getResourceId(currentQuestionIndex * 2 + 1, -1)));
            String option = "";

            Integer value = 0;
            for (int i = 0; i < temp.size(); i++) {
                Log.d(TAG, "updateOptions:" + temp.get(i));
                if (i % 2 == 0) {
                    option = temp.get(i);
                    options.add(option);
                } else {
                    Log.d(TAG, "updateOptions: value:" + Integer.parseInt(temp.get(i)));
                    optionsValueMap.put(option.toLowerCase(), Integer.parseInt(temp.get(i)));
                    updated = true;
                }
            }

            Log.d(TAG, "updateOptions: options : " + options.toString());
        }
    }


    private void configureView() {

        if (currentQuestionIndex < questions.size()) {


            String question = questions.get(currentQuestionIndex);
            mBinding.question.setText(question);

            if (question.equals("Age")) {
                mBinding.ageEdittext.setVisibility(View.VISIBLE);
                mBinding.optionsGroup.setVisibility(View.GONE);
                mBinding.next.setVisibility(View.VISIBLE);
            } else {
                mBinding.ageEdittext.setVisibility(View.GONE);
                mBinding.optionsGroup.setVisibility(View.VISIBLE);
                mBinding.next.setVisibility(View.GONE);

                hideButtons();

                switch (options.size()) {
                    case 5:
                        setOption(mBinding.option5, options.get(4));
                    case 4:
                        setOption(mBinding.option4, options.get(3));
                    case 3:
                        setOption(mBinding.option3, options.get(2));
                    case 2:
                        setOption(mBinding.option2, options.get(1));
                    case 1:
                        setOption(mBinding.option1, options.get(0));
                }
            }
        } else {
            mBinding.questionsLayout.setVisibility(View.GONE);
            mBinding.scoreLayout.setVisibility(View.VISIBLE);


            try {
                doInference();
            } catch (IOException e) {
                Toast.makeText(QuizActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

//            infer();

        }
    }


    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("ANN_80_1.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len);
    }


    public void doInference() throws IOException {
//        float[] input = new float[1];
//        input[0] = Float.parseFloat(val);

        float[] vector = new float[scores.size()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = scores.get(i);

        }
        float[][] output = new float[1][1];
        Interpreter interpreter = new Interpreter(loadModelFile(), null);


        interpreter.run(vector, output);
        Log.d(TAG, "doInference: output : " + output[0][0]);
        float pred = output[0][0];

        mBinding.score.setText(String.valueOf(pred));
        if (pred > 0.3) {
            mBinding.status.setText(R.string.consider_treatment);
            mBinding.status.setTextColor(getResources().getColor(R.color.red));
        } else {
            mBinding.status.setText(R.string.report_normal);
            mBinding.status.setTextColor(getResources().getColor(R.color.green));
        }
    }


//    private void infer() {
//        try {
//            Ann80 model = Ann80.newInstance(getApplicationContext());
//
//            float[] vector = new float[scores.size()];
//            for (int i = 0; i < vector.length; i++) {
//                vector[i] = scores.get(i);
//
//            }
//            Log.d(TAG, "infer: vector : " + Arrays.toString(vector));
//            int capacity = scores.size() * 4;
//            Log.d(TAG, "infer: capacity : " + capacity);
//            ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
//            byteBuffer.rewind();
//
//            Log.d(TAG, "infer: bytebuffer" + byteBuffer);
//
//            for (float i : vector)
//                byteBuffer.putFloat((float) i);
////            ByteBuffer byteBuffer = ByteBuffer.allocate(224 * 224 * 3);
//
//            byteBuffer.rewind();
//            for (int i = 0; i < capacity / 4; i++) {
//                Log.d(TAG, "infer: float : " + i + " : " + byteBuffer.getFloat());
//            }
//
//
//            Log.d(TAG, "infer: hasarray:" + byteBuffer.hasArray());
//
////            byteBuffer.rewind();
//
//            String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
//
//            Log.d(TAG, "infer: string : " + s);
//
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 8}, DataType.FLOAT32);
//            inputFeature0.loadBuffer(byteBuffer);
//
//
//            // Runs model inference and gets result.
//            Ann80.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            float[] data = outputFeature0.getFloatArray();
//
//            Log.d(TAG, "infer: data : " + Arrays.toString(data));
//
//            float pred = outputFeature0.getFloatArray()[0];
//
//            mBinding.score.setText(String.valueOf(pred));
//            if (pred > 0.3) {
//
////                mBinding.result.setTextColor(getResources().getColor(R.color.red));
//            } else {
////                mBinding.result.setText("NEGATIVE");
////                mBinding.result.setTextColor(getResources().getColor(R.color.green));
//            }
//            // Releases model resources if no longer used.
//            model.close();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private void setOption(Button button, String text) {
        button.setVisibility(View.VISIBLE);
        button.setText(text);
    }

    private void hideButtons() {
        mBinding.option1.setVisibility(View.GONE);
        mBinding.option2.setVisibility(View.GONE);
        mBinding.option3.setVisibility(View.GONE);
        mBinding.option4.setVisibility(View.GONE);
        mBinding.option5.setVisibility(View.GONE);
    }


}