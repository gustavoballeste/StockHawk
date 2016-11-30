package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.graphics.Color.GRAY;
import static android.graphics.Color.WHITE;

/**
 * Created by gustavoballeste on 29/11/16.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String STOCK_URI = "STOCK_URI";
    private Uri mUri;
    private static final int CHART_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {Contract.Quote.COLUMN_HISTORY};
    public static final int COL_HISTORY = 0;
    private LineChart lineChart;
    private String chartValues;
    private static final String CHART_VALUES_KEY = "CHART_VALUES_KEY";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(DetailFragment.STOCK_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        lineChart = (LineChart) rootView.findViewById(R.id.stock_chart);
        formatChartText();
        if(savedInstanceState != null){
            chartValues = savedInstanceState.getString(CHART_VALUES_KEY);
            loadChart(chartValues);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CHART_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CHART_VALUES_KEY, chartValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null != mUri){
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new android.support.v4.content.CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToNext()){
            chartValues = data.getString(COL_HISTORY);
            loadChart(chartValues);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadChart(String values){

        String chartEntries[] = values.split("\\r?\\n");
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i=chartEntries.length-1; i>=0; i--) {
            String chartEntry = chartEntries[i];
            String timeMilliSecondsStr = chartEntry.split(",")[0];
            long timeInMilliSeconds = Long.parseLong(timeMilliSecondsStr);
            String time = getMonth(timeInMilliSeconds);
            String value = chartEntry.split(",")[1];
            entries.add(new Entry( Float.parseFloat(value), chartEntries.length-i) );
            labels.add(time);
        }
        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.chartYAxis));

        int color = ContextCompat.getColor(getContext(), R.color.chart_yellow);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setCircleColorHole(color);
        LineData lineData = new LineData(labels, dataSet);
        lineData.setValueTextColor(GRAY);
        lineChart.setData(lineData);
    }

    public void formatChartText() {
        lineChart.setDescriptionTextSize(15f);
        lineChart.setDescriptionColor(WHITE);
        lineChart.getXAxis().setTextColor(WHITE);
        lineChart.getLegend().setTextColor(WHITE);
        lineChart.getAxisLeft().setTextColor(WHITE);
        lineChart.getAxisRight().setTextColor(WHITE);
        lineChart.setDescription("");

        lineChart.animateX(2500);
    }

    public static String getMonth(long dateInMillis ) {
        SimpleDateFormat month = new SimpleDateFormat("MMM/yyyy");
        String monthString = month.format(dateInMillis);
        return monthString;
    }
}
