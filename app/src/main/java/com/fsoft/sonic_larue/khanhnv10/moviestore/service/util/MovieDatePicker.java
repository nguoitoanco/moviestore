package com.fsoft.sonic_larue.khanhnv10.moviestore.service.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.app.DatePickerDialog.OnDateSetListener;
import static android.content.DialogInterface.*;


/**
 * Created by KhanhNV10 on 2015/12/02.
 */
public class MovieDatePicker extends DialogFragment {
    Calendar c = GregorianCalendar.getInstance();
    int startYear = c.get(Calendar.YEAR);
    int startMonth = c.get(Calendar.MONTH);
    int startDay = c.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog dialog;
    OnDateSetListener onDateSetListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        dialog = new DatePickerDialog(getContext(), onDateSetListener, startYear, startMonth, startDay);
        return dialog;

    }

    public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }
}