package com.fsoft.sonic_larue.khanhnv10.moviestore.service.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.app.TimePickerDialog.OnTimeSetListener;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by KhanhNV10 on 2015/12/02.
 */
public class MovieTimePicker extends DialogFragment {

    Calendar c = GregorianCalendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minutes = c.get(Calendar.MINUTE);

    private OnTimeSetListener onTimeSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        TimePickerDialog dialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minutes, false);
        return dialog;
    }

    public void setOnTimeSetListener(OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }
}
