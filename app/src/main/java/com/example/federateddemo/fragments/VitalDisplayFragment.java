package com.example.federateddemo.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.federateddemo.Constants;
import com.example.federateddemo.Helpers.XAxisValueFormatter;
import com.example.federateddemo.R;
import com.example.federateddemo.databinding.FragmentVitalDisplayBinding;
import com.example.federateddemo.room.VitalViewModel;
import com.example.federateddemo.room.entities.Vital;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class VitalDisplayFragment extends Fragment {

    private static final String TAG = "VitalDisplayFragment";
    LineChart chart;
    VitalViewModel vitalViewModel;
    FragmentVitalDisplayBinding binding;
    int currentVital = Constants.TEMPERATURE_ID;
    int leftCardVital = Constants.PULSE_ID;
    int rightCardVital = Constants.SPO2_ID;
    int pagerType;
    List<Vital> vitalsData;


    public VitalDisplayFragment() {
        // Required empty public constructor
    }

    public static VitalDisplayFragment newInstance() {
        VitalDisplayFragment fragment = new VitalDisplayFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void updateUI() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.leftVitalCard.setOnClickListener(view1 -> {
            int temp = currentVital;
            currentVital = leftCardVital;
            leftCardVital = temp;
            setData();
        });

        binding.rightVitalCard.setOnClickListener(view1 -> {
            int temp = currentVital;
            currentVital = rightCardVital;
            rightCardVital = temp;
            setData();
        });


        Bundle args = getArguments();

        assert args != null;
        pagerType = args.getInt(Constants.PAGER_TYPE);
        String userId = args.getString(Constants.USER_ID);
        vitalViewModel = ViewModelProviders.of(this).get(VitalViewModel.class);


        binding.vitalValue.setText(String.valueOf(pagerType));

        getData(pagerType, userId);

        chart = binding.chart;
//
//        LimitLine ll1 = new LimitLine(30f, "Title");
//        ll1.setLineColor(getResources().getColor(R.color.olive));
//        ll1.setLineWidth(4f);
//        ll1.enableDashedLine(10f, 10f, 0f);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        ll1.setTextSize(10f);
//
//        LimitLine ll2 = new LimitLine(35f, "");
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);

        XAxis xAxis = chart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        xAxis.enableGridDashedLine(2f, 7f, 0f);
//        xAxis.setAxisMaximum(5f);
//        xAxis.setAxisMinimum(0f);
//        xAxis.setLabelCount(6, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(315f);


//        xAxis.setValueFormatter(new XAxisValueFormatter());

//        TODO set formatter according to tab
//        xAxis.setValueFormatter(new XAxisValueFormatter(pagerType));
//        xAxis.setCenterAxisLabels(true);
//
//
//        xAxis.setDrawLimitLinesBehindData(true);

        chart.getDescription().setEnabled(false);
        Description description = new Description();

        ArrayList<Entry> values = new ArrayList<>();


        LineDataSet lineDataSet = new LineDataSet(values, "VALUES");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.animate();
//        chart.notifyDataSetChanged();


//        xAxis.setValueFormatter(new XAxisValueFormatter(dates));
//        leftAxis.setValueFormatter(new YAxisValueFormatter());
        description.setText("Week");
        description.setTextSize(15f);


    }

    private void getData(int pagerType, String userId) {

        final List<Vital>[] data = new List[]{new ArrayList<>()};

//        TODO average the values of one day for a week, month and per month for year!
        switch (pagerType) {
            case Constants.WEEK:

                vitalViewModel.getUserVitalWeek()
                        .observe(getViewLifecycleOwner(), vitals -> {
                            Log.d(TAG, "onChanged: vitals fragment : " + vitals);
                            vitalsData = vitals;
                            setData();

                            XAxis xAxis = chart.getXAxis();
                            xAxis.setValueFormatter(new XAxisValueFormatter(Constants.WEEK));


                        });


                break;

            case Constants.MONTH:

                vitalViewModel.getUserVitalsMonth().observe(getViewLifecycleOwner(), vitals -> {
                    Log.d(TAG, "onChanged: Month : " + vitals);
                    vitalsData = vitals;
                    setData();

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new XAxisValueFormatter(Constants.MONTH));


                });

                break;
            case Constants.YEAR:
                vitalViewModel.getUserVitalsYear().observe(getViewLifecycleOwner(), vitals -> {
                    Log.d(TAG, "onChanged Year : " + vitals);
                    vitalsData = vitals;
                    setData();

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new XAxisValueFormatter(Constants.YEAR));
                });
            default:
        }


    }

    void setData() {

        ArrayList<Entry> values = new ArrayList<>();
        Vital lastVital = vitalsData.get(vitalsData.size() - 1);

        setVitalInfo(lastVital);

        for (Vital vital : vitalsData) {
            switch (currentVital) {
                case Constants.TEMPERATURE_ID:
                    values.add(new Entry(vital.getDate().getTime(), vital.getTemperature().floatValue()));
                    break;
                case Constants.SPO2_ID:
                    values.add(new Entry(vital.getDate().getTime(), vital.getSpo2().floatValue()));
                    break;
                case Constants.PULSE_ID:
                    values.add(new Entry(vital.getDate().getTime(), vital.getPulse().floatValue()));
                    break;

                default:



            }
        }

//
//        values.add(new Entry(0.5f, 300));
//        values.add(new Entry(1, 300));
//
//        values.add(new Entry(2, 100));
//        values.add(new Entry(3, 400));
//        values.add(new Entry(5, 90));
//        values.add(new Entry(8, 250));


        LineDataSet lineDataSet = new LineDataSet(values, getLineDataName());

        Log.d(TAG, "setData: linedataset : " + lineDataSet);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate();
    }

    private String getLineDataName() {
        switch (currentVital) {
            case Constants.TEMPERATURE_ID:
                return getString(R.string.temperature);
            case Constants.PULSE_ID:
                return getString(R.string.pulse);
            case Constants.SPO2_ID:
                return getString(R.string.spo2);
        }
        return "";
    }

    private void setVitalInfo(Vital vital) {
        updateVitalInfoUI(vital, leftCardVital, binding.leftVitalValue, binding.leftVitalName);
        updateVitalInfoUI(vital, rightCardVital, binding.rightVitalValue, binding.rightVitalName);
        updateVitalInfoUI(vital, currentVital, binding.vitalValue, binding.vitalName);
    }


    private void updateVitalInfoUI(Vital vital, int switchOn, TextView vitalValue, TextView vitalName) {
        String value;
        switch (switchOn) {
            case Constants.TEMPERATURE_ID:
                value = vital.getTemperature() + " " + Constants.DEGREE_C;
                vitalValue.setText(value);
                vitalName.setText(getString(R.string.temperature));
                break;
            case Constants.SPO2_ID:
                value = vital.getSpo2() + " %";
                vitalValue.setText(value);
                vitalName.setText(getString(R.string.spo2));
                break;
            case Constants.PULSE_ID:
                value = vital.getPulse() + " bpm";
                vitalValue.setText(value);
                vitalName.setText(getString(R.string.pulse));
                break;

            default:
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVitalDisplayBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}