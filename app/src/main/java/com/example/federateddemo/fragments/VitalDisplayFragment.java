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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        assert args != null;
//        ViewPagerItem viewPagerItem = (ViewPagerItem) args.getSerializable(Constants.PAGER_ITEM);
        int pagerType = args.getInt(Constants.PAGER_TYPE);
        String userId = args.getString(Constants.USER_ID);
        vitalViewModel = ViewModelProviders.of(this).get(VitalViewModel.class);


//        int temp = args.getInt(Constants.PAGER_ITEM);
        TextView textView = view.findViewById(R.id.vital_value);

        getData(pagerType, userId);

        chart = view.findViewById(R.id.chart);
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


//        values.add(new Entry(0.5f, 300));
//        values.add(new Entry(1, 300));
//
//        values.add(new Entry(2, 100));
//        values.add(new Entry(3, 400));
//        values.add(new Entry(5, 90));
//        values.add(new Entry(8, 250));


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


        textView.setText(String.valueOf(pagerType));
    }

    private void getData(int pagerType, String userId) {

        final List<Vital>[] data = new List[]{new ArrayList<>()};

//        TODO average the values of one day for a week, month and per month for year!
        switch (pagerType) {
            case Constants.WEEK:

                vitalViewModel.getUserVitalWeek()
                        .observe(getViewLifecycleOwner(), vitals -> {
                            Log.d(TAG, "onChanged: vitals fragment : " + vitals);
                            data[0] = vitals;
                            setData(data[0], Constants.TEMPERATURE);

                            XAxis xAxis = chart.getXAxis();
                            xAxis.setValueFormatter(new XAxisValueFormatter(Constants.WEEK));


                        });


                break;

            case Constants.MONTH:

                vitalViewModel.getUserVitalsMonth().observe(getViewLifecycleOwner(), vitals -> {
                    Log.d(TAG, "onChanged: Month : " + vitals);
                    data[0] = vitals;
                    setData(data[0], Constants.TEMPERATURE);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new XAxisValueFormatter(Constants.MONTH));


                });

                break;
            case Constants.YEAR:
                vitalViewModel.getUserVitalsYear().observe(getViewLifecycleOwner(), vitals -> {
                    Log.d(TAG, "onChanged Year : " + vitals);
                    data[0] = vitals;
                    setData(data[0], Constants.TEMPERATURE);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new XAxisValueFormatter(Constants.YEAR));
                });
            default:
        }



    }

    void setData(List<Vital> data, String vitalType) {

        ArrayList<Entry> values = new ArrayList<>();


        for (Vital vital : data) {
            switch (vitalType) {
                case Constants.TEMPERATURE:
                    values.add(new Entry(vital.getDate().getTime(), vital.getTemperature().floatValue()));
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


        LineDataSet lineDataSet = new LineDataSet(values, Constants.TEMPERATURE.toUpperCase(Locale.ROOT));

        Log.d(TAG, "setData: linedataset : " + lineDataSet);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vital_display, container, false);
    }
}