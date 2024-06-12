package com.example.trading;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateValueFormatter extends ValueFormatter {
    private final SimpleDateFormat dateFormat;

    public DateValueFormatter() {
        this.dateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value) {
        long millis = (long) value;
        return dateFormat.format(new Date(millis));
    }
}
